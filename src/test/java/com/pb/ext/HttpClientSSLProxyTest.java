package com.pb.ext;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.vividus.http.keystore.KeyStoreOptions;
import org.vividus.proxy.IProxyServerFactory;
import org.vividus.proxy.dns.HostNameResolver;
import org.vividus.util.ResourceUtils;

import com.browserup.bup.client.ClientUtil;
import com.browserup.bup.proxy.CaptureType;
import com.google.common.io.Resources;
import com.pb.ext.proxy.IProxy;
import com.pb.ext.proxy.Proxy;
import com.pb.ext.proxy.ProxyServerFactory;
import com.pb.ext.proxy.mitm.MitmManagerFactory;
import com.pb.ext.proxy.mitm.MitmManagerOptions;
import com.pb.ext.proxy.model.MitmManagerType;

public class HttpClientSSLProxyTest {
	
	
	/*
	 * Test Parameters
	 */
	
	private static final String client_cert ="certificates/client-app.p12";
	private static final String client_pass_cert ="certificates/client-app.p12.pwd";
	
	private static final String root_cert ="/rootMitmCA.p12";
	private static final String root_pass_cert ="rootMitmCA.p12.pwd";
	
	
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
	public void test() throws ClientProtocolException, IOException {
		HttpClientBuilder clientBuilder = HttpClients.custom();
		
		InetSocketAddress connectableAddressPort = new InetSocketAddress(ClientUtil.getConnectableAddress(), ((Proxy)proxy).getProxyServer().getPort());
		
		clientBuilder.setProxy(new HttpHost(connectableAddressPort.getAddress(), connectableAddressPort.getPort()));
		
		CloseableHttpClient httpClient = clientBuilder.build();
		
		HttpGet httpGet = new HttpGet("https://localhost:8443/server-app/data");
		
		//HttpGet httpGet = new HttpGet("https://www.google.com");
		
		HttpResponse response = httpClient.execute(httpGet);
		
		HttpEntity entity = response.getEntity();
		
		if(entity !=null) {
			String content = IOUtils.toString(entity.getContent(), "utf-8");
			System.out.println(content);
		}
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
		
		byte[] rootCAPassCertificate = Resources.asByteSource(Resources.getResource(root_pass_cert)).read();
		
		KeyStoreOptions keyStoreOptions = new KeyStoreOptions(root_cert, new String(rootCAPassCertificate), "PKCS12");
		
		MitmManagerOptions mitmManagerOptions = new MitmManagerOptions(MitmManagerType.IMPERSONATED, "inprabal_root_ca", true, keyStoreOptions,keyManagerFactory);
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
