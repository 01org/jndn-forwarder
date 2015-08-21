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
import com.intel.jndn.forwarder.api.callbacks.OnFailed;
import com.intel.jnfd.deamon.face.tcp.TcpChannel;
import com.intel.jnfd.deamon.face.tcp.TcpFactory;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author zht
 */
public class DefaultFaceManager implements FaceManager {

    public DefaultFaceManager(ExecutorService executorService){
        this.executorService = executorService;
		try {
			asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(executorService);
			tcpFactory = new TcpFactory(asynchronousChannelGroup);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
    }
    
    public DefaultFaceManager() {
        this(Executors.newCachedThreadPool());
    }

    public TcpFactory getTcpFactory() {
        return tcpFactory;
    }

    public void setTcpFactory(TcpFactory tcpFactory) {
        this.tcpFactory = tcpFactory;
    }

    private ExecutorService executorService;
    private AsynchronousChannelGroup asynchronousChannelGroup;
    private TcpFactory tcpFactory;
    
    public static void main(String[] args) throws IOException, 
            ParseFaceUriException, InterruptedException, ExecutionException {
        DefaultFaceManager faceManager = new DefaultFaceManager();
        TcpChannel tcpChannel = faceManager.getTcpFactory().CreateChannel("127.0.0.1", 6363);
        System.out.println("create channel");
        tcpChannel.open(null, null);
//        tcpChannel.connect(new FaceUri("tcp4://127.0.0.1:2000"), null, null);
//        Thread.sleep(5);
//        TcpFace tcpFace = tcpChannel.getFace("127.0.0.1", 2000);
//        if (tcpFace == null) {
//            System.out.println("connect to remote socket failed");
//        }
        while(true) {
//            tcpFace.send("hello");
            Thread.sleep(5);
        }
    }

	@Override
	public void registerProtocol(ProtocolFactory protocolFactory) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<ProtocolFactory> listProtocols() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Channel createChannel(FaceUri localUri) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Channel> listChannels() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Channel destroyChannel(FaceUri localUri) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Face> listFaces() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void createFace(FaceUri uri, OnCompleted<Face> onFaceCreated, OnFailed onFaceCreationFailed) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void destroyFace(FaceUri uri, OnCompleted<Face> onFaceDestroyed, OnFailed onFaceDestructionFailed) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
