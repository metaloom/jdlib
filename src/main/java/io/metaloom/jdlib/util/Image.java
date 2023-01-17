package io.metaloom.jdlib.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Image {

	public final int width;
	public final int height;
	private final boolean hasAlphaChannel;
	private int pixelLength;
	public final byte[] pixels;

	public Image(BufferedImage image) {
		pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		width = image.getWidth();
		height = image.getHeight();
		hasAlphaChannel = image.getAlphaRaster() != null;
		pixelLength = 3;
		if (hasAlphaChannel) {
			pixelLength = 4;
		}
	}

	short[] getRGB(int x, int y) {
		int pos = (y * pixelLength * width) + (x * pixelLength);
		short rgb[] = new short[4];
		if (hasAlphaChannel) {
			rgb[3] = (short) (pixels[pos++] & 0xFF); // Alpha
		}
		rgb[2] = (short) (pixels[pos++] & 0xFF); // Blue
		rgb[1] = (short) (pixels[pos++] & 0xFF); // Green
		rgb[0] = (short) (pixels[pos++] & 0xFF); // Red
		return rgb;
	}
}
