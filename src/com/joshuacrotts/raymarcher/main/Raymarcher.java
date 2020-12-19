package com.joshuacrotts.raymarcher.main;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import com.theta.graphic.ThetaGraphics;
import com.theta.model.Vec2;
import com.theta.platform.ThetaGraphicalApplication;
import com.theta.util.ThetaUtils;

/**
 * No obviously this code looks like shit; i aint done yet
 * @author joshuacrotts
 *
 */
public class Raymarcher extends ThetaGraphicalApplication {

  private static final int WIDTH = 600;
  private static final int HEIGHT = 600;
  
  private ArrayList<Ellipse2D.Double> ellipses;
  
  Vec2 p;
  
  public Raymarcher() {
    super(WIDTH, HEIGHT, "Raymarcher");
    
    this.ellipses = new ArrayList<>();
    p = new Vec2(400d, 400d);
    
    for (int i = 0; i < 10; i++) {
      double x = ThetaUtils.randomDouble(100, 500);
      double y = ThetaUtils.randomDouble(100, 500);
      double r = ThetaUtils.randomDouble(10, 25);
      this.ellipses.add(new Ellipse2D.Double(x,y,r,r));
    }
    
    this.startGame();
  }
  double d = 0;
  double angle = 0;
  @Override
  public void update() {
    d = Integer.MAX_VALUE;
    
    // Finds the min ray
    for (Ellipse2D.Double e : this.ellipses) {
      double ex = e.getCenterX();
      double ey = e.getCenterY();
      double er = e.getWidth();
      d = Math.min(d, RaymarcherUtils.signedDistToCircle(p, new Vec2(ex, ey), er));
    }
    
    // Fucking around
    angle++;
  }
  
  @Override
  public void render() {
    // Draws the ellipses
    for (Ellipse2D.Double e : this.ellipses) {
      double ex = e.getCenterX();
      double ey = e.getCenterY();
      double er = e.getWidth();
      ThetaGraphics.ellipse(ex, ey, er, er, Color.BLUE, true);
    }
    
    // Draws the small point
    ThetaGraphics.ellipse(p.getX(), p.getY(), 2.0, 2.0, Color.GREEN, true);
    
    // Draws the current march circle step
    ThetaGraphics.ellipse(p.getX(), p.getY(), d, d, Color.BLUE, false);
    
    // Draws the ray
    ThetaGraphics.GFXContext.setColor(Color.YELLOW);
    double ex = (p.getX() + d * Math.cos(Math.toRadians(angle)));
    double ey = (p.getY() + d * Math.sin(Math.toRadians(angle)));
    ThetaGraphics.GFXContext.draw(new Line2D.Double(p.getX(), p.getY(), ex,ey));
  }
}
