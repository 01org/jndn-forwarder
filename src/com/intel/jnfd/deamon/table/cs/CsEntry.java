/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.cs;

import com.intel.jnfd.util.NameUtil;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public class CsEntry implements Comparable {

    public CsEntry() {
    }

    public CsEntry(Data data, boolean isUnsolicited) {
        this.data = data;
        this.isUnsolicited = isUnsolicited;
        updateStaleTime();
    }

    public Data getData() {
        return data;
    }

    public Name getName() {
        return hasData() ? data.getName() : null;
    }

//    In the c++ NFD, they have a method called getFullName, we will see if we 
//    need it or not int he near future.
//    public Name getFullName() {
//        return data.getName();
//    }
    public boolean getIsUnsolicited() {
        return isUnsolicited;
    }

    public long getStaleTime() {
        return staleTime;
    }

    public boolean isStale() {
        return hasData()
                ? staleTime < System.currentTimeMillis() : true;

    }

    public boolean hasData() {
        return data != null;
    }

    public boolean canSatisfy(Interest interest) {
        if (!hasData()) {
            return false;
        }
        if (NameUtil.interestMatchesData(interest, data)) {
            return false;
        }
        return !(interest.getMustBeFresh() && isStale());
    }

    public void setData(Data data, boolean isUnsolicited) {
        this.data = data;
        this.isUnsolicited = isUnsolicited;
        updateStaleTime();
    }

    public void updateStaleTime() {
        if (data.getMetaInfo().getFreshnessPeriod() > 0) {
            this.staleTime = System.currentTimeMillis()
                    + (long) data.getMetaInfo().getFreshnessPeriod();
        } else {
            this.staleTime = Long.MAX_VALUE;
        }
    }

    public void reset() {
        data = null;
        isUnsolicited = true;
        staleTime = System.currentTimeMillis();

    }

    private Data data;
    private boolean isUnsolicited = true;
    private long staleTime;

    //Why here is the warnning?
    @Override
    public final boolean equals(Object o) {
        return compareTo(o) == 0;
    }

    @Override
    public final int compareTo(Object o) {
        return data.getName().compare(((CsEntry) o).getData().getName());
    }

}
