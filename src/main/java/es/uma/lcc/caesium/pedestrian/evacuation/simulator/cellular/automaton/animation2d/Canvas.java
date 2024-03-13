package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.animation2d;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * A drawing class using double buffering.
 *
 * @author Pepe Gallardo
 */
public abstract class Canvas extends JLabel {
  private final int height, width;
  private final Color backgroundColor;
  private final BufferedImage offScreenBufferedImage;
  private final Graphics2D offScreenGraphics2D, onScreenGraphics2D;

  public Canvas(int height, int width, int pixelsPerUnit, Color backgroundColor) {
    super();

    this.height = height * pixelsPerUnit;
    this.width = width * pixelsPerUnit;
    this.backgroundColor = backgroundColor;

    var dimension = new Dimension(width * pixelsPerUnit, height * pixelsPerUnit);
    setPreferredSize(dimension);

    this.offScreenBufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
    BufferedImage onScreenBufferedImage = new BufferedImage(dimension.width, dimension.height,
        BufferedImage.TYPE_INT_ARGB);
    this.offScreenGraphics2D = offScreenBufferedImage.createGraphics();
    this.onScreenGraphics2D = onScreenBufferedImage.createGraphics();

    RenderingHints hints = new RenderingHints(null);
    hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    offScreenGraphics2D.addRenderingHints(hints);
    offScreenGraphics2D.setStroke(new BasicStroke(1.5f));

    // flip y axis and scale properly
    offScreenGraphics2D.translate(0, getHeight());
    offScreenGraphics2D.scale(pixelsPerUnit, -pixelsPerUnit);

    ImageIcon imageIcon = new ImageIcon(onScreenBufferedImage);
    setIcon(imageIcon);
  }

  public Graphics2D graphics2D() {
    return offScreenGraphics2D;
  }

  public abstract void paint(Canvas canvas);

  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);

    // Clear canvas
    offScreenGraphics2D.setColor(backgroundColor);
    offScreenGraphics2D.fillRect(0, 0, width, height);

    // Draw on canvas
    paint(this);
    onScreenGraphics2D.drawImage(offScreenBufferedImage, 0, 0, null);
  }

  public void update() {
    // Show canvas on screen
    repaint();
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public int getWidth() {
    return width;
  }

  /**
   * Class for building a canvas by providing its parameters.
   */
  public static final class Builder {
    int height = 200, width = 200, pixelsPerUnit = 5;
    Color color = Color.white;
    Consumer<Canvas> paint = canvas -> {
    };

    public Builder() {
    }

    public Builder height(int height) {
      this.height = height;
      return this;
    }

    public Builder width(int width) {
      this.width = width;
      return this;
    }

    public Builder pixelsPerUnit(int pixelsPerUnit) {
      this.pixelsPerUnit = pixelsPerUnit;
      return this;
    }

    public Builder background(Color color) {
      this.color = color;
      return this;
    }

    public Builder paint(Consumer<Canvas> paint) {
      this.paint = paint;
      return this;
    }

    public Canvas build() {
      return new Canvas(height, width, pixelsPerUnit, color) {
        @Override
        public void paint(Canvas canvas) {
          paint.accept(canvas);
        }
      };
    }
  }
}