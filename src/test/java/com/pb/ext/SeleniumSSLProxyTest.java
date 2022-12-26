package com.pb.ext;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import com.pb.ext.proxy.selenium.SeleniumProxy;

public class SeleniumSSLProxyTest {
	
	
	private static final String client_cert ="certificates/client-app.p12";
	private static final String client_pass_cert ="certificates/client-app.p12.pwd";
	
	private static final String root_cert ="/rootMitmCA.p12";
	private static final String root_pass_cert ="rootMitmCA.p12.pwd";
	
	private SeleniumProxy seleniumProxy;
	
	private WebDriver webdriver;

	@Before
	public void setUp() throws Exception {
		seleniumProxy = new SeleniumProxy(root_cert, root_pass_cert, client_cert, client_pass_cert);
		seleniumProxy.start();
		setupWebDriver();
	}

	@After
	public void tearDown() throws Exception {
		seleniumProxy.stop();
	}

	@Test
	public void test() {
		String url ="https://www.google.com";
		this.webdriver.get(url);
		this.webdriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}

	private void setupWebDriver() {
		System.setProperty("webdriver.edge.driver", "D:\\eclipse\\projects\\drivers\\msedgedriver.exe");
		EdgeOptions options = new EdgeOptions();
		if(seleniumProxy!=null) {
			options.setProxy(seleniumProxy);
		}
		
		this.webdriver = new EdgeDriver(options);
		
	}
	
	
}
