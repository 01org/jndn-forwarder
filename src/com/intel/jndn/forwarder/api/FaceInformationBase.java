/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jndn.forwarder.api;

import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import com.intel.jnfd.deamon.table.Pair;
import com.intel.jnfd.deamon.table.fib.FibEntry;
import java.util.Collection;
import net.named_data.jndn.Name;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public interface FaceInformationBase extends OnInterestReceived, OnDataReceived {

    public Pair<FibEntry> insert(Name prefix, Face face, int cost);

    public FibEntry remove(Name prefix);

    public Collection<FibEntry> list();

    public FibEntry findLongestPrefixMatch(Name prefix);

    public FibEntry findExactMatch(Name prefix);

    public void removeNextHopFromAllEntries(Face face);
}
