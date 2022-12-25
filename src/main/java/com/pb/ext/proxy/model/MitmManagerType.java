package com.pb.ext.proxy.model;

/*
 * Copied from original ...
 */

import java.io.File;

import javax.net.ssl.KeyManagerFactory;

import org.littleshoot.proxy.MitmManager;
import org.littleshoot.proxy.extras.SelfSignedMitmManager;
import org.littleshoot.proxy.extras.SelfSignedSslEngineSource;
import org.vividus.http.keystore.KeyStoreOptions;
import org.vividus.util.ResourceUtils;

import com.browserup.bup.mitm.KeyStoreFileCertificateSource;
import com.google.common.io.Resources;
import com.pb.ext.proxy.mitm.ImpersonatingMitmManager;
import com.pb.ext.proxy.mitm.MitmManagerOptions;

public enum MitmManagerType {
	
	IMPERSONATED
    {
        @Override
        public MitmManager buildMitmManager(MitmManagerOptions options, KeyStoreOptions keyStoreOptions)
        {
            //File keyStore = ResourceUtils.loadFile(getClass(), keyStoreOptions.getPath());
        	File keyStore = new File(Resources.getResource(keyStoreOptions.getPath()).getFile());
            KeyStoreFileCertificateSource certificateSource = new KeyStoreFileCertificateSource(
                    keyStoreOptions.getType(), keyStore, options.getAlias(), keyStoreOptions.getPassword());

            return ImpersonatingMitmManager
                    .builder()
                    .rootCertificateSource(certificateSource)
                    .keyManagerFactory(options.getKeyManagerFactory())
                    .trustAllServers(options.isTrustAllServers())
                    .build();
        }
    },
    SELF_SIGNED
    {
        @Override
        public MitmManager buildMitmManager(MitmManagerOptions options, KeyStoreOptions keyStoreOptions)
        {
            SelfSignedSslEngineSource sslEngineSource = new SelfSignedSslEngineSource(
                    keyStoreOptions.getPath(),
                    options.isTrustAllServers(),
                    true,
                    options.getAlias(),
                    keyStoreOptions.getPassword());

            return new SelfSignedMitmManager(sslEngineSource);
        }
    };

    public abstract MitmManager buildMitmManager(MitmManagerOptions options, KeyStoreOptions keyStoreOptions);

}
