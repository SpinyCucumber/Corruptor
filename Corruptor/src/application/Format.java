package application;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TreeItem;

public enum Format {
	
	MIDI("mid") {
		
		@Override
		public Collection<TreeItem<Chunk>> getChunks(byte[] buffer) throws IOException {
			List<TreeItem<Chunk>> chunks = new ArrayList<TreeItem<Chunk>>();
			ByteBuffer in = ByteBuffer.wrap(buffer);
			while(in.hasRemaining()) {
				byte[] type = new byte[4];
				in.get(type);
				String name = chunkTypes.get(new String(type, StandardCharsets.US_ASCII));
				int size = in.getInt(), begin = in.position(), end = begin+size;
				Chunk chunk = new Chunk(name == null ? "Unknown Chunk" : name, begin, end);
				chunks.add(new TreeItem<Chunk>(chunk));
				in.position(end);
			}
			return chunks;
		}
		
	},
	NONE("txt") {

		@Override
		public Collection<TreeItem<Chunk>> getChunks(byte[] buffer) throws IOException {
			return Collections.emptyList();
		}
		
	};
	
	private static final Map<String, String> chunkTypes;
	private static Map<String, Format> map;
	
	static {
		chunkTypes = new HashMap<String, String>();
		chunkTypes.put("MThd", "MIDI Header");
		chunkTypes.put("MTrk", "MIDI Track");
		map = new HashMap<String, Format>();
		for(Format fmt : values()) {
			for(String ext : fmt.exts) map.put(ext, fmt);
		}
	}
	
	public static Format getByExt(File file) {
		Format fmt = map.get(file.getName().split("\\.")[1]);
		return fmt == null ? NONE : fmt;
	}
	
	private String[] exts;
	
	private Format(String...exts) {
		this.exts = exts;
	}
	
	//JavaFX provides a good tree class. I prefer not to use the TreeView because of its graphical nature.
	public abstract Collection<TreeItem<Chunk>> getChunks(byte[] buffer) throws IOException;
	
	public static int swap (int value) {
	    int b1 = (value >>  0) & 0xff;
	    int b2 = (value >>  8) & 0xff;
	    int b3 = (value >> 16) & 0xff;
	    int b4 = (value >> 24) & 0xff;

	    return b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0;
	}
	
}
