/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.cs;

import com.intel.jndn.forwarder.api.Face;
import com.intel.jnfd.deamon.table.pit.PitEntry;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;

/**
 *
 * @author zht
 */
public interface SearchCsCallback {
    public void onContentStoreHit(Face inFace, PitEntry pitEntry,
            Interest interest, Data data);
    
    public void onContentStoreMiss(Face inFace, PitEntry pitEntry,
            Interest interest);
}
