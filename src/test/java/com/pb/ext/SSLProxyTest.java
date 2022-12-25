package com.pb.ext;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.KeyManagerFactory;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.vividus.http.keystore.KeyStoreOptions;
import org.vividus.proxy.IProxy;
import org.vividus.proxy.IProxyServerFactory;
import org.vividus.proxy.Proxy;
import org.vividus.proxy.dns.HostNameResolver;
import org.vividus.util.ResourceUtils;

import com.browserup.bup.proxy.CaptureType;
import com.google.common.io.Resources;
import com.pb.ext.proxy.ProxyServerFactory;
import com.pb.ext.proxy.mitm.MitmManagerFactory;
import com.pb.ext.proxy.mitm.MitmManagerOptions;
import com.pb.ext.proxy.model.MitmManagerType;

public class SSLProxyTest {
	
	
	/*
	 * Test Parameters
	 */
	
	private static final String client_cert ="certificates/client-app.p12";
	private static final String client_pass_cert ="certificates/client-app.p12.pwd";
	
	private static final String root_cert ="rootCA.p12";
	private static final String root_pass_cert ="rootCA.p12.pwd";
	
	
	private IProxy proxy;

	@Before
	public void setUp() throws Exception {
		
		configureProxy();
		proxy.start();
	}

	@After
	public void tearDown() throws Exception {
		if(proxy.isStarted()) {
			proxy.stop();
		}
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	private void configureProxy() throws IOException {
		
		ProxyServerFactory proxyServerFactory = new ProxyServerFactory();
		Set<CaptureType> captureTypes = EnumSet.copyOf(CaptureType.getAllContentCaptureTypes());
		proxyServerFactory.setCaptureTypes(captureTypes);
		proxyServerFactory.setTrustAllServers(true);
		proxyServerFactory.setMitmEnabled(true);
		
		byte[] clientSslCertificate = Resources.asByteSource(Resources.getResource(client_cert)).read();
		byte[] clientSslPassCertificate = Resources.asByteSource(Resources.getResource(client_pass_cert)).read();
		
		KeyManagerFactory keyManagerFactory = createKeyManagerFactory(clientSslCertificate, clientSslPassCertificate);
		
		KeyStoreOptions keyStoreOptions = new KeyStoreOptions(root_cert, root_pass_cert, "PKCS12");
		
		MitmManagerOptions mitmManagerOptions = new MitmManagerOptions(MitmManagerType.IMPERSONATED, "root_ca", true, keyStoreOptions,keyManagerFactory);
		proxyServerFactory.setMitmManagerOptions(mitmManagerOptions);
		
		MitmManagerFactory  mitmManagerFactory = new MitmManagerFactory();		
		proxyServerFactory.setMitmManagerFactory(mitmManagerFactory);
		
		HostNameResolver hostNameResolver = new HostNameResolver();
		Map<String,String> dnsMappingStorage = new HashMap<>();
		hostNameResolver.setDnsMappingStorage(dnsMappingStorage);
		
		proxyServerFactory.setAdvancedHostResolver(hostNameResolver);
		
		this.proxy = new Proxy(proxyServerFactory,null);
		
		
	}
	
	private KeyManagerFactory createKeyManagerFactory(byte[] clientSslCertificate,byte[] clientSslPassCertificate) {
		
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(new ByteArrayInputStream(clientSslCertificate), new String(clientSslPassCertificate).toCharArray());
			
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, new String(clientSslPassCertificate).toCharArray());
			
			return keyManagerFactory;
			
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			throw new RuntimeException(ExceptionUtils.getStackTrace(e));
		}
	
	}

}
