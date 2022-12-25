package com.pb.ext.proxy.mitm;

import org.apache.commons.lang3.Validate;
import org.littleshoot.proxy.MitmManager;
import org.vividus.http.keystore.KeyStoreOptions;

public class MitmManagerFactory implements IMitmManagerFactory {
	
	@Override
    public MitmManager createMitmManager(MitmManagerOptions options)
    {
        KeyStoreOptions keyStoreOptions = options.getKeyStoreOptions();
        checkNotNull(keyStoreOptions.getPath(), "key store path");
        checkNotNull(keyStoreOptions.getType(), "key store type");
        checkNotNull(keyStoreOptions.getPassword(), "key store password");
        checkNotNull(options.getAlias(), "alias");
        checkNotNull(options.getKeyManagerFactory(), "key manager factory");
      
        return options.getMitmManagerType().buildMitmManager(options, keyStoreOptions);
    }

    private void checkNotNull(Object value, String parameter)
    {
        Validate.isTrue(value != null, parameter + " parameter must be set");
    }

}
