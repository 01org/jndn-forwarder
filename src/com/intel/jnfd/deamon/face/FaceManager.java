/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face;

import com.intel.jnfd.deamon.face.tcp.TcpChannel;
import com.intel.jnfd.deamon.face.tcp.TcpFace;
import com.intel.jnfd.deamon.face.tcp.TcpFactory;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author zht
 */
public class FaceManager {

    public FaceManager(ExecutorService executorService) throws IOException {
        this.executorService = executorService;
        asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(executorService);
        tcpFactory = new TcpFactory(asynchronousChannelGroup);
    }
    
    public FaceManager() throws IOException {
        executorService = Executors.newCachedThreadPool();
        asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(executorService);
        tcpFactory = new TcpFactory(asynchronousChannelGroup);
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
        FaceManager faceManager = new FaceManager();
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
}
