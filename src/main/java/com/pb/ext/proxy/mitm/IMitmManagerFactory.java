package com.pb.ext.proxy.mitm;

import org.littleshoot.proxy.MitmManager;

public interface IMitmManagerFactory {
	
	MitmManager createMitmManager(MitmManagerOptions options);

}
