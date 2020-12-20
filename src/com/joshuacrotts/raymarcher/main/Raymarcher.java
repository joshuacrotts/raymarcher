package com.joshuacrotts.raymarcher.main;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import com.theta.graphic.ThetaGraphics;
import com.theta.input.Command;
import com.theta.model.Vec2;
import com.theta.platform.ThetaGraphicalApplication;
import com.theta.util.ThetaUtils;

public class Raymarcher extends ThetaGraphicalApplication {

  /**
   * 
   */
  private static final int WIDTH = 600;
  
  /**
   * 
   */
  private static final int HEIGHT = 600;

  /**
   * 
   */
  private ArrayList<Ellipse2D.Double> ellipses;

  /**
   * 
   */
  private Vec2 currentVertex;

  /**
   * 
   */
  private IncAngCmd inc;
  
  /**
   * 
   */
  private DecAngCmd dec;
  
  /**
   * 
   */
  private double angle = 0;

  public Raymarcher() {
    super(WIDTH, HEIGHT, "Raymarcher");

    this.ellipses = new ArrayList<>();
    this.currentVertex = new Vec2(400d, 400d);
    this.generateEllipses(15);

    this.inc = new IncAngCmd(this);
    this.dec = new DecAngCmd(this);

    this.startGame();
  }

  @Override
  public void update() {
  }

  @Override
  public void render() {
    // Draws the ellipses/boundaries.
    for (Ellipse2D.Double e : this.ellipses) {
      double ex = e.getCenterX();
      double ey = e.getCenterY();
      double er = e.getWidth();
      ThetaGraphics.ellipse(ex, ey, er, er, Color.RED, true);
    }

    ArrayList<March> marches = this.march();

    // Draw each step of the march.
    for (March m : marches) {
      ThetaGraphics.ellipse(m.circle.getCenterX(), m.circle.getCenterY(), m.dist, m.dist, Color.BLUE, false);
      ThetaGraphics.GFXContext.setColor(Color.YELLOW);
      ThetaGraphics.GFXContext.draw(m.ray);
    }

    // We want to highlight the last march.
    if (!marches.isEmpty()) {
      March lastMarch = marches.get(marches.size() - 1);
      ThetaGraphics.ellipse(lastMarch.ray.x2, lastMarch.ray.y2, 5, 5, Color.GREEN, true);
    }
  }
  
  /**
   * 
   * @param n
   */
  private void generateEllipses(int n) {
    for (int i = 0; i < n; i++) {
      double x = ThetaUtils.randomDouble(100, 500);
      double y = ThetaUtils.randomDouble(100, 500);
      double r = ThetaUtils.randomDouble(10, 25);
      this.ellipses.add(new Ellipse2D.Double(x, y, r, r));
    }
  }

  /**
   * 
   * @return
   */
  private ArrayList<March> march() {
    ArrayList<March> rayMarchSteps = new ArrayList<>();
    Vec2 oldVertex = this.currentVertex.clone();

    while (this.currentVertex.getX() >= 0 && this.currentVertex.getX() <= this.getGameWidth()
        && this.currentVertex.getY() >= 0 && this.currentVertex.getY() <= this.getGameHeight()) {
      double dist = Integer.MAX_VALUE;

      // Finds the minimum ray.
      for (Ellipse2D.Double e : this.ellipses) {
        double ex = e.getCenterX();
        double ey = e.getCenterY();
        double er = e.getWidth();
        dist = Math.min(dist, RaymarcherUtils.signedDistToCircle(this.currentVertex, new Vec2(ex, ey), er));
      }

      // If the new distance has barely moved from the previous, then it's probably
      // a collision.
      if (dist < 0.01) {
        break;
      }

      // Generate the ray circle.
      Ellipse2D.Double currCircle = new Ellipse2D.Double(this.currentVertex.getX() - dist / 2,
          this.currentVertex.getY() - dist / 2, dist, dist);

      // Compute the endpoints of this ray.
      double ex = (this.currentVertex.getX() + dist * Math.cos(Math.toRadians(angle)));
      double ey = (this.currentVertex.getY() + dist * Math.sin(Math.toRadians(angle)));

      // Generate the ray line.
      Line2D.Double ray = new Line2D.Double(this.currentVertex.getX(), this.currentVertex.getY(), ex, ey);

      rayMarchSteps.add(new March(currCircle, ray, dist));

      // Reassign the ray to be the next point on the circle.
      this.currentVertex = new Vec2(ex, ey);
    }

    this.currentVertex = oldVertex;
    return rayMarchSteps;
  }

  private class March {

    private Ellipse2D.Double circle;
    private Line2D.Double ray;
    private double dist;

    public March(Ellipse2D.Double e, Line2D.Double ray, double dist) {
      this.circle = e;
      this.dist = dist;
      this.ray = ray;
    }
  }

  private class IncAngCmd extends Command {
    
    private Raymarcher rm;

    public IncAngCmd(Raymarcher rm) {
      this.rm = rm;
      this.bind(rm.getKeyboard(), KeyEvent.VK_D);
    }

    @Override
    public void pressed(float dt) {
      this.rm.angle++;
    }

    @Override
    public void down(float dt) {
      this.rm.angle++;
    }
  }

  private class DecAngCmd extends Command {
    
    private Raymarcher rm;

    public DecAngCmd(Raymarcher rm) {
      this.rm = rm;
      this.bind(rm.getKeyboard(), KeyEvent.VK_A);
    }

    @Override
    public void pressed(float dt) {
      this.rm.angle = (this.rm.angle - 1 + 360 % 360);
    }

    @Override
    public void down(float dt) {
      this.rm.angle = (this.rm.angle - 1 + 360 % 360);
    }
  }
}
