package nl.visi.interaction_framework.editor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RotatingButton extends JButton {

    private static final long serialVersionUID = 1L;

    protected static final RenderingHints qualityHints;

    static {
     qualityHints = new RenderingHints(null);
     qualityHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
          RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
     qualityHints.put(RenderingHints.KEY_RENDERING,
          RenderingHints.VALUE_RENDER_QUALITY);
     qualityHints.put(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
     qualityHints.put(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BILINEAR);
     qualityHints.put(RenderingHints.KEY_COLOR_RENDERING,
          RenderingHints.VALUE_COLOR_RENDER_QUALITY);
     qualityHints.put(RenderingHints.KEY_DITHERING,
          RenderingHints.VALUE_DITHER_ENABLE);
    }

    public static void main(String[] args) {
     SwingUtilities.invokeLater(new Runnable() {

         @Override
         public void run() {
          final JFrame frame = new JFrame();
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          final JSlider slider = new JSlider(0, 255, 0);
          frame.add(slider, BorderLayout.PAGE_START);
          final RotatingButton button = new RotatingButton("New button");
          // button.setBorder(new TitledBorder("Button"));

          final JPanel container = new JPanel(new GridBagLayout());
          container.setPreferredSize(new Dimension(400, 400));

          container.add(button, new GridBagConstraints(0, 0, 1, 1, 1.0,
               1.0, GridBagConstraints.CENTER,
               GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

          frame.add(container, BorderLayout.CENTER);
          slider.addChangeListener(new ChangeListener() {

              @Override
              public void stateChanged(ChangeEvent event) {
               button.setRotation(Math.PI * slider.getValue() / 255.0);
              }
          });
          frame.pack();
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);
         }
     });
    }

    private double rotation = 0;

    private BufferedImage image;

    public RotatingButton() {
     super();
     init();
    }

    public RotatingButton(Action action) {
     super(action);
     init();
    }

    public RotatingButton(Icon icon) {
     super(icon);
     init();
    }

    public RotatingButton(String text) {
     super(text);
     init();
    }

    public RotatingButton(String text, Icon icon) {
     super(text, icon);
     init();
    }

    @Override
    public Dimension getPreferredSize() {
     Dimension size = super.getPreferredSize();
     Area area = new Area(new Rectangle(size.width, size.height));
     area.transform(AffineTransform.getRotateInstance(rotation, size
          .getWidth() / 2.0, size.getHeight() / 2.0));
     Rectangle bounds = area.getBounds();
     size.setSize(bounds.width, bounds.height);
     return size;
    }

    public double getRotation() {
     return rotation;
    }

    @Override
    public void paint(Graphics g) {
     Graphics2D g2 = (Graphics2D) g;
     g2.setRenderingHints(qualityHints);
     g2.setColor(this.getBackground());
     g2.fillRect(0, 0, getWidth(), getHeight());
     g2.rotate(rotation, getWidth() / 2.0, getHeight() / 2.0);
     g2.drawImage(image, null, (getWidth() - image.getWidth()) / 2,
          (getHeight() - image.getHeight()) / 2);
    }

    public void setRotation(double rotation) {
     this.rotation = rotation;
     this.updateImage();
    }

    private void init() {
     updateImage();
     this.addPropertyChangeListener(new PropertyChangeListener() {

         @Override
         public void propertyChange(PropertyChangeEvent event) {
          updateImage();
         }
     });
     this.addChangeListener(new ChangeListener() {

         @Override
         public void stateChanged(ChangeEvent event) {
          updateImage();
         }
     });
    }

    private void updateImage() {
     this.setSize(super.getPreferredSize());
     image = new BufferedImage(getWidth(), getHeight(),
          BufferedImage.TYPE_INT_ARGB);
     Graphics2D ig = image.createGraphics();
     super.paint(ig);
     ig.dispose();
     this.setSize(this.getPreferredSize());
     if (this.getParent() instanceof JComponent) {
         ((JComponent) this.getParent()).revalidate();
         ((JComponent) this.getParent()).repaint();
     }
    }

}