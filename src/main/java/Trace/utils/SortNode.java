package Trace.utils;

public class SortNode {
    private String priority;
    private String value;

    public SortNode(String priority, String value) {
        this.priority = priority;
        this.value = value;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
