import ar.com.hjg.pngj.ImageLineHelper;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReaderInt;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;
import ar.com.hjg.pngj.chunks.PngChunkTextVar;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PEDModel {

    private ModelListener listener;

    private byte[] key = {(byte)0xac, (byte)0x09, (byte)0xa3, (byte)0x03, (byte)0xbd, (byte)0x4d, (byte)0x7b, (byte)0x18, (byte)0x85, (byte)0xb0, (byte)0x29, (byte)0xdb, (byte)0x8e, (byte)0x17, (byte)0xe7, (byte)0xd9 };
    private byte[] key4 = {(byte)0x8e, (byte)0xd9, (byte)0x09, (byte)0xbd};

    public PEDModel(){
    }

    public void addModelListener(ModelListener listener){
        this.listener = listener;
    }

    /**
     * Loads the image based on the name
     * @param imageName     name of the image
     * @return  returns the image file
     */
    public BufferedImage loadImage(String imageName){
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File("pictures/"+imageName));


        } catch(IOException ex){
            System.err.println("Error loading image: "+imageName);
        }
        System.out.println(img.getPropertyNames());
        return img;
    }

    /**
     * Loads a png image using pngj library
     * @param imageName     The name of the image
     */
    public void pngjLoadImage(String imageName){
        PngReaderInt pngr = new PngReaderInt(new File("pictures/" + imageName));

        // 3 for RGB, 4 for RGBA
        int channels = pngr.imgInfo.channels;

        PngWriter pngw = new PngWriter(new File("pictures/encrypted-" + imageName), pngr.imgInfo, true);
// instruct the writer to grab all ancillary chunks from the original
        pngw.copyChunksFrom(pngr.getChunksList(), ChunkCopyBehaviour.COPY_ALL_SAFE);
        // add a textual chunk to writer
        pngw.getMetadata().setText(PngChunkTextVar.KEY_Description, "Decreased red and increased green");
        // also: while(pngr.hasMoreRows())
        for (int row = 0; row < pngr.imgInfo.rows; row++) {
            ImageLineInt l1 = pngr.readRowInt(); // each element is a sample

            // scanline's lenght = cols * channels, for png it's R, G, B, or A for each pixel in the row
            int[] scanline = l1.getScanline(); // to save typing
            System.out.println("Scanline: "+scanline.length + " cols: "+pngr.imgInfo.cols);
            for (int j = 0; j < pngr.imgInfo.cols; j++) {

                byte[] pixel = new byte[channels];
                for(int i = 0 ; i < channels; i++) {
                    pixel[i] = (byte)scanline[j * channels + i];
                }
                pixel = encryptPixel(pixel);
                for(int i = 0 ; i < channels; i++) {
                    scanline[j * channels + i] = (int)pixel[i];
                }

            }
            pngw.writeRow(l1);
        }
        pngr.end(); // it's recommended to end the reader first, in case there are trailing chunks to read
        pngw.end();
        System.out.println("Done writing ");
    }

    /**
     * Writes the new image given
     * @param image         The image to write
     * @param imageName     The name of the image with file type
     */
    public void writeImage(BufferedImage image, String imageName){
        try{
            System.out.println("Image type: "+image.getType());

            String[] imageNameSplit = imageName.split("\\.");
            String fileType = imageNameSplit[imageNameSplit.length - 1];
            ImageIO.write(image, fileType, new File("pictures/"+imageName));
        } catch(IOException ex){
            System.err.println("Error writing image: "+imageName);
        }
        System.out.println("Done writing");
    }


    /**
     * Encrypts jpg iamges
     * @param image
     * @return
     */
    public BufferedImage encryptJPG(BufferedImage image){
        BufferedImage newImage = image;


        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = newImage.getRGB(x,y);

                // Gets the values of each color and alpha
//                int alpha = (color & 0xff000000) >>> 24;
//                int red = (color & 0xFF0000) >> 16;
//                int green = (color & 0xFF00) >> 8;
//                int blue = (color & 0xFF);
//                System.out.println("INTS: ( "+ red +", "+ green + ", " + blue + ")\t");

                // Creates the byte array
                byte[] result = new byte[4];

                result[0] = (byte) (color >> 24);
                result[1] = (byte) (color >> 16);
                result[2] = (byte) (color >> 8);
                result[3] = (byte) (color /*>> 0*/);
//                System.out.println("Bytes: ( "+ (result[1]& 0xff) +", "+ (result[2] & 0xff) + ", " + (result[3] & 0xff)+ ", " + (result[0] & 0xff) + ")\t");
                // Where encryption occurs
                // @todo might want to do a block cipher to swap values of pixels
                // Ignores the first byte which is the alpha value
                result = encryptPixel(result);
//                System.out.println("Bytes: ( "+ (result[1]& 0xff) +", "+ (result[2] & 0xff) + ", " + (result[3] & 0xff)+ ", " + (result[0] & 0xff) + ")\t");


                Color newColor = new Color((result[1]& 0xff), (result[2] & 0xff), (result[3] & 0xff), (result[0] & 0xff));

                newImage.setRGB(x,y,newColor.getRGB());
            }
            //System.out.println();
        }

        System.out.println("Done");
        // Idea: after arranging the colors, then swap positions of the pixels
        return newImage;
    }


    /**
     * Encrypts the pixel using a stream cipher
     * @param pixelData     The RGB(A) values of a pixel
     * @return              The cipher version of the pixel
     */
    private byte[] encryptPixel(byte[] pixelData){
        byte[] newPixel = new byte[pixelData.length];
        for(int i = 0 ; i < pixelData.length; i++){
            int keyIndex = i % key4.length;

            newPixel[i] = (byte)(pixelData[i] ^ key4[keyIndex]);
        }

        return newPixel;
    }
}
