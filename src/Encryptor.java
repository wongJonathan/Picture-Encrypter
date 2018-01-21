import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReaderInt;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;
import ar.com.hjg.pngj.chunks.PngChunkTextVar;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Handles the encryption and decryption
 */
public class Encryptor {

    private byte[] key;

    public Encryptor(String keyString) {
        key = keyString.getBytes();
    }

    /**
     * Encrypts png image using the pngj library
     * @param pngw      The writer for png image
     * @param image     The reader for the png image
     */
    public void encryptPng(PngWriter pngw, PngReaderInt image){

        // 3 for RGB, 4 for RGBA
        int channels = image.imgInfo.channels;

        for (int row = 0; row < image.imgInfo.rows; row++) {
            ImageLineInt l1 = image.readRowInt(); // each element is a sample

            // scanline's lenght = cols * channels, for png it's R, G, B, or A for each pixel in the row
            int[] scanline = l1.getScanline(); // to save typing
            for (int j = 0; j < image.imgInfo.cols; j++) {

                // Gets the color values of the pixel
                byte[] pixel = new byte[channels];
                for(int i = 0 ; i < channels; i++) {
                    pixel[i] = (byte)scanline[j * channels + i];
                }
                pixel = encryptPixel(pixel);

                // Replaces the value with the new value
                for(int i = 0 ; i < channels; i++) {
                    scanline[j * channels + i] = (int)pixel[i];
                }

            }
            pngw.writeRow(l1);
        }
    }

    /**
     * Encrypts jpg image
     * @param image
     * @return
     */
    public BufferedImage encryptJPG(BufferedImage image) {
        BufferedImage newImage = image;


        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = newImage.getRGB(x, y);

                // Creates the byte array
                byte[] result = new byte[4];

                result[0] = (byte) (color >> 24);
                result[1] = (byte) (color >> 16);
                result[2] = (byte) (color >> 8);
                result[3] = (byte) (color /*>> 0*/);

                result = encryptPixel(result);

                // Converts the encrypted results to a new color
                Color newColor = new Color((result[1] & 0xff), (result[2] & 0xff), (result[3] & 0xff), (result[0] & 0xff));

                newImage.setRGB(x, y, newColor.getRGB());
            }
        }
System.out.println("Encrypte");
        return newImage;
    }


    /**
     * Encrypts the pixel using a stream cipher
     *
     * @param pixelData The RGB(A) values of a pixel
     * @return The cipher version of the pixel
     */
    private byte[] encryptPixel(byte[] pixelData) {
        byte[] newPixel = new byte[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            int keyIndex = i % key.length;

            newPixel[i] = (byte) (pixelData[i] ^ key[keyIndex]);
        }

        return newPixel;
    }

    public byte[] getKey() {
        return key;
    }
}
