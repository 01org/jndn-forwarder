/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.deadnonce;

import java.util.concurrent.TimeUnit;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public class DeadNonce {

    public static long DEFAULT_LIFETIME = TimeUnit.SECONDS.toMillis(6);
    public static long MIN_LIFETIME = TimeUnit.MILLISECONDS.toMillis(1);
    public static int INITIAL_CAPACITY = (1 << 7);
    public static int MIN_CAPACITY = (1 << 3);
    public static int MAX_CAPACITY = (1 << 24);
//const DeadNonceList::Entry DeadNonceList::MARK = 0;
    public static int EXPECTED_MARK_COUNT = 5;
    public static double CAPACITY_UP = 1.2;
    public static double CAPACITY_DOWN = 0.9;
    public static int EVICT_LIMIT = (1 << 6);

    /**
     * the unit of the lifetime is millisecond
     *
     * @param lifetime
     */
    public DeadNonce(long lifetime/*millisecond*/) {
        this.lifetime = lifetime;
        capacity = INITIAL_CAPACITY;
    }

    public boolean has(Name name, long nonce) {
        return false;

    }

    public void add(Name name, long nonce) {

    }

    public int size() {
        return 0;
    }

    public long getLifetime() {
        return lifetime;
    }

    private int countMarks() {
        return 0;

    }

    private void mark() {

    }

    private void adjustCapacity() {

    }

    private void evictEntries() {

    }

    private long makeEntry(Name name, long nonce) {
        return 0;
    }
    private long lifetime;
    private int capacity;
}
