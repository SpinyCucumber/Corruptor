package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.Node;
import javafx.scene.control.TextField;

public final class Corruptors {
	
	public static abstract class Corruptor {
		
		private String name;
		protected List<Option> options;
		
		public abstract void corrupt(byte[] buffer, int bytes);

		public List<Option> getOptions() {
			return options;
		}
		
		public Corruptor(String name) {
			this.name = name;
			options = new ArrayList<Option>();
		}

		@Override
		public String toString() {
			return name;
		}
		
	}
	
	public static class Option {
		
		private String name;
		private Node node;
		
		public String getName() {
			return name;
		}

		public Node getNode() {
			return node;
		}

		public Option(String name, Node node) {
			this.name = name;
			this.node = node;
		}
		
	}
	
	public static final Corruptor RANDOM = new Corruptor("RANDOM") {
		
		@Override
		public void corrupt(byte[] buffer, int bytes) {
			byte[] newData = new byte[bytes];
			random.nextBytes(newData);
			for(int i = 0; i < bytes; i++) {
				buffer[random.nextInt(buffer.length)] = newData[i];
			}
		}
		
	};
	
	public static final Corruptor SWAP = new Corruptor("SWAP") {
		
		@Override
		public void corrupt(byte[] buffer, int bytes) {
			for(int i = 0; i < bytes/2; i++) {
				int i_0 = random.nextInt(buffer.length), i_1 = random.nextInt(buffer.length);
				byte a = buffer[i_0], b = buffer[i_1];
				buffer[i_0] = b;
				buffer[i_1] = a;
			}
		}
		
	};
	
	public static final Corruptor SHIFT = new Corruptor("SHIFT") {
		
		private TextField amt;
		
		{
			amt = new TextField();
			amt.setPrefWidth(40);
			options.add(new Option("Amount:", (Node) amt));
		}
		
		@Override
		public void corrupt(byte[] buffer, int bytes) {
			int shift = Integer.parseInt(amt.getText());
			for(int i = 0; i < bytes; i++) {
				buffer[random.nextInt(buffer.length)] += shift;
			}
		}
		
	};
	
	private static Random random = new Random();
	public static final List<Corruptor> LIST;
	
	static {
		LIST = new ArrayList<Corruptor>();
		LIST.add(RANDOM);
		LIST.add(SHIFT);
		LIST.add(SWAP);
	}
	
}
