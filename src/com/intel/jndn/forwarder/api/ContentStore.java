/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jnfd.deamon.table.cs.SearchCsCallback;
import com.intel.jnfd.deamon.table.pit.PitEntry;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public interface ContentStore {

	public boolean insert(Data data, boolean isUnsolicited);

	public void erase(Name exactName);

	public void find(Face inFace, PitEntry pitEntry, Interest interest, SearchCsCallback searchCsCallback) throws Exception;
	
	public int limit();
	
	public void limit(int maxNumberOfDataPackets);
}
