/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face.tcp;

import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jnfd.deamon.face.ParseFaceUriException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author zht
 */
public class Tcp4Factory extends TcpFactory {

    public Tcp4Factory(ExecutorService pool) {
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

    public static final String SCHEME_NAME = "tcp4";
    public static final String DEFAULT_HOST = "0.0.0.0";
    private static FaceUri defaultUri;
}
