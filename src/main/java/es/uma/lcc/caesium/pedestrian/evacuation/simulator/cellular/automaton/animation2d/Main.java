package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.animation2d;

import com.github.cliftonlabs.json_simple.JsonException;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.trace.Trace;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.Environment;

import java.io.IOException;

/**
 * Main application.
 *
 * @author Pepe Gallardo
 */
public class Main {
  public static void main(String[] args) throws IOException, JsonException {
    var environmentFileName = (args.length < 1) ? "data/environments/environment-supermarket.json" :
                              args[0];
    var environment = Environment.fromFile(environmentFileName);
    var domain = environment.getDomain(1);

    var traceFileName = (args.length < 2) ? "data/traces/supermarket-trace-01.json" : args[1];
    var trace = Trace.fromFile(traceFileName);

    var animation2D = new Animation2D(domain, trace);
    animation2D.run();
  }
}
