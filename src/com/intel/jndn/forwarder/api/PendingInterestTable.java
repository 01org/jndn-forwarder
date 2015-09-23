/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jnfd.deamon.table.Pair;
import com.intel.jnfd.deamon.table.pit.PitEntry;
import java.util.List;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public interface PendingInterestTable {

	public Pair<PitEntry> insert(Interest interest);

	public void erase(PitEntry pitEntry);

	public List<List<PitEntry>> findAllMatches(Data data);

	public List<PitEntry> findLongestPrefixMatches(Data data);

	public int size();
}
