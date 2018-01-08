import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PEDModel {

    private ModelListener listener;

    private byte[] key = {(byte)0xac, (byte)0x09, (byte)0xa3, (byte)0x03, (byte)0xbd, (byte)0x4d, (byte)0x7b, (byte)0x18, (byte)0x85, (byte)0xb0, (byte)0x29, (byte)0xdb, (byte)0x8e, (byte)0x17, (byte)0xe7, (byte)0xd9 };
    private byte[] key4 = {(byte)0xac, (byte)0x09, (byte)0xa3, (byte)0x03};

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
        System.out.println(img);
        return img;
    }

    /**
     * Writes the new image given
     * @param image         The image to write
     * @param imageName     The name of the image with file type
     */
    public void writeImage(BufferedImage image, String imageName){
        try{
            String[] imageNameSplit = imageName.split("\\.");
            String fileType = imageNameSplit[imageNameSplit.length - 1];
            ImageIO.write(image, fileType, new File("pictures/"+imageName));
        } catch(IOException ex){
            System.err.println("Error writing image: "+imageName);
        }
    }

    public BufferedImage encrypt(BufferedImage image){
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
                System.out.println("Bytes: ( "+ (result[1]& 0xff) +", "+ (result[2] & 0xff) + ", " + (result[3] & 0xff)+ ")\t");
                // Where encryption occurs
                // @todo might want to do a block cipher to swap values of pixels
                for(int i = 0 ; i < 4; i++){
                    result[i] = (byte)(result[i] ^ key4[i]);
                }
                System.out.println("Bytes: ( "+ (result[1]& 0xff) +", "+ (result[2] & 0xff) + ", " + (result[3] & 0xff)+ ")\t");


                Color newColor = new Color((result[1]& 0xff), (result[2] & 0xff), (result[3] & 0xff), 1);

                newImage.setRGB(x,y,newColor.getRGB());
            }
            //System.out.println();
        }


        // Idea: after arranging the colors, then swap positions of the pixels
        return newImage;
    }
}
