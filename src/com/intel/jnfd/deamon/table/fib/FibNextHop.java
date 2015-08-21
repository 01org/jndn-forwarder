/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.fib;

import com.intel.jnfd.deamon.face.AbstractFace;

/**
 *
 * @author zht
 */
public class FibNextHop implements Comparable{
    
    public FibNextHop(AbstractFace face) {
        this.face = face;
    }
    
    public FibNextHop(AbstractFace face, long cost) {
        this.face = face;
        this.cost = cost;
    }

    public AbstractFace getFace() {
        return face;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }
    
    
    private AbstractFace face;
    private long cost;

    @Override
    public int compareTo(Object o) {
        if(cost < ((FibNextHop)o).getCost())
            return -1;
        if(cost > ((FibNextHop)o).getCost())
            return 1;
        return 0;
    }
}
