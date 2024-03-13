package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.animation2d;

import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.trace.Snapshot;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.trace.Trace;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.Domain;

import java.util.Arrays;
import java.util.Comparator;

/**
 * A class to animate a cellular automaton simulation in 2D.
 *
 * @author Pepe Gallardo
 */
public class Animation2D {
  private final Domain domain;
  private final Trace trace;
  private int tick;

  public Animation2D(Domain domain, Trace trace) {
    this.domain = domain;
    this.tick = 0;

    // sort snapshots according to their timestamps (use Comparator.comparing(s -> -s.timestamp()) to play backwards)
    Arrays.sort(trace.snapshots(), Comparator.comparing(Snapshot::timestamp));

    // linearly interpolate snapshots to make animation smoother
    this.trace = new Interpolator(trace).interpolate(7);
  }

  void paint(Canvas canvas) {
    var renderer = new Renderer.Builder()
        .domain(domain)
        .trace(trace)
        .tick(tick)
        .build();
    renderer.renderOn(canvas);
  }

  public void run() {
    var canvas =
        new Canvas.Builder()
            .height((int) domain.getHeight())
            .width((int) domain.getWidth())
            .pixelsPerUnit(20)
            .paint(this::paint)
            .build();

    new Frame(canvas);

    var acceleration = 90;

    var millisBefore = System.currentTimeMillis();
    while (tick < trace.snapshots().length) {
      canvas.update();
      tick += 1;
      var elapsedMillis = (System.currentTimeMillis() - millisBefore);
      try {
        // wait some milliseconds to synchronize animation
        var millis = Math.max((1000 - elapsedMillis) / (10 * acceleration), 1);
        Thread.sleep(millis);
        millisBefore = System.currentTimeMillis();
      } catch (Exception ignored) {
      }
    }
    canvas.update();
  }
}
