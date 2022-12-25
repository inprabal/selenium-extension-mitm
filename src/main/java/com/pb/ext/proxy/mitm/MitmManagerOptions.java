package com.pb.ext.proxy.mitm;

import javax.net.ssl.KeyManagerFactory;

import org.vividus.http.keystore.KeyStoreOptions;

import com.pb.ext.proxy.model.MitmManagerType;

public class MitmManagerOptions
{
    private final MitmManagerType mitmManagerType;
    private final String alias;
    private final boolean trustAllServers;
    private final KeyStoreOptions keyStoreOptions;
    private final KeyManagerFactory keyManagerFactory;

    public MitmManagerOptions(MitmManagerType mitmManagerType, String alias, boolean trustAllServers,
            KeyStoreOptions keyStoreOptions,KeyManagerFactory keyManagerFactory)
    {
        this.mitmManagerType = mitmManagerType;
        this.alias = alias;
        this.trustAllServers = trustAllServers;
        this.keyStoreOptions = keyStoreOptions;
        this.keyManagerFactory= keyManagerFactory;
    }

    public MitmManagerType getMitmManagerType()
    {
        return mitmManagerType;
    }

    public String getAlias()
    {
        return alias;
    }

    public boolean isTrustAllServers()
    {
        return trustAllServers;
    }

    public KeyStoreOptions getKeyStoreOptions()
    {
        return keyStoreOptions;
    }

	public KeyManagerFactory getKeyManagerFactory() {
		return keyManagerFactory;
	}
    
    
}
