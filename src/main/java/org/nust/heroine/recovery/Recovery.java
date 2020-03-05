//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.nust.heroine.recovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.nust.heroine.basicstruct.MyPetriNet;
import org.nust.heroine.basicstruct.Node;
import org.nust.heroine.basicstruct.Trace;

public class Recovery {
    public static MyPetriNet petriNet = new MyPetriNet();
    public static ArrayList<MyPetriNet> petriNets = new ArrayList();
    public static Node enabled = null;
    public static HashMap<String, Integer> quot;
    public static String boundary = null;

    public Recovery() {
    }

    public static ArrayList<Trace> recovery(ArrayList<Trace> traceSet) {
        ArrayList<Trace> results = new ArrayList();

        for(int i = 0; i < traceSet.size(); ++i) {

            Trace trace = (Trace)traceSet.get(i);
            trace.countFrequency();
            trace = removeAllActivityNotInPN(trace);

            MyPetriNet min = null;
            if (petriNets.size() > 0) {
                double value = 0.0D;
                Iterator var9 = petriNets.iterator();

                while(var9.hasNext()) {
                    MyPetriNet pn = (MyPetriNet)var9.next();
                    double temp;
                    if ((temp = criterion1(trace, pn)) > value) {
                        value = temp;
                        min = pn;
                    }
                }
            } else {
                min = petriNet;
            }

            Trace result;
            if (min.hasCycle) {

                int dominator = domi(trace.getEventFrequency(), min, 0);

                if (dominator == 0) {
                    dominator = 1;
                }

                quot = new HashMap();

                initQuot(dominator, min);

                if (dominator > 1) {
                    Iterator var13 = ((List)min.f1.get(0)).iterator();

                    while(var13.hasNext()) {
                        Node node = (Node)var13.next();
                        if ((Integer)quot.get(node.getID()) == dominator) {
                            boundary = node.getID();
                        }
                    }
                } else {
                    boundary = null;
                }

                min.initToken();

                result = sr_plus(trace, min);
            } else {
                min.initToken();
                result = sr(trace, min);
            }

            results.add(result);
        }

        return results;
    }

    public static Trace sr(Trace trace, MyPetriNet pn) {
        Trace rTrace = new Trace();
        int length = trace.getEvents().size();

        HashMap<String, Boolean> replayed = new HashMap();

        int i;
        for(i = 0; i < pn.getTransitons().size(); ++i) {
            replayed.put(((Node)pn.getTransitons().get(i)).getID(), false);
        }

        i = 0;

        while(true) {
            while(pn.getSinkPlace().gettoken() <= 0) {
                if (i < trace.getEvents().size()) {
                    String s = (String)trace.getEvents().get(i);
                    Node node = (Node)pn.getTransMap().get(s);
                    if (pn.getTransMap().get(s) == null) {
                        ++i;
                    } else if ((Boolean)replayed.get(s)) {
                        ++i;
                    } else {
                        if (pn.enabled(node)) {
                            enabled = node;
                            rTrace.getEvents().add(enabled.getID());
                            ++i;
                        } else if (enableTransition(trace, pn, i, length) < 0) {
                            rTrace.getEvents().add(enabled.getID());
                        } else if (enableTransition(trace, pn, i, length) > 0) {
                            rTrace.getEvents().add(enabled.getID());
                        }

                        replayed.put(enabled.getID(), true);
                        pn.fire(enabled);
                    }
                } else {
                    Iterator var8 = pn.getTransitons().iterator();

                    while(var8.hasNext()) {
                        Node temp = (Node)var8.next();
                        if (pn.getSinkPlace().gettoken() >= 1) {
                            break;
                        }

                        if (pn.enabled(temp)) {
                            pn.fire(temp);
                            rTrace.getEvents().add(temp.getID());
                        }
                    }
                }
            }

            return rTrace;
        }
    }

    public static Trace sr_plus(Trace trace, MyPetriNet pn) {

        HashMap<String, Integer> ft = new HashMap();
        Trace rTrace = new Trace();

        int k = trace.getEvents().size();

        int i;
        for(i = 0; i < pn.getTransitons().size(); ++i) {
            ft.put(((Node)pn.getTransitons().get(i)).getID(), 0);
        }

        i = 0;
        String pre = boundary;

        while(true) {
            while(true) {
                while(pn.getSinkPlace().gettoken() <= 0) {
                    if (i < trace.getEvents().size()) {
                        String s = (String)trace.getEvents().get(i);
                        int boe;

                        //是否存在循环交叉？
                        if (pn.isHasIntersection && pre == boundary) {
                            HashMap<String, Integer> temp = new HashMap();

                            for(boe = i; boe < trace.getEvents().size(); ++boe) {
                                String s1 = (String)trace.getEvents().get(boe);
                                Integer integer = (Integer)temp.get(s1);
                                if (integer == null) {
                                    temp.put(s1, 1);
                                } else {
                                    temp.put(s1, integer + 1);
                                }

                                if (s1.equalsIgnoreCase(boundary)) {
                                    k = boe;
                                    break;
                                }
                            }

                            boe = domi(temp, pn, 1);
                            if (boe > 1) {
                                Iterator var17 = ((List)pn.f1.get(1)).iterator();

                                Node node;
                                while(var17.hasNext()) {
                                    node = (Node)var17.next();
                                    quot.put(node.getID(), boe);
                                }

                                var17 = ((List)pn.f2.get(1)).iterator();

                                while(var17.hasNext()) {
                                    node = (Node)var17.next();
                                    quot.put(node.getID(), boe - 1);
                                }
                            }
                        }

                        Node node = (Node)pn.getTransMap().get(s);
                        if (pn.getTransMap().get(s) == null) {
                            ++i;
                        } else if ((Integer)ft.get(s) >= (Integer)quot.get(s)) {
                            ++i;
                        } else {
                            Node tb;
                            //当前节点能够触发
                            if (pn.enabled(node) && (Integer)ft.get(s) < (Integer)quot.get(s)) {

                                //是back或者exit 并且能够触发
                                if ((boe = backorexit(pn)) >= 0) {
                                    tb = (Node)pn.backs.get(boe);
                                    if ((Integer)ft.get(tb.getID()) < (Integer)quot.get(tb.getID())) {
                                        enabled = tb;
                                        if (node.getID().equalsIgnoreCase(tb.getID())) {
                                            ++i;
                                        }
                                    } else {
                                        enabled = node;
                                        ++i;
                                    }
                                } else {
                                    enabled = node;
                                    ++i;
                                }
                            } else if (enableTransition(trace, pn, i, k) < 0) {

                                if ((boe = backorexit(pn)) >= 0) {
                                    tb = (Node)pn.backs.get(boe);
                                    if ((Integer)ft.get(tb.getID()) < (Integer)quot.get(tb.getID())) {
                                        enabled = tb;
                                    } else {
                                        enabled = (Node)pn.getTransMap().get(((Node)pn.exits.get(boe)).getID());
                                    }
                                }
                            } else if (enableTransition(trace, pn, i, k) > 0 && (boe = backorexit(pn)) >= 0) {
                                tb = (Node)pn.backs.get(boe);
                                if ((Integer)ft.get(tb.getID()) < (Integer)quot.get(tb.getID())) {
                                    enabled = tb;
                                } else {
                                    enabled = (Node)pn.getTransMap().get(((Node)pn.exits.get(boe)).getID());
                                }
                            }

                            ft.put(enabled.getID(), (Integer)ft.get(enabled.getID()) + 1);
                            rTrace.getEvents().add(enabled.getID());
                            pn.fire(enabled);
                            pre = enabled.getID();
                        }
                    } else {
                        Iterator var8 = pn.getTransitons().iterator();

                        while(var8.hasNext()) {
                            Node transition = (Node)var8.next();
                            if (pn.getSinkPlace().gettoken() >= 1) {
                                break;
                            }

                            if (pn.enabled(transition) && !pn.backs.contains(transition)) {
                                pn.fire(transition);
                                rTrace.getEvents().add(transition.getID());
                            }
                        }
                    }
                }

                return rTrace;
            }
        }
    }

    public static Trace removeAllActivityNotInPN(Trace trace) {
        Iterator itr = trace.getEvents().iterator();

        while(itr.hasNext()) {
            String temp = (String)itr.next();
            if (petriNet.getTransMap().get(temp) == null) {
                itr.remove();
            }
        }

        return trace;
    }

    public static int enableTransition(Trace trace, MyPetriNet pn, int from, int to) {
        ArrayList<Node> enabledTransitions = new ArrayList();
        List<String> subTrace = trace.getEvents().subList(from, to);
        Iterator var7 = pn.getTransitons().iterator();

        while(var7.hasNext()) {
            Node temp = (Node)var7.next();
            if (pn.enabled(temp)) {
                if (!subTrace.contains(temp.getID())) {
                    enabled = temp;
                    return -1;
                }

                enabledTransitions.add(temp);
            }
        }

        int index = 2147483647;
        Iterator var9 = enabledTransitions.iterator();

        while(var9.hasNext()) {
            Node temp = (Node)var9.next();
            int num;
            if ((num = subTrace.indexOf(temp.getID())) < index) {
                if(num!=-1){
                    index = num;
                    enabled = temp;
                }
            }
        }
        return index;
    }

    public static double criterion1(Trace trace, MyPetriNet pn) {
        int numerator = 0;
        int denominator = 0;
        HashMap<String, Boolean> visited = new HashMap();
        Iterator var6 = trace.getEvents().iterator();

        String s;
        while(var6.hasNext()) {
            s = (String)var6.next();
            visited.put(s, false);
        }

        var6 = trace.getEvents().iterator();

        while(var6.hasNext()) {
            s = (String)var6.next();
            if (!(Boolean)visited.get(s)) {
                if (pn.getTransMap().get(s) != null) {
                    ++numerator;
                }

                ++denominator;
                visited.put(s, true);
            }
        }

        var6 = pn.getTransMap().keySet().iterator();

        while(var6.hasNext()) {
            s = (String)var6.next();
            if (visited.get(s) == null) {
                ++denominator;
            } else if (!(Boolean)visited.get(s)) {
                ++denominator;
            }
        }

        return (double)numerator * 1.0D / (double)denominator;
    }

    public static int domi(HashMap<String, Integer> ef, MyPetriNet pn, int level) {
        int[] numCounter = new int[40];
        Integer num = null;
        int times = 0;
        int dominate = 0;
        Node node;
        Iterator var8;
        if (level == 0) {
            if (pn.isHasIntersection) {
                var8 = ((List)pn.f1.get(0)).iterator();

                while(var8.hasNext()) {
                    node = (Node)var8.next();
                    if ((num = (Integer)ef.get(node.getID())) != null && !((List)pn.f1.get(1)).contains(pn.getTransMap().get(node.getID()))) {
                        ++numCounter[num];
                    }
                }
            } else {
                var8 = ((List)pn.f1.get(0)).iterator();

                while(var8.hasNext()) {
                    node = (Node)var8.next();
                    if ((num = (Integer)ef.get(node.getID())) != null) {
                        ++numCounter[num];
                    }
                }
            }

            var8 = ((List)pn.f2.get(0)).iterator();

            while(var8.hasNext()) {
                node = (Node)var8.next();
                if ((num = (Integer)ef.get(node.getID())) != null) {
                    ++numCounter[num + 1];
                }
            }
        } else if (level == 1) {
            var8 = ((List)pn.f1.get(1)).iterator();

            while(var8.hasNext()) {
                node = (Node)var8.next();
                if ((num = (Integer)ef.get(node.getID())) != null) {
                    ++numCounter[num];
                }
            }

            var8 = ((List)pn.f2.get(1)).iterator();

            while(var8.hasNext()) {
                node = (Node)var8.next();
                if ((num = (Integer)ef.get(node.getID())) != null) {
                    ++numCounter[num + 1];
                }
            }
        }

        for(int i = 0; i < 30; ++i) {
            if (times < numCounter[i]) {
                times = numCounter[i];
                dominate = i;
            }
        }

        return dominate;
    }

    public static void initQuot(int domi, MyPetriNet pn) {
        Iterator var3 = pn.getTransitons().iterator();

        Node node;
        while(var3.hasNext()) {
            node = (Node)var3.next();
            quot.put(node.getID(), 1);
        }

        var3 = ((List)pn.f1.get(0)).iterator();

        while(var3.hasNext()) {
            node = (Node)var3.next();
            quot.put(node.getID(), domi);
        }

        var3 = ((List)pn.f2.get(0)).iterator();

        while(var3.hasNext()) {
            node = (Node)var3.next();
            quot.put(node.getID(), domi - 1);
        }

        if (pn.isHasIntersection) {
            var3 = ((List)pn.f2.get(1)).iterator();

            while(var3.hasNext()) {
                node = (Node)var3.next();
                quot.put(node.getID(), 0);
            }
        }

    }

    public static int backorexit(MyPetriNet pn) {
        for(int i = 0; i < pn.backs.size(); ++i) {
            if (pn.enabled((Node)pn.backs.get(i)) && pn.enabled((Node)pn.exits.get(i))) {
                return i;
            }
        }

        return -1;
    }
}
