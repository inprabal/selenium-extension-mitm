package com.pb.ext.proxy;

import org.openqa.selenium.Proxy;

import com.browserup.bup.BrowserUpProxy;
import com.browserup.bup.filters.RequestFilter;
import com.browserup.harreader.model.Har;

public interface IProxy {
	
	void start();

    void startRecording();

    /**
     * Retrieves the current HAR.
     *
     * @return current HAR, or null if proxy recording was not enabled
     */
    Har getRecordedData();

    void clearRecordedData();

    void stopRecording();

    void stop();

    boolean isStarted();

    void addRequestFilter(RequestFilter requestFilter);

    void clearRequestFilters();

    Proxy createSeleniumProxy();
	
	BrowserUpProxy getProxyServer();

}
