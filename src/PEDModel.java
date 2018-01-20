import ar.com.hjg.pngj.ImageLineHelper;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReaderInt;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;
import ar.com.hjg.pngj.chunks.PngChunkTextVar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PEDModel {

    private final String algo = "AES/CBC/NoPadding";

    private ModelListener listener;

    private byte[] key16 = {(byte)0xac, (byte)0x09, (byte)0xa3, (byte)0x03, (byte)0xbd, (byte)0x4d, (byte)0x7b, (byte)0x18, (byte)0x85, (byte)0xb0, (byte)0x29, (byte)0xdb, (byte)0x8e, (byte)0x17, (byte)0xe7, (byte)0xd9 };
    private byte[] key4 = new byte[]{'t','e','s','t'};

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
     * Writes the encrypted file
     * @param encryptedBytes
     * @param filename
     */
    public void writeFile(byte[] encryptedBytes, String filename ){
        try {
            FileOutputStream fos = new FileOutputStream("pictures/encrypted.txt");
            fos.write(encryptedBytes);
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public byte[] readFile(String fileName) {
        try {
            Path file = Paths.get("pictures/" + fileName);
            return Files.readAllBytes(file);
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Encrypts jpg iamges
     * @param image
     * @return
     */
    public byte[] encryptJPG(BufferedImage image){

        int[] colors = new int[image.getWidth() * image.getHeight()];
        int colorsIndex = 0;
        //@todo try getting all of the image into one byte array
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = image.getRGB(x,y);
                System.out.println("Color: "+color);

                colorToBytes(color);

                colors[colorsIndex++] = color;
            }
        }

        // Idea: after arranging the colors, then swap positions of the pixels
        return testEncrypt(colors);
    }

    /**
     * Decrypts jpg iamges
     * @param
     * @return
     */
    public BufferedImage decryptJPG(byte[] imageFile){

        BufferedImage decryptedImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        byte[] decryptedByte = imageFile;
//        for(int i = 0; i < decryptedByte.length; i++) {
//            System.out.println("byte: "+decryptedByte[i]);
//
//        }
        int byteIndex = 0;
        System.out.println("Decrypting");

//        for (int y = image.getHeight() - 1; y >= 0 ; y--) {
//            for (int x = image.getWidth() - 1; x >= 0 ; x--) {
        for (int y = 0; y < decryptedImage.getHeight(); y++) {

            for (int x = 0; x < decryptedImage.getWidth(); x++) {

                int alpha = decryptedByte[byteIndex++] & 0xFF;
                int red = decryptedByte[byteIndex++] & 0xFF;
                int green = decryptedByte[byteIndex++] & 0xFF;
                int blue = decryptedByte[byteIndex++] & 0xFF;
                System.out.println("Alpha: "+alpha + " r: "+red+" g: "+green+" b: "+blue);
                Color newColor = new Color(red, green, blue, alpha);
                colorToBytes(newColor.getRGB());
                decryptedImage.setRGB(x,y, newColor.getRGB());
            }
        }

        System.out.println("Done Decrypting");
        // Idea: after arranging the colors, then swap positions of the pixels
        return decryptedImage;
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





    /**
     * Creates the byte array
     * @param color     The color to be converted
     * @return  the byte array
     */
    private byte[] colorToBytes(int color) {
        byte[] result = new byte[4];
        result[0] = (byte) (color >> 24);
        result[1] = (byte) (color >> 16);
        result[2] = (byte) (color >> 8);
        result[3] = (byte) (color /*>> 0*/);
        System.out.println("Alpha: "+(result[0] & 0xff) + " r: "+(result[1] & 0xff)+" g: "+(result[2] & 0xff)+" b: "+(result[3] & 0xff));
        return result;
    }

    /**
     * Testing java's encryptions by encrypting each row of pictures
     * @param imageFile
     * @return
     */
    private byte[] testEncrypt(int[] imageFile){
        // Converts the colors to a row of byte arrays
        ByteBuffer byteBuffer = ByteBuffer.allocate(imageFile.length * 4);

        // I'ts getting 0's here after a certain amount
        for (int i = 0; i < imageFile.length; i++){
            byteBuffer.putInt(imageFile[i]);
        }

        byte[] array = byteBuffer.array();


//
//        try {
//
//            Key key = new SecretKeySpec(key16, "AES");
//            Cipher c = Cipher.getInstance(algo);
//            c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
//
//
//            // need to see if splitting int is okay
//            byte[] encryptedBytes = c.doFinal(array);
//            System.out.println("encryptedBytes length: "+encryptedBytes.length);
//
//            return encryptedBytes;
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        }
        return array;
    }

    private byte[] testDecrypt(byte[] imageFile) {

        try {
            Key key = new SecretKeySpec(key16, "AES");
            Cipher c = Cipher.getInstance(algo);
// decryption replaces with 0s
            c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));

            // need to see if splitting int is okay
            byte[] encryptedBytes = c.doFinal(imageFile);
            System.out.println("encryptedBytes length: "+encryptedBytes.length);

            return encryptedBytes;

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
