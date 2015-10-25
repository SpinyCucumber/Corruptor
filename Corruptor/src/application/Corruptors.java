package application;

import java.util.Random;

public final class Corruptors {
	
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
	
	public static final Corruptor SWAP = new Corruptor("SWAPPER") {
		
		@Override
		public void corrupt(byte[] buffer, int bytes) {
			for(int i = 0; i < bytes; i++) {
				int i_0 = random.nextInt(buffer.length), i_1 = random.nextInt(buffer.length);
				byte a = buffer[i_0], b = buffer[i_1];
				buffer[i_0] = b;
				buffer[i_1] = a;
			}
		}
		
	};
	
	public static final Corruptor SHIFT = new Corruptor("SHIFT") {

		@Override
		public void corrupt(byte[] buffer, int bytes) {
			for(int i = 0; i < bytes; i++) {
				buffer[random.nextInt(buffer.length)] += 1;
			}
		}
		
	};
	
	private static Random random = new Random();
	
}
