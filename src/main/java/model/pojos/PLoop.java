package model.pojos;

public class PLoop {
    private String start="";
    private String transition="";
    private String end="";

    public PLoop(String start, String transition, String end) {
        this.start = start;
        this.transition = transition;
        this.end = end;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }




    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
