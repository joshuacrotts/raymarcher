package com.joshuacrotts.raymarcher.main;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class March {
  
  private Ellipse2D.Double circle;
  private Line2D.Double ray;
  private double dist;

  public March(Ellipse2D.Double e, Line2D.Double ray, double dist) {
    this.circle = e;
    this.dist = dist;
    this.ray = ray;
  }

  public Ellipse2D.Double getCircle() {
    return circle;
  }

  public void setCircle(Ellipse2D.Double circle) {
    this.circle = circle;
  }

  public Line2D.Double getRay() {
    return ray;
  }

  public void setRay(Line2D.Double ray) {
    this.ray = ray;
  }

  public double getDist() {
    return dist;
  }

  public void setDist(double dist) {
    this.dist = dist;
  }
}
