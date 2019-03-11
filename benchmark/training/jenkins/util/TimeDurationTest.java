package jenkins.util;


import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;


@Issue("JENKINS-44052")
public class TimeDurationTest {
    @Test
    public void fromString() throws Exception {
        Assert.assertEquals(1, TimeDuration.TimeDuration.fromString("1").getTimeInMillis());
        Assert.assertEquals(1000, TimeDuration.TimeDuration.fromString("1sec").getTimeInMillis());
        Assert.assertEquals(1000, TimeDuration.TimeDuration.fromString("1secs").getTimeInMillis());
        Assert.assertEquals(1000, TimeDuration.TimeDuration.fromString("1 secs ").getTimeInMillis());
        Assert.assertEquals(1000, TimeDuration.TimeDuration.fromString(" 1 secs ").getTimeInMillis());
        Assert.assertEquals(1, TimeDuration.TimeDuration.fromString(" 1 secs ").getTimeInSeconds());
        Assert.assertEquals(21000, TimeDuration.TimeDuration.fromString(" 21  secs ").getTimeInMillis());
        Assert.assertEquals(21, TimeDuration.TimeDuration.fromString(" 21  secs ").getTimeInSeconds());
    }
}

