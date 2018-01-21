import ar.com.hjg.pngj.PngReaderInt;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PEDModel {


    // For loading and writing png files
    private PngReaderInt pngr;
    private PngWriter pngw;

    public PEDModel(){
    }

    /**
     * Loads the image based on the name
     * @param imageName     name of the image
     * @return  returns the image file
     */
    public BufferedImage loadImage(String imageName){
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File(imageName));

        } catch(IOException ex){
            System.err.println(ex);
            System.err.println("Error loading image: "+imageName);
        }
        return img;
    }

    /**
     * Loads image into pngr and pngw and sets it up to be written on
     * @param imagePath     The path to the image
     * @param imageName     The name of the image
     */
    public void pngjLoadImage(String imagePath, String imageName){
        pngr = new PngReaderInt(new File(imagePath));

        if (pngr != null) {


            // Sets up the writer
            pngw = new PngWriter(new File(imageName), pngr.imgInfo, true);
            // instruct the writer to grab all ancillary chunks from the original
            pngw.copyChunksFrom(pngr.getChunksList(), ChunkCopyBehaviour.COPY_ALL_SAFE);

        } else {
            System.err.println("Could not find file");
        }

    }

    /**
     * Cleans up the png reader and writer
     */
    public void closePng(){
        pngr.end();
        pngw.end();
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
            ImageIO.write(image, fileType, new File(imageName));

        } catch(IOException ex){
            System.err.println("Error writing image: "+imageName);
        }
    }


    public PngReaderInt getPngr() {
        return pngr;
    }

    public PngWriter getPngw() {
        return pngw;
    }
}
