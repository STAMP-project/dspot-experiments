/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.security;


import Severity.DEBUG;
import Severity.ERROR;
import Severity.INFO;
import java.net.URI;
import java.util.List;
import org.geogig.geoserver.GeoGigTestData;
import org.geogig.geoserver.config.LogEvent;
import org.geogig.geoserver.config.LogStore;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.test.TestSetup;
import org.geoserver.test.TestSetupFrequency;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Document;


// SecurityLogger has been disabled in the plugin, ignore.
@Ignore
@TestSetup(run = TestSetupFrequency.REPEAT)
public class SecurityLoggerTestIntegrationTest extends GeoServerSystemTestSupport {
    /**
     * {@code /geogig/repos/<repoId>}
     */
    private String BASE_URL;

    @Rule
    public GeoGigTestData geogigData = new GeoGigTestData();

    private LogStore logStore;

    private URI repoURL;

    @Test
    public void testRemoteAdd() throws Exception {
        String remoteURL = "http://example.com/geogig/upstream";
        final String url = ((BASE_URL) + "/remote?remoteName=upstream&remoteURL=") + remoteURL;
        Document dom = getAsDOM(url);
        // <response><success>true</success><name>upstream</name></response>
        assertXpathEvaluatesTo("true", "/response/success", dom);
        List<LogEvent> entries = new java.util.ArrayList(logStore.getLogEntries(0, 10));
        Assert.assertEquals(entries.toString(), 2, entries.size());
        LogEvent first = entries.get(1);
        Assert.assertEquals(DEBUG, first.getSeverity());
        Assert.assertEquals("anonymous", first.getUser());
        Assert.assertEquals(repoURL.toString(), first.getRepositoryURL());
        Assert.assertTrue(first.getMessage(), first.getMessage().contains("Remote add:"));
        Assert.assertTrue(first.getMessage(), first.getMessage().contains("name='upstream'"));
        LogEvent second = entries.get(0);
        Assert.assertEquals(INFO, second.getSeverity());
        Assert.assertEquals("anonymous", second.getUser());
        Assert.assertEquals(repoURL.toString(), second.getRepositoryURL());
        Assert.assertTrue(first.getMessage(), second.getMessage().contains("Remote add success"));
        Assert.assertTrue(first.getMessage(), second.getMessage().contains("name='upstream'"));
    }

    @Test
    public void testRemoteAddExisting() throws Exception {
        String remoteURL = "http://example.com/geogig/upstream";
        final String url = ((BASE_URL) + "/remote?remoteName=upstream&remoteURL=") + remoteURL;
        Document dom = getAsDOM(url);
        assertXpathEvaluatesTo("true", "/response/success", dom);
        dom = getAsDOM(url);
        assertXpathEvaluatesTo("false", "/response/success", dom);
        List<LogEvent> entries = new java.util.ArrayList(logStore.getLogEntries(0, 10));
        Assert.assertTrue(entries.toString(), ((entries.size()) > 0));
        LogEvent last = entries.get(0);
        Assert.assertEquals(ERROR, last.getSeverity());
        Assert.assertEquals("anonymous", last.getUser());
        Assert.assertEquals(repoURL.toString(), last.getRepositoryURL());
        Assert.assertTrue(last.getMessage(), last.getMessage().contains("Remote add failed"));
        Assert.assertTrue(last.getMessage(), last.getMessage().contains("name='upstream'"));
        Assert.assertTrue(last.getMessage(), last.getMessage().contains("REMOTE_ALREADY_EXISTS"));
    }

    @Test
    public void testUserLogged() throws Exception {
        login();
        setRequestAuth("admin", "geoserver");
        String remoteURL = "http://example.com/geogig/upstream";
        final String url = ((BASE_URL) + "/remote?remoteName=upstream&remoteURL=") + remoteURL;
        Document dom = getAsDOM(url);
        print(dom);
        assertXpathEvaluatesTo("true", "/response/success", dom);
        List<LogEvent> entries = new java.util.ArrayList(logStore.getLogEntries(0, 10));
        Assert.assertTrue(((entries.size()) > 0));
        for (LogEvent e : entries) {
            Assert.assertEquals("admin", e.getUser());
        }
    }
}

