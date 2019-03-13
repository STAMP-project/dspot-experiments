/**
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.audio;


import C.BUFFER_FLAG_END_OF_STREAM;
import MimeTypes.AUDIO_RAW;
import RendererConfiguration.DEFAULT;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import com.google.android.exoplayer2.decoder.SimpleOutputBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Unit test for {@link SimpleDecoderAudioRenderer}.
 */
@RunWith(RobolectricTestRunner.class)
public class SimpleDecoderAudioRendererTest {
    private static final Format FORMAT = Format.createSampleFormat(null, AUDIO_RAW, 0);

    @Mock
    private AudioSink mockAudioSink;

    private SimpleDecoderAudioRenderer audioRenderer;

    @Config(sdk = 19)
    @Test
    public void testSupportsFormatAtApi19() {
        assertThat(audioRenderer.supportsFormat(SimpleDecoderAudioRendererTest.FORMAT)).isEqualTo((((RendererCapabilities.ADAPTIVE_NOT_SEAMLESS) | (RendererCapabilities.TUNNELING_NOT_SUPPORTED)) | (RendererCapabilities.FORMAT_HANDLED)));
    }

    @Config(sdk = 21)
    @Test
    public void testSupportsFormatAtApi21() {
        // From API 21, tunneling is supported.
        assertThat(audioRenderer.supportsFormat(SimpleDecoderAudioRendererTest.FORMAT)).isEqualTo((((RendererCapabilities.ADAPTIVE_NOT_SEAMLESS) | (RendererCapabilities.TUNNELING_SUPPORTED)) | (RendererCapabilities.FORMAT_HANDLED)));
    }

    @Test
    public void testImmediatelyReadEndOfStreamPlaysAudioSinkToEndOfStream() throws Exception {
        audioRenderer.enable(DEFAULT, new Format[]{ SimpleDecoderAudioRendererTest.FORMAT }, /* eventDispatcher= */
        /* shouldOutputSample= */
        new com.google.android.exoplayer2.testutil.FakeSampleStream(SimpleDecoderAudioRendererTest.FORMAT, null, false), 0, false, 0);
        audioRenderer.setCurrentStreamFinal();
        Mockito.when(mockAudioSink.isEnded()).thenReturn(true);
        while (!(audioRenderer.isEnded())) {
            audioRenderer.render(0, 0);
        } 
        Mockito.verify(mockAudioSink, Mockito.times(1)).playToEndOfStream();
        audioRenderer.disable();
        Mockito.verify(mockAudioSink, Mockito.times(1)).release();
    }

    private static final class FakeDecoder extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, AudioDecoderException> {
        public FakeDecoder() {
            super(new DecoderInputBuffer[1], new SimpleOutputBuffer[1]);
        }

        @Override
        public String getName() {
            return "FakeDecoder";
        }

        @Override
        protected DecoderInputBuffer createInputBuffer() {
            return new DecoderInputBuffer(DecoderInputBuffer.BUFFER_REPLACEMENT_MODE_DIRECT);
        }

        @Override
        protected SimpleOutputBuffer createOutputBuffer() {
            return new SimpleOutputBuffer(this);
        }

        @Override
        protected AudioDecoderException createUnexpectedDecodeException(Throwable error) {
            return new AudioDecoderException("Unexpected decode error", error);
        }

        @Override
        protected AudioDecoderException decode(DecoderInputBuffer inputBuffer, SimpleOutputBuffer outputBuffer, boolean reset) {
            if (inputBuffer.isEndOfStream()) {
                outputBuffer.setFlags(BUFFER_FLAG_END_OF_STREAM);
            }
            return null;
        }
    }
}

