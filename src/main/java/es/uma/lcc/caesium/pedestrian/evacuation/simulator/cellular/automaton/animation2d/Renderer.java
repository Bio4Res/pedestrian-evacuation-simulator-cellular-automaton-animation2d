package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.animation2d;

import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.trace.Trace;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.Domain;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class for rendering a domain and its pedestrians at a given time tick.
 *
 * @author Pepe Gallardo
 */
public record Renderer(Domain domain, Trace trace, int tick) {

  private void renderDomain(Graphics2D graphics2D) {
    // Draw obstacles
    graphics2D.setColor(Color.red);
    for (var obstacle : domain.getObstacles()) {
      var shape = obstacle.getShape().getAWTShape();
      graphics2D.fill(shape);
    }

    // Draw accesses
    graphics2D.setColor(Color.green);
    for (var access : domain.getAccesses()) {
      var shape = access.getShape().getAWTShape();
      graphics2D.fill(shape);
    }
  }

  private void renderPedestrians(Graphics2D graphics2D) {
    if (tick >= 0 && tick < trace.snapshots().length) {
      var snapshot = trace.snapshots()[tick];
      var cellDimension = trace.cellDimension();

      // Draw pedestrians in current snapshot
      graphics2D.setColor(Color.blue);
      for (var pedestrian : snapshot.crowd()) {
        if (pedestrian.location().domain() == domain.id()) {
          var coordinates = pedestrian.location().coordinates();

          // ellipse2D takes coordinates of the lower left corner of the rectangle that contains the ellipse
          graphics2D.fill(new Ellipse2D.Double(coordinates.x() - cellDimension / 2, coordinates.y() - cellDimension / 2,
              cellDimension, cellDimension));
        }
      }
    }
  }

  public void renderOn(Canvas canvas) {
    var graphics2D = canvas.graphics2D();
    renderDomain(graphics2D);
    renderPedestrians(graphics2D);
  }

  public void renderOnSVG(String filename, double pixelsPerUnit) throws IOException {
    // Get a DOMImplementation
    var domImpl = GenericDOMImplementation.getDOMImplementation();

    // Create an instance of org.w3c.dom.Document
    var document = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);

    // Create an instance of the SVG Generator
    var svgGraphics2D = new SVGGraphics2D(document);
    var width = (int) domain.getWidth();
    var height = (int) domain.getHeight();
    svgGraphics2D.translate(0, pixelsPerUnit * height);
    svgGraphics2D.scale(pixelsPerUnit, -pixelsPerUnit);
    svgGraphics2D.setColor(Color.white);
    svgGraphics2D.fillRect(0, 0, width, height);
    renderDomain(svgGraphics2D);
    renderPedestrians(svgGraphics2D);
    var useCSS = true; // we want to use CSS style attributes
    var writer = new FileWriter(filename);
    svgGraphics2D.stream(writer, useCSS);
    writer.close();
  }

  /**
   * Class for building a renderer by providing its parameters.
   */
  public static class BuilderWithDomain {
    private final Domain domain;

    private BuilderWithDomain(Domain domain) {
      this.domain = domain;
    }

    public BuilderWithDomainAndTrace trace(Trace trace) {
      return new BuilderWithDomainAndTrace(domain, trace);
    }
  }

  public static class BuilderWithDomainAndTrace {
    private final Domain domain;
    private final Trace trace;

    private BuilderWithDomainAndTrace(Domain domain, Trace trace) {
      this.domain = domain;
      this.trace = trace;
    }

    public BuilderWithDomainAndTraceAndTick tick(int tick) {
      return new BuilderWithDomainAndTraceAndTick(domain, trace, tick);
    }
  }

  public static class BuilderWithDomainAndTraceAndTick {
    private final Domain domain;
    private final Trace trace;
    private final int tick;

    private BuilderWithDomainAndTraceAndTick(Domain domain, Trace trace, int tick) {
      this.domain = domain;
      this.trace = trace;
      this.tick = tick;
    }

    public Renderer build() {
      return new Renderer(domain, trace, tick);
    }
  }

  public static final class Builder {
    public Builder() {
    }

    public BuilderWithDomain domain(Domain domain) {
      return new BuilderWithDomain(domain);
    }
  }
}
