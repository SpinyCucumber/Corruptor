package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Arrays;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller implements Initializable {
	
	private Stage stage;
	private Corruptor corruptor;
	private byte[] buffer_0, buffer_1;
	private File dir;
	
	@FXML private TreeView<Chunk> tree;
	@FXML private RadioButton bytesRadio;
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
			byte[] bytes = Arrays.copyOfRange(buffer_0, chunk.getBegin(), chunk.getEnd());
			double amt = Double.parseDouble(hi.getText());
			corruptor.corrupt(bytes, (int) (bytesRadio.isSelected() ? amt : amt*bytes.length));
			System.arraycopy(bytes, 0, buffer_1, chunk.getBegin(), bytes.length);
		}
	}
	
	@FXML
	void reset(ActionEvent event) {
		buffer_1 = buffer_0.clone();
	}
	
	@FXML
    void changeCorruptor(ActionEvent event) {
		corruptor = corruptorBox.getValue();
    }
	
	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        corruptorBox.getItems().add(Corruptors.RANDOM);
        corruptorBox.getItems().add(Corruptors.SWAP);
        corruptorBox.getItems().add(Corruptors.SHIFT);
        optionsPane.getChildren().add(new Label("NOPE"));
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

	
}
