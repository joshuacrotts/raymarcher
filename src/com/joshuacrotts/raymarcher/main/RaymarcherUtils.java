package com.joshuacrotts.raymarcher.main;

import com.theta.model.Vec2;

public class RaymarcherUtils {
  
  public static double signedDistToCircle(Vec2 v, Vec2 c, double cr) {
    Vec2 w = c.clone();
    w.sub(v);
    return w.mag() - cr;
  }
}
