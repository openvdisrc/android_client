package com.openvdi.remoteclient.models;

import java.util.Vector;

public class Pool {
	public String id = "";
	public boolean ready = false;
	public String name = "";
	public Vector<String> gateWays;
	
	public Pool() {
		gateWays = new Vector<String>();
	}
	
	public void addGateWay(String gate){
		gateWays.add(gate);
	}
}
