package net.openhft.chronicle.queue;


import org.junit.Test;


public class BackwardWithPretouchTest extends ChronicleQueueTestBase {
    @Test
    public void testAppenderBackwardWithPretoucher() {
        test(1000);
    }

    @Test
    public void testAppenderBackwardWithPretoucherPause2Seconds() {
        test(2000);
    }

    @Test
    public void testAppenderBackwardWithPretoucherPause3Seconds() {
        test(3000);
    }
}

