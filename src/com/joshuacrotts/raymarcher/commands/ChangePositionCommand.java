package com.joshuacrotts.raymarcher.commands;

import java.awt.event.MouseEvent;

import com.joshuacrotts.raymarcher.main.Raymarcher;
import com.theta.input.Command;
import com.theta.model.Vec2;

public class ChangePositionCommand extends Command {
  
  private Raymarcher rm;

  public ChangePositionCommand(Raymarcher rm) {
    this.rm = rm;
    this.bind(rm.getMouse(), MouseEvent.BUTTON1);
  }

  @Override
  public void pressed(float dt) {
    this.rm.setCurrentVertex(new Vec2(this.rm.getMouse().getMouseX(), this.rm.getMouse().getMouseY()));
  }
}
