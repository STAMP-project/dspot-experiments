package com.baeldung.mbassador;


import java.util.concurrent.atomic.AtomicBoolean;
import net.engio.mbassy.bus.MBassador;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MBassadorAsyncInvocationUnitTest {
    private MBassador dispatcher = new MBassador<Integer>();

    private Integer testInteger;

    private String invocationThreadName;

    private AtomicBoolean ready = new AtomicBoolean(false);

    @Test
    public void whenHandlerAsync_thenHandled() {
        dispatcher.post(42).now();
        await().untilAtomic(ready, Matchers.equalTo(true));
        Assert.assertNotNull(testInteger);
        Assert.assertFalse(Thread.currentThread().getName().equals(invocationThreadName));
    }
}

