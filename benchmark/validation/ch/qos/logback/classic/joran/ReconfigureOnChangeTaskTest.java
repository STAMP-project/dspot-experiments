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
package ch.qos.logback.classic.joran;


import Status.INFO;
import Status.WARN;
import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.CoreConstants;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import org.junit.Assert;
import org.junit.Test;


public class ReconfigureOnChangeTaskTest {
    static final int THREAD_COUNT = 5;

    int diff = RandomUtil.getPositiveInt();

    // the space in the file name mandated by
    // http://jira.qos.ch/browse/LBCORE-119
    static final String SCAN1_FILE_AS_STR = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "roct/scan 1.xml";

    static final String G_SCAN1_FILE_AS_STR = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "roct/scan 1.groovy";

    static final String SCAN_LOGBACK_474_FILE_AS_STR = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "roct/scan_logback_474.xml";

    static final String INCLUSION_SCAN_TOPLEVEL0_AS_STR = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "roct/inclusion/topLevel0.xml";

    static final String INCLUSION_SCAN_TOP_BY_RESOURCE_AS_STR = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "roct/inclusion/topByResource.xml";

    static final String INCLUSION_SCAN_INNER0_AS_STR = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "roct/inclusion/inner0.xml";

    static final String INCLUSION_SCAN_INNER1_AS_STR = "target/test-classes/asResource/inner1.xml";

    private static final String SCAN_PERIOD_DEFAULT_FILE_AS_STR = (ClassicTestConstants.JORAN_INPUT_PREFIX) + "roct/scan_period_default.xml";

    LoggerContext loggerContext = new LoggerContext();

    Logger logger = loggerContext.getLogger(this.getClass());

    StatusChecker statusChecker = new StatusChecker(loggerContext);

    // void gConfigure(File file) throws JoranException {
    // GafferConfigurator gc = new GafferConfigurator(loggerContext);
    // gc.run(file);
    // }
    @Test(timeout = 4000L)
    public void checkBasicLifecyle() throws JoranException, IOException, InterruptedException {
        File file = new File(ReconfigureOnChangeTaskTest.SCAN1_FILE_AS_STR);
        configure(file);
        List<File> fileList = getConfigurationWatchList(loggerContext);
        assertThatListContainsFile(fileList, file);
        checkThatTaskHasRan();
        checkThatTaskCanBeStopped();
    }

    @Test(timeout = 4000L)
    public void scanWithFileInclusion() throws JoranException, IOException, InterruptedException {
        File topLevelFile = new File(ReconfigureOnChangeTaskTest.INCLUSION_SCAN_TOPLEVEL0_AS_STR);
        File innerFile = new File(ReconfigureOnChangeTaskTest.INCLUSION_SCAN_INNER0_AS_STR);
        configure(topLevelFile);
        List<File> fileList = getConfigurationWatchList(loggerContext);
        assertThatListContainsFile(fileList, topLevelFile);
        assertThatListContainsFile(fileList, innerFile);
        checkThatTaskHasRan();
        checkThatTaskCanBeStopped();
    }

    @Test(timeout = 4000L)
    public void scanWithResourceInclusion() throws JoranException, IOException, InterruptedException {
        File topLevelFile = new File(ReconfigureOnChangeTaskTest.INCLUSION_SCAN_TOP_BY_RESOURCE_AS_STR);
        File innerFile = new File(ReconfigureOnChangeTaskTest.INCLUSION_SCAN_INNER1_AS_STR);
        configure(topLevelFile);
        List<File> fileList = getConfigurationWatchList(loggerContext);
        assertThatListContainsFile(fileList, topLevelFile);
        assertThatListContainsFile(fileList, innerFile);
    }

    // See also http://jira.qos.ch/browse/LOGBACK-338
    @Test(timeout = 4000L)
    public void reconfigurationIsNotPossibleInTheAbsenceOfATopFile() throws JoranException, IOException, InterruptedException {
        String configurationStr = "<configuration scan=\"true\" scanPeriod=\"50 millisecond\"><include resource=\"asResource/inner1.xml\"/></configuration>";
        configure(new ByteArrayInputStream(configurationStr.getBytes("UTF-8")));
        ConfigurationWatchList configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList(loggerContext);
        Assert.assertNull(configurationWatchList);
        // assertNull(configurationWatchList.getMainURL());
        statusChecker.containsMatch(WARN, "Due to missing top level");
        StatusPrinter.print(loggerContext);
        ReconfigureOnChangeTask roct = getRegisteredReconfigureTask();
        Assert.assertNull(roct);
        Assert.assertEquals(0, loggerContext.getScheduledFutures().size());
    }

    @Test(timeout = 3000L)
    public void fallbackToSafe_FollowedByRecovery() throws JoranException, IOException, InterruptedException {
        String path = (((CoreTestConstants.OUTPUT_DIR_PREFIX) + "reconfigureOnChangeConfig_fallbackToSafe-") + (diff)) + ".xml";
        File topLevelFile = new File(path);
        writeToFile(topLevelFile, "<configuration scan=\"true\" scanPeriod=\"5 millisecond\"><root level=\"ERROR\"/></configuration> ");
        configure(topLevelFile);
        CountDownLatch changeDetectedLatch = waitForReconfigurationToBeDone(null);
        ReconfigureOnChangeTask oldRoct = getRegisteredReconfigureTask();
        Assert.assertNotNull(oldRoct);
        writeToFile(topLevelFile, ("<configuration scan=\"true\" scanPeriod=\"5 millisecond\">\n" + "  <root></configuration>"));
        changeDetectedLatch.await();
        statusChecker.assertContainsMatch(WARN, ReconfigureOnChangeTask.FALLING_BACK_TO_SAFE_CONFIGURATION);
        statusChecker.assertContainsMatch(INFO, ReconfigureOnChangeTask.RE_REGISTERING_PREVIOUS_SAFE_CONFIGURATION);
        loggerContext.getStatusManager().clear();
        CountDownLatch secondDoneLatch = waitForReconfigurationToBeDone(oldRoct);
        writeToFile(topLevelFile, "<configuration scan=\"true\" scanPeriod=\"5 millisecond\"><root level=\"ERROR\"/></configuration> ");
        secondDoneLatch.await();
        StatusPrinter.print(loggerContext);
        statusChecker.assertIsErrorFree();
        statusChecker.containsMatch(ReconfigureOnChangeTask.DETECTED_CHANGE_IN_CONFIGURATION_FILES);
    }

    @Test(timeout = 4000L)
    public void fallbackToSafeWithIncludedFile_FollowedByRecovery() throws JoranException, IOException, InterruptedException, ExecutionException {
        String topLevelFileAsStr = (((CoreTestConstants.OUTPUT_DIR_PREFIX) + "reconfigureOnChangeConfig_top-") + (diff)) + ".xml";
        String innerFileAsStr = (((CoreTestConstants.OUTPUT_DIR_PREFIX) + "reconfigureOnChangeConfig_inner-") + (diff)) + ".xml";
        File topLevelFile = new File(topLevelFileAsStr);
        writeToFile(topLevelFile, (("<configuration xdebug=\"true\" scan=\"true\" scanPeriod=\"5 millisecond\"><include file=\"" + innerFileAsStr) + "\"/></configuration> "));
        File innerFile = new File(innerFileAsStr);
        writeToFile(innerFile, "<included><root level=\"ERROR\"/></included> ");
        configure(topLevelFile);
        CountDownLatch doneLatch = waitForReconfigurationToBeDone(null);
        ReconfigureOnChangeTask oldRoct = getRegisteredReconfigureTask();
        Assert.assertNotNull(oldRoct);
        writeToFile(innerFile, "<included>\n<root>\n</included>");
        doneLatch.await();
        statusChecker.assertContainsMatch(WARN, ReconfigureOnChangeTask.FALLING_BACK_TO_SAFE_CONFIGURATION);
        statusChecker.assertContainsMatch(INFO, ReconfigureOnChangeTask.RE_REGISTERING_PREVIOUS_SAFE_CONFIGURATION);
        loggerContext.getStatusManager().clear();
        CountDownLatch secondDoneLatch = waitForReconfigurationToBeDone(oldRoct);
        writeToFile(innerFile, "<included><root level=\"ERROR\"/></included> ");
        secondDoneLatch.await();
        StatusPrinter.print(loggerContext);
        statusChecker.assertIsErrorFree();
        statusChecker.containsMatch(ReconfigureOnChangeTask.DETECTED_CHANGE_IN_CONFIGURATION_FILES);
    }

    class RunMethodInvokedListener extends ReconfigureOnChangeTaskListener {
        CountDownLatch countDownLatch;

        RunMethodInvokedListener(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void enteredRunMethod() {
            countDownLatch.countDown();
        }
    }

    class ChangeDetectedListener extends ReconfigureOnChangeTaskListener {
        CountDownLatch countDownLatch;

        ChangeDetectedListener(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void changeDetected() {
            countDownLatch.countDown();
        }
    }

    class ReconfigurationDoneListener extends ReconfigureOnChangeTaskListener {
        CountDownLatch countDownLatch;

        ReconfigurationDoneListener(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void doneReconfiguring() {
            countDownLatch.countDown();
        }
    }

    @Test
    public void checkReconfigureTaskScheduledWhenDefaultScanPeriodUsed() throws JoranException {
        File file = new File(ReconfigureOnChangeTaskTest.SCAN_PERIOD_DEFAULT_FILE_AS_STR);
        configure(file);
        final List<ScheduledFuture<?>> scheduledFutures = loggerContext.getScheduledFutures();
        StatusPrinter.print(loggerContext);
        Assert.assertFalse(scheduledFutures.isEmpty());
        statusChecker.containsMatch("No 'scanPeriod' specified. Defaulting to");
    }

    // check for deadlocks
    @Test(timeout = 4000L)
    public void scan_LOGBACK_474() throws JoranException, IOException, InterruptedException {
        loggerContext.setName("scan_LOGBACK_474");
        File file = new File(ReconfigureOnChangeTaskTest.SCAN_LOGBACK_474_FILE_AS_STR);
        // StatusListenerConfigHelper.addOnConsoleListenerInstance(loggerContext, new OnConsoleStatusListener());
        configure(file);
        // ReconfigureOnChangeTask roct = waitForReconfigureOnChangeTaskToRun();
        int expectedResets = 2;
        ReconfigureOnChangeTaskTest.Harness harness = new ReconfigureOnChangeTaskTest.Harness(expectedResets);
        RunnableWithCounterAndDone[] runnableArray = buildRunnableArray(file, ReconfigureOnChangeTaskTest.UpdateType.TOUCH);
        harness.execute(runnableArray);
        loggerContext.getStatusManager().add(new InfoStatus("end of execution ", this));
        StatusPrinter.print(loggerContext);
        checkResetCount(expectedResets);
    }

    enum UpdateType {

        TOUCH,
        MALFORMED,
        MALFORMED_INNER;}

    class Harness extends AbstractMultiThreadedHarness {
        int changeCountLimit;

        Harness(int changeCount) {
            this.changeCountLimit = changeCount;
        }

        public void waitUntilEndCondition() throws InterruptedException {
            ReconfigureOnChangeTaskTest.this.addInfo((("Entering " + (this.getClass())) + ".waitUntilEndCondition()"), this);
            int changeCount = 0;
            ReconfigureOnChangeTask lastRoct = null;
            CountDownLatch countDownLatch = null;
            while (changeCount < (changeCountLimit)) {
                ReconfigureOnChangeTask roct = ((ReconfigureOnChangeTask) (loggerContext.getObject(CoreConstants.RECONFIGURE_ON_CHANGE_TASK)));
                if ((lastRoct != roct) && (roct != null)) {
                    lastRoct = roct;
                    countDownLatch = new CountDownLatch(1);
                    roct.addListener(new ReconfigureOnChangeTaskTest.ChangeDetectedListener(countDownLatch));
                } else
                    if (countDownLatch != null) {
                        countDownLatch.await();
                        countDownLatch = null;
                        changeCount++;
                    }

                Thread.yield();
            } 
            ReconfigureOnChangeTaskTest.this.addInfo((("*****Exiting " + (this.getClass())) + ".waitUntilEndCondition()"), this);
        }
    }

    class Updater extends RunnableWithCounterAndDone {
        File configFile;

        ReconfigureOnChangeTaskTest.UpdateType updateType;

        // it actually takes time for Windows to propagate file modification changes
        // values below 100 milliseconds can be problematic the same propagation
        // latency occurs in Linux but is even larger (>600 ms)
        // final static int DEFAULT_SLEEP_BETWEEN_UPDATES = 60;
        int sleepBetweenUpdates = 100;

        Updater(File configFile, ReconfigureOnChangeTaskTest.UpdateType updateType) {
            this.configFile = configFile;
            this.updateType = updateType;
        }

        Updater(File configFile) {
            this(configFile, ReconfigureOnChangeTaskTest.UpdateType.TOUCH);
        }

        public void run() {
            while (!(isDone())) {
                try {
                    Thread.sleep(sleepBetweenUpdates);
                } catch (InterruptedException e) {
                }
                if (isDone()) {
                    ReconfigureOnChangeTaskTest.this.addInfo("Exiting Updater.run()", this);
                    return;
                }
                (counter)++;
                ReconfigureOnChangeTaskTest.this.addInfo((("Touching [" + (configFile)) + "]"), this);
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
            ReconfigureOnChangeTaskTest.this.addInfo("Exiting Updater.run()", this);
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

