/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.com.intel.jnfd;

import com.intel.jnfd.deamon.face.FaceUri;
import com.intel.jnfd.deamon.face.ParseFaceUriException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Haitao Zhang <zhtaoxiang@gmail.com>
 */
public class TestFaceUri {

	@Test
	public void testUriRegex() throws ParseFaceUriException, UnknownHostException {
		FaceUri faceUri = new FaceUri("udp4://192.168.1.1:35");
		assertEquals("udp4", faceUri.getScheme());
		assertEquals(InetAddress.getByName("192.168.1.1"), faceUri.getInet());
		assertEquals(35, faceUri.getPort());

		faceUri = new FaceUri("tcp6://[fe08::1]");
		assertEquals("tcp6", faceUri.getScheme());
		assertEquals(InetAddress.getByName("fe08::1"), faceUri.getInet());
		assertEquals(0, faceUri.getPort());

		InetAddress[] addressesByName = FaceUri.getAddressesByName("www.google.com");
		faceUri = new FaceUri("tcp4", addressesByName[0], 80);
		assertEquals("tcp4", faceUri.getScheme());
		assertEquals("www.google.com", addressesByName[0].getHostName());
		assertEquals(80, faceUri.getPort());
	}

}
