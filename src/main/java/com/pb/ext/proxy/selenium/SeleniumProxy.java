package com.pb.ext.proxy.selenium;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
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
import org.vividus.http.keystore.KeyStoreOptions;
import org.vividus.proxy.dns.HostNameResolver;

import com.browserup.bup.client.ClientUtil;
import com.browserup.bup.proxy.CaptureType;
import com.google.common.io.Resources;
import com.pb.ext.proxy.IProxy;
import com.pb.ext.proxy.Proxy;
import com.pb.ext.proxy.ProxyServerFactory;
import com.pb.ext.proxy.mitm.MitmManagerFactory;
import com.pb.ext.proxy.mitm.MitmManagerOptions;
import com.pb.ext.proxy.model.MitmManagerType;

public class SeleniumProxy extends org.openqa.selenium.Proxy {

	private IProxy proxy;
	
	private String root_cert;
	
	private String root_pass_cert;
	
	private String client_cert;
	
	private String client_pass_cert;
	
	

	public SeleniumProxy(String root_cert, String root_pass_cert, String client_cert, String client_pass_cert) throws IOException {
		super();
		this.root_cert = root_cert;
		this.root_pass_cert = root_pass_cert;
		this.client_cert = client_cert;
		this.client_pass_cert = client_pass_cert;
		
		configureProxy();
	}
	
	public void start() {
		if(!proxy.isStarted()) {
			proxy.start();
		}
		InetSocketAddress connectableAddressPort = new InetSocketAddress(ClientUtil.getConnectableAddress(), ((Proxy)proxy).getProxyServer().getPort());
		String proxyStr= String.format("%s:%d", connectableAddressPort.getHostString(),connectableAddressPort.getPort());
		this.setHttpProxy(proxyStr);
		this.setSslProxy(proxyStr);
	}
	
	public void stop() {
		if(proxy!=null && proxy.isStarted()) {
			proxy.stop();
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

		MitmManagerOptions mitmManagerOptions = new MitmManagerOptions(MitmManagerType.IMPERSONATED, "inprabal_root_ca",
				true, keyStoreOptions, keyManagerFactory);
		proxyServerFactory.setMitmManagerOptions(mitmManagerOptions);

		MitmManagerFactory mitmManagerFactory = new MitmManagerFactory();
		proxyServerFactory.setMitmManagerFactory(mitmManagerFactory);

		HostNameResolver hostNameResolver = new HostNameResolver();
		Map<String, String> dnsMappingStorage = new HashMap<>();
		hostNameResolver.setDnsMappingStorage(dnsMappingStorage);

		proxyServerFactory.setAdvancedHostResolver(hostNameResolver);

		this.proxy = new Proxy(proxyServerFactory, null);

	}

	private KeyManagerFactory createKeyManagerFactory(byte[] clientSslCertificate, byte[] clientSslPassCertificate) {

		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(new ByteArrayInputStream(clientSslCertificate),
					new String(clientSslPassCertificate).toCharArray());

			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, new String(clientSslPassCertificate).toCharArray());

			return keyManagerFactory;

		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
				| UnrecoverableKeyException e) {
			throw new RuntimeException(ExceptionUtils.getStackTrace(e));
		}

	}

}
