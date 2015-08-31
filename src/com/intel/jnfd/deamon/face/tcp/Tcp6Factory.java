/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jndn.forwarder.api.Channel;
import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jnfd.deamon.face.ParseFaceUriException;
import static com.intel.jnfd.deamon.face.tcp.TcpFactory.DEFAULT_PORT;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author zht
 */
public class Tcp6Factory extends TcpFactory {

    public Tcp6Factory(ExecutorService pool) {
        super(pool);
    }

    @Override
    public String scheme() {
        return SCHEME_NAME;
    }

    @Override
    public FaceUri defaultLocalUri() {
        if (defaultUri == null) {
            try {
                defaultUri = new FaceUri(SCHEME_NAME, DEFAULT_HOST, DEFAULT_PORT);
            } catch (ParseFaceUriException | UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        }
        return defaultUri;
    }

    public static final String SCHEME_NAME = "tcp6";
    public static final String DEFAULT_HOST = "::";
    private static FaceUri defaultUri;


}
