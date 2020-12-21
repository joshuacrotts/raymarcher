package com.joshuacrotts.raymarcher.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.joshuacrotts.raymarcher.commands.ChangePositionCommand;
import com.joshuacrotts.raymarcher.commands.DecreaseAngleCommand;
import com.joshuacrotts.raymarcher.commands.IncreaseAngleCommand;
import com.theta.graphic.ThetaGraphics;
import com.theta.model.Vec2;
import com.theta.platform.ThetaGraphicalApplication;
import com.theta.util.ThetaUtils;

public class Raymarcher extends ThetaGraphicalApplication {

  /**
   * Width of the frame.
   */
  private static final int WIDTH = 600;

  /**
   * Height of the frame.
   */
  private static final int HEIGHT = 600;

  /**
   * Stroke for all other graphical elements for consistency. We instantiate them
   * here so we don't have to continuously generate them in the render loop.
   */
  private static final BasicStroke THIN_STROKE = new BasicStroke(1);

  /**
   * Stroke of the ray march circles that are generated so they show up better.
   */
  private static final BasicStroke THICK_STROKE = new BasicStroke(2);

  /**
   * Array of shapes generated in the scene.
   */
  private ArrayList<Shape> boundaries;

  /**
   * Current vertex that we start marching from. This changes overtime and is
   * always reset.
   */
  private Vec2 currentVertex;

  /**
   * Change position command - uses the mouse to readjust where we start the
   * march.
   */
  private ChangePositionCommand changePosCmd;

  /**
   * COmmand to increase the angle by one degree. Turns the ray clockwise.
   */
  private IncreaseAngleCommand incCmd;

  /**
   * COmmand to decrease the angle by one degree. Turns the ray counterclockwise.
   */
  private DecreaseAngleCommand decCmd;

  /**
   * Keeps track of the current angle.
   */
  private double angle = 0;

  public Raymarcher() {
    super(WIDTH, HEIGHT, "Raymarcher");

    this.boundaries = new ArrayList<>();
    this.currentVertex = new Vec2(400d, 400d);
    this.generateBoundaries(15);

    this.incCmd = new IncreaseAngleCommand(this);
    this.decCmd = new DecreaseAngleCommand(this);
    this.changePosCmd = new ChangePositionCommand(this);

    this.startGame();
  }

  @Override
  public void update() {
  }

  @Override
  public void render() {
    // Draws the ellipses/boundaries.
    for (Shape b : this.boundaries) {
      double ex = b.getBounds2D().getCenterX();
      double ey = b.getBounds2D().getCenterY();
      double er = b.getBounds2D().getWidth();
      if (b instanceof Ellipse2D.Double) {
        ThetaGraphics.ellipse(ex, ey, er, er, Color.RED, true);
      } else {
        ThetaGraphics.rect(ex, ey, er, er, Color.GREEN, true, 0);
      }
    }

    ArrayList<March> marches = this.march();

    // Draw each step of the march.
    for (March m : marches) {
      ThetaGraphics.GFXContext.setStroke(new BasicStroke(2));
      ThetaGraphics.ellipse(m.getCircle().getCenterX(), m.getCircle().getCenterY(), m.getDist(), m.getDist(),
          Color.BLUE, false);
      ThetaGraphics.GFXContext.setStroke(new BasicStroke(1));
      ThetaGraphics.GFXContext.setColor(Color.YELLOW);
      ThetaGraphics.GFXContext.draw(m.getRay());
    }

    // We want to highlight the last march.
    if (!marches.isEmpty()) {
      March lastMarch = marches.get(marches.size() - 1);
      ThetaGraphics.ellipse(lastMarch.getRay().x2, lastMarch.getRay().y2, 4, 4, ThetaGraphics.DARK_ORANGE, true);
    }
  }

  /**
   * 
   * @param n
   */
  private void generateBoundaries(int n) {
    for (int i = 0; i < n; i++) {
      double x = ThetaUtils.randomDouble(100, 500);
      double y = ThetaUtils.randomDouble(100, 500);

      if (ThetaUtils.randomDouble() < 0.5) {
        double r = ThetaUtils.randomDouble(10, 25);
        this.boundaries.add(new Rectangle2D.Double(x, y, r, r));
      } else {
        double r = ThetaUtils.randomDouble(10, 25);
        this.boundaries.add(new Ellipse2D.Double(x, y, r, r));
      }
    }
  }

  /**
   * Generates the steps necessary to complete one march cycle. A march first finds the 
   * closest object in the scene. We then generate a circle with a radius of that length 
   * which extends out to that object. Then, the current vertex is reassigned to the end of
   * this generated ray. This process is repeated until we either "hit" an object/boundary, or 
   * we exceed the bounds of the screen. 
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
      for (Shape b : this.boundaries) {
        double ex = b.getBounds2D().getCenterX();
        double ey = b.getBounds2D().getCenterY();
        double er = b.getBounds2D().getWidth();

        if (b instanceof Rectangle2D.Double) {
          er /= 2;
        }

        dist = Math.min(dist, RaymarcherUtils.signedDistToCircle(this.currentVertex, new Vec2(ex, ey), er));
      }

      // If the new distance has barely moved from the previous, then it's probably
      // a collision.
      if (dist < 0.01) {
        break;
      }

      // Generate the ray circle (we want to offset it by the diameter).
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

  public Vec2 getCurrentVertex() {
    return currentVertex;
  }

  public void setCurrentVertex(Vec2 currentVertex) {
    this.currentVertex = currentVertex;
  }

  public double getAngle() {
    return angle;
  }

  public void setAngle(double angle) {
    this.angle = angle;
  }
}
