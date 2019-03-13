package org.robolectric.shadows;


import MediaCodec.BufferInfo;
import MediaFormat.KEY_MIME;
import MediaFormat.MIMETYPE_AUDIO_AAC;
import MediaMuxer.OutputFormat;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build.VERSION_CODES;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.util.TempDirectory;


/**
 * Tests for {@link ShadowMediaMuxer}.
 */
@RunWith(AndroidJUnit4.class)
public final class ShadowMediaMuxerTest {
    private TempDirectory tempDirectory;

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void basicMuxingFlow() throws IOException {
        int inputSize = 512;
        String tempFilePath = tempDirectory.create("dir").resolve(UUID.randomUUID().toString()).toString();
        MediaMuxer muxer = new MediaMuxer(tempFilePath, OutputFormat.MUXER_OUTPUT_MPEG_4);
        MediaFormat format = new MediaFormat();
        format.setString(KEY_MIME, MIMETYPE_AUDIO_AAC);
        int trackIndex = muxer.addTrack(format);
        muxer.start();
        byte[] inputBytes = new byte[inputSize];
        new Random().nextBytes(inputBytes);
        ByteBuffer inputBuffer = ByteBuffer.wrap(inputBytes);
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        bufferInfo.set(0, inputSize, 0, 0);
        muxer.writeSampleData(trackIndex, inputBuffer, bufferInfo);
        muxer.stop();
        // Read in what was muxed.
        byte[] outputBytes = new byte[inputSize];
        FileInputStream tempFile = new FileInputStream(tempFilePath);
        int offset = 0;
        int bytesRead = 0;
        while (((inputSize - offset) > 0) && ((bytesRead = tempFile.read(outputBytes, offset, (inputSize - offset))) != (-1))) {
            offset += bytesRead;
        } 
        assertThat(outputBytes).isEqualTo(inputBytes);
    }
}

