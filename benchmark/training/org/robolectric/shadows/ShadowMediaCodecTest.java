package org.robolectric.shadows;


import MediaCodec.BUFFER_FLAG_END_OF_STREAM;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.Callback;
import android.media.MediaFormat;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.nio.ByteBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;


/**
 * Tests for {@link ShadowMediaCodec}.
 */
@RunWith(AndroidJUnit4.class)
public final class ShadowMediaCodecTest {
    private MediaCodec codec;

    private Callback callback;

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void formatChangeReported() {
        Mockito.verify(callback).onOutputFormatChanged(ArgumentMatchers.same(codec), ArgumentMatchers.any());
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void presentsInputBuffer() {
        Mockito.verify(callback).onInputBufferAvailable(ArgumentMatchers.same(codec), ArgumentMatchers.anyInt());
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void providesValidInputBuffer() {
        ArgumentCaptor<Integer> indexCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(callback).onInputBufferAvailable(ArgumentMatchers.same(codec), indexCaptor.capture());
        ByteBuffer buffer = codec.getInputBuffer(indexCaptor.getValue());
        assertThat(buffer.remaining()).isGreaterThan(0);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void presentsOutputBufferAfterQueuingInputBuffer() {
        ArgumentCaptor<Integer> indexCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(callback).onInputBufferAvailable(ArgumentMatchers.same(codec), indexCaptor.capture());
        ByteBuffer buffer = codec.getInputBuffer(indexCaptor.getValue());
        int start = buffer.position();
        // "Write" to the buffer.
        buffer.position(buffer.limit());
        /* offset= */
        /* size= */
        /* presentationTimeUs= */
        /* flags= */
        codec.queueInputBuffer(indexCaptor.getValue(), start, ((buffer.position()) - start), 0, 0);
        Mockito.verify(callback).onOutputBufferAvailable(ArgumentMatchers.same(codec), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void providesValidOutputBuffer() {
        ArgumentCaptor<Integer> indexCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(callback).onInputBufferAvailable(ArgumentMatchers.same(codec), indexCaptor.capture());
        ByteBuffer buffer = codec.getInputBuffer(indexCaptor.getValue());
        int start = buffer.position();
        // "Write" to the buffer.
        buffer.position(buffer.limit());
        /* offset= */
        /* size= */
        /* presentationTimeUs= */
        /* flags= */
        codec.queueInputBuffer(indexCaptor.getValue(), start, ((buffer.position()) - start), 0, 0);
        Mockito.verify(callback).onOutputBufferAvailable(ArgumentMatchers.same(codec), indexCaptor.capture(), ArgumentMatchers.any());
        buffer = codec.getOutputBuffer(indexCaptor.getValue());
        assertThat(buffer.remaining()).isGreaterThan(0);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void presentsInputBufferAfterReleasingOutputBufferWhenNotFinished() {
        ArgumentCaptor<Integer> indexCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(callback).onInputBufferAvailable(ArgumentMatchers.same(codec), indexCaptor.capture());
        ByteBuffer buffer = codec.getInputBuffer(indexCaptor.getValue());
        int start = buffer.position();
        // "Write" to the buffer.
        buffer.position(buffer.limit());
        /* offset= */
        /* size= */
        /* presentationTimeUs= */
        /* flags= */
        codec.queueInputBuffer(indexCaptor.getValue(), start, ((buffer.position()) - start), 0, 0);
        Mockito.verify(callback).onOutputBufferAvailable(ArgumentMatchers.same(codec), indexCaptor.capture(), ArgumentMatchers.any());
        /* render= */
        codec.releaseOutputBuffer(indexCaptor.getValue(), false);
        Mockito.verify(callback, Mockito.times(2)).onInputBufferAvailable(ArgumentMatchers.same(codec), ArgumentMatchers.anyInt());
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void doesNotPresentInputBufferAfterReleasingOutputBufferFinished() {
        ArgumentCaptor<Integer> indexCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(callback).onInputBufferAvailable(ArgumentMatchers.same(codec), indexCaptor.capture());
        ByteBuffer buffer = codec.getInputBuffer(indexCaptor.getValue());
        int start = buffer.position();
        // "Write" to the buffer.
        buffer.position(buffer.limit());
        /* offset= */
        /* size= */
        /* presentationTimeUs= */
        /* flags= */
        codec.queueInputBuffer(indexCaptor.getValue(), start, ((buffer.position()) - start), 0, BUFFER_FLAG_END_OF_STREAM);
        Mockito.verify(callback).onOutputBufferAvailable(ArgumentMatchers.same(codec), indexCaptor.capture(), ArgumentMatchers.any());
        /* render= */
        codec.releaseOutputBuffer(indexCaptor.getValue(), false);
        Mockito.verify(callback, Mockito.times(1)).onInputBufferAvailable(ArgumentMatchers.same(codec), ArgumentMatchers.anyInt());
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void passesEndOfStreamFlagWithFinalOutputBuffer() {
        ArgumentCaptor<Integer> indexCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(callback).onInputBufferAvailable(ArgumentMatchers.same(codec), indexCaptor.capture());
        ByteBuffer buffer = codec.getInputBuffer(indexCaptor.getValue());
        int start = buffer.position();
        // "Write" to the buffer.
        buffer.position(buffer.limit());
        /* offset= */
        /* size= */
        /* presentationTimeUs= */
        /* flags= */
        codec.queueInputBuffer(indexCaptor.getValue(), start, ((buffer.position()) - start), 0, BUFFER_FLAG_END_OF_STREAM);
        ArgumentCaptor<BufferInfo> infoCaptor = ArgumentCaptor.forClass(BufferInfo.class);
        Mockito.verify(callback).onOutputBufferAvailable(ArgumentMatchers.same(codec), indexCaptor.capture(), infoCaptor.capture());
        assertThat(((infoCaptor.getValue().flags) & (MediaCodec.BUFFER_FLAG_END_OF_STREAM))).isNotEqualTo(0);
    }

    /**
     * Concrete class extending MediaCodec.Callback to facilitate mocking.
     */
    public static class MediaCodecCallback extends MediaCodec.Callback {
        @Override
        public void onInputBufferAvailable(MediaCodec codec, int inputBufferId) {
        }

        @Override
        public void onOutputBufferAvailable(MediaCodec codec, int outputBufferId, BufferInfo info) {
        }

        @Override
        public void onOutputFormatChanged(MediaCodec codec, MediaFormat format) {
        }

        @Override
        public void onError(MediaCodec codec, MediaCodec.CodecException e) {
        }
    }
}

