package application;

public abstract class Corruptor {
	
	private String name;
	
	public abstract void corrupt(byte[] buffer, int bytes);

	public Corruptor(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
}
