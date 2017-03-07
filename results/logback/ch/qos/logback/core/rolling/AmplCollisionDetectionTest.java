

package ch.qos.logback.core.rolling;


public class AmplCollisionDetectionTest {
    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    ch.qos.logback.core.status.StatusChecker statusChecker = new ch.qos.logback.core.status.StatusChecker(context);

    int diff = ch.qos.logback.core.testUtil.RandomUtil.getPositiveInt();

    protected java.lang.String randomOutputDir = ((ch.qos.logback.core.util.CoreTestConstants.OUTPUT_DIR_PREFIX) + (diff)) + "/";

    @org.junit.Before
    public void setUp() throws java.lang.Exception {
    }

    @org.junit.After
    public void tearDown() throws java.lang.Exception {
    }

    ch.qos.logback.core.FileAppender<java.lang.String> buildFileAppender(java.lang.String name, java.lang.String filenameSuffix) {
        ch.qos.logback.core.FileAppender<java.lang.String> fileAppender = new ch.qos.logback.core.FileAppender<java.lang.String>();
        fileAppender.setName(name);
        fileAppender.setContext(context);
        fileAppender.setFile(((randomOutputDir) + filenameSuffix));
        fileAppender.setEncoder(new ch.qos.logback.core.encoder.NopEncoder<java.lang.String>());
        return fileAppender;
    }

    ch.qos.logback.core.rolling.RollingFileAppender<java.lang.String> buildRollingFileAppender(java.lang.String name, java.lang.String filenameSuffix, java.lang.String patternSuffix) {
        ch.qos.logback.core.rolling.RollingFileAppender<java.lang.String> rollingFileAppender = new ch.qos.logback.core.rolling.RollingFileAppender<java.lang.String>();
        rollingFileAppender.setName(name);
        rollingFileAppender.setContext(context);
        rollingFileAppender.setFile(((randomOutputDir) + filenameSuffix));
        rollingFileAppender.setEncoder(new ch.qos.logback.core.encoder.NopEncoder<java.lang.String>());
        ch.qos.logback.core.rolling.TimeBasedRollingPolicy<java.lang.String> tbrp = new ch.qos.logback.core.rolling.TimeBasedRollingPolicy<java.lang.String>();
        tbrp.setContext(context);
        tbrp.setFileNamePattern(((randomOutputDir) + patternSuffix));
        tbrp.setParent(rollingFileAppender);
        // tbrp.timeBasedFileNamingAndTriggeringPolicy = new DefaultTimeBasedFileNamingAndTriggeringPolicy<Object>();
        // tbrp.timeBasedFileNamingAndTriggeringPolicy.setCurrentTime(givenTime);
        rollingFileAppender.setRollingPolicy(tbrp);
        tbrp.start();
        return rollingFileAppender;
    }

    @org.junit.Test
    public void collisionImpossibleForSingleAppender() {
        ch.qos.logback.core.FileAppender<java.lang.String> fileAppender = buildFileAppender("FA", "collisionImpossibleForSingleAppender");
        fileAppender.start();
        statusChecker.assertIsErrorFree();
    }

    @org.junit.Test
    public void appenderStopShouldClearEntryInCollisionMap() {
        java.lang.String key = "FA";
        ch.qos.logback.core.FileAppender<java.lang.String> fileAppender = buildFileAppender(key, "collisionImpossibleForSingleAppender");
        fileAppender.start();
        assertCollisionMapHasEntry(ch.qos.logback.core.CoreConstants.FA_FILENAME_COLLISION_MAP, key);
        fileAppender.stop();
        assertCollisionMapHasNoEntry(ch.qos.logback.core.CoreConstants.FA_FILENAME_COLLISION_MAP, key);
        statusChecker.assertIsErrorFree();
    }

    private void assertCollisionMapHasEntry(java.lang.String mapName, java.lang.String key) {
        @java.lang.SuppressWarnings(value = "unchecked")
        java.util.Map<java.lang.String, ?> map = ((java.util.Map<java.lang.String, ?>) (context.getObject(mapName)));
        org.junit.Assert.assertNotNull(map);
        org.junit.Assert.assertNotNull(map.get(key));
    }

    private void assertCollisionMapHasNoEntry(java.lang.String mapName, java.lang.String key) {
        @java.lang.SuppressWarnings(value = "unchecked")
        java.util.Map<java.lang.String, ?> map = ((java.util.Map<java.lang.String, ?>) (context.getObject(mapName)));
        org.junit.Assert.assertNotNull(map);
        org.junit.Assert.assertNull(map.get(key));
    }

    @org.junit.Test
    public void collisionWithTwoFileAppenders() {
        java.lang.String suffix = "collisionWithToFileAppenders";
        ch.qos.logback.core.FileAppender<java.lang.String> fileAppender1 = buildFileAppender("FA1", suffix);
        fileAppender1.start();
        ch.qos.logback.core.FileAppender<java.lang.String> fileAppender2 = buildFileAppender("FA2", suffix);
        fileAppender2.start();
        statusChecker.assertContainsMatch(ch.qos.logback.core.status.Status.ERROR, "'File' option has the same value");
        // StatusPrinter.print(context);
    }

    @org.junit.Test
    public void collisionWith_FA_RFA() {
        java.lang.String suffix = "collisionWith_FA_RFA";
        ch.qos.logback.core.FileAppender<java.lang.String> fileAppender1 = buildFileAppender("FA", suffix);
        fileAppender1.start();
        ch.qos.logback.core.rolling.RollingFileAppender<java.lang.String> rollingfileAppender = buildRollingFileAppender("RFA", suffix, "bla-%d.log");
        rollingfileAppender.start();
        ch.qos.logback.core.util.StatusPrinter.print(context);
        statusChecker.assertContainsMatch(ch.qos.logback.core.status.Status.ERROR, "'File' option has the same value");
    }

    @org.junit.Test
    public void collisionWith_2RFA() {
        java.lang.String suffix = "collisionWith_2RFA";
        ch.qos.logback.core.rolling.RollingFileAppender<java.lang.String> rollingfileAppender1 = buildRollingFileAppender("RFA1", suffix, "bla-%d.log");
        rollingfileAppender1.start();
        ch.qos.logback.core.rolling.RollingFileAppender<java.lang.String> rollingfileAppender2 = buildRollingFileAppender("RFA1", suffix, "bla-%d.log");
        rollingfileAppender2.start();
        ch.qos.logback.core.util.StatusPrinter.print(context);
        statusChecker.assertContainsMatch(ch.qos.logback.core.status.Status.ERROR, "'FileNamePattern' option has the same value");
    }
}

