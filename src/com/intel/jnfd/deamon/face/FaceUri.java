/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.face;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author zht
 */
public class FaceUri {

	public FaceUri() {
	}

	public FaceUri(String uri) throws ParseFaceUriException, UnknownHostException {
		Pattern pattern = Pattern.compile(faceUriRegex);
		Matcher matcher = pattern.matcher(uri);
		if (matcher.matches()) {
			scheme = matcher.group("Scheme");
			host = matcher.group("Host");
			inet = InetAddress.getByName(host);
			isV6 = inet instanceof Inet6Address;
			if (matcher.group("Port") != null) {
				port = Integer.parseInt(matcher.group("Port"));
			}
		} else {
			throw new ParseFaceUriException("The format of the provided face URI is wrong!");
		}
		if (!isConsistency()) {
			throw new ParseFaceUriException("The format of the provided face URI is wrong!");
		}
	}

	public FaceUri(String scheme, String name, int port) throws ParseFaceUriException, UnknownHostException {
		Pattern schemePattern = Pattern.compile(schemeRegex);
		Matcher schemeMatcher = schemePattern.matcher(scheme);
		if (schemeMatcher.matches()) {
			this.scheme = scheme;
		} else {
			throw new ParseFaceUriException("The format of the provided face URI is wrong!");
		}
		host = name;
		inet = InetAddress.getByName(host);
		isV6 = inet instanceof Inet6Address;
		if (65536 > port && 0 <= port) {
			this.port = port;
		} else {
			throw new ParseFaceUriException("The format of the provided face URI is wrong!");
		}
		if (!isConsistency()) {
			throw new ParseFaceUriException("The format of the provided face URI is wrong!");
		}
	}

	public FaceUri(InetSocketAddress inetSocketAddress, String scheme) throws ParseFaceUriException {
		port = inetSocketAddress.getPort();
		inet = inetSocketAddress.getAddress();
		host = inet.getHostAddress();
		isV6 = inet instanceof Inet6Address;
		this.scheme = scheme;
		if (!isConsistency()) {
			throw new ParseFaceUriException("The format of the provided face URI is wrong!");
		}
	}

	private boolean isConsistency() {
		if (isV6) {
			if (scheme.endsWith("4")) {
				return false;
			}
			if (!scheme.endsWith("6")) {
				scheme = scheme + "6";
			}
			return true;
		} else {
			if (scheme.endsWith("6")) {
				return false;
			}
			if (!scheme.endsWith("4")) {
				scheme = scheme + "4";
			}
			return true;
		}
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean getIsV6() {
		return isV6;
	}

	public void setIsV6(boolean isV6) {
		this.isV6 = isV6;
	}

	public InetAddress getInet() {
		return inet;
	}

	public void setInet(InetAddress inet) {
		this.inet = inet;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FaceUri) {
			FaceUri other = ((FaceUri) o);
			if (scheme.equals(other.getScheme())
					&& inet.equals(other.getInet())
					&& other.getPort() == port) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.scheme);
		hash = 37 * hash + Objects.hashCode(this.host);
		hash = 37 * hash + this.port;
		hash = 37 * hash + (this.isV6 ? 1 : 0);
		return hash;
	}

	@Override
	public String toString() {
		// TODO add IPv6 handling
		return String.format("%s://%s:%s", scheme, host, port);
	}

	private String scheme;
	private String host;
	private int port;
	private boolean isV6;
	private InetAddress inet;

	private static final String faceUriRegex = "(?<Scheme>(tcp|udp)[46]?):\\/\\/" //the scheme part
			// between this is the host part
			+ "(?<Host>localhost|" //local host
			+ "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)|" //ipv4 address
			+ "(\\[[\\dA-Fa-f:]*\\])|"
			+ "(([\\w-]+\\.)+\\w{2,6}))" //host name
			//
			+ "(:(?<Port>[0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))?"; //the port part
	private static final String schemeRegex = "(tcp|udp)[46]?";
}
