/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.com.intel.jnfd;

import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jnfd.deamon.face.ParseFaceUriException;
import java.net.UnknownHostException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author zht
 */
public class TestFaceUri {
    @Test
    public void testUriRegex() throws ParseFaceUriException, UnknownHostException {
        FaceUri faceUri = new FaceUri("udp4://192.168.1.1:35");
        assertEquals("udp4", faceUri.getScheme());
        assertEquals("192.168.1.1", faceUri.getHost());
        assertEquals("35", faceUri.getPort());
        
        faceUri = new FaceUri("tcp6://[fe08::1]");
        assertEquals("tcp6", faceUri.getScheme());
        assertEquals("[fe08::1]", faceUri.getHost());
        assertEquals(null, faceUri.getPort());
        
        faceUri = new FaceUri("tcp4://www.google.com:80");
        assertEquals("tcp4", faceUri.getScheme());
        assertEquals("www.google.com", faceUri.getHost());
        assertEquals("80", faceUri.getPort());
        // Since google has so many ipv4 address, we simply print the address 
        // out to see if it is valid
//        System.out.println(faceUri.getInet().getHostAddress());
    }
    
}
