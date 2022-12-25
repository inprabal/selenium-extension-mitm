package com.pb.ext.proxy.util;

import java.io.File;
import java.net.URI;
import java.time.ZonedDateTime;

import com.browserup.bup.mitm.CertificateInfo;
import com.browserup.bup.mitm.keys.KeyGenerator;
import com.browserup.bup.mitm.keys.RSAKeyGenerator;
import com.browserup.bup.mitm.tools.DefaultSecurityProviderTool;
import com.browserup.bup.mitm.tools.SecurityProviderTool;
import com.browserup.bup.mitm.util.MitmConstants;

public class RootCertificateGenerator {

	public static void main(String[] args) {
		File rootCAfile = new File(getResourcePath(args[0]) +"/rootCA.p12");

		CertificateInfo certificateInfo = new CertificateInfo().commonName("INPRABAL SSL Proxy Handler")
				.countryCode("IN")
				.organization("INPRABAL SSL Proxy Handler")
				.email("in.prabal@gmail.com")
				.notBefore(ZonedDateTime.now().minusYears(1).toInstant())
				.notAfter(ZonedDateTime.now().plusYears(5).toInstant());

		KeyGenerator keyGenerator = new RSAKeyGenerator();
		String messageDigest = MitmConstants.DEFAULT_MESSAGE_DIGEST;
		SecurityProviderTool securityProviderTool = new DefaultSecurityProviderTool();

		com.browserup.bup.mitm.RootCertificateGenerator rootCertificateGenerator = new com.browserup.bup.mitm.RootCertificateGenerator(
				certificateInfo, messageDigest, keyGenerator, securityProviderTool);

		
		rootCertificateGenerator.saveRootCertificateAndKey("PKCS12", rootCAfile, "inprabal_root_ca", "Change1t");
	}
	
	/**
	 * Reads the relative path to the resource directory from the <code>RESOURCE_PATH</code> file located in
	 * <code>src/main/resources</code>
	 * @return the relative path to the <code>resources</code> in the file system, or
	 *         <code>null</code> if there was an error
	 */
	private static String getResourcePath(String resourcePath) {
	    try {
	        //URI resourcePathFile = System.class.getResource("/RESOURCE_PATH").toURI();
	        //String resourcePath = Files.readAllLines(Paths.get(resourcePathFile)).get(0);
	        URI rootURI = new File("").toURI();
	        URI resourceURI = new File(resourcePath).toURI();
	        URI relativeResourceURI = rootURI.relativize(resourceURI);
	        return relativeResourceURI.getPath();
	    } catch (Exception e) {
	        return null;
	    }
	}

}
