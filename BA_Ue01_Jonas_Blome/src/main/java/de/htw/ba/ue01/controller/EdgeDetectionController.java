/**
 * @author Nico Hezel
 */
package de.htw.ba.ue01.controller;

import java.text.ParseException;

public class EdgeDetectionController extends EdgeDetectionBase {

	protected enum Methods {
		Kopie, Graustufen, XGradient, YGradient, XGradientSobel, YGradientSobel, GradientAbs
	}

	@Override
	public void runMethod(Methods currentMethod, int[] srcPixels, int[] dstPixels, int width, int height) throws Exception {
		double parameter1 = getParameter();
		double[][] filter;

		switch (currentMethod) {
			case Graustufen:
				doGray(srcPixels, dstPixels, width, height, parameter1);
				break;
			case XGradient:
				doGray(srcPixels, dstPixels, width, height, parameter1);
				filter = new double[][] {
						{-0.5, 0, 0.5}
				};

				for(int i = 0; i < filter.length; i++) {
					for(int j = 0; j < filter[0].length; j++) {
						filter[i][j] *= 1.0;
					}
				}

				doFilter(dstPixels, dstPixels, width, height, filter);
				break;
			case YGradient:
				doGray(srcPixels, dstPixels, width, height, parameter1);
				filter = new double[][] {
						{-0.5},
						{0},
						{0.5}
				};

				for(int i = 0; i < filter.length; i++) {
					for(int j = 0; j < filter[0].length; j++) {
						filter[i][j] *= 1.0;
					}
				}

				doFilter(dstPixels, dstPixels, width, height, filter);
				break;
			case XGradientSobel:
				doGray(srcPixels, dstPixels, width, height, parameter1);
				filter = new double[][] {
						{-1, 0, 1},
						{-2, 0, 2},
						{-1, 0, 1}
				};

				for(int i = 0; i < filter.length; i++) {
					for(int j = 0; j < filter[0].length; j++) {
						filter[i][j] *= 0.125;
					}
				}

				doFilter(dstPixels, dstPixels, width, height, filter);
				break;
			case YGradientSobel:
				doGray(srcPixels, dstPixels, width, height, parameter1);
				filter = new double[][] {
						{-1, -2, -1},
						{0, 0, 0},
						{1, 2, 1}
				};

				for(int i = 0; i < filter.length; i++) {
					for(int j = 0; j < filter[0].length; j++) {
						filter[i][j] *= 0.125;
					}
				}

				doFilter(dstPixels, dstPixels, width, height, filter);
				break;
			case Kopie:
				doCopy(srcPixels, dstPixels, width, height);
				break;
			default:
				doCopy(srcPixels, dstPixels, width, height);
				break;
		}
	}

	private void doGray(int[] srcPixels, int[] dstPixels, int width, int height, double parameter) throws ParseException {
		// Loop over all pixels of the destination image
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;

				int c = srcPixels[pos];
				int r = (c >> 16) & 0xFF;
				int g = (c >> 8) & 0xFF;
				int b = (c) & 0xFF;

				int lum = (int) (0.299 * r + 0.587 * g + 0.114 * b + parameter);

				// Clipping RGB values
				lum = Math.min(255, Math.max(0, lum));
				dstPixels[pos] = 0xFF000000 | (lum << 16) | (lum << 8) | lum;
			}
		}
	}

	private void doCopy(int[] srcPixels, int[] dstPixels, int width, int height) {
		// Loop over all pixels of the destination image
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				dstPixels[pos] = srcPixels[pos];
			}
		}
	}

	private void doFilter(int[] srcPixels, int[] dstPixels, int width, int height, double[][] filter) {
		// Loop over all pixels of the destination image
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				int pos = y * width + x;

				int filteredPixelGray = 0;
				int yFilterOffset = -filter.length / 2;
				int xFilterOffset = -filter[0].length / 2;

				// Loop over filter and apply convolution for filter kernel
				for(int yInFilter = 0; yInFilter < filter.length; yInFilter++) {
					for(int xInFilter = 0; xInFilter < filter[0].length; xInFilter++) {
						int yFiltered = Math.max(0, Math.min(height - 1, y + yInFilter + yFilterOffset));
						int xFiltered = Math.max(0, Math.min(width - 1, x + xInFilter + xFilterOffset));
						int posWithFilter = yFiltered * width + xFiltered;

						filteredPixelGray += (int) ((srcPixels[posWithFilter] & 0xFF) * filter[yInFilter][xInFilter]);
					}
				}

				filteredPixelGray = Math.min(255, Math.max(0, filteredPixelGray));
				dstPixels[pos] = 0xFF000000 | (filteredPixelGray << 16) | (filteredPixelGray << 8) | filteredPixelGray;
			}
		}
	}
}
