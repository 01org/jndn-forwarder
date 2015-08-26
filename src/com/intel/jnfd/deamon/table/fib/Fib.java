/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.fib;

import com.intel.jndn.forwarder.api.Face;
import com.intel.jnfd.deamon.table.HashMapRepo;
import com.intel.jnfd.deamon.table.Pair;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public class Fib {
    
    public int size() {
        return fib.size();
    }
    
    public FibEntry findLongestPrefixMatch(Name prefix) {
        return fib.findLongestPrefixMatch(prefix);
    }
    
    public FibEntry findExactMatch(Name prefix) {
        return fib.findExactMatch(prefix);
    }
    
    public Pair<FibEntry> insert(Name prefix) {
        FibEntry fibEntry = fib.findExactMatch(prefix);
        if (fibEntry != null) {
            return new Pair(fibEntry, false);
        }
        fibEntry = new FibEntry(prefix);
        fib.insert(prefix, fibEntry);
        return new Pair(fibEntry, true);
    }
    
    public void erase(Name prefix) {
        fib.erase(prefix);
    }
    
    public void erase(FibEntry entry) {
        fib.erase(entry.getPrefix());
    }
    
    public void removeNextHop(Name prefix, Face face) {
        FibEntry entry = fib.findExactMatch(prefix);
        if (entry == null) {
            return;
        }
        Vector<FibNextHop> nextHopList = entry.getNextHopList();
        if (nextHopList == null || nextHopList.isEmpty()) {
            fib.erase(prefix);
            return;
        }
        nextHopList.remove(face);
        if (nextHopList.isEmpty()) {
            fib.erase(prefix);
        }
    }
    
    public void removeNextHopFromAllEntries(Face face) {
        Set<Map.Entry<Name, FibEntry>> entrySet = fib.EntrySet();
        for (Map.Entry<Name, FibEntry> one : entrySet) {
            FibEntry fibEntry = one.getValue();
            if (fibEntry == null) {
                fib.erase(one.getKey());
                continue;
            }
            Vector<FibNextHop> nextHopList = fibEntry.getNextHopList();
            if (nextHopList == null || nextHopList.isEmpty()) {
                fib.erase(one.getKey());
                continue;
            }
            nextHopList.remove(face);
            if (nextHopList.isEmpty()) {
                fib.erase(one.getKey());
            }
        }
    }
    
    private HashMapRepo<FibEntry> fib = new HashMapRepo<>();
    
}
