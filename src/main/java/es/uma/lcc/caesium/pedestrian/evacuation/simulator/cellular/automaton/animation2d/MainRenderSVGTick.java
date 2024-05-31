package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.animation2d;

import com.github.cliftonlabs.json_simple.JsonException;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.trace.Trace;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.Environment;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Main application to render a specific tick of a simulation in SVG format.
 *
 * @author Pepe Gallardo
 */
public class MainRenderSVGTick {
  public static void main(String[] args) throws IOException, JsonException {
    var environmentFileName = (args.length < 1) ? "data/environments/environment-supermarket.json" :
                              args[0];
    var environment = Environment.fromFile(environmentFileName);
    var domain = environment.getDomain(1);

    var traceFileName = (args.length < 2) ? "data/traces/supermarket-trace-01.json" : args[1];
    var trace = Trace.fromFile(traceFileName);

    var step = 10;
    for (int tick = -step; tick < trace.snapshots().length; tick += step) {
      var renderer = new Renderer.Builder()
          .domain(domain)
          .trace(trace)
          .tick(tick)
          .build();

      var environmentPath = Path.of(environmentFileName);
      var filename = "data/render/%s%s.svg".formatted(
          environmentPath.getFileName().toString().replaceFirst("[.][^.]+$", "")
          , tick >= 0 ? ".tick-%05d".formatted(tick) : "");

      var pixelsPerUnit = 15;
      renderer.renderOnSVG(filename, pixelsPerUnit);
      System.out.println("Rendered to " + filename);
    }
  }
}
