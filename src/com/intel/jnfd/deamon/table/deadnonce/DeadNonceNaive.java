/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.deadnonce;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public class DeadNonceNaive {

    public static long DEFAULT_LIFETIME = TimeUnit.SECONDS.toMillis(6);

    private Set<Entry> nonceCache
            = Collections.synchronizedSet(new LinkedHashSet<Entry>());

    public void add(Name name, int nonce) {
        nonceCache.add(new Entry(name, nonce));
    }

    public void evictStaleEntries() {
        Iterator<Entry> iterator = nonceCache.iterator();
        long currentTimeMillis = System.currentTimeMillis();
        while (iterator.hasNext()) {
            Entry next = iterator.next();
            if (next.getStaleTime() < currentTimeMillis) {
                nonceCache.remove(next);
                // Because we remove the last one, so we need to 
                // get the iterator again.
                iterator = nonceCache.iterator();
            } else {
                break;
            }
        }
    }

    public boolean find(Name name, int nonce) {
        evictStaleEntries();
        return nonceCache.contains(new Entry(name, nonce));
    }

    public int size() {
        return nonceCache.size();
    }

    private class Entry implements Comparable {

        public Entry(Name name, int nonce) {
            this.name = name;
            this.nonce = nonce;
            staleTime = System.currentTimeMillis() + DEFAULT_LIFETIME;
        }

        public Name getName() {
            return name;
        }

        public int getNonce() {
            return nonce;
        }

        public long getStaleTime() {
            return staleTime;
        }

        private Name name;
        private int nonce;
        private long staleTime;

        @Override
        public final int compareTo(Object o) {
            int nameCompare = name.compareTo(((Entry) o).name);
            if (nameCompare != 0) {
                return nameCompare;
            } else {
                if (nonce < ((Entry) o).nonce) {
                    return -1;
                }
                if (nonce > ((Entry) o).nonce) {
                    return 1;
                }
                return 0;
            }
        }

        @Override
        public final boolean equals(Object o) {
            return this.compareTo((Entry) o) == 0;
        }

    }

    public static void main(String[] args) {
        Set<Integer> test
                = Collections.synchronizedSet(new LinkedHashSet<Integer>());
        for (int i = 0; i <= 10; i++) {
            test.add(i);
        }
        Iterator<Integer> iterator = test.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            System.out.println(next);
            if (next < 5) {
                test.remove(next);
                iterator = test.iterator();
            }
        }
        iterator = test.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

}
