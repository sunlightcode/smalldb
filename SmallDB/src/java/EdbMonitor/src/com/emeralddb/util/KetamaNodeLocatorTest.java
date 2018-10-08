package com.emeralddb.util;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.emeralddb.net.ServerAddress;

public class KetamaNodeLocatorTest {
	static Random ran = new Random();

	private static final Integer EXE_TIMES = 100000;
	private static final Integer NODE_COUNT = 5;
	private static final Integer VIRTUAL_NODE_COUNT = 160;
	
	public static void main(String [] args) {
		KetamaNodeLocatorTest  test = new KetamaNodeLocatorTest();
		
		Map<ServerAddress,Integer> nodeRecord = new HashMap<ServerAddress, Integer>();
		
		List<ServerAddress> all = test.getServerAddress(NODE_COUNT);
		List<String> allKeys  = test.getAllString();
		KetamaNodeLocator locator = new KetamaNodeLocator(all, HashAlgorithm.KETAMA_HASH,VIRTUAL_NODE_COUNT );
		
		for(String key : allKeys) {
			ServerAddress sa = locator.getPrimary(key);
			Integer times = nodeRecord.get(sa);
			if(times == null) {
				nodeRecord.put(sa, 1);
			} else {
				nodeRecord.put(sa, times+1);
			}
		}
		
		System.out.println("Nodes count: " + NODE_COUNT + ", Keys count: " + EXE_TIMES + ", Normal percent : " + (float)100/NODE_COUNT + "%");
		System.out.println("-------------------------------boundary--------");
		for(Map.Entry<ServerAddress, Integer> entry : nodeRecord.entrySet()) {
			System.out.println("Node name:" + entry.getKey().getPort() + "-Times:" + entry.getValue() + " - Percent: " + (float)entry.getValue()/EXE_TIMES*100 + "%");
		}
	}
	
	private List<String> getAllString() {
		List<String> allStrings = new ArrayList<String>(EXE_TIMES);
		for(int i=0; i<EXE_TIMES; i++) {
			allStrings.add(gennerateRandomString(ran.nextInt(50)));
		}
		return allStrings;
	}
	
	private String gennerateRandomString(int length) {
		StringBuffer sb = new StringBuffer(length);
		for(int i=0; i<length; i++) {
			sb.append((char)(ran.nextInt(95) + 32));
		}
		return sb.toString();
	}
	
	private List<ServerAddress> getServerAddress(int nodeCount) {
		try {
			List<ServerAddress> nodes = new ArrayList<ServerAddress>();
			ServerAddress sa = new ServerAddress("192.168.20.107", 48123);
			nodes.add(sa);
			sa = new ServerAddress("192.168.20.107", 48124);
			nodes.add(sa);
			sa = new ServerAddress("192.168.20.107", 48125);
			nodes.add(sa);
			sa = new ServerAddress("192.168.20.107", 48126);
			nodes.add(sa);
			sa = new ServerAddress("192.168.20.107", 48127);
			nodes.add(sa);
			return nodes;
		} catch(UnknownHostException e ) {
			return null;
		}
	}
}
