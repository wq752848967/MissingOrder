//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.nust.heroine.basicstruct;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

public class Place extends Node {
    public Place() {
        this.type = "place";
    }

    public Place(String Name) {
        this.Name = Name;
    }

    public Place(String Name, int DX, int DY, int PX, int PY) {
        this.Name = Name;
        this.DX = DX;
        this.DY = DY;
        this.PX = PX;
        this.PY = PY;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        Color c = g2d.getColor();
        Stroke s = new BasicStroke(2.0F);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(s);
        g2d.drawOval(this.PX, this.PY, this.DX, this.DY);
        g2d.setColor(Color.RED);
        Font f = new Font("time new romans", 1, 14);
        g2d.setFont(f);
        g2d.drawString(this.ID, this.PX + 7, this.PY + 13);
        g2d.setColor(c);
    }

    public Rectangle getRect() {
        return new Rectangle(this.PX, this.PY, this.DX, this.DY);
    }
}
