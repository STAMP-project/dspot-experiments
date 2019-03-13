package org.stagemonitor.core.metrics.metrics2;


import com.codahale.metrics.Clock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class QuantizedClockTest {
    private final Clock delegate = Mockito.mock(Clock.class);

    @Test
    public void getTime1() throws Exception {
        Mockito.when(delegate.getTime()).thenReturn(1001L);
        Assert.assertEquals(1000, getTime());
    }

    @Test
    public void getTime2() throws Exception {
        Mockito.when(delegate.getTime()).thenReturn(1999L);
        Assert.assertEquals(1900, getTime());
    }

    @Test
    public void getTime3() throws Exception {
        Mockito.when(delegate.getTime()).thenReturn(1000L);
        Assert.assertEquals(1000, getTime());
    }
}

