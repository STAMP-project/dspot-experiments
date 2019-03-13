package io.aeron.archive;


import io.aeron.Publication;
import java.util.concurrent.TimeUnit;
import org.agrona.concurrent.EpochClock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ControlSessionTest {
    private static final long CONNECT_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(5);

    private final ControlSessionDemuxer mockDemuxer = Mockito.mock(ControlSessionDemuxer.class);

    private final ArchiveConductor mockConductor = Mockito.mock(ArchiveConductor.class);

    private final EpochClock mockEpochClock = Mockito.mock(EpochClock.class);

    private final Publication mockControlPublication = Mockito.mock(Publication.class);

    private final ControlResponseProxy mockProxy = Mockito.mock(ControlResponseProxy.class);

    private ControlSession session;

    @Test
    public void shouldTimeoutIfConnectSentButPublicationNotConnected() {
        Mockito.when(mockEpochClock.time()).thenReturn(0L);
        Mockito.when(mockControlPublication.isClosed()).thenReturn(false);
        Mockito.when(mockControlPublication.isConnected()).thenReturn(false);
        session.doWork();
        Mockito.when(mockEpochClock.time()).thenReturn(((ControlSessionTest.CONNECT_TIMEOUT_MS) + 1L));
        session.doWork();
        Assert.assertTrue(session.isDone());
    }

    @Test
    public void shouldTimeoutIfConnectSentButPublicationFailsToSend() {
        Mockito.when(mockEpochClock.time()).thenReturn(0L);
        Mockito.when(mockControlPublication.isClosed()).thenReturn(false);
        Mockito.when(mockControlPublication.isConnected()).thenReturn(true);
        session.doWork();
        session.sendOkResponse(1L, mockProxy);
        session.doWork();
        Mockito.when(mockEpochClock.time()).thenReturn(((ControlSessionTest.CONNECT_TIMEOUT_MS) + 1L));
        session.doWork();
        Assert.assertTrue(session.isDone());
    }
}

