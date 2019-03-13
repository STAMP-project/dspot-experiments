/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.turbo;


import Status.INFO;
import Status.WARN;
import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.contention.AbstractMultiThreadedHarness;
import ch.qos.logback.core.contention.RunnableWithCounterAndDone;
import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.testUtil.StatusChecker;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class ReconfigureOnChangeTest {
    static final int THREAD_COUNT = 5;

    static final int LOOP_LEN = 1000 * 1000;

    int diff = RandomUtil.getPositiveInt();

    // the space in the file name mandated by
    // http://jira.qos.ch/browse/LBCORE-119
    static final String SCAN1_FILE_AS_STR = (ClassicTestConstants.INPUT_PREFIX) + "turbo/scan 1.xml";

    static final String G_SCAN1_FILE_AS_STR = (ClassicTestConstants.INPUT_PREFIX) + "turbo/scan 1.groovy";

    static final String SCAN_LOGBACK_474_FILE_AS_STR = (ClassicTestConstants.INPUT_PREFIX) + "turbo/scan_logback_474.xml";

    static final String INCLUSION_SCAN_TOPLEVEL0_AS_STR = (ClassicTestConstants.INPUT_PREFIX) + "turbo/inclusion/topLevel0.xml";

    static final String INCLUSION_SCAN_TOP_BY_RESOURCE_AS_STR = (ClassicTestConstants.INPUT_PREFIX) + "turbo/inclusion/topByResource.xml";

    static final String INCLUSION_SCAN_INNER0_AS_STR = (ClassicTestConstants.INPUT_PREFIX) + "turbo/inclusion/inner0.xml";

    static final String INCLUSION_SCAN_INNER1_AS_STR = "target/test-classes/asResource/inner1.xml";

    // it actually takes time for Windows to propagate file modification changes
    // values below 100 milliseconds can be problematic the same propagation
    // latency occurs in Linux but is even larger (>600 ms)
    // final static int DEFAULT_SLEEP_BETWEEN_UPDATES = 60;
    int sleepBetweenUpdates = 100;

    LoggerContext loggerContext = new LoggerContext();

    Logger logger = loggerContext.getLogger(this.getClass());

    ExecutorService executorService = loggerContext.getExecutorService();

    StatusChecker checker = new StatusChecker(loggerContext);

    AbstractMultiThreadedHarness harness;

    ThreadPoolExecutor executor = ((ThreadPoolExecutor) (loggerContext.getExecutorService()));

    int expectedResets = 2;

    // Tests whether ConfigurationAction is installing ReconfigureOnChangeFilter
    @Test
    public void installFilter() throws JoranException, IOException, InterruptedException {
        File file = new File(ReconfigureOnChangeTest.SCAN1_FILE_AS_STR);
        configure(file);
        List<File> fileList = getConfigurationFileList(loggerContext);
        assertThatListContainsFile(fileList, file);
        assertThatFirstFilterIsROCF();
        StatusPrinter.print(loggerContext);
    }

    @Test(timeout = 4000L)
    public void scanWithFileInclusion() throws JoranException, IOException, InterruptedException {
        File topLevelFile = new File(ReconfigureOnChangeTest.INCLUSION_SCAN_TOPLEVEL0_AS_STR);
        File innerFile = new File(ReconfigureOnChangeTest.INCLUSION_SCAN_INNER0_AS_STR);
        configure(topLevelFile);
        List<File> fileList = getConfigurationFileList(loggerContext);
        assertThatListContainsFile(fileList, topLevelFile);
        assertThatListContainsFile(fileList, innerFile);
    }

    @Test(timeout = 4000L)
    public void scanWithResourceInclusion() throws JoranException, IOException, InterruptedException {
        File topLevelFile = new File(ReconfigureOnChangeTest.INCLUSION_SCAN_TOP_BY_RESOURCE_AS_STR);
        File innerFile = new File(ReconfigureOnChangeTest.INCLUSION_SCAN_INNER1_AS_STR);
        configure(topLevelFile);
        List<File> fileList = getConfigurationFileList(loggerContext);
        assertThatListContainsFile(fileList, topLevelFile);
        assertThatListContainsFile(fileList, innerFile);
    }

    // See also http://jira.qos.ch/browse/LOGBACK-338
    @Test
    public void includeScanViaInputStreamSuppliedConfigFile() throws JoranException, IOException, InterruptedException {
        String configurationStr = "<configuration scan=\"true\" scanPeriod=\"50 millisecond\"><include resource=\"asResource/inner1.xml\"/></configuration>";
        configure(new ByteArrayInputStream(configurationStr.getBytes("UTF-8")));
        ConfigurationWatchList configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList(loggerContext);
        Assert.assertNull(configurationWatchList.getMainURL());
        ReconfigureOnChangeFilter reconfigureOnChangeFilter = ((ReconfigureOnChangeFilter) (getFirstTurboFilter()));
        // without a top level file, reconfigureOnChangeFilter should not start
        Assert.assertFalse(reconfigureOnChangeFilter.isStarted());
    }

    @Test(timeout = 4000L)
    public void fallbackToSafe() throws JoranException, IOException, InterruptedException {
        String path = (((CoreTestConstants.OUTPUT_DIR_PREFIX) + "reconfigureOnChangeConfig_fallbackToSafe-") + (diff)) + ".xml";
        File topLevelFile = new File(path);
        writeToFile(topLevelFile, "<configuration scan=\"true\" scanPeriod=\"50 millisecond\"><root level=\"ERROR\"/></configuration> ");
        configure(topLevelFile);
        writeToFile(topLevelFile, ("<configuration scan=\"true\" scanPeriod=\"50 millisecond\">\n" + "  <root></configuration>"));
        rocfDetachReconfigurationToNewThreadAndAwaitTermination();
        checker.assertContainsMatch(WARN, "Falling back to previously registered safe configuration.");
        checker.assertContainsMatch(INFO, "Re-registering previous fallback configuration once more");
        assertThatFirstFilterIsROCF();
    }

    @Test(timeout = 4000L)
    public void fallbackToSafeWithIncludedFile() throws JoranException, IOException, InterruptedException {
        String topLevelFileAsStr = (((CoreTestConstants.OUTPUT_DIR_PREFIX) + "reconfigureOnChangeConfig_top-") + (diff)) + ".xml";
        String innerFileAsStr = (((CoreTestConstants.OUTPUT_DIR_PREFIX) + "reconfigureOnChangeConfig_inner-") + (diff)) + ".xml";
        File topLevelFile = new File(topLevelFileAsStr);
        writeToFile(topLevelFile, (("<configuration scan=\"true\" scanPeriod=\"50 millisecond\"><include file=\"" + innerFileAsStr) + "\"/></configuration> "));
        File innerFile = new File(innerFileAsStr);
        writeToFile(innerFile, "<included><root level=\"ERROR\"/></included> ");
        configure(topLevelFile);
        writeToFile(innerFile, "<included>\n<root>\n</included>");
        rocfDetachReconfigurationToNewThreadAndAwaitTermination();
        checker.assertContainsMatch(WARN, "Falling back to previously registered safe configuration.");
        checker.assertContainsMatch(INFO, "Re-registering previous fallback configuration once more");
        assertThatFirstFilterIsROCF();
    }

    // check for deadlocks
    @Test(timeout = 4000L)
    public void scan_LOGBACK_474() throws JoranException, IOException, InterruptedException {
        File file = new File(ReconfigureOnChangeTest.SCAN_LOGBACK_474_FILE_AS_STR);
        configure(file);
        RunnableWithCounterAndDone[] runnableArray = buildRunnableArray(file, ReconfigureOnChangeTest.UpdateType.TOUCH);
        harness.execute(runnableArray);
        loggerContext.getStatusManager().add(new InfoStatus("end of execution ", this));
        verify(expectedResets);
    }

    enum UpdateType {

        TOUCH,
        MALFORMED,
        MALFORMED_INNER;}

    class Updater extends RunnableWithCounterAndDone {
        File configFile;

        ReconfigureOnChangeTest.UpdateType updateType;

        Updater(File configFile, ReconfigureOnChangeTest.UpdateType updateType) {
            this.configFile = configFile;
            this.updateType = updateType;
        }

        Updater(File configFile) {
            this(configFile, ReconfigureOnChangeTest.UpdateType.TOUCH);
        }

        public void run() {
            while (!(isDone())) {
                try {
                    Thread.sleep(sleepBetweenUpdates);
                } catch (InterruptedException e) {
                }
                if (isDone()) {
                    return;
                }
                (counter)++;
                ReconfigureOnChangeTest.this.addInfo("***settting last modified", this);
                switch (updateType) {
                    case TOUCH :
                        touchFile();
                        break;
                    case MALFORMED :
                        try {
                            malformedUpdate();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Assert.fail("malformedUpdate failed");
                        }
                        break;
                    case MALFORMED_INNER :
                        try {
                            malformedInnerUpdate();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Assert.fail("malformedInnerUpdate failed");
                        }
                }
            } 
        }

        private void malformedUpdate() throws IOException {
            writeToFile(configFile, ("<configuration scan=\"true\" scanPeriod=\"50 millisecond\">\n" + ("  <root level=\"ERROR\">\n" + "</configuration>")));
        }

        private void malformedInnerUpdate() throws IOException {
            writeToFile(configFile, ("<included>\n" + ("  <root>\n" + "</included>")));
        }

        void touchFile() {
            configFile.setLastModified(System.currentTimeMillis());
        }
    }
}

