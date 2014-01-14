package com.openvdi.remoteclient.activities;

import java.util.ArrayList;
import java.util.List;

import com.openvdi.remoteclient.models.*;

public class GlobalData {
	static public String userName = "";
	static public String password = "";
	static public String userId = "";
	static public String joinDomain = "";
	static public String DOMAIN = "";
	static public String pDal = "";
	static public String requestId = "";
	
	static public void clear() {
		userName = "";
		password = "";
		joinDomain = "";
		userId = "";
		DOMAIN = "";
		pDal = "";
		
		if(DOMAIN_LIST != null){
			DOMAIN_LIST.clear();
		}
		if(POOL_LIST != null){
			POOL_LIST.clear();
		}
	}

	static final public List<Server> DOMAIN_LIST = new ArrayList<Server>();

	static public void addServer(Server server) {
		DOMAIN_LIST.add(server);
	}

	static public List<Pool> POOL_LIST;

	static public void addPool(Pool pool) {
		if (POOL_LIST == null) {
			POOL_LIST = new ArrayList<Pool>();
		}
		POOL_LIST.add(pool);
	}
}
