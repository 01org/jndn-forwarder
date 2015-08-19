/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon;

import java.io.IOException;
import java.nio.channels.Channel;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnData;
import net.named_data.jndn.encoding.EncodingException;

/**
 *
 * @author zht
 */
public class Main {
    public static void main(String[] args) throws IOException, EncodingException{
        Face face = new Face();
        face.expressInterest(new Interest(new Name("/localhost/nfd")), new OnData(){

            @Override
            public void onData(Interest interest, Data data) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
        });
        while(true){
            face.processEvents();
        }
    }
}
