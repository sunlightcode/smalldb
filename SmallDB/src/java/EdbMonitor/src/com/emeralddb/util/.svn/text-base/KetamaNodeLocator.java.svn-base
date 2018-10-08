package com.emeralddb.util;

import java.net.UnknownHostException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import com.emeralddb.net.ServerAddress;
public final class KetamaNodeLocator {
	private TreeMap<Long, ServerAddress> _ketamaNodes;
	private HashAlgorithm 		_hashAlg;
	private int					_numReps = 160;
	
	public KetamaNodeLocator(List<ServerAddress> nodeList, HashAlgorithm alg, int nodeCopies) {
		_hashAlg = alg;
		_ketamaNodes = new TreeMap<Long,ServerAddress>();
		
		_numReps = nodeCopies;
		
		for(ServerAddress node : nodeList) {
			for(int i=0; i<_numReps/4; i++) {
				byte[] digest = _hashAlg.md5(node.getHost() + ":" + node.getPort() + i);
				for(int h=0; h<4; h++) {
					long m = _hashAlg.hash(digest,h);
					_ketamaNodes.put(m, node);
				}
			}
		}
	}
	
	public ServerAddress getPrimary(final String str) {
		byte[] digest = _hashAlg.md5(str);
		ServerAddress rv = getNodeForKey(_hashAlg.hash(digest, 0));
		return rv;
	}
	
	ServerAddress getNodeForKey(long hash) {
		final ServerAddress rv;
		Long key = hash;
		if(!_ketamaNodes.containsKey(key)) {
			SortedMap<Long,ServerAddress> tailMap = _ketamaNodes.tailMap(key);
			if(tailMap.isEmpty()) {
				key = _ketamaNodes.firstKey();
			} else {
				key = tailMap.firstKey();
			}
		}
		
		rv = _ketamaNodes.get(key);
		return rv;
	}
}
