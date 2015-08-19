/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.pit;

import com.intel.jnfd.deamon.face.Face;
import com.intel.jnfd.deamon.table.strategy.StrategyInfoHost;
import com.intel.jnfd.util.NfdCommon;
import net.named_data.jndn.Interest;

/**
 *
 * @author zht
 */
public class PitFaceRecord extends StrategyInfoHost {

    public PitFaceRecord(Face face) {
        this.face = face;
        lastNonce = 0;
        lastRenewed = 0;
        expiry = 0;
    }

    public Face getFace() {
        return face;
    }

    public long getLastNonce() {
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
        
        lastNonceStr = interest.getNonce().toString();
        
        // TODO: this is a guess, I don't know if it is right or not
        byte[] immutableArray = interest.getNonce().getImmutableArray();
        int size = interest.getNonce().size();
        lastNonce = 0;
        for (int i = 0; i < size; i ++) {
            lastNonce = 256 * lastNonce + immutableArray[i];
        }
        
        lastRenewed = System.currentTimeMillis();
        long lifeTime = (long) (interest.getInterestLifetimeMilliseconds());
        if (lifeTime < 0) {
            lifeTime = NfdCommon.DEFAULT_INTEREST_LIFETIME;
        }
        expiry = lastRenewed + lifeTime;
    }

    private String lastNonceStr; //This is another way of recording nonce.
    private Face face;
    private long lastNonce;
    private long lastRenewed;
    private long expiry;
}
