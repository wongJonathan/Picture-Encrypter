import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Class PED is the main class for the Picture Encryptor Decryptor
 *
 * @author Jonathan Wong
 */
public class PED {


    public static void main(String args[]) {

        if (args.length != 3) {
            printError("Not enough arguments");
        }

        String key = args[0];
        String command = args[1];
        String imagePath = args[2];     // for the initial path
        String[] imageFiles = {imagePath};            // for multiple files

        boolean encrypt = false;

        PEDModel model = new PEDModel();
        Encryptor encryptor = new Encryptor(key);

        if (command.contains("encrypt")) {
            encrypt = true;
        } else if (!command.contains("decrypt")) {
            printError("Must select encrypt or decrypt for command option.");
        }

        // Checks to see if a folder was given
        if (imagePath.substring(imagePath.length() - 1).equals("/")) {
            // Folder is called
            // Change the number of files to the number of files
            // Probably need an array of file names for the for loop

            File[] fileArray = new File(imagePath).listFiles();
            imageFiles = new String[fileArray.length];

            int imageFilesIndex = 0;
            // Gets all the files in the folder storing it in an array
            for (int i = 0; i < fileArray.length; i++) {
                if (!encrypt) {
                    if (fileArray[i].getName().contains("-encrypted")) {
                        imageFiles[imageFilesIndex++] = imagePath + fileArray[i].getName();
                    }
                } else {
                    if (!fileArray[i].getName().contains("-encrypted")) {
                        imageFiles[imageFilesIndex++] = imagePath + fileArray[i].getName();
                    }
                }
            }
        }

        for (int i = 0; i < imageFiles.length; i++) {
            System.out.println(imageFiles[i]);
            if (imageFiles[i] != null) {
                String imageName = getFileName(imageFiles[i], encrypt);
                if (imageFiles[i].contains(".png") || imageFiles[i].contains(".PNG")) {

                    // use png version
                    model.pngjLoadImage(imageFiles[i], imageName);
                    encryptor.encryptPng(model.getPngw(), model.getPngr());
                    model.closePng();

                } else {
                    BufferedImage loadedImage = model.loadImage(imageFiles[i]);

                    BufferedImage newImage = encryptor.encryptJPG(loadedImage);

                    model.writeImage(newImage, imageName);

                }
                System.out.println("Done writing ");

            } else {
                break;
            }
        }
    }

    /**
     * Gets the name based on the file path
     *
     * @param imagePath
     * @return
     */
    private static String getFileName(String imagePath, boolean encrypt) {
        String[] imageNameSplit = imagePath.split("\\.");
        String fileType = imageNameSplit[imageNameSplit.length - 1];

        String postFix = "-encrypted";

        if (!encrypt) {
            postFix = "-decrypted";
        }

        return imageNameSplit[0] + postFix + "." + fileType;
    }

    /**
     * Prints the usage error message
     *
     * @param errorMessage The error message
     */
    private static void printError(String errorMessage) {
        System.out.println(errorMessage);
        String usageMessage = "Usage: java PED <key> <command> <image file>\n" +
                "<key> is the key to encrypt and decrypt with\n" +
                "<command> is either encrypt or decrypt\n" +
                "<image path> is the image to either encrypt or decrypt, for folder <folder name>/";
        System.out.println(usageMessage);
        System.exit(0);
    }

}
