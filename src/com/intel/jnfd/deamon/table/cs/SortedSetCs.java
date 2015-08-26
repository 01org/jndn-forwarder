/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.cs;

import com.intel.jnfd.deamon.face.Face;
import com.intel.jnfd.deamon.table.pit.PitEntry;
import com.intel.jnfd.util.NameUtil;
import java.util.Iterator;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public class SortedSetCs extends Cs {

    public SortedSetCs(int nMaxPackets) {
        setLimit(nMaxPackets);
    }

    @Override
    public boolean insert(Data data, boolean isUnsolicited) {
        // TODO: recognize CachingPolicy
        // this part should be added after the jNDN library add
        // "LocalControlHeader" attribute
        CsEntry csEntry = new CsEntry(data, isUnsolicited);
        dataCache.put(data.getName(), csEntry);
        return true;
    }

    @Override
    public void find(Face inFace, PitEntry pitEntry, 
            Interest interest, SearchCsCallback searchCsCallback) throws Exception {
        Name prefix = interest.getName();
        boolean isRightMost = (interest.getChildSelector() == 1);
        CsEntry match = null;
        if (isRightMost) {
            match = findRightMost(interest, prefix, NameUtil.getNameSuccessor(prefix));
        } else {
            match = findLeftMost(interest, prefix, NameUtil.getNameSuccessor(prefix));
        }
        if (match == null) {
            searchCsCallback.onContentStoreMiss(inFace, pitEntry, interest);
            return;
        }
        searchCsCallback.onContentStoreHit(inFace, pitEntry, interest, 
                match.getData());
    }

    @Override
    public void erase(Name exactName) {
        dataCache.remove(exactName);
    }

    private CsEntry findRightMost(Interest interest, Name first, Name last) throws Exception {
        // Each loop visits a sub-namespace under a prefix one component longer than Interest Name.
        // If there is a match in that sub-namespace, the leftmost match is returned;
        // otherwise, loop continues.
        int interestNameLength = interest.getName().size();

        for (Name right = last; !right.equals(first);) {
            Name prev = dataCache.lowerKey(right);

            // special case: [first,prev] have exact Names
            if (prev.size() == interestNameLength) {
                return findRightmostAmongExact(interest, first, right);
            }

            Name prefix = prev.getPrefix(interestNameLength + 1);
            Name left = dataCache.ceilingKey(prefix);
            // normal case: [left,right) are under one-component-longer prefix
            CsEntry match = findLeftMost(interest, left, right);
            if (match != null) {
                return match;
            }

            right = left;
        }
        return null;
    }

    private CsEntry findRightmostAmongExact(Interest interest, Name first, Name last) throws Exception {
        ConcurrentNavigableMap<Name, CsEntry> subMap
                = dataCache.subMap(first, true, last, false);
        if (subMap == null) {
            return null;
        }
        Iterator<Name> descendingIterator = subMap.keySet().descendingIterator();
        while (descendingIterator.hasNext()) {
            Name dataName = descendingIterator.next();
            if (dataCache.get(dataName).canSatisfy(interest)) {
                return dataCache.get(dataName);
            }
        }
        return null;
    }

    private CsEntry findLeftMost(Interest interest, Name first, Name last)
            throws Exception {
        ConcurrentNavigableMap<Name, CsEntry> subMap
                = dataCache.subMap(first, true, last, false);
        if (subMap == null) {
            return null;
        }
        Iterator<Name> iterator = subMap.keySet().iterator();
        while (iterator.hasNext()) {
            Name dataName = iterator.next();
            if (dataCache.get(dataName).canSatisfy(interest)) {
                return dataCache.get(dataName);
            }
        }
        return null;
    }

    private final ConcurrentSkipListMap<Name, CsEntry> dataCache = new ConcurrentSkipListMap<>();

    public static void main(String[] args) {
        //Write code to test cs
    }
}
