package com.baeldung.mbassador;


import java.util.concurrent.atomic.AtomicBoolean;
import net.engio.mbassy.bus.MBassador;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MBassadorAsyncDispatchUnitTest {
    private MBassador dispatcher = new MBassador<String>();

    private String testString;

    private AtomicBoolean ready = new AtomicBoolean(false);

    @Test
    public void whenAsyncDispatched_thenMessageReceived() {
        dispatcher.post("foobar").asynchronously();
        await().untilAtomic(ready, Matchers.equalTo(true));
        Assert.assertNotNull(testString);
    }
}

