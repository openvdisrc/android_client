package com.openvdi.remoteclient.models;

public class OpenVdiGateway {
	public String ip = "";
	public String port = "";
	public String os = "";
	public String redirects = "";
	public String protocols = "";

	public OpenVdiGateway() {
	}

	public String toSimpleString() {
		return "{ip=" + ip + " port=" + port + " os=" + os + " redirects=" + redirects + " protocols=" + protocols + "}";
	}
}
