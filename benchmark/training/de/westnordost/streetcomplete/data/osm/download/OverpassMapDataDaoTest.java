package de.westnordost.streetcomplete.data.osm.download;


import de.westnordost.osmapi.OsmConnection;
import javax.inject.Provider;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;


public class OverpassMapDataDaoTest {
    @Test
    public void handleOverpassQuota() throws InterruptedException {
        Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.get()).thenReturn(Mockito.mock(OverpassMapDataParser.class));
        OverpassStatus status = new OverpassStatus();
        status.availableSlots = 0;
        status.nextAvailableSlotIn = 2;
        OsmConnection osm = Mockito.mock(OsmConnection.class);
        Mockito.when(osm.makeRequest(ArgumentMatchers.eq("status"), ArgumentMatchers.any())).thenReturn(status);
        Mockito.when(osm.makeRequest(ArgumentMatchers.eq("interpreter"), ArgumentMatchers.eq("POST"), ArgumentMatchers.eq(false), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(OsmTooManyRequestsException.class);
        final OverpassMapDataDao dao = new OverpassMapDataDao(osm, provider);
        // the dao will call get(), get an exception in return, ask its status
        // then and at least wait for the specified amount of time before calling again
        final boolean[] result = new boolean[1];
        Thread dlThread = new Thread() {
            @Override
            public void run() {
                // assert false because we interrupt the thread further down...
                result[0] = dao.getAndHandleQuota("", null);
            }
        };
        dlThread.start();
        // sleep the wait time: Downloader should not try to call
        // overpass again in this time
        Thread.sleep(((status.nextAvailableSlotIn) * 1000));
        Mockito.verify(osm, VerificationModeFactory.times(1)).makeRequest(ArgumentMatchers.eq("interpreter"), ArgumentMatchers.eq("POST"), ArgumentMatchers.eq(false), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(osm, VerificationModeFactory.times(1)).makeRequest(ArgumentMatchers.eq("status"), ArgumentMatchers.any());
        // now we test if dao will call overpass again after that time. It is not really
        // defined when the downloader must call overpass again, lets assume 1.5 secs here and
        // change it when it fails
        Thread.sleep(1500);
        Mockito.verify(osm, VerificationModeFactory.times(2)).makeRequest(ArgumentMatchers.eq("interpreter"), ArgumentMatchers.eq("POST"), ArgumentMatchers.eq(false), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(osm, VerificationModeFactory.times(2)).makeRequest(ArgumentMatchers.eq("status"), ArgumentMatchers.any());
        // we are done here, interrupt thread (still part of the test though...)
        dlThread.interrupt();
        dlThread.join();
        Assert.assertFalse(result[0]);
    }
}

