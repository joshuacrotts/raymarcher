package com.joshuacrotts.raymarcher.main;

import java.awt.geom.Point2D;

import com.theta.model.Vec2;

public class RaymarcherUtils {
  
  public static double length(Vec2 v) {
    return Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY());
  }
  
  public static double signedDistToCircle(Vec2 v, Vec2 c, double cr) {
    Vec2 w = c.clone();
    w.sub(v);
    return length(w) - cr;
  }
}
