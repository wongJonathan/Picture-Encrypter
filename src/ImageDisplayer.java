import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageDisplayer extends Component {

    private BufferedImage image;

    public ImageDisplayer(){
    }

    public void paint(Graphics g){
        g.drawImage(image, 0, 0, null);
    }

    public Dimension getPreferredSize() {
        if (image == null) {
            return new Dimension(400,400);
        } else {
            return new Dimension(image.getWidth(null), image.getHeight(null));
        }
    }

    public void updateImage(BufferedImage image){
        this.image = image;
    }
}
