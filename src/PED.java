import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Class PED is the main class for the Picture Encryptor Decryptor
 *
 * @author Jonathan Wong
 */
public class PED {


    public static void main(String args[]) {

        if (args.length != 2) {
            printError("Not enough arguments");
        }

        String command = args[0];
        String[] imageFile = new String[1];
        imageFile[0] = args[1];

        PEDUI ui = new PEDUI();
        PEDModel model = new PEDModel();

        int numberOfFiles = 1;

        // Checks to see if a folder was given
        if (imageFile[0].substring(imageFile[0].length() - 1).equals("/")) {
            // Folder is called
            // Change the number of files to the number of files
            // Probably need an array of file names for the for loop

            File[] fileArray=new File(imageFile[0]).listFiles();
            imageFile = new String[fileArray.length];

            // Gets all the files in the folder storing it in an array
            for (int i = 0; i < fileArray.length; i++) {
                imageFile[i] = fileArray[i].getName();
            }
        }

        for (int i = 0 ; i < numberOfFiles; i++) {
            model.addModelListener(ui);

            if(imageFile[i].contains(".png") || imageFile[i].contains(".PNG")){
                // use png version
                model.pngjLoadImage(imageFile[i]);

            } else {
                BufferedImage loadedImage = model.loadImage(imageFile[i]);
        //        model.writeImage(loadedImage, "encrypt.jpg");

                ui.setImage(loadedImage);
                byte[] encryptedBytes = model.encryptJPG(loadedImage);
                model.writeFile(encryptedBytes, "encrypted.txt");


//                encryptedBytes = model.readFile("encrypted.txt");
                BufferedImage decrypt = model.decryptJPG(encryptedBytes);
                ui.setImage(decrypt);
                model.writeImage(decrypt, "decrypt.jpg");
            }




        }
    }

    /**
     * Prints the usage error message
     * @param errorMessage  The error message
     */
    private static void printError(String errorMessage){
        System.out.println(errorMessage);
        String usageMessage = "Usage: java PED <command> <image file>\n"+
                "<command> is either encrypt or decrypt\n" +
                "<image file> is the image to either encrypt or decrypt, for folder <folder name>/";
        System.out.println(usageMessage);
        System.exit(0);
    }

}
