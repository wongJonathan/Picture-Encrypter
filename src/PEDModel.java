import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PEDModel {

    private ModelListener listener;

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
            String[] imageNameSplit = imageName.split(".");
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
                int red = (color & 0xFF0000) >> 16;
                int green = (color & 0xFF00) >> 8;
                int blue = (color & 0xFF);
                int alpha = (color & 0xff000000) >>> 24;
                System.out.print("( "+ red +", "+ green + ", " + blue + ")\t");
            }
            System.out.println();
        }
        return newImage;
    }
}
