package jonathanfinerty.once;


import Once.THIS_APP_INSTALL;
import Once.THIS_APP_SESSION;
import Once.THIS_APP_VERSION;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@SuppressWarnings("ConstantConditions")
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class OnceTests {
    private static final String tagUnderTest = "testTag";

    @Test
    public void unseenTags() {
        Once.clearAll();
        boolean seenThisSession = Once.beenDone(THIS_APP_SESSION, OnceTests.tagUnderTest);
        Assert.assertFalse(seenThisSession);
        boolean seenThisInstall = Once.beenDone(THIS_APP_INSTALL, OnceTests.tagUnderTest);
        Assert.assertFalse(seenThisInstall);
        boolean seenThisAppVersion = Once.beenDone(THIS_APP_VERSION, OnceTests.tagUnderTest);
        Assert.assertFalse(seenThisAppVersion);
        boolean seenInTheLastDay = Once.beenDone(TimeUnit.DAYS, 1, OnceTests.tagUnderTest);
        Assert.assertFalse(seenInTheLastDay);
    }

    @Test
    public void seenTagImmediately() {
        Once.markDone(OnceTests.tagUnderTest);
        boolean seenThisSession = Once.beenDone(THIS_APP_SESSION, OnceTests.tagUnderTest);
        Assert.assertTrue(seenThisSession);
        boolean seenThisInstall = Once.beenDone(THIS_APP_INSTALL, OnceTests.tagUnderTest);
        Assert.assertTrue(seenThisInstall);
        boolean seenThisAppVersion = Once.beenDone(THIS_APP_VERSION, OnceTests.tagUnderTest);
        Assert.assertTrue(seenThisAppVersion);
        boolean seenThisMinute = Once.beenDone(TimeUnit.MINUTES, 1, OnceTests.tagUnderTest);
        Assert.assertTrue(seenThisMinute);
    }

    @Test
    public void removeFromDone() {
        Once.markDone(OnceTests.tagUnderTest);
        Once.clearDone(OnceTests.tagUnderTest);
        boolean seenThisSession = Once.beenDone(THIS_APP_SESSION, OnceTests.tagUnderTest);
        Assert.assertFalse(seenThisSession);
        boolean seenThisInstall = Once.beenDone(THIS_APP_INSTALL, OnceTests.tagUnderTest);
        Assert.assertFalse(seenThisInstall);
        boolean seenThisAppVersion = Once.beenDone(THIS_APP_VERSION, OnceTests.tagUnderTest);
        Assert.assertFalse(seenThisAppVersion);
        boolean seenInTheLastDay = Once.beenDone(TimeUnit.DAYS, 1, OnceTests.tagUnderTest);
        Assert.assertFalse(seenInTheLastDay);
    }

    @Test
    public void seenTagAfterAppUpdate() {
        Once.markDone(OnceTests.tagUnderTest);
        TestUtils.simulateAppUpdate();
        boolean seenThisSession = Once.beenDone(THIS_APP_SESSION, OnceTests.tagUnderTest);
        Assert.assertTrue(seenThisSession);
        boolean seenThisInstall = Once.beenDone(THIS_APP_INSTALL, OnceTests.tagUnderTest);
        Assert.assertTrue(seenThisInstall);
        boolean seenThisAppVersion = Once.beenDone(THIS_APP_VERSION, OnceTests.tagUnderTest);
        Assert.assertFalse(seenThisAppVersion);
        boolean seenThisMinute = Once.beenDone(TimeUnit.MINUTES, 1, OnceTests.tagUnderTest);
        Assert.assertTrue(seenThisMinute);
    }

    @Test
    public void seenTagAfterSecond() throws InterruptedException {
        Once.markDone(OnceTests.tagUnderTest);
        boolean seenThisSession = Once.beenDone(THIS_APP_SESSION, OnceTests.tagUnderTest);
        Assert.assertTrue(seenThisSession);
        boolean seenThisInstall = Once.beenDone(THIS_APP_INSTALL, OnceTests.tagUnderTest);
        Assert.assertTrue(seenThisInstall);
        boolean seenThisAppVersion = Once.beenDone(THIS_APP_VERSION, OnceTests.tagUnderTest);
        Assert.assertTrue(seenThisAppVersion);
        Thread.sleep(((TimeUnit.SECONDS.toMillis(1)) + 1));
        boolean seenThisSecond = Once.beenDone(TimeUnit.SECONDS, 1, OnceTests.tagUnderTest);
        Assert.assertFalse(seenThisSecond);
        long secondInMillis = 1000;
        boolean seenThisSecondInMillis = Once.beenDone(secondInMillis, OnceTests.tagUnderTest);
        Assert.assertFalse(seenThisSecondInMillis);
    }

    @Test
    public void clearAll() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        Once.markDone(tag1);
        Once.markDone(tag2);
        Once.clearAll();
        Assert.assertFalse(Once.beenDone(THIS_APP_SESSION, tag1));
        Assert.assertFalse(Once.beenDone(THIS_APP_INSTALL, tag1));
        Assert.assertFalse(Once.beenDone(THIS_APP_VERSION, tag1));
        Assert.assertFalse(Once.beenDone(1000L, tag1));
        Assert.assertFalse(Once.beenDone(THIS_APP_SESSION, tag2));
        Assert.assertFalse(Once.beenDone(THIS_APP_INSTALL, tag2));
        Assert.assertFalse(Once.beenDone(THIS_APP_VERSION, tag2));
        Assert.assertFalse(Once.beenDone(1000L, tag2));
    }

    @Test
    public void emptyTag() {
        String emptyTag = "";
        Assert.assertFalse(Once.beenDone(emptyTag));
        Once.markDone(emptyTag);
        Assert.assertTrue(Once.beenDone(emptyTag));
    }

    @Test
    public void beenDoneMultipleTimes() {
        String testTag = "action done several times";
        Once.markDone(testTag);
        Once.markDone(testTag);
        Assert.assertFalse(Once.beenDone(testTag, Amount.exactly(3)));
        Once.markDone(testTag);
        Assert.assertTrue(Once.beenDone(testTag, Amount.exactly(3)));
    }

    @Test
    public void beenDoneMultipleTimesAcrossScopes() throws InterruptedException {
        String testTag = "action done several times in different scopes";
        Once.markDone(testTag);
        TestUtils.simulateAppUpdate();
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        Once.markDone(testTag);
        Assert.assertTrue(Once.beenDone(THIS_APP_INSTALL, testTag, Amount.exactly(2)));
        Assert.assertFalse(Once.beenDone(THIS_APP_VERSION, testTag, Amount.exactly(2)));
        Once.markDone(testTag);
        Assert.assertTrue(Once.beenDone(THIS_APP_INSTALL, testTag, Amount.exactly(3)));
        Assert.assertTrue(Once.beenDone(THIS_APP_VERSION, testTag, Amount.exactly(2)));
    }

    @Test
    public void beenDoneDifferentTimeChecks() {
        String testTag = "test tag";
        Once.markDone(testTag);
        Once.markDone(testTag);
        Once.markDone(testTag);
        Assert.assertTrue(Once.beenDone(testTag, Amount.moreThan((-1))));
        Assert.assertTrue(Once.beenDone(testTag, Amount.moreThan(2)));
        Assert.assertFalse(Once.beenDone(testTag, Amount.moreThan(3)));
        Assert.assertTrue(Once.beenDone(testTag, Amount.lessThan(10)));
        Assert.assertTrue(Once.beenDone(testTag, Amount.lessThan(4)));
        Assert.assertFalse(Once.beenDone(testTag, Amount.lessThan(3)));
    }

    @Test
    public void beenDoneMultipleTimesWithTimeStamps() throws InterruptedException {
        Once.markDone(OnceTests.tagUnderTest);
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        Once.markDone(OnceTests.tagUnderTest);
        Assert.assertTrue(Once.beenDone(TimeUnit.SECONDS, 3, OnceTests.tagUnderTest, Amount.exactly(2)));
        Assert.assertTrue(Once.beenDone(TimeUnit.SECONDS, 1, OnceTests.tagUnderTest, Amount.exactly(1)));
    }

    @Test
    public void lastDoneWhenNeverDone() {
        Date lastDoneDate = Once.lastDone(OnceTests.tagUnderTest);
        Assert.assertNull(lastDoneDate);
    }

    @Test
    public void lastDone() {
        Once.markDone(OnceTests.tagUnderTest);
        Date expectedDate = new Date();
        Date lastDoneDate = Once.lastDone(OnceTests.tagUnderTest);
        Assert.assertTrue((((lastDoneDate.getTime()) - (expectedDate.getTime())) < 10));
    }

    @Test
    public void lastDoneMultipleDates() throws InterruptedException {
        Once.markDone(OnceTests.tagUnderTest);
        Thread.sleep(100);
        Once.markDone(OnceTests.tagUnderTest);
        Date expectedDate = new Date();
        Date lastDoneDate = Once.lastDone(OnceTests.tagUnderTest);
        Assert.assertTrue((((lastDoneDate.getTime()) - (expectedDate.getTime())) < 10));
    }
}

