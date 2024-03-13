package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.animation2d;

import javax.swing.*;

/**
 * A simple frame for a GUI application.
 *
 * @author Pepe Gallardo
 */
public class Frame extends JFrame {
  public Frame(Canvas canvas) {
    super();
    this.add(canvas);
    this.pack();
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
  }
}