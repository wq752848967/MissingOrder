//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.nust.heroine.basicstruct;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

public class Arc implements Serializable, Cloneable {
    private static final long serialVersionUID = 3486802786745182175L;
    private Node Source;
    private Node Target;
    private String ID;
    private String Type;
    private String label;
    private int[] splineX = new int[100];
    private int[] splineY = new int[100];
    private int endX;
    private int endY;
    private boolean visited = false;
    private boolean printVisited = false;
    private boolean petriNet = false;
    public boolean isDeleted = false;

    public Arc() {
    }

    public Arc(Node Source, Node Target) {
        this.Source = Source;
        this.Target = Target;
    }

    public Arc(Node Source, Node Target, String label, String Type) {
        this.Source = Source;
        this.Target = Target;
        this.label = label;
        this.Type = Type;
    }

    public Node getSource() {
        return this.Source;
    }

    public void setSource(Node Source) {
        this.Source = Source;
    }

    public Node getTarget() {
        return this.Target;
    }

    public void setTarget(Node Target) {
        this.Target = Target;
    }

    public String getID() {
        return this.ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getType() {
        return this.Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int[] getSplineX() {
        return this.splineX;
    }

    public int[] getSplineY() {
        return this.splineY;
    }

    public void setSplineX(int i, int j) {
        this.splineX[i] = j;
    }

    public void setSplineY(int i, int j) {
        this.splineY[i] = j;
    }

    public int getEndX() {
        return this.endX;
    }

    public int getEndY() {
        return this.endY;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public Boolean getVisited() {
        return this.visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }

    public Boolean getprintVisited() {
        return this.printVisited;
    }

    public void setprintVisited(Boolean printVisited) {
        this.printVisited = printVisited;
    }

    public boolean isPetriNet() {
        return this.petriNet;
    }

    public void setPetriNet(boolean petriNet) {
        this.petriNet = petriNet;
    }

    public void show() {
        System.out.print(this.Source.getID() + this.Target.getID() + " label: " + this.label + " type: " + this.Type);
        System.out.println();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Arc)) {
            return false;
        } else {
            Arc a = (Arc)obj;
            return this.getSource().equals(a.getSource()) && this.getTarget().equals(a.getTarget());
        }
    }

    public boolean isControlArc() {
        return this.getSource().getType().equalsIgnoreCase("entry") || this.getSource().getType().equalsIgnoreCase("while") || this.getSource().getType().equalsIgnoreCase("if") || this.getSource().getType().equalsIgnoreCase("switch");
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(this.Source.getPX() + 9, this.Source.getPY() + 12, this.Target.getPX() + 9, this.Target.getPY());
        double d = Math.sqrt((double)((this.Target.getPY() - this.Source.getPY()) * (this.Target.getPY() - this.Source.getPY()) + (this.Target.getPX() - this.Source.getPX()) * (this.Target.getPX() - this.Source.getPX())));
        double xa = (double)(this.Target.getPX() + 9) + (double)(10 * (this.Source.getPX() - this.Target.getPX() + (this.Source.getPY() - this.Target.getPY()) / 2)) / d;
        double ya = (double)this.Target.getPY() + (double)(10 * (this.Source.getPY() - this.Target.getPY() - (this.Source.getPX() - this.Target.getPX()) / 2)) / d;
        double xb = (double)(this.Target.getPX() + 9) + (double)(10 * (this.Source.getPX() - this.Target.getPX() - (this.Source.getPY() - this.Target.getPY()) / 2)) / d;
        double yb = (double)this.Target.getPY() + (double)(10 * (this.Source.getPY() - this.Target.getPY() + (this.Source.getPX() - this.Target.getPX()) / 2)) / d;
        g.drawLine(this.Target.getPX() + 9, this.Target.getPY(), (int)xa, (int)ya);
        g.drawLine(this.Target.getPX() + 9, this.Target.getPY(), (int)xb, (int)yb);
        g.setColor(Color.BLACK);
    }

    public String toString() {
        return this.Source.toString() + "-" + this.Target.toString();
    }



    public int hashCode() {
        return 7 * this.getSource().hashCode() + 2 * this.getTarget().hashCode();
    }
}
