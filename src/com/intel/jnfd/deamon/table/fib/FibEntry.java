/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.fib;

import com.intel.jnfd.deamon.face.AbstractFace;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public class FibEntry {
    
    public FibEntry(Name prefix) {
        this.prefix = prefix;
    }
    
    public Name getPrefix() {
        return prefix;
    }
    
    public Vector<FibNextHop> getNextHopList() {
        return nextHopList;
    }
    
    public boolean hasNextHop() {
        return nextHopList.isEmpty();
    }
    
    public boolean hasNextHop(AbstractFace face) {
        return findNextHop(face) != null;
    }
    
    public void addNextHop(AbstractFace face, long cost) {
        FibNextHop nextHop = findNextHop(face);
        if (nextHop == null) {
            nextHopList.add(new FibNextHop(face, cost));
        } else {
            nextHop.setCost(cost);
        }
        sortNextHops();
    }
    
    public void removeNextHop(AbstractFace face) {
        FibNextHop nextHop = findNextHop(face);
        if (nextHop != null) {
            nextHopList.remove(nextHop);
        }
    }
    
    private void sortNextHops() {
        Collections.sort(nextHopList);
    }
    
    private FibNextHop findNextHop(AbstractFace face) {
        Iterator<FibNextHop> iterator = nextHopList.iterator();
        while (iterator.hasNext()) {
            FibNextHop next = iterator.next();
            if (next.getFace() == face || next.getFace().equals(face)) {
                return next;
            }
        }
        return null;
    }
    
    private Name prefix;
    private Vector<FibNextHop> nextHopList = new Vector<>();
}
