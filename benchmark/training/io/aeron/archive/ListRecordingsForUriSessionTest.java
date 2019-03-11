package io.aeron.archive;


import io.aeron.archive.codecs.RecordingDescriptorDecoder;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.agrona.collections.MutableLong;
import org.agrona.concurrent.EpochClock;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ListRecordingsForUriSessionTest {
    private static final long MAX_ENTRIES = 1024;

    private static final int SEGMENT_FILE_SIZE = (128 * 1024) * 1024;

    public static final byte[] LOCALHOST_BYTES = "localhost".getBytes(StandardCharsets.US_ASCII);

    private final UnsafeBuffer descriptorBuffer = new UnsafeBuffer();

    private final RecordingDescriptorDecoder recordingDescriptorDecoder = new RecordingDescriptorDecoder();

    private final long[] matchingRecordingIds = new long[3];

    private final File archiveDir = TestUtil.makeTestDirectory();

    private final EpochClock clock = Mockito.mock(EpochClock.class);

    private Catalog catalog;

    private final long correlationId = 1;

    private final ControlResponseProxy controlResponseProxy = Mockito.mock(ControlResponseProxy.class);

    private final ControlSession controlSession = Mockito.mock(ControlSession.class);

    @Test
    public void shouldSendAllDescriptors() {
        final ListRecordingsForUriSession session = new ListRecordingsForUriSession(correlationId, 0, 3, ListRecordingsForUriSessionTest.LOCALHOST_BYTES, 1, catalog, controlResponseProxy, controlSession, descriptorBuffer, recordingDescriptorDecoder);
        final MutableLong counter = new MutableLong(0);
        Mockito.when(controlSession.sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy))).then(verifySendDescriptor(counter));
        session.doWork();
        Mockito.verify(controlSession, Mockito.times(3)).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
    }

    @Test
    public void shouldSend2Descriptors() {
        final long fromRecordingId = 1;
        final ListRecordingsForUriSession session = new ListRecordingsForUriSession(correlationId, fromRecordingId, 2, ListRecordingsForUriSessionTest.LOCALHOST_BYTES, 1, catalog, controlResponseProxy, controlSession, descriptorBuffer, recordingDescriptorDecoder);
        final MutableLong counter = new MutableLong(fromRecordingId);
        Mockito.when(controlSession.sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy))).then(verifySendDescriptor(counter));
        session.doWork();
        Mockito.verify(controlSession, Mockito.times(2)).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
    }

    @Test
    public void shouldResendDescriptorWhenSendFails() {
        final long fromRecordingId = 1;
        final ListRecordingsForUriSession session = new ListRecordingsForUriSession(correlationId, fromRecordingId, 1, ListRecordingsForUriSessionTest.LOCALHOST_BYTES, 1, catalog, controlResponseProxy, controlSession, descriptorBuffer, recordingDescriptorDecoder);
        Mockito.when(controlSession.sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy))).thenReturn(0);
        session.doWork();
        Mockito.verify(controlSession, Mockito.times(1)).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
        final MutableLong counter = new MutableLong(fromRecordingId);
        Mockito.when(controlSession.sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy))).then(verifySendDescriptor(counter));
        session.doWork();
        Mockito.verify(controlSession, Mockito.times(2)).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
    }

    @Test
    public void shouldSend2DescriptorsAndRecordingUnknown() {
        final ListRecordingsForUriSession session = new ListRecordingsForUriSession(correlationId, 1, 5, ListRecordingsForUriSessionTest.LOCALHOST_BYTES, 1, catalog, controlResponseProxy, controlSession, descriptorBuffer, recordingDescriptorDecoder);
        final MutableLong counter = new MutableLong(1);
        Mockito.when(controlSession.sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy))).then(verifySendDescriptor(counter));
        session.doWork();
        Mockito.verify(controlSession, Mockito.times(2)).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
        Mockito.verify(controlSession).sendRecordingUnknown(ArgumentMatchers.eq(correlationId), ArgumentMatchers.eq(5L), ArgumentMatchers.eq(controlResponseProxy));
    }

    @Test
    public void shouldSendRecordingUnknown() {
        final ListRecordingsForUriSession session = new ListRecordingsForUriSession(correlationId, 1, 3, "notChannel".getBytes(StandardCharsets.US_ASCII), 1, catalog, controlResponseProxy, controlSession, descriptorBuffer, recordingDescriptorDecoder);
        session.doWork();
        Mockito.verify(controlSession, Mockito.never()).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
        Mockito.verify(controlSession).sendRecordingUnknown(ArgumentMatchers.eq(correlationId), ArgumentMatchers.eq(5L), ArgumentMatchers.eq(controlResponseProxy));
    }

    @Test
    public void shouldSendUnknownOnFirst() {
        Mockito.when(controlSession.maxPayloadLength()).thenReturn((4096 - 32));
        final ListRecordingsForUriSession session = new ListRecordingsForUriSession(correlationId, 5, 3, ListRecordingsForUriSessionTest.LOCALHOST_BYTES, 1, catalog, controlResponseProxy, controlSession, descriptorBuffer, recordingDescriptorDecoder);
        session.doWork();
        Mockito.verify(controlSession, Mockito.never()).sendDescriptor(ArgumentMatchers.eq(correlationId), ArgumentMatchers.any(), ArgumentMatchers.eq(controlResponseProxy));
        Mockito.verify(controlSession).sendRecordingUnknown(ArgumentMatchers.eq(correlationId), ArgumentMatchers.eq(5L), ArgumentMatchers.eq(controlResponseProxy));
    }
}

