package com.jfinal.ext.kit;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Image {
	private static final Logger logger = LoggerFactory.getLogger(Image.class);
	private BufferedImage bufferedImage;
	private String fileName;

	public Image(File imageFile) {
		try {
			this.bufferedImage = ImageIO.read(imageFile);
			this.fileName = imageFile.getAbsolutePath();
		} catch (Exception e) {
			logger.error(e.getMessage());
			this.bufferedImage = null;
			imageFile = null;
		}
	}

	public Image(String imageFilePath) {
		this(new File(imageFilePath));
	}

	public BufferedImage getAsBufferedImage() {
		return this.bufferedImage;
	}

	public void saveAs(String fileName) {
		saveImage(new File(fileName));
		this.fileName = fileName;
	}

	public void save() {
		saveImage(new File(this.fileName));
	}

	public void resize(int percentOfOriginal) {
		int newWidth = this.bufferedImage.getWidth() * percentOfOriginal / 100;
		int newHeight = this.bufferedImage.getHeight() * percentOfOriginal / 100;
		resize(newWidth, newHeight);
	}

	public void resize(int newWidth, int newHeight) {
		int oldWidth = this.bufferedImage.getWidth();
		int oldHeight = this.bufferedImage.getHeight();

		if ((newWidth == -1) || (newHeight == -1))
			if (newWidth == -1) {
				if (newHeight == -1) {
					return;
				}

				newWidth = newHeight * oldWidth / oldHeight;
			} else {
				newHeight = newWidth * oldHeight / oldWidth;
			}

		BufferedImage result = new BufferedImage(newWidth, newHeight, 4);

		int widthSkip = oldWidth / newWidth;
		int heightSkip = oldHeight / newHeight;

		if (widthSkip == 0)
			widthSkip = 1;
		if (heightSkip == 0)
			heightSkip = 1;

		for (int x = 0; x < oldWidth; x += widthSkip) {
			for (int y = 0; y < oldHeight; y += heightSkip) {
				int rgb = this.bufferedImage.getRGB(x, y);

				if ((x / widthSkip < newWidth) && (y / heightSkip < newHeight))
					result.setRGB(x / widthSkip, y / heightSkip, rgb);

			}

		}

		this.bufferedImage = result;
	}

	public void addPixelColor(int numToAdd) {
		int width = this.bufferedImage.getWidth();
		int height = this.bufferedImage.getHeight();

		for (int x = 0; x < width; ++x)
			for (int y = 0; y < height; ++y) {
				int rgb = this.bufferedImage.getRGB(x, y);
				this.bufferedImage.setRGB(x, y, rgb + numToAdd);
			}
	}

	public void convertToBlackAndWhite() {
		ColorSpace gray_space = ColorSpace.getInstance(1003);
		ColorConvertOp convert_to_gray_op = new ColorConvertOp(gray_space, null);
		convert_to_gray_op.filter(this.bufferedImage, this.bufferedImage);
	}

	public void rotateLeft() {
		int width = this.bufferedImage.getWidth();
		int height = this.bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(height, width, 4);

		for (int x = 0; x < width; ++x)
			for (int y = 0; y < height; ++y) {
				int rgb = this.bufferedImage.getRGB(x, y);
				result.setRGB(y, x, rgb);
			}

		this.bufferedImage = result;
	}

	public void rotateRight() {
		int width = this.bufferedImage.getWidth();
		int height = this.bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(height, width, 4);

		for (int x = 0; x < width; ++x)
			for (int y = 0; y < height; ++y) {
				int rgb = this.bufferedImage.getRGB(x, y);
				result.setRGB(height - y - 1, x, rgb);
			}

		this.bufferedImage = result;
	}

	public void rotate180() {
		int width = this.bufferedImage.getWidth();
		int height = this.bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(width, height, 4);

		for (int x = 0; x < width; ++x)
			for (int y = 0; y < height; ++y) {
				int rgb = this.bufferedImage.getRGB(x, y);
				result.setRGB(width - x - 1, height - y - 1, rgb);
			}

		this.bufferedImage = result;
	}

	public void flipHorizontally() {
		int width = this.bufferedImage.getWidth();
		int height = this.bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(width, height, 4);

		for (int x = 0; x < width; ++x)
			for (int y = 0; y < height; ++y) {
				int rgb = this.bufferedImage.getRGB(x, y);
				result.setRGB(width - x - 1, y, rgb);
			}

		this.bufferedImage = result;
	}

	public void flipVertically() {
		int width = this.bufferedImage.getWidth();
		int height = this.bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(width, height, 4);

		for (int x = 0; x < width; ++x)
			for (int y = 0; y < height; ++y) {
				int rgb = this.bufferedImage.getRGB(x, y);
				result.setRGB(x, height - y - 1, rgb);
			}

		this.bufferedImage = result;
	}

	public void multiply(int timesToMultiplyVertically,
			int timesToMultiplyHorizantelly) {
		multiply(timesToMultiplyVertically, timesToMultiplyHorizantelly, 0);
	}

	public void multiply(int timesToMultiplyVertically,
			int timesToMultiplyHorizantelly, int colorToHenhancePerPixel) {
		int width = this.bufferedImage.getWidth();
		int height = this.bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(width
				* timesToMultiplyVertically, height
				* timesToMultiplyHorizantelly, 4);

		for (int xx = 0; xx < timesToMultiplyVertically; ++xx) {
			for (int yy = 0; yy < timesToMultiplyHorizantelly; ++yy)
				for (int x = 0; x < width; ++x)
					for (int y = 0; y < height; ++y) {
						int rgb = this.bufferedImage.getRGB(x, y);
						result.setRGB(width * xx + x, height * yy + y, rgb
								+ colorToHenhancePerPixel * (yy + xx));
					}

		}

		this.bufferedImage = result;
	}

	public void combineWithPicture(String newImagePath) {
		combineWithPicture(newImagePath, 2);
	}

	public void combineWithPicture(String newImagePath, int jump) {
		BufferedImage bufferedImage2;
		try {
			bufferedImage2 = ImageIO.read(new File(newImagePath));
			combineWithPicture(bufferedImage2, jump, null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public void combineWithPicture(Image image2) {
		combineWithPicture(image2.getAsBufferedImage(), 2, null);
	}

	public void combineWithPicture(Image image2, int jump) {
		combineWithPicture(image2.getAsBufferedImage(), jump, null);
	}

	public void combineWithPicture(Image image2, Color ignoreColor) {
		combineWithPicture(image2.getAsBufferedImage(), 2, ignoreColor);
	}

	public void combineWithPicture(Image image2, int jump, Color ignoreColor) {
		combineWithPicture(image2.getAsBufferedImage(), jump, ignoreColor);
	}

	private void combineWithPicture(BufferedImage bufferedImage2, int jump,
			Color ignoreColor) {
		checkJump(jump);

		int width = this.bufferedImage.getWidth();
		int height = this.bufferedImage.getHeight();

		int width2 = bufferedImage2.getWidth();
		int height2 = bufferedImage2.getHeight();

		int ignoreColorRgb = -1;

		if (ignoreColor != null) {
			ignoreColorRgb = ignoreColor.getRGB();
		}

		for (int y = 0; y < height; ++y)
			for (int x = y % jump; x < width; x += jump)
				if (x < width2) {
					if (y >= height2) {
						continue;
					}

					int rgb = bufferedImage2.getRGB(x, y);

					if (rgb != ignoreColorRgb)
						this.bufferedImage.setRGB(x, y, rgb);
				}
	}

	public void crop(int startX, int startY, int endX, int endY) {
		int width = this.bufferedImage.getWidth();
		int height = this.bufferedImage.getHeight();

		if (startX == -1) {
			startX = 0;
		}

		if (startY == -1) {
			startY = 0;
		}

		if (endX == -1) {
			endX = width - 1;
		}

		if (endY == -1) {
			endY = height - 1;
		}

		BufferedImage result = new BufferedImage(endX - startX + 1, endY
				- startY + 1, 4);

		for (int y = startY; y < endY; ++y)
			for (int x = startX; x < endX; ++x) {
				int rgb = this.bufferedImage.getRGB(x, y);
				result.setRGB(x - startX, y - startY, rgb);
			}

		this.bufferedImage = result;
	}

	private void saveImage(File file) {
		try {
			ImageIO.write(this.bufferedImage, getFileType(file), file);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public void emphasize(int startX, int startY, int endX, int endY) {
		emphasize(startX, startY, endX, endY, Color.BLACK, 3);
	}

	public void emphasize(int startX, int startY, int endX, int endY,
			Color backgroundColor) {
		emphasize(startX, startY, endX, endY, backgroundColor, 3);
	}

	public void emphasize(int startX, int startY, int endX, int endY, int jump) {
		emphasize(startX, startY, endX, endY, Color.BLACK, jump);
	}

	public void emphasize(int startX, int startY, int endX, int endY,
			Color backgroundColor, int jump) {
		checkJump(jump);

		int width = this.bufferedImage.getWidth();
		int height = this.bufferedImage.getHeight();

		if (startX == -1) {
			startX = 0;
		}

		if (startY == -1) {
			startY = 0;
		}

		if (endX == -1) {
			endX = width - 1;
		}

		if (endY == -1) {
			endY = height - 1;
		}

		for (int y = 0; y < height; ++y)
			for (int x = y % jump; x < width; x += jump) {
				if ((y >= startY) && (y <= endY) && (x >= startX)
						&& (x <= endX)) {
					continue;
				}

				this.bufferedImage.setRGB(x, y, backgroundColor.getRGB());
			}
	}

	private void checkJump(int jump) {
		if (jump < 1)
			throw new RuntimeException("Error: jump can not be less than 1");
	}

	public void addColorToImage(Color color, int jump) {
		addColorToImage(color.getRGB(), jump);
	}

	public void addColorToImage(int rgb, int jump) {
		checkJump(jump);

		int width = this.bufferedImage.getWidth();
		int height = this.bufferedImage.getHeight();

		for (int y = 0; y < height; ++y)
			for (int x = y % jump; x < width; x += jump)
				this.bufferedImage.setRGB(x, y, rgb);
	}

	public void affineTransform(double fShxFactor, double fShyFactor) {
		AffineTransform shearer;
		try {
			shearer = AffineTransform.getShearInstance(fShxFactor, fShyFactor);
			AffineTransformOp shear_op = new AffineTransformOp(shearer, null);
			this.bufferedImage = shear_op.filter(this.bufferedImage, null);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private String getFileType(File file) {
		String fileName = file.getName();
		int idx = fileName.lastIndexOf(".");
		if (idx == -1) {
			throw new RuntimeException("Invalid file name");
		}

		return fileName.substring(idx + 1);
	}

	public int getWidth() {
		return this.bufferedImage.getWidth();
	}

	public int getHeight() {
		return this.bufferedImage.getHeight();
	}
}