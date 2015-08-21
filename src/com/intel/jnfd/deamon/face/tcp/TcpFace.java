/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jnfd.deamon.face.AbstractFace;
import com.intel.jnfd.deamon.face.FaceUri;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.encoding.ElementReader;
import net.named_data.jndn.util.Blob;
import net.named_data.jndn.util.Common;
import net.named_data.jndn.util.SignedBlob;

/**
 *
 * @author zht
 */
public class TcpFace extends AbstractFace {

    public TcpFace(FaceUri localUri, FaceUri remoteUri,
            AsynchronousSocketChannel asynchronousSocketChannel,
            boolean isLocal, boolean isMultiAccess) {
        super(localUri, remoteUri, isLocal, isMultiAccess);
        this.asynchronousSocketChannel = asynchronousSocketChannel;
        ReceiveAttachment attachment = new ReceiveAttachment();
        ReceiveHandler receiveHandler = new ReceiveHandler();
        this.asynchronousSocketChannel.read(inputBuffer, attachment, receiveHandler);
    }

    @Override
    public void sendInterest(Interest interest) {
        boolean wasQueueEmpty = sendQueue.isEmpty();
        sendQueue.add(interest.wireEncode());
        if (wasQueueEmpty) {
            sendFromQueue();
        }
    }
    
    /**
     * This method is created for test purpose. It should not be invoked by the 
     * formal method.
     * @param str 
     */
    public void send(String str) {
        boolean wasQueueEmpty = sendQueue.isEmpty();
        sendQueue.add(new Blob(str));
        if (wasQueueEmpty) {
            sendFromQueue();
        }
    }

    @Override
    public void sendData(Data data) {
        boolean wasQueueEmpty = sendQueue.isEmpty();
        sendQueue.add(data.wireEncode());
        if (wasQueueEmpty) {
            sendFromQueue();
        }
    }

    @Override
    public void close() {
        if (!asynchronousSocketChannel.isOpen()) {
            return;
        }
        try {
            asynchronousSocketChannel.close();
        } catch (IOException ex) {
            Logger.getLogger(TcpFace.class.getName()).log(Level.SEVERE, null, ex);
        }
        sendQueue.clear();
    }

    /**
     * Check the sendQueue and send data out.
     */
    protected void sendFromQueue() {
        SendAttachment sendAttachment = new SendAttachment();
        SendHandler sendHandler = new SendHandler();
        asynchronousSocketChannel.write(sendQueue.poll().buf(), sendAttachment, sendHandler);
    }

    protected AsynchronousSocketChannel asynchronousSocketChannel;
    private ByteBuffer inputBuffer = ByteBuffer.allocate(Common.MAX_NDN_PACKET_SIZE);
    private Queue<Blob> sendQueue = new ConcurrentLinkedQueue<>();
//    private Queue<SignedBlob> sendQueue = new ConcurrentLinkedQueue<>();

    private ElementReader elementReader;

    private class ReceiveAttachment {

    }

    /**
     * This class is used to handle the received data. The ReceiveAttachment is 
     * used to pass the parameters.
     */
    private class ReceiveHandler implements CompletionHandler<Integer, ReceiveAttachment> {

        @Override
        public void completed(Integer result, ReceiveAttachment attachment) {
            if(result == -1) {
//                System.out.println("stop listening");
                return;
            }
            System.out.println("receive someting");
            asynchronousSocketChannel.read(inputBuffer, attachment, this);
        }

        @Override
        public void failed(Throwable exc, ReceiveAttachment attachment) {
            //TODO: add actions in the future;
        }

    }

    private class SendAttachment {

    }

    /**
     * This class is used to handle the send data. The SendAttachment is 
     * used to pass the parameters.
     */
    private class SendHandler implements CompletionHandler<Integer, SendAttachment> {

        @Override
        public void completed(Integer result, SendAttachment attachment) {
            if (!sendQueue.isEmpty()) {
                asynchronousSocketChannel.write(inputBuffer, attachment, this);
            }
        }

        @Override
        public void failed(Throwable exc, SendAttachment attachment) {
            //TODO: add actions in the future;
        }

    }
}
