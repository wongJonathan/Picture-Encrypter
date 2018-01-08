import java.awt.image.BufferedImage;

/**
 * Class PED is the main class for the Picture Encryptor Decryptor
 *
 * @author Jonathan Wong
 */
public class PED {


    public static void main(String args[]) {
        PEDUI ui = new PEDUI();
        PEDModel model = new PEDModel();

        model.addModelListener(ui);
        BufferedImage loadedImage = model.loadImage("mePic.jpg");
//        model.writeImage(loadedImage, "encrypt.jpg");
//        model.pngrLoadImage("Jeremy16.png");
        ui.setImage(loadedImage);
        BufferedImage newImage = model.encrypt(loadedImage);
//        ui.setImage(newImage);
        model.writeImage(newImage,"encrypt.jpg");
//
        BufferedImage decrypt = model.encrypt(newImage);
        ui.setImage(decrypt);
        model.writeImage(decrypt, "decrypt.jpg");

    }

}
