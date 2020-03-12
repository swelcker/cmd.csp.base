package cmd.csp.utils.pdf;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.tiff.BaselineTIFFTagSet;
import javax.imageio.plugins.tiff.TIFFDirectory;
import javax.imageio.plugins.tiff.TIFFField;
import javax.imageio.plugins.tiff.TIFFTag;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import com.recognition.software.jdeskew.ImageDeskew;

import cmd.csp.platform.CSPLogDelegate;




public class PDFUtils {
	private static int weight = 2;
	protected final CSPLogDelegate LOGGER = new CSPLogDelegate(CSPLogDelegate.class.getName());;

	public static String getPDFRawText(String strFilePath) throws IOException {
		File pf = new File(strFilePath);
		String content="";
		PDDocument doc=null;
		PDFTextStripper stripper= null;
		if(pf!=null) {
			if(pf.exists()) {
		    	doc = PDDocument.load(pf);
		    	if(doc!=null) {
		    		if(!doc.isEncrypted()) {
			    		stripper=new PDFTextStripper();
			    		if(stripper!=null) {
			    			stripper.setSortByPosition(true);

			    			content=  stripper.getText(doc);
			    		}
		    		}
		    	}
		    	
		    	doc.close();
		    	
			}			
		}
		if(content.length()<=20) content="";
	    return content;
	}
	public static Boolean hasPDFText(String strFilePath, Integer lenTreshhold)  {
		String content="";
		Boolean bln=false;
		if(lenTreshhold==null)lenTreshhold=100;
		if(lenTreshhold<1)lenTreshhold=100;
		try {
			content = getPDFRawText(strFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(content.length()>lenTreshhold) bln=true;
	    return bln;
	}
	public static String getISOLanguage(String language) {
		return 	LocaleUtils.toLocale(language).getISO3Language();
	}
	public static BufferedImage do_ImagePreProcessing(BufferedImage imgInput) {
		BufferedImage image2 = new BufferedImage(imgInput.getWidth(),imgInput.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D ig2 = image2.createGraphics();

		ig2.setBackground(Color.WHITE);
		ig2.clearRect(0, 0, imgInput.getWidth(),imgInput.getHeight());
		double imageSkewAngle=0.;		
		image2=imgInput;
		if(imgInput!=null) {
	    	
	    	//System.out.println(new Date().toString()+"do_ImagePreProcessing: START");
			ImageDeskew id = new ImageDeskew(image2);
			imageSkewAngle = id.getSkewAngle(); // determine skew angle
	    	//System.out.println(new Date().toString()+"do_ImagePreProcessing: ImageDeskew:"+imageSkewAngle);
			if(imageSkewAngle!=0.) {

				image2 = do_rotate2(image2, Math.toRadians(imageSkewAngle*-1));
			}
	    	//System.out.println(new Date().toString()+"do_ImagePreProcessing: Denoise");
			//do_denoise(imgInput);
	    	//System.out.println(new Date().toString()+"do_ImagePreProcessing: Sharpen");
			//do_sharpen(imgInput);
	    	//System.out.println(new Date().toString()+"do_ImagePreProcessing: Binarize");
			//image2= do_binarize(imgInput);
	    	//System.out.println(new Date().toString()+"do_ImagePreProcessing: FINISHED");
		
//	    	imgInput=image2; //resize(image2,imgInput.getWidth(),imgInput.getHeight());
			//do_sharpen(imgInput);
		
	    }
        return image2;
	}
	public static BufferedImage do_SubImagePreProcessing(BufferedImage imgInput) {
		BufferedImage image2 = new BufferedImage(imgInput.getWidth(),imgInput.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D ig2 = image2.createGraphics();

		ig2.setBackground(Color.WHITE);
		ig2.clearRect(0, 0, imgInput.getWidth(),imgInput.getHeight());
		double imageSkewAngle=0.;		
		image2=imgInput;
		if(imgInput!=null) {
	    	
	    	//System.out.println(new Date().toString()+"do_ImagePreProcessing: START");
			ImageDeskew id = new ImageDeskew(image2);
			imageSkewAngle = id.getSkewAngle(); // determine skew angle
	    	//System.out.println(new Date().toString()+"do_ImagePreProcessing: ImageDeskew:"+imageSkewAngle);
			if(imageSkewAngle!=0.) {

				image2 = do_rotate2(image2, Math.toRadians(imageSkewAngle*-1));
			}
	    	//System.out.println(new Date().toString()+"do_ImagePreProcessing: Denoise");
			//image2 = do_denoise(image2);
	    	//System.out.println(new Date().toString()+"do_ImagePreProcessing: Sharpen");
			//image2 = do_sharpen(image2);
	    	//System.out.println(new Date().toString()+"do_ImagePreProcessing: Binarize");
			//image2= do_binarize(imgInput);
	    	//System.out.println(new Date().toString()+"do_ImagePreProcessing: FINISHED");
		
//	    	imgInput=image2; //resize(image2,imgInput.getWidth(),imgInput.getHeight());
			//do_sharpen(imgInput);
		
	    }
        return image2;
	}
	public static BufferedImage do_Crop(BufferedImage proxyimage, int x, int y, int width, int height, Boolean rgb) {
		// TODO crop the picture
		//System.out.println("do_Crop start");
		if(x<=0||y<=0||width<=0||height<=0||x>=width||y>=height) return proxyimage;
		// the copied and buffered images
			BufferedImage cimage = null;



		// setup the copy
		if (rgb == true) {
			//System.out.println("do_Crop start RGB");
			// create rgb copy
			cimage = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
		} else {
			// create greyscale copy
			//System.out.println("do_Crop start GRAY");
			cimage = new BufferedImage(width, height,
					BufferedImage.TYPE_BYTE_GRAY);
		}
	
		//System.out.println("do_Crop getgraphics");
	
	    Graphics g = cimage.getGraphics();
		//System.out.println("do_Crop draw image");
	    g.drawImage(proxyimage, 0, 0, width, height, x, y, x + width, y + height, null);
		//System.out.println("do_Crop dispose");
	    g.dispose();
		//System.out.println("do_Crop finished");

	    return cimage;

	}
    public static void writeImage(String strFilePath, BufferedImage bi, String fFormat) throws IOException {
/*        File file = new File(strFilePath);
        ImageIO.write(bi, fFormat, file);*/

    	
    	@SuppressWarnings("unused")
		Boolean bln = false;
    	bln=ImageIOUtil.writeImage(bi, strFilePath, 300);
    	//System.out.println("WriteImage " + bln +" "+strFilePath);
    }
    @SuppressWarnings("unused")
	private static  BufferedImage do_rotate(BufferedImage bufferedImage, Double theta) {
       

            AffineTransform tx = new AffineTransform();
            tx.rotate(theta, bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2);

            AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_BILINEAR);
            return op.filter(bufferedImage, null);
    }
	private static BufferedImage do_rotate2(BufferedImage oldbi, Double theta ){
		
		//create the rotational matrix (for some reason [y][x] felt more right as in move up a level or iterate further)
		double[][] newpos=new double[2][2];
		newpos[0][0]=Math.cos(theta);
		newpos[0][1]=(Math.sin(theta)*-1);
		newpos[1][0]=Math.sin(theta);
		newpos[1][1]=Math.cos(theta);
		
		//set a new 'Canvas'

			BufferedImage bi=new BufferedImage(oldbi.getWidth(),oldbi.getHeight(),BufferedImage.TYPE_3BYTE_BGR);

			Graphics2D ig2 = bi.createGraphics();

			ig2.setBackground(Color.WHITE);
			ig2.clearRect(0, 0, bi.getWidth(),bi.getHeight());
		
		int newx=-1;
		int newy=-1;
		

		Color rgb;
		
		//perform the rotate multiplication by figuring out the new x,y coordinates for each point
		for(int w=0;w<oldbi.getWidth();w++){
			for(int h=0;h<oldbi.getHeight();h++){
				rgb=new Color(oldbi.getRGB(w, h));
				newx=(int)Math.ceil(w*newpos[0][0]+h*newpos[0][1]);
				newy=(int)Math.ceil(w*newpos[1][0]+h*newpos[1][1]);
			
				//set the coordinates of our new image canvas
				if(newx>=0 && newx< bi.getWidth() && newy>=0 && newy<bi.getHeight()){
					bi.setRGB(newx, newy, rgb.getRGB());
				}
				
				//reset everything for gc and insurance
				rgb=null;
				newx=-1;
				newy=-1;
			}
		}
		oldbi=bi;
		return oldbi;
	}
    // Return histogram of grayscale image
    public static int[] imageHistogram(BufferedImage input) {
 
        int[] histogram = new int[256];
 
        for(int i=0; i<histogram.length; i++) histogram[i] = 0;
 
        for(int i=0; i<input.getWidth(); i++) {
            for(int j=0; j<input.getHeight(); j++) {
                int red = new Color(input.getRGB (i, j)).getRed();
                histogram[red]++;
            }
        }
 
        return histogram;
 
    }
 
    // The luminance method
    @SuppressWarnings("unused")
	private static BufferedImage toGray(BufferedImage original) {
 
        int alpha, red, green, blue;
        int newPixel;
 
        BufferedImage lum = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
 
        for(int i=0; i<original.getWidth(); i++) {
            for(int j=0; j<original.getHeight(); j++) {
 
                // Get pixels by R, G, B
                alpha = new Color(original.getRGB(i, j)).getAlpha();
                red = new Color(original.getRGB(i, j)).getRed();
                green = new Color(original.getRGB(i, j)).getGreen();
                blue = new Color(original.getRGB(i, j)).getBlue();
 
                red = (int) (0.21 * red + 0.71 * green + 0.07 * blue);
                // Return back to original format
                newPixel = colorToRGB(alpha, red, red, red);
 
                // Write pixels into image
                lum.setRGB(i, j, newPixel);
 
            }
        }
 
        return lum;
 
    }
 
    // Get binary treshold using Otsu's method
    private static int otsuTreshold(BufferedImage original) {
 
        int[] histogram = imageHistogram(original);
        int total = original.getHeight() * original.getWidth();
 
        float sum = 0;
        for(int i=0; i<256; i++) sum += i * histogram[i];
 
        float sumB = 0;
        int wB = 0;
        int wF = 0;
 
        float varMax = 0;
        int threshold = 0;
 
        for(int i=0 ; i<256 ; i++) {
            wB += histogram[i];
            if(wB == 0) continue;
            wF = total - wB;
 
            if(wF == 0) break;
 
            sumB += (float) (i * histogram[i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;
 
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);
 
            if(varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }
 
        return threshold;
 
    }
 
    @SuppressWarnings("unused")
	private static BufferedImage do_binarize(BufferedImage original) {
 
        int red;
        int newPixel;
 
        int threshold = otsuTreshold(original);
 
        BufferedImage binarized = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
 
        for(int i=0; i<original.getWidth(); i++) {
            for(int j=0; j<original.getHeight(); j++) {
 
                // Get pixels
                red = new Color(original.getRGB(i, j)).getRed();
                int alpha = new Color(original.getRGB(i, j)).getAlpha();
                if(red > threshold) {
                    newPixel = 255;
                }
                else {
                    newPixel = 0;
                }
                newPixel = colorToRGB(alpha, newPixel, newPixel, newPixel);
                binarized.setRGB(i, j, newPixel); 
 
            }
        }
 
        return binarized;
 
    }
 
    // Convert R, G, B, Alpha to standard 8 bit
    private static int colorToRGB(int alpha, int red, int green, int blue) {
 
        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red; newPixel = newPixel << 8;
        newPixel += green; newPixel = newPixel << 8;
        newPixel += blue;
 
        return newPixel;
 
    }
	@SuppressWarnings("unused")
	private static BufferedImage do_sharpen(BufferedImage proxyimage) {
		// TODO sharpen using a convulution kernel
		/*
		 * Kernel is |-1|-1|-1| |-1|weight|-1||-1|-1|-1|
		 */

		
		// a secondary image for storing new outcomes
		BufferedImage image2 = new BufferedImage(proxyimage.getWidth(),proxyimage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

		// image width and height
		int width = proxyimage.getWidth();
		int height = proxyimage.getHeight();
		int r = 0;
		int g = 0;
		int b = 0;
		Color c = null;
		for (int x = 1; x < width - 1; x++) {
			for (int y = 1; y < height - 1; y++) {
				// sharpen the image using the kernel (center is c5)
				Color c00 = new Color(proxyimage.getRGB(x - 1, y - 1));
				Color c01 = new Color(proxyimage.getRGB(x - 1, y));
				Color c02 = new Color(proxyimage.getRGB(x - 1, y + 1));
				Color c10 = new Color(proxyimage.getRGB(x, y - 1));
				Color c11 = new Color(proxyimage.getRGB(x, y));
				Color c12 = new Color(proxyimage.getRGB(x, y + 1));
				Color c20 = new Color(proxyimage.getRGB(x + 1, y - 1));
				Color c21 = new Color(proxyimage.getRGB(x + 1, y));
				Color c22 = new Color(proxyimage.getRGB(x + 1, y + 1));

				// apply the kernel for r
				r = -c00.getRed() - c01.getRed() - c02.getRed() - c10.getRed()
						+ (weight * c11.getRed()) - c12.getRed() - c20.getRed()
						- c21.getRed() - c22.getRed();

				// apply the kernel for g
				g = c00.getGreen() - c01.getGreen() - c02.getGreen()
						- c10.getGreen() + (weight * c11.getGreen())
						- c12.getGreen() - c20.getGreen() - c21.getGreen()
						- c22.getGreen();

				// apply the transformation for b
				b = c00.getBlue() - c01.getBlue() - c02.getBlue()
						- c10.getBlue() + (weight * c11.getBlue())
						- c12.getBlue() - c20.getBlue() - c21.getBlue()
						- c22.getBlue();

				// set the new rgb values
				r = Math.min(255, Math.max(0, r));
				g = Math.min(255, Math.max(0, g));
				b = Math.min(255, Math.max(0, b));

				c = new Color(r, g, b);

				// set the new mask colors in the new image
				image2.setRGB(x, y, c.getRGB());

			}

		}

		// add the new values back to the original image
		Color cmask = null;
		Color corig = null;
		for (int x = 1; x < width - 1; x++) {
			for (int y = 1; y < height - 1; y++) {
				// get the 2 colors
				cmask = new Color(image2.getRGB(x, y));
				corig = new Color(proxyimage.getRGB(x, y));

				// add the new values
				r = cmask.getRed() + corig.getRed();
				g = cmask.getGreen() + corig.getGreen();
				b = cmask.getBlue() + corig.getBlue();

				// set the new rgb values
				r = Math.min(255, Math.max(0, r));
				g = Math.min(255, Math.max(0, g));
				b = Math.min(255, Math.max(0, b));

				proxyimage.setRGB(x, y, new Color(r, g, b).getRGB());
			}
		}
		return proxyimage;
	}

	/**
	 * Average out the noise from the denoise filter
	 * Take the new buffered image and take averages where the white spots
	 * are since these are where the noise is noise is denoted by a pixel whose 
	 * laplace position is white insead of black/grey
	 * 
	 * @param img2
	 */
	private static  void denoise_compare_from_fft(BufferedImage proxyimage, BufferedImage img2) {
		// TODO take the laplace bitmap and compare the FFT
		
		
		
		int ri = 0;
		int gi = 0;
		int bi = 0;

		for (int x = 0; x < proxyimage.getWidth(); x++) {
			for (int y = 0; y < proxyimage.getHeight(); y++) {
				// the values from the laplace map
				ri = new Color(img2.getRGB(x, y)).getRed();
				gi = new Color(img2.getRGB(x, y)).getGreen();
				bi = new Color(img2.getRGB(x, y)).getBlue();

				// if laplace map is white, this is noise and average are
				// taken
				if (ri == 255 && gi == 255 && bi == 255) {
					// get the neighbor pixels for the transform
					Color c00 = new Color(proxyimage.getRGB(x - 1, y - 1));
					Color c01 = new Color(proxyimage.getRGB(x - 1, y));
					Color c02 = new Color(proxyimage.getRGB(x - 1, y + 1));
					Color c10 = new Color(proxyimage.getRGB(x, y - 1));
					Color c11 = new Color(proxyimage.getRGB(x, y));
					Color c12 = new Color(proxyimage.getRGB(x, y + 1));
					Color c20 = new Color(proxyimage.getRGB(x + 1, y - 1));
					Color c21 = new Color(proxyimage.getRGB(x + 1, y));
					Color c22 = new Color(proxyimage.getRGB(x + 1, y + 1));

					// apply the kernel for r
					int r = c00.getRed() / 9 + c01.getRed() / 9 + c02.getRed()
							/ 9 + c10.getRed() / 9 + c11.getRed() / 9
							+ c12.getRed() / 9 + c20.getRed() / 9
							+ c21.getRed() / 9 + c22.getRed() / 9;

					// apply the kernel for g
					int g = c00.getGreen() / 9 + c01.getGreen() / 9
							+ c02.getGreen() / 9 + c10.getGreen() / 9
							+ c11.getGreen() / 9 + c12.getGreen() / 9
							+ c20.getGreen() / 9 + c21.getGreen() / 9
							+ c22.getGreen() / 9;

					// apply the transformation for b
					int b = c00.getBlue() / 9 + c01.getBlue() / 9
							+ c02.getBlue() + c10.getBlue() / 9 + c11.getBlue()
							/ 9 + c12.getBlue() / 9 + c20.getBlue() / 9
							+ c21.getBlue() / 9 + c22.getBlue() / 9;

					// set the new rgb values
					r = Math.min(255, Math.max(0, r));
					g = Math.min(255, Math.max(0, g));
					b = Math.min(255, Math.max(0, b));

					Color c = new Color(r, g, b);

					proxyimage.setRGB(x, y, c.getRGB());
				}
			}
		}

	}

	/**
	 * Perform a denoise
	 */
	public static  BufferedImage do_denoise(BufferedImage proxyimage) {
		// TODO performs the denoise in laplace *default

		
		// the new buffered image for the denoising algorithm
		BufferedImage image2 = new BufferedImage(proxyimage.getWidth(),proxyimage.getHeight(), BufferedImage.TYPE_INT_RGB);

		// gives ultimate control can also use image libraries
		// the current position properties
		int x = 0;
		int y = 0;

		// the image width and height properties
		int width = proxyimage.getWidth();
		int height = proxyimage.getHeight();

		/*
		 * Denoise Using Rewritten Code found at
		 * http://introcs.cs.princeton.edu/
		 * java/31datatype/LaplaceFilter.java.html
		 * 
		 * Using laplace is better than averaging the neighbors from each part
		 * of an image as it does a better job of getting rid of gaussian noise
		 * without overdoing it
		 * 
		 * Applies a default filter:
		 * 
		 * -1|-1|-1 -1|8|-1 -1|-1|-1
		 */

		// perform the laplace for each number
		for (y = 1; y < height - 1; y++) {
			for (x = 1; x < width - 1; x++) {

				// get the neighbor pixels for the transform
				Color c00 = new Color(proxyimage.getRGB(x - 1, y - 1));
				Color c01 = new Color(proxyimage.getRGB(x - 1, y));
				Color c02 = new Color(proxyimage.getRGB(x - 1, y + 1));
				Color c10 = new Color(proxyimage.getRGB(x, y - 1));
				Color c11 = new Color(proxyimage.getRGB(x, y));
				Color c12 = new Color(proxyimage.getRGB(x, y + 1));
				Color c20 = new Color(proxyimage.getRGB(x + 1, y - 1));
				Color c21 = new Color(proxyimage.getRGB(x + 1, y));
				Color c22 = new Color(proxyimage.getRGB(x + 1, y + 1));

				/* apply the matrix */
				// to check, try using gauss jordan

				// apply the transformation for r
				int r = -c00.getRed() - c01.getRed() - c02.getRed()
						+ -c10.getRed() + 8 * c11.getRed() - c12.getRed()
						+ -c20.getRed() - c21.getRed() - c22.getRed();

				// apply the transformation for g
				int g = -c00.getGreen() - c01.getGreen() - c02.getGreen()
						+ -c10.getGreen() + 8 * c11.getGreen() - c12.getGreen()
						+ -c20.getGreen() - c21.getGreen() - c22.getGreen();

				// apply the transformation for b
				int b = -c00.getBlue() - c01.getBlue() - c02.getBlue()
						+ -c10.getBlue() + 8 * c11.getBlue() - c12.getBlue()
						+ -c20.getBlue() - c21.getBlue() - c22.getBlue();

				// set the new rgb values
				r = Math.min(255, Math.max(0, r));
				g = Math.min(255, Math.max(0, g));
				b = Math.min(255, Math.max(0, b));
				Color c = new Color(r, g, b);

				image2.setRGB(x, y, c.getRGB());
			}
		}

		// compare the original image and the image where noise was found and
		// average where noise was found
		denoise_compare_from_fft(proxyimage, image2);
		return proxyimage;

	}
	
    public static BufferedImage resize(BufferedImage source, int destWidth, int destHeight) {
        if (source == null)
            throw new NullPointerException("source image is NULL!");
        if (destWidth <= 0 && destHeight <= 0)
            throw new IllegalArgumentException("destination width & height are both <=0!");
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        double xScale = ((double) destWidth) / (double) sourceWidth;
        double yScale = ((double) destHeight) / (double) sourceHeight;
        if (destWidth <= 0) {
            xScale = yScale;
            destWidth = (int) Math.rint(xScale * sourceWidth);
        }
        if (destHeight <= 0) {
            yScale = xScale;
            destHeight = (int) Math.rint(yScale * sourceHeight);
        }
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(destWidth, destHeight, source.getColorModel().getTransparency());
        Graphics2D g2d = null;
        try {
            g2d = result.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);


            AffineTransform at =
                    AffineTransform.getScaleInstance(xScale, yScale);
            g2d.drawRenderedImage(source, at);
        } finally {
            if (g2d != null)
                g2d.dispose();
        }
        return result;
    }
    public static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }

    public static IIOMetadata createMetadata(ImageWriter writer, ImageWriteParam writerParams, int resolution) throws IIOInvalidTreeException {
    	// Get default metadata from writer
    	ImageTypeSpecifier type = writerParams.getDestinationType();
    	IIOMetadata meta = writer.getDefaultImageMetadata(type, writerParams);
     
    	// Convert default metadata to TIFF metadata
    	TIFFDirectory dir = TIFFDirectory.createFromMetadata(meta);
     
    	// Get {X,Y} resolution tags
    	BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
    	TIFFTag tagXRes = base.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION);
    	TIFFTag tagYRes = base.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION);
     
    	// Create {X,Y} resolution fields
    	TIFFField fieldXRes = new TIFFField(tagXRes, TIFFTag.TIFF_RATIONAL, 1, new long[][] { { resolution, 1 } });
    	TIFFField fieldYRes = new TIFFField(tagYRes, TIFFTag.TIFF_RATIONAL, 1, new long[][] { { resolution, 1 } });
     
    	// Add {X,Y} resolution fields to TIFFDirectory
    	dir.addTIFFField(fieldXRes);
    	dir.addTIFFField(fieldYRes);
     
    	// Return TIFF metadata so it can be picked up by the IIOImage
    	return dir.getAsMetadata();
    }


}
