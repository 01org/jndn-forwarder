/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.fw;

import com.intel.jnfd.deamon.face.AbstractFace;
import java.util.Map;

/**
 *
 * @author zht
 */
public class FaceTable {

    /// indicates an invalid FaceId
    public static final int INVALID_FACEID = -1;
    /// identifies the InternalFace used in management
    public static final int FACEID_INTERNAL_FACE = 1;
    /// identifies a packet comes from the ContentStore, in LocalControlHeader incomingFaceId
    public static final int FACEID_CONTENT_STORE = 254;
    /// identifies the NullFace that drops every packet
    public static final int FACEID_NULL = 255;
    /// upper bound of reserved FaceIds
    public static final int FACEID_RESERVED_MAX = 255;

    public FaceTable(Forwarder forwarder) {
        this.forwarder = forwarder;
        lastFaceId = FACEID_RESERVED_MAX;
    }

    //TODO: question, where does the initial faceId come from?
    public void add(AbstractFace face) {
        if (face.getFaceId() != INVALID_FACEID && faces.get(face.getFaceId()) != null) {
            return;
        }
        int faceId = ++this.lastFaceId;
        if (faceId < FACEID_RESERVED_MAX) {
            return;
        }
        addImp(face, faceId);
    }

    public void addImp(AbstractFace face, int faceId) {
        face.setFaceId(faceId);
        faces.put(faceId, face);

    }

    // add a special AbstractFace with a reserved FaceId
    public void addReserved(AbstractFace face, int faceId) {
        if (face.getFaceId() != INVALID_FACEID 
                || faces.get(face.getFaceId()) != null
                || faceId > FACEID_RESERVED_MAX) {
            return;
        }
        addImp(face, faceId);
    }

    public AbstractFace get(int faceId) {
        return faces.get(faceId);
    }

    public int size() {
        return faces.size();
    }

    // remove is private because it's a handler of face.onFail signal.
    // face->close() closes the face and triggers .remove()
    private void remove(AbstractFace face, String reason) {

        int faceId = face.getFaceId();
        faces.remove(faceId);
        face.setFaceId(INVALID_FACEID);
        
        forwarder.getFib().removeNextHopFromAllEntries(face);
    }

    private Forwarder forwarder;
    private int lastFaceId;
    private Map<Integer, AbstractFace> faces;
}
