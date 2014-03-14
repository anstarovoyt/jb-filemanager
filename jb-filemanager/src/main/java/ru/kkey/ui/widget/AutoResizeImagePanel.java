package ru.kkey.ui.widget;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * The panel render image with scaling over window size
 *
 * @author astarovoyt
 *
 */
public class AutoResizeImagePanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage)img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    private final BufferedImage image;

    public AutoResizeImagePanel(Image image)
    {
        this.image = toBufferedImage(image);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        int w = getWidth();
        int h = getHeight();
        int iw = image.getWidth();
        int ih = image.getHeight();
        double xScale = (double)w / iw;
        double yScale = (double)h / ih;
        double scale = Math.min(xScale, yScale);
        int width = scale < 1 ? (int)(scale * iw) : iw;
        int height = scale < 1 ? (int)(scale * ih) : ih;
        int x = (w - width) / 2;
        int y = (h - height) / 2;
        g2.drawImage(image, x, y, width, height, this);

    }
}
