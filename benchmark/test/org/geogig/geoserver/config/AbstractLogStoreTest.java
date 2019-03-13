/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.config;


import Severity.ERROR;
import java.util.List;
import org.geoserver.platform.resource.ResourceStore;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


@Ignore
public class AbstractLogStoreTest {
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    protected ResourceStore resourceStore;

    protected LogStore logStore;

    protected String repoUrl = "file:/home/testuser/repos/myrepo";

    @Test
    public void testLogEntries() throws Exception {
        logStore.afterPropertiesSet();
        logStore.debug(repoUrl, "debug message");
        Throwable ex = new RuntimeException("test exception");
        ex.fillInStackTrace();
        logStore.error(repoUrl, "error message", ex);
        logStore.info(repoUrl, "info message");
        List<LogEvent> entries = logStore.getLogEntries(0, 10);
        Assert.assertNotNull(entries);
        Assert.assertEquals(3, entries.size());
        Assert.assertEquals(repoUrl, entries.get(0).getRepositoryURL());
        Assert.assertEquals(repoUrl, entries.get(1).getRepositoryURL());
        Assert.assertEquals(repoUrl, entries.get(2).getRepositoryURL());
        Assert.assertEquals("anonymous", entries.get(0).getUser());
        Assert.assertEquals("anonymous", entries.get(1).getUser());
        Assert.assertEquals("anonymous", entries.get(2).getUser());
    }

    @Test
    public void testLogEntriesOrderedByTimestampDecreasingOrder() throws Exception {
        logStore.afterPropertiesSet();
        logStore.debug(repoUrl, "debug message");
        Throwable ex = new RuntimeException("test exception");
        ex.fillInStackTrace();
        logStore.error(repoUrl, "error message", ex);
        logStore.info(repoUrl, "info message");
        List<LogEvent> entries = logStore.getLogEntries(0, 10);
        Assert.assertNotNull(entries);
        Assert.assertEquals(3, entries.size());
        Assert.assertEquals("info message", entries.get(0).getMessage());
        Assert.assertEquals("error message", entries.get(1).getMessage());
        Assert.assertEquals("debug message", entries.get(2).getMessage());
    }

    @Test
    public void testLogEntriesFilterBySeverity() throws Exception {
        logStore.afterPropertiesSet();
        Throwable ex = new RuntimeException("test exception");
        ex.fillInStackTrace();
        logStore.debug(repoUrl, "debug message 1");
        logStore.error(repoUrl, "error message 1", ex);
        logStore.info(repoUrl, "info message 1");
        logStore.debug(repoUrl, "debug message 2");
        logStore.error(repoUrl, "error message 2", ex);
        logStore.info(repoUrl, "info message 2");
        Assert.assertEquals(6, logStore.getLogEntries(0, 10, Severity.INFO, Severity.DEBUG, Severity.ERROR).size());
        Assert.assertEquals(4, logStore.getLogEntries(0, 10, Severity.INFO, Severity.DEBUG).size());
        Assert.assertEquals(4, logStore.getLogEntries(0, 10, Severity.INFO, Severity.ERROR).size());
        Assert.assertEquals(4, logStore.getLogEntries(0, 10, Severity.DEBUG, Severity.ERROR).size());
        Assert.assertEquals(2, logStore.getLogEntries(0, 10, Severity.INFO).size());
        Assert.assertEquals(2, logStore.getLogEntries(0, 10, Severity.ERROR).size());
        Assert.assertEquals(2, logStore.getLogEntries(0, 10, Severity.DEBUG).size());
    }

    @Test
    public void testLogEntriesOffsetLimit() throws Exception {
        logStore.afterPropertiesSet();
        Throwable ex = new RuntimeException("test exception");
        ex.fillInStackTrace();
        logStore.debug(repoUrl, "debug message 1");
        logStore.error(repoUrl, "error message 1", ex);
        logStore.info(repoUrl, "info message 1");
        logStore.debug(repoUrl, "debug message 2");
        logStore.error(repoUrl, "error message 2", ex);
        logStore.info(repoUrl, "info message 2");
        Assert.assertEquals(6, logStore.getLogEntries(0, 10).size());
        Assert.assertEquals(5, logStore.getLogEntries(0, 5).size());
        Assert.assertEquals(4, logStore.getLogEntries(2, 5).size());
        Assert.assertEquals(3, logStore.getLogEntries(2, 3).size());
        Assert.assertEquals(2, logStore.getLogEntries(0, 10, Severity.INFO).size());
        Assert.assertEquals(1, logStore.getLogEntries(0, 1, Severity.INFO, Severity.DEBUG).size());
        Assert.assertEquals(2, logStore.getLogEntries(2, 4, Severity.INFO, Severity.DEBUG).size());
        Assert.assertEquals(3, logStore.getLogEntries(0, 3, Severity.INFO, Severity.DEBUG).size());
    }

    @Test
    public void testGetStackTrace() throws Exception {
        logStore.afterPropertiesSet();
        Throwable ex = new RuntimeException("test exception");
        ex.fillInStackTrace();
        logStore.error(repoUrl, "error message 1", ex);
        LogEvent event = logStore.getLogEntries(0, 10).get(0);
        Assert.assertEquals(ERROR, event.getSeverity());
        long eventId = event.getEventId();
        String stackTrace = logStore.getStackTrace(eventId);
        Assert.assertNotNull(stackTrace);
        Assert.assertTrue(stackTrace, stackTrace.contains("test exception"));
        stackTrace = logStore.getStackTrace((eventId + 1));
        Assert.assertNull(stackTrace);
    }

    @Test
    public void testGetFullSize() throws Exception {
        logStore.afterPropertiesSet();
        Assert.assertEquals(0, logStore.getFullSize());
        logStore.debug(repoUrl, "debug message");
        Assert.assertEquals(1, logStore.getFullSize());
        Throwable ex = new RuntimeException("test exception");
        ex.fillInStackTrace();
        logStore.error(repoUrl, "error message", ex);
        Assert.assertEquals(2, logStore.getFullSize());
        logStore.info(repoUrl, "info message");
        Assert.assertEquals(3, logStore.getFullSize());
    }
}

