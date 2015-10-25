	package application;

public class Chunk {
	
	private String name;
	private int begin, end;
	
	public String getName() {
		return name;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public Chunk(String name, int begin, int end) {
		this.name = name;
		this.begin = begin;
		this.end = end;
	}
	
	@Override
	public String toString() {
		return name + " (" + (end-begin) + ")";
	}
	
}
