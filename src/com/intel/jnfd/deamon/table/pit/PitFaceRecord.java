/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.pit;

import com.intel.jndn.forwarder.api.Face;
import com.intel.jnfd.deamon.table.strategy.StrategyInfoHost;
import com.intel.jnfd.util.NfdCommon;
import net.named_data.jndn.Interest;
import net.named_data.jndn.util.Blob;

/**
 *
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
 */
public class PitFaceRecord extends StrategyInfoHost {

    public PitFaceRecord(Face face) {
        this.face = face;
        lastNonce = new Blob();
        lastRenewed = 0;
        expiry = 0;
    }

    public Face getFace() {
        return face;
    }

    public Blob getLastNonce() {
        return lastNonce;
    }

    public long getLastRenewed() {
        return lastRenewed;
    }

    /**
     * gives the time point this record expires getLastRenewed() +
     * InterestLifetime
     *
     * @return
     */
    public long getExpiry() {
        return expiry;
    }

    /**
     * updates lastNonce, lastRenewed, expiry fields
     *
     * @param interest
     */
    public void update(Interest interest) {
        
        lastNonce = interest.getNonce();  
        lastRenewed = System.currentTimeMillis();
        long lifeTime = (long) (interest.getInterestLifetimeMilliseconds());
        if (lifeTime < 0) {
            lifeTime = NfdCommon.DEFAULT_INTEREST_LIFETIME;
        }
        expiry = lastRenewed + lifeTime;
    }

    private Face face;
    private Blob lastNonce;
    private long lastRenewed;
    private long expiry;
}
