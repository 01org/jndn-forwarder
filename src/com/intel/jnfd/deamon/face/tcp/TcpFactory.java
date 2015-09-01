/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jndn.forwarder.api.Channel;
import com.intel.jndn.forwarder.api.Face;
import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jndn.forwarder.api.ProtocolFactory;
import com.intel.jndn.forwarder.api.callbacks.OnCompleted;
import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zht
 */
public abstract class TcpFactory implements ProtocolFactory {

    public TcpFactory(ExecutorService pool) {
        try {
            this.asynchronousChannelGroup
                    = AsynchronousChannelGroup.withThreadPool(pool);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Channel createChannelAndListen(FaceUri faceUri,
            OnCompleted<Channel> onChannelCreated,
            OnFailed onChannelCreationFailed,
            OnDataReceived onDataReceived,
            OnInterestReceived onInterestReceived) {
        TcpChannel channel = null;
        if (channelMap.containsKey(faceUri)) {
            channel = channelMap.get(faceUri);
            onChannelCreated.onCompleted(channelMap.get(faceUri));
        } else {
            try {
                channel = new TcpChannel(faceUri,
                        asynchronousChannelGroup, onInterestReceived,
                        onDataReceived);
                channelMap.put(faceUri, channel);
                channel.open(onChannelCreated, onChannelCreationFailed);
            } catch (IOException ex) {
                onChannelCreationFailed.onFailed(ex);
            }
        }
        return channel;
    }

    @Override
    public void destroyChannel(FaceUri faceUri,
            OnCompleted<Channel> onChannelDestroyed,
            OnFailed onChannelDestructionFailure) {
        Channel channel = channelMap.remove(faceUri);
        channel.close(onChannelDestroyed, onChannelDestructionFailure);
    }

    @Override
    public void createFace(FaceUri remoteFaceUri, OnCompleted<Face> onFaceCreated,
            OnFailed onFaceCreationFailed, OnDataReceived onDataReceived,
            OnInterestReceived onInterestReceived) {
        for (Map.Entry<FaceUri, TcpChannel> entry : channelMap.entrySet()) {
            if ((!entry.getKey().getIsV6()) && (!remoteFaceUri.getIsV6())
                    || entry.getKey().getIsV6() && remoteFaceUri.getIsV6()) {
                try {
                    entry.getValue().connect(remoteFaceUri, onFaceCreated,
                            onFaceCreationFailed);
                } catch (IOException | InterruptedException | ExecutionException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
                return;
            }
        }
        onFaceCreationFailed.onFailed(new IOException(
                "No channels available to connect to for " + remoteFaceUri));
    }

    @Override
    public void createFace(FaceUri localFaceUri, FaceUri remoteFaceUri,
            OnCompleted<Face> onFaceCreated,
            OnFailed onFaceCreationFailed, OnDataReceived onDataReceived,
            OnInterestReceived onInterestReceived) {
        TcpChannel channel = (TcpChannel) findChannel(localFaceUri);
        if (channel == null) {
            try {
                channel = new TcpChannel(localFaceUri,
                        asynchronousChannelGroup, onInterestReceived,
                        onDataReceived);
                channelMap.put(localFaceUri, channel);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        if (channel == null) {
            onFaceCreationFailed.onFailed(new Exception("No channel found or "
                    + "created for " + localFaceUri));
        }
        try {
            channel.connect(remoteFaceUri, onFaceCreated,
                    onFaceCreationFailed);
        } catch (IOException | InterruptedException | ExecutionException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void destroyFace(Face face, OnCompleted<Face> onFaceDestroyed,
            OnFailed onFaceDestructionFailed) {
        destroyFace(face.getLocalUri(), face.getRemoteUri(),
                onFaceDestroyed, onFaceDestructionFailed);
    }

    public void destroyFace(FaceUri localFaceUri, FaceUri remoteFaceUri,
            OnCompleted<Face> onFaceDestroyed,
            OnFailed onFaceDestructionFailed) {
        Channel channel = findChannel(localFaceUri);
        if (channel == null) {
            return;
        }
        channel.destroyFace(remoteFaceUri, onFaceDestroyed,
                onFaceDestructionFailed);
    }

    protected Channel findChannel(FaceUri uri) {
        return channelMap.get(uri);
    }

    @Override
    public Collection<? extends Channel> listChannels() {
        return channelMap.values();
    }

    @Override
    public Collection<? extends Face> listFaces() {
        Collection<Face> result = new HashSet<>();
        for (TcpChannel one : channelMap.values()) {
            result.addAll(one.listFaces());
        }
        return result;
    }

    public static final int DEFAULT_PORT = 6363;

    private static final Logger logger = Logger.getLogger(TcpFactory.class.getName());
    private final Map<FaceUri, TcpChannel> channelMap = new HashMap<>();
    private AsynchronousChannelGroup asynchronousChannelGroup = null;

	//    private Set<Node> prohibitedNodes = new HashSet<Node>();
    //    TODO: if the prohibition function is necessary, we can implement this funtion 
    //    in the future;
}
