package org.mockserver.serialization.java;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.matchers.Times;


/**
 *
 *
 * @author jamesdbloom
 */
public class TimesToJavaSerializerTest {
    @Test
    public void shouldSerializeUnlimitedTimesAsJava() {
        Assert.assertEquals(((NEW_LINE) + "        Times.unlimited()"), new TimesToJavaSerializer().serialize(1, Times.unlimited()));
    }

    @Test
    public void shouldSerializeOnceTimesAsJava() {
        Assert.assertEquals(((NEW_LINE) + "        Times.once()"), new TimesToJavaSerializer().serialize(1, Times.once()));
    }

    @Test
    public void shouldSerializeExactlyTimesAsJava() {
        Assert.assertEquals(((NEW_LINE) + "        Times.exactly(2)"), new TimesToJavaSerializer().serialize(1, Times.exactly(2)));
    }
}

