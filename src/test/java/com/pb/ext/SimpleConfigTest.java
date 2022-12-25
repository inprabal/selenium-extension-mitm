package com.pb.ext;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.test.SimpleTest;
import org.junit.Test;
import org.testng.Assert;
import org.testng.annotations.Guice;

import com.google.inject.Inject;

import ch.racic.testing.cm.ConfigEnvironment;
import ch.racic.testing.cm.ConfigProvider;
import ch.racic.testing.cm.annotation.ClassConfig;
import ch.racic.testing.cm.guice.ConfigModuleFactory;


@Guice(moduleFactory = ConfigModuleFactory.class)
@ClassConfig(fileName = "simple-client-cer.properties")
public class SimpleConfigTest {

	private static final Logger log = LogManager.getLogger(SimpleTest.class);
	
	@Inject
    ConfigProvider cfg;

	/*
	 * public ConfigProvider getCfg() throws IOException { ConfigEnvironment env =
	 * new ConfigEnvironment("Test environment 1",
	 * "Just for testing the config provider", "env1"); return new
	 * ConfigProvider(null, this.getClass()); }
	 */
    
    @Test
    public void simpleTest() throws Exception {
        //ConfigProvider cfg = getCfg();
        log.debug("Config loaded from: " + cfg.get("proxy.enabled"));
        Assert.assertEquals(cfg.get("proxy.enabled"), "false", "proxy.enabled is loaded from global folder"); 
        Assert.assertEquals(cfg.get("client.key-store.alias"), "simplet_test", "Client cert is loaded from global class folder");
    }

}
