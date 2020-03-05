//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.nust.heroine.basicstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Trace {
    private String fileName;
    private ArrayList<String> events;
    private HashMap<String, Integer> eventFrequency;

    public Trace() {
        this.fileName = "";
        this.events = new ArrayList();
    }

    public Trace(ArrayList<String> events) {
        this.events = events;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<String> getEvents() {
        return this.events;
    }

    public void setEvents(ArrayList<String> events) {
        this.events = events;
    }

    public HashMap<String, Integer> getEventFrequency() {
        return this.eventFrequency;
    }

    public void setEventFrequency(HashMap<String, Integer> eventFrequency) {
        this.eventFrequency = eventFrequency;
    }

    public void countFrequency() {
        this.eventFrequency = new HashMap();
        Iterator var2 = this.getEvents().iterator();

        while(var2.hasNext()) {
            String s = (String)var2.next();
            Integer freq = (Integer)this.eventFrequency.get(s);
            this.eventFrequency.put(s, freq == null ? 1 : freq + 1);
        }

    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (!(obj instanceof Trace)) {
            return false;
        } else {
            Trace t = (Trace)obj;
            return this.getEvents().equals(t.getEvents());
        }
    }

    public String toString() {
        return this.events.toString();
    }

    public Trace clone() throws CloneNotSupportedException {
        Trace clone = new Trace();
        clone.setEvents(this.events);
        return clone;
    }
}
