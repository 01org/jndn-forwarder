/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face;

import com.intel.jndn.forwarder.api.Channel;
import com.intel.jndn.forwarder.api.Face;
import com.intel.jndn.forwarder.api.FaceManager;
import com.intel.jndn.forwarder.api.ProtocolFactory;
import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author zht
 */
public class DefaultFaceManager implements FaceManager {

    public DefaultFaceManager(ExecutorService pool) {
        this.pool = pool;
    }

    public DefaultFaceManager() {
        this(Executors.newCachedThreadPool());
    }

    @Override
    public void registerProtocol(ProtocolFactory protocolFactory) {
        if (!protocols.containsKey(protocolFactory.scheme())) {
            protocols.put(protocolFactory.scheme(), protocolFactory);
        }
    }

    @Override
    public Collection<ProtocolFactory> listProtocols() {
        return protocols.values();
    }

    @Override
    public Collection<String> listProtocolNames() {
        return protocols.keySet();
    }

    public ProtocolFactory findProtocol(String scheme) {
        if (!protocols.containsKey(scheme)) {
            throw new IllegalArgumentException("Unknown protocol scheme: " + scheme);
        } else {
            return protocols.get(scheme);
        }
    }

    @Override
    public void createChannelAndListen(FaceUri localUri, OnCompleted<Channel> onChannelCreated,
            OnFailed onChannelCreationFailed, OnDataReceived onDataReceived,
            OnInterestReceived onInterestReceived) {
        ProtocolFactory protocol = findProtocol(localUri.getScheme());
        if (protocol == null) {
            onChannelCreationFailed.onFailed(new Exception("No factory found "
                    + "for " + localUri.getScheme()));
        }
        //Create the channel instance.
        Channel channel = protocol.createChannel(localUri, new OnCompleted<Channel>() {
            @Override
            public void onCompleted(Channel result) {
                // TODO 
            }
        }, onChannelCreationFailed, onDataReceived, onInterestReceived);
        // start to listen on the channel
        channel.open(onChannelCreated, onChannelCreationFailed);
    }

    @Override
    public void destroyChannel(FaceUri localUri,
            OnCompleted<Channel> onChannelCreated,
            OnFailed onChannelCreationFailed) {
        ProtocolFactory protocol = protocols.get(localUri.getScheme());
        if (protocol == null) {
            return;
        }
        protocol.destroyChannel(localUri, onChannelCreated,
                onChannelCreationFailed);
    }

    @Override
    public Collection<? extends Channel> listChannels() {
        Collection<Channel> result = new HashSet<>();
        for (ProtocolFactory one : protocols.values()) {
            result.addAll(one.listChannels());
        }
        return result;
    }

    @Override
    public Collection<? extends Channel> listChannels(String scheme) {
        if (!protocols.containsKey(scheme)) {
            return null;
        }
        return protocols.get(scheme).listChannels();
    }

    @Override
    public void createFaceAndConnect(FaceUri remoteUri,
            OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed,
            OnDataReceived onDataReceived, OnInterestReceived onInterestReceived) {
        ProtocolFactory protocol = protocols.get(remoteUri.getScheme());
        if (protocol == null) {
            onFaceCreationFailed.onFailed(new Exception("No such scheme found"
                    + remoteUri.getScheme()));
        }
        protocol.createFace(remoteUri, onFaceCreated,
                onFaceCreationFailed, onDataReceived, onInterestReceived);
    }

    @Override
    public void createFaceAndConnect(FaceUri localUri, FaceUri remoteUri,
            OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed,
            OnDataReceived onDataReceived, OnInterestReceived onInterestReceived) {
        ProtocolFactory protocol = protocols.get(localUri.getScheme());
        if (protocol == null) {
            onFaceCreationFailed.onFailed(new Exception("No such scheme found"
                    + localUri.getScheme()));
        }
        protocol.createFace(localUri, remoteUri, onFaceCreated,
                onFaceCreationFailed, onDataReceived, onInterestReceived);
    }

    @Override
    public void destroyFace(Face face, OnCompleted<Face> onFaceDestroyed,
            OnFailed onFaceDestructionFailed) {
        ProtocolFactory protocol = protocols.get(face.getLocalUri().getScheme());
        if (protocol == null) {
            onFaceDestructionFailed.onFailed(new Exception("No such scheme found "
                    + face.getLocalUri().getScheme()));
        }
        protocol.destroyFace(face, onFaceDestroyed, onFaceDestructionFailed);
    }

    @Override
    public void destroyFace(FaceUri localFaceUri, FaceUri remoteFaceUri, 
            OnCompleted<Face> onFaceDestroyed,
            OnFailed onFaceDestructionFailed) {
        ProtocolFactory protocol = protocols.get(localFaceUri.getScheme());
        if (protocol == null) {
            onFaceDestructionFailed.onFailed(new Exception("No such face found "
                    + localFaceUri.getScheme()));
        }
        protocol.destroyFace(localFaceUri, remoteFaceUri, 
                onFaceDestroyed, onFaceDestructionFailed);
    }

    @Override
    public Collection<? extends Face> listFaces() {
        Collection<Face> result = new HashSet<>();
        for (ProtocolFactory one : protocols.values()) {
            result.addAll(one.listFaces());
        }
        return result;
    }

    @Override
    public Collection<? extends Face> listFaces(String scheme) {
        if (!protocols.containsKey(scheme)) {
            return null;
        }
        return protocols.get(scheme).listFaces();
    }

    private Map<String, ProtocolFactory> protocols = new HashMap<>();
    private ExecutorService pool;
}
