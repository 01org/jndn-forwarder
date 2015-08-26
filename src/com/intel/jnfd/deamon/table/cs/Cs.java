/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.cs;

import com.intel.jnfd.deamon.face.Face;
import com.intel.jnfd.deamon.table.pit.PitEntry;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public abstract class Cs {

    public abstract boolean insert(Data data, boolean isUnsolicited);

    public abstract void find(Face inFace, PitEntry pitEntry, Interest interest, 
            SearchCsCallback searchCsCallback) throws Exception;

    public abstract void erase(Name exactName);

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    private int limit;
}
