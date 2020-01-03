package Trace.pojos;

import java.util.ArrayList;

public class CharNode {
    private int index = -1;
    private ArrayList<String> pre = new ArrayList<String>();
    private String cur = "";
    private ArrayList<String> post = new ArrayList<String>();


    public void setPre(ArrayList<String> pre) {
        this.pre = pre;
    }

    public void setPost(ArrayList<String> post) {
        this.post = post;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ArrayList<String> getPre() {
        return pre;
    }

    public void addPre(String preTrans) {
        this.pre.add(preTrans);
    }
//    public void setPre(String pre) {
//        this.pre += pre;
//    }

    public String getCur() {
        return cur;
    }

    public void setCur(String cur) {
        this.cur = cur;
    }

    public ArrayList<String> getPost() {
        return post;
    }

    public void addPost(String postTrans) {
        this.post.add(postTrans);
    }
//    public void setPost(String post) {
//        this.post += post;
//    }
}
