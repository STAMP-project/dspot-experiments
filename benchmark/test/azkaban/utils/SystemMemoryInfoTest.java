package azkaban.utils;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SystemMemoryInfoTest {
    private static final long GB_UNIT = 1024L * 1024L;

    @Test
    public void grantedIfFreeMemoryAvailable() throws Exception {
        final OsMemoryUtil memUtil = Mockito.mock(OsMemoryUtil.class);
        final long availableFreeMem = (10L * 1024L) * 1024L;// 10 GB

        Mockito.when(memUtil.getOsTotalFreeMemorySize()).thenReturn(availableFreeMem);
        final SystemMemoryInfo memInfo = new SystemMemoryInfo(memUtil);
        final boolean isGranted = memInfo.canSystemGrantMemory(1);
        Assert.assertTrue(isGranted);
    }

    @Test
    public void notGrantedIfFreeMemoryAvailableLessThanMinimal() throws Exception {
        final OsMemoryUtil memUtil = Mockito.mock(OsMemoryUtil.class);
        final long availableFreeMem = (4L * 1024L) * 1024L;// 4 GB

        Mockito.when(memUtil.getOsTotalFreeMemorySize()).thenReturn(availableFreeMem);
        final SystemMemoryInfo memInfo = new SystemMemoryInfo(memUtil);
        final long xmx = 2 * (SystemMemoryInfoTest.GB_UNIT);// 2 GB

        final boolean isGranted = memInfo.canSystemGrantMemory(xmx);
        Assert.assertFalse(isGranted);
    }

    @Test
    public void grantedIfFreeMemoryCheckReturnsZero() throws Exception {
        final OsMemoryUtil memUtil = Mockito.mock(OsMemoryUtil.class);
        final long availableFreeMem = 0;
        Mockito.when(memUtil.getOsTotalFreeMemorySize()).thenReturn(availableFreeMem);
        final SystemMemoryInfo memInfo = new SystemMemoryInfo(memUtil);
        final long xmx = 0;
        final boolean isGranted = memInfo.canSystemGrantMemory(xmx);
        Assert.assertTrue("Memory check failed. Should fail open", isGranted);
    }
}

