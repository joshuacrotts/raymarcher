package com.joshuacrotts.raymarcher.commands;

import java.awt.event.KeyEvent;

import com.joshuacrotts.raymarcher.main.Raymarcher;
import com.theta.input.Command;

public class DecreaseAngleCommand extends Command {

  private Raymarcher rm;

  public DecreaseAngleCommand(Raymarcher rm) {
    this.rm = rm;
    this.bind(rm.getKeyboard(), KeyEvent.VK_A);
  }

  @Override
  public void pressed(float dt) {
    this.rm.setAngle(this.rm.getAngle() - 1 + 360 % 360);
  }

  @Override
  public void down(float dt) {
    this.rm.setAngle(this.rm.getAngle() - 1 + 360 % 360);
  }
}
