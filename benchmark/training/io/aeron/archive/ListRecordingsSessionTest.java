package io.aeron.archive;


import io.aeron.archive.codecs.RecordingDescriptorDecoder;
import java.io.File;
import org.agrona.collections.MutableLong;
import org.agrona.concurrent.EpochClock;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ListRecordingsSessionTest {
    private static final int MAX_ENTRIES = 1024;

    private static final int SEGMENT_FILE_SIZE = (128 * 1024) * 1024;

    private final RecordingDescriptorDecoder recordingDescriptorDecoder = new RecordingDescriptorDecoder();

    private final long[] recordingIds = new long[3];

    private final File archiveDir = TestUtil.makeTestDirectory();

    private final EpochClock clock = Mockito.mock(EpochClock.class);

    private Catalog catalog;

    private final long correlationId = 1;

    private final ControlResponseProxy controlResponseProxy = Mockito.mock(ControlResponseProxy.class);

    private final ControlSession controlSession = Mockito.mock(ControlSession.class);

    private final UnsafeBuffer descriptorBuffer = new UnsafeBuffer();

    @Test
    public void shouldSendAllDescriptors() {
        final ListRecordingsSession session = new ListRecordingsSession(correlationId, 0, 3, catalog, controlResponseProxy, controlSession, descriptorBuffer);
        final MutableLong counter = new MutableLong(0);
        Mockito.when(controlSession.sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy))).then(verifySendDescriptor(counter));
        session.doWork();
        Mockito.verify(controlSession, Mockito.times(3)).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
    }

    @Test
    public void shouldSend2Descriptors() {
        final int fromId = 1;
        final ListRecordingsSession session = new ListRecordingsSession(correlationId, fromId, 2, catalog, controlResponseProxy, controlSession, descriptorBuffer);
        final MutableLong counter = new MutableLong(fromId);
        Mockito.when(controlSession.sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy))).then(verifySendDescriptor(counter));
        session.doWork();
        Mockito.verify(controlSession, Mockito.times(2)).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
    }

    @Test
    public void shouldResendDescriptorWhenSendFails() {
        final long fromRecordingId = 1;
        final ListRecordingsSession session = new ListRecordingsSession(correlationId, fromRecordingId, 1, catalog, controlResponseProxy, controlSession, descriptorBuffer);
        Mockito.when(controlSession.sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy))).thenReturn(0);
        session.doWork();
        Mockito.verify(controlSession, Mockito.times(1)).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
        final MutableLong counter = new MutableLong(fromRecordingId);
        Mockito.when(controlSession.sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy))).then(verifySendDescriptor(counter));
        session.doWork();
        Mockito.verify(controlSession, Mockito.times(2)).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
    }

    @Test
    public void shouldSendTwoDescriptorsThenRecordingUnknown() {
        final ListRecordingsSession session = new ListRecordingsSession(correlationId, 1, 3, catalog, controlResponseProxy, controlSession, descriptorBuffer);
        final MutableLong counter = new MutableLong(1);
        Mockito.when(controlSession.sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy))).then(verifySendDescriptor(counter));
        session.doWork();
        Mockito.verify(controlSession, Mockito.times(2)).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
        Mockito.verify(controlSession).sendRecordingUnknown(ArgumentMatchers.eq(correlationId), ArgumentMatchers.eq(3L), ArgumentMatchers.eq(controlResponseProxy));
    }

    @Test
    public void shouldSendRecordingUnknownOnFirst() {
        final ListRecordingsSession session = new ListRecordingsSession(correlationId, 3, 3, catalog, controlResponseProxy, controlSession, descriptorBuffer);
        session.doWork();
        Mockito.verify(controlSession, Mockito.never()).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
        Mockito.verify(controlSession).sendRecordingUnknown(ArgumentMatchers.eq(correlationId), ArgumentMatchers.eq(3L), ArgumentMatchers.eq(controlResponseProxy));
    }
}

