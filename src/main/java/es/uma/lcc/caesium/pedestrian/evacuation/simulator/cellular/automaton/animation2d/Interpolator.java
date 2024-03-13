package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.animation2d;

import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.trace.*;

/**
 * Class for interpolating a trace by averaging the positions of pedestrians in consecutive snapshots.
 *
 * @author Pepe Gallardo
 */
public class Interpolator {
  private final Trace trace;

  public Interpolator(Trace trace) {
    this.trace = trace;
  }

  private Pedestrian average(Pedestrian pedestrian1, Pedestrian pedestrian2) {
    var x = (pedestrian1.location().coordinates().x() + pedestrian2.location().coordinates().x()) / 2;
    var y = (pedestrian1.location().coordinates().y() + pedestrian2.location().coordinates().y()) / 2;
    var coordinates = new Coordinates(x, y);
    return new Pedestrian(pedestrian1.id(), new Location(pedestrian1.location().domain(), coordinates));
  }

  private Pedestrian searchPedestrian(Pedestrian[] crowd, int id) {
    for (var pedestrian : crowd) {
      if (pedestrian.id() == id) {
        return pedestrian;
      }
    }
    return null;
  }

  private Snapshot average(Snapshot snapshot1, Snapshot snapshot2) {
    var crowd1 = snapshot1.crowd();
    var crowd2 = snapshot2.crowd();
    var crowd = new Pedestrian[crowd2.length];

    for (int i = 0; i < crowd2.length; i += 1) {
      var pedestrian2 = crowd2[i];
      var pedestrian1 = searchPedestrian(crowd1, pedestrian2.id());
      assert pedestrian1 != null;
      crowd[i] = average(pedestrian1, pedestrian2);
    }

    var timestamp = (snapshot1.timestamp() + snapshot2.timestamp()) / 2;
    return new Snapshot(timestamp, crowd);
  }

  private Snapshot[] interpolate(Snapshot[] snapshots) {
    var length = 2 * snapshots.length - 1;
    var extendedSnapshots = new Snapshot[length];
    var step = 2;

    for (int i = 0, j = 0; i < length; i += step, j += 1) {
      extendedSnapshots[i] = snapshots[j];
    }

    for (int i = 0; i + step < length; i += step) {
      var avg = average(extendedSnapshots[i], extendedSnapshots[i + step]);
      extendedSnapshots[i + 1] = avg;
    }

    return extendedSnapshots;
  }

  public Trace interpolate(int n) {
    var snapshots = trace.snapshots();
    for (int i = 0; i < n; i += 1) {
      snapshots = interpolate(snapshots);
    }
    return new Trace(trace.cellDimension(), snapshots);
  }

  public Trace interpolate() {
    return interpolate(1);
  }
}
