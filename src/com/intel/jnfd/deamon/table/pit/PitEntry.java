/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.pit;

import com.intel.jnfd.deamon.face.AbstractFace;
import java.util.List;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public class PitEntry {

    /**
     * indicates where duplicate Nonces are found
     */
    public static int DUPLICATE_NONCE_NONE = 0;
    /// in-record of same face
    public static int DUPLICATE_NONCE_IN_SAME = 1;
    /// in-record of other face
    public static int DUPLICATE_NONCE_IN_OTHER = 1 << 1;
    /// out-record of same face
    public static int DUPLICATE_NONCE_OUT_SAME = 1 << 2;
    /// out-record of other face
    public static int DUPLICATE_NONCE_OUT_OTHER = 1 << 3;

    public PitEntry(Interest interest) {
        this.interest = interest;
    }

    PitEntry() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Interest getInterest() {
        return interest;
    }

    /**
     * Interest Name
     *
     * @return
     */
    public Name getName() {
        return interest.getName();
    }

    /**
     * decides whether Interest can be forwarded to face
     *
     * @param face
     * @return true if OutRecord of this face does not exist or has expired, and
     * there is an InRecord not of this face, and scope is not violated
     */
    public boolean canForwardTo(AbstractFace face) {
        long currentTime = System.currentTimeMillis();
        boolean hasUnexpiredOutRecord = false;

        for (PitOutRecord one : outRecords) {
            if (face.equals(one.getFace()) && one.getExpiry() >= currentTime) {
                hasUnexpiredOutRecord = true;
                break;
            }
        }
        if (hasUnexpiredOutRecord) {
            return false;
        }

        boolean hasUnexpiredOtherInRecord = false;
        for (PitInRecord one : inRecords) {
            if (!face.equals(one.getFace())
                    && one.getExpiry() >= currentTime) {
                break;
            }
        }
        if (!hasUnexpiredOtherInRecord) {
            return false;
        }

        return !violatesScope(face);
    }

    /**
     * decides whether forwarding Interest to face would violate scope
     *
     * canForwardTo has more comprehensive checks (including scope control) and
     * should be used by most strategies. Outgoing Interest pipeline should only
     * check scope because some strategy (eg. vehicular) needs to retransmit
     * sooner than OutRecord expiry, or forward Interest back to incoming face
     *
     * @param face
     * @return true if scope control would be violated
     */
    public boolean violatesScope(AbstractFace face) {
        return false;
    }

    public int findNonce(long nonce, AbstractFace face) {
        // TODO should we ignore expired in/out records?

        int dnw = DUPLICATE_NONCE_NONE;

        for (PitInRecord inRecord : inRecords) {
            if (inRecord.getLastNonce() == nonce) {
                if (face.equals(inRecord.getFace())) {
                    dnw |= DUPLICATE_NONCE_IN_SAME;
                } else {
                    dnw |= DUPLICATE_NONCE_IN_OTHER;
                }
            }
        }

        for (PitOutRecord outRecord : outRecords) {
            if (outRecord.getLastNonce() == nonce) {
                if (face.equals(outRecord.getFace())) {
                    dnw |= DUPLICATE_NONCE_OUT_SAME;
                } else {
                    dnw |= DUPLICATE_NONCE_OUT_OTHER;
                }
            }
        }

        return dnw;
    }

    public List<PitInRecord> getInRecords() {
        return inRecords;
    }

    /**
     * TODO: in the c++ code, they return an iterator, I am not sure what we
     * should do here
     *
     * @param face
     * @param interest
     * @return
     */
    public PitInRecord insertOrUpdateInRecord(AbstractFace face, Interest interest) {
        PitInRecord result = null;
        for (PitInRecord one : inRecords) {
            if (face.equals(one.getFace())) {
                result = one;
                break;
            }
        }
        if (result == null) {
            result = new PitInRecord(face);
            inRecords.add(result);
        }
        result.update(interest);
        return result;
    }

    /**
     * TODO: in the c++ code, they return an iterator, I am not sure what we
     * should do here
     *
     * @param face
     * @return
     */
    public PitInRecord getInRecord(AbstractFace face) {
        for (PitInRecord one : inRecords) {
            if (face.equals(one.getFace())) {
                return one;
            }
        }
        return null;
    }

    public void deleteInRecords() {
        inRecords.clear();
    }

    public List<PitOutRecord> getOutRecords() {
        return outRecords;
    }

    /**
     * TODO: in the c++ code, they return an iterator, I am not sure what we
     * should do here
     *
     * @param face
     * @param interest
     * @return 
     */
    public PitOutRecord insertOrUpdateOutRecord(AbstractFace face, Interest interest) {
        PitOutRecord result = null;
        for (PitOutRecord one : outRecords) {
            if (face.equals(one.getFace())) {
                result = one;
                break;
            }
        }
        if (result == null) {
            result = new PitOutRecord(face);
            outRecords.add(result);
        }
        result.update(interest);
        return result;
    }

    /**
     * TODO: in the c++ code, they return an iterator, I am not sure what we
     * should do here
     *
     * @param face
     * @return
     */
    public PitOutRecord getOutRecord(AbstractFace face) {
        for (PitOutRecord one : outRecords) {
            if (face.equals(one.getFace())) {
                return one;
            }
        }
        return null;
    }

    public void deleteOutRecord(AbstractFace face) {
        for (PitOutRecord one : outRecords) {
            if (face.equals(one.getFace())) {
                outRecords.remove(one);
                return;
            }
        }
    }

    public boolean hasUnexpiredOutRecords() {
        long currentTime = System.currentTimeMillis();
        for (PitOutRecord one : outRecords) {
            if(one.getExpiry() >= currentTime) {
                return true;
            }
        }
        return false;
    }

    private Interest interest;
    private List<PitInRecord> inRecords;
    private List<PitOutRecord> outRecords;

    //TODO: these two are used for scop checking, but in the current code, we
    //haven't implemented that
    private static Name LOCALHOST_NAME;
    private static Name LOCALHOP_NAME;
}
