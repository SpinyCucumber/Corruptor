package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Arrays;

import application.Corruptors.Corruptor;
import application.Corruptors.Option;
import application.Format.Chunk;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller extends Application implements Initializable {
	
	private static final double H_SPACING = 10;
	
	private Stage stage;
	private Corruptor corruptor;
	private byte[] buffer_0, buffer_1;
	private File dir;
	
	@FXML private TreeView<Chunk> tree;
	@FXML private ToggleGroup quantityGroup;
	@FXML private TextField hi;
	@FXML private ComboBox<Corruptor> corruptorBox;
	@FXML private FlowPane optionsPane;
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
    void loadFile(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load File");
		if(dir != null) fileChooser.setInitialDirectory(dir);
		File file = fileChooser.showOpenDialog(stage);
		if(file == null) return;
		try {
			dir = file.getParentFile();
			buffer_0 = Files.readAllBytes(file.toPath());
			buffer_1 = buffer_0.clone();
			Format fmt = Format.getByExt(file);
			TreeItem<Chunk> root = new TreeItem<Chunk>(new Chunk(file.getName(), 0, buffer_0.length));
			root.getChildren().addAll(fmt.getChunks(buffer_0));
			tree.setRoot(root);
		} catch (IOException e) {
			error("Error loading file", e);
		}
    }
	
	@FXML
	void saveFile(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save to File");
		if(dir != null) fileChooser.setInitialDirectory(dir);
		File file = fileChooser.showSaveDialog(stage);
		if(file == null) return;
		try {
			dir = file.getParentFile();
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file); //Will never throw FileNotFoundException.
			out.write(buffer_1);
			out.close();
		} catch (IOException e) {
			error("Error saving to file", e);
		}
	}
	
	@FXML
	void corrupt(ActionEvent event) {
		if(corruptor == null) return;
		for(TreeItem<Chunk> item : tree.getSelectionModel().getSelectedItems()) {
			Chunk chunk = item.getValue();
			byte[] selection = Arrays.copyOfRange(buffer_0, chunk.getBegin(), chunk.getEnd());
			double amt = Double.parseDouble(hi.getText());
			int bytes = 0;
			switch(((RadioButton) quantityGroup.getSelectedToggle()).getText()) {
				case "Bytes" : bytes = (int) amt; break;
				case "Ratio" : bytes = (int) (amt*selection.length); break;
			}
			corruptor.corrupt(selection, bytes);
			System.arraycopy(selection, 0, buffer_1, chunk.getBegin(), selection.length);
		}
	}
	
	@FXML
	void reset(ActionEvent event) {
		buffer_1 = buffer_0.clone();
	}
	
	@FXML
    void changeCorruptor(ActionEvent event) {
		corruptor = corruptorBox.getValue();
		optionsPane.getChildren().clear();
		for(Option optn : corruptor.getOptions()) {
			HBox box = new HBox();
			box.setSpacing(H_SPACING);
			box.getChildren().add(new Label(optn.getName()));
			box.getChildren().add(optn.getNode());
			optionsPane.getChildren().add(box);
		}
    }
	
	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        corruptorBox.getItems().addAll(Corruptors.LIST);
    }
	
	private static void error(String info, Exception ex) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("An Exception has Occured");
		alert.setContentText(info);

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("Here's the stacktrace:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Corruptor.fxml"));
			Scene scene = new Scene(loader.load(), 547, 401);
			((Controller) loader.getController()).setStage(primaryStage);
			primaryStage.setTitle("Corruptor");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
