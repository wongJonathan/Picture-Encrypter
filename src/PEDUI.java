import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Class for the Picture Encryptor Decryptor user interface
 */
public class PEDUI implements ModelListener{

    private JFrame frame;
    private JTextField messageField;
    private ImageDisplayer imageDisplayer;

    /**
     * Sets up the display panel
     */
    public PEDUI(){

        frame = new JFrame("Encryptor and Decryptor");
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout (panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        imageDisplayer = new ImageDisplayer();
        frame.add(imageDisplayer);
        frame.pack();
        frame.setVisible(true);

    }

    /**
     * Sets the image to be displayed
     * @param image     The image to be displayed
     */
    public void setImage(BufferedImage image){
        imageDisplayer.updateImage(image);
        imageDisplayer.repaint();
    }
    /**
     * For testing the ui
     * @param args
     */
    public static void main(String args[]){
        PEDUI ui = new PEDUI();
    }
}
