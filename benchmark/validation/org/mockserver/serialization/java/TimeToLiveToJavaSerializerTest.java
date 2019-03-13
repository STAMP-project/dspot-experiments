package org.mockserver.serialization.java;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.matchers.TimeToLive;


/**
 *
 *
 * @author jamesdbloom
 */
public class TimeToLiveToJavaSerializerTest {
    @Test
    public void shouldSerializeUnlimitedTimeToLiveAsJava() {
        Assert.assertEquals(((NEW_LINE) + "        TimeToLive.unlimited()"), new TimeToLiveToJavaSerializer().serialize(1, TimeToLive.unlimited()));
    }

    @Test
    public void shouldSerializeExactlyTimeToLiveAsJava() {
        Assert.assertEquals(((NEW_LINE) + "        TimeToLive.exactly(TimeUnit.SECONDS, 100L)"), new TimeToLiveToJavaSerializer().serialize(1, TimeToLive.exactly(TimeUnit.SECONDS, 100L)));
    }
}

