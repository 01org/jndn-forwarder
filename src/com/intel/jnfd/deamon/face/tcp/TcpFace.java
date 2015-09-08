/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jndn.forwarder.api.callbacks.OnDataReceived;
import com.intel.jndn.forwarder.api.callbacks.OnInterestReceived;
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
import net.named_data.jndn.encoding.ElementListener;
import net.named_data.jndn.encoding.ElementReader;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.encoding.TlvWireFormat;
import net.named_data.jndn.encoding.tlv.Tlv;
import net.named_data.jndn.encoding.tlv.TlvDecoder;
import net.named_data.jndn.util.Blob;
import net.named_data.jndn.util.Common;

/**
 *
 * @author zht
 */
public class TcpFace extends AbstractFace {

    public TcpFace(FaceUri localUri, FaceUri remoteUri,
            AsynchronousSocketChannel asynchronousSocketChannel,
            boolean isLocal, boolean isMultiAccess,
            OnDataReceived onDataReceived,
            OnInterestReceived onInterestReceived) {
        super(localUri, remoteUri, isLocal, isMultiAccess);
        this.asynchronousSocketChannel = asynchronousSocketChannel;

        // callbacks
        this.onInterestReceived = onInterestReceived;
        this.onDataReceived = onDataReceived;
        this.elementReader = new ElementReader(new Deserializer(onDataReceived, onInterestReceived));

        
        ReceiveHandler receiveHandler = new ReceiveHandler();
        
        
        ReceiveAttachment attachment = new ReceiveAttachment();
        attachment.byteBuffer.limit(attachment.byteBuffer.capacity());
        attachment.byteBuffer.position(0);
        this.asynchronousSocketChannel.read(attachment.byteBuffer, attachment, receiveHandler);
    }

    @Override
    public void sendInterest(Interest interest) {
        logger.info("send interest");
        boolean wasQueueEmpty = sendQueue.isEmpty();
        // FIX: since the default wireformat will be null, so here a new 
        // TlvWireFormat is created.
        sendQueue.add(interest.wireEncode(new TlvWireFormat()));
        if (wasQueueEmpty) {
            sendFromQueue();
        }
    }

    /**
     * This method is created for test purpose. It should not be invoked by the
     * formal method.
     *
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
        logger.info("send data");
        boolean wasQueueEmpty = sendQueue.isEmpty();
        // FIX: since the default wireformat will be null, so here a new 
        // TlvWireFormat is created.
        boolean added = sendQueue.add(data.wireEncode(new TlvWireFormat()));
        if (added && wasQueueEmpty) {
            sendFromQueue();
        }
    }

    @Override
    public void close() throws IOException {
        if (!asynchronousSocketChannel.isOpen()) {
            return;
        }
        asynchronousSocketChannel.close();
        sendQueue.clear();
    }

    /**
     * Check the sendQueue and send data out.
     */
    protected void sendFromQueue() {
        logger.info("send from queue");
        asynchronousSocketChannel.write(sendQueue.poll().buf(), null, new SendHandler());
    }
    
     private class ReceiveAttachment {
         public ByteBuffer byteBuffer = ByteBuffer.allocate(Common.MAX_NDN_PACKET_SIZE);
     }

    /**
     * Receive data and split it into elements (e.g. Data, Interest) using the
     * ElementReader
     */
    private class ReceiveHandler implements CompletionHandler<Integer, ReceiveAttachment> {

        @Override
        public void completed(Integer result, ReceiveAttachment attachment) {
            if (result != -1) {
                System.out.println(attachment.byteBuffer);
                attachment.byteBuffer.flip();
                try {
                    logger.info("decode the packet");
                    elementReader.onReceivedData(attachment.byteBuffer);

                } catch (EncodingException ex) {
                    logger.log(Level.WARNING, "Failed to decode bytes on face.", ex);
                }
                ReceiveAttachment newAttachment = new ReceiveAttachment();
                newAttachment.byteBuffer.limit(newAttachment.byteBuffer.capacity());
                newAttachment.byteBuffer.position(0);
                asynchronousSocketChannel.read(newAttachment.byteBuffer, newAttachment, this);
            }
        }

        @Override
        public void failed(Throwable exc, ReceiveAttachment attachment) {
            logger.log(Level.INFO, "Failed to receive bytes on face.");
        }
    }

    /**
     * Parse bytes into Interest and Data packets
     */
    private class Deserializer implements ElementListener {

        private final OnDataReceived onDataReceived;
        private final OnInterestReceived onInterestReceived;

        public Deserializer(OnDataReceived onDataReceived, OnInterestReceived onInterestReceived) {
            this.onDataReceived = onDataReceived;
            this.onInterestReceived = onInterestReceived;
        }

        @Override
        public final void onReceivedElement(ByteBuffer element) throws EncodingException {
            logger.info("onReceivedElement is called");
            if (element.get(0) == Tlv.Interest || element.get(0) == Tlv.Data) {
                logger.info("receive Data or Interest packet");
                TlvDecoder decoder = new TlvDecoder(element);
                if (decoder.peekType(Tlv.Interest, element.remaining())) {
                    logger.info("receive Interest packet");
                    Interest interest = new Interest();
                    interest.wireDecode(element, TlvWireFormat.get());
                    onInterestReceived.onInterest(interest, TcpFace.this);

                } else if (decoder.peekType(Tlv.Data, element.remaining())) {
                    logger.info("receive Data packet");
                    Data data = new Data();
                    data.wireDecode(element, TlvWireFormat.get());
                    onDataReceived.onData(data, TcpFace.this);
                }
            }
        }

    }

    /**
     * This class is used to handle the send data. The Void is used to pass the
     * parameters.
     */
    private class SendHandler implements CompletionHandler<Integer, Void> {

        @Override
        public void completed(Integer result, Void attachment) {
            if (!sendQueue.isEmpty()) {
                System.out.println("send something");
                asynchronousSocketChannel.write(sendQueue.poll().buf(), attachment, this);
            }
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            //TODO: add actions in the future;
            logger.log(Level.INFO, "Failed to send bytes on face.");
        }

    }

    private static final Logger logger = Logger.getLogger(TcpFace.class.getName());
    protected AsynchronousSocketChannel asynchronousSocketChannel;
//    private final ByteBuffer inputBuffer = ByteBuffer.allocate(Common.MAX_NDN_PACKET_SIZE);
    private final Queue<Blob> sendQueue = new ConcurrentLinkedQueue<>();
    private final ElementReader elementReader;
    private final OnInterestReceived onInterestReceived;
    private final OnDataReceived onDataReceived;
}
