/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.devtools.livereload;


import Frame.Type;
import Frame.Type.BINARY;
import Frame.Type.CLOSE;
import Frame.Type.CONTINUATION;
import Frame.Type.PING;
import Frame.Type.PONG;
import Frame.Type.TEXT;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import org.junit.Test;


/**
 * Tests for {@link Frame}.
 *
 * @author Phillip Webb
 */
public class FrameTests {
    @Test
    public void payloadMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Frame(((String) (null)))).withMessageContaining("Payload must not be null");
    }

    @Test
    public void typeMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Frame(((Frame.Type) (null)))).withMessageContaining("Type must not be null");
    }

    @Test
    public void textPayload() {
        Frame frame = new Frame("abc");
        assertThat(frame.getType()).isEqualTo(TEXT);
        assertThat(frame.getPayload()).isEqualTo("abc".getBytes());
    }

    @Test
    public void typedPayload() {
        Frame frame = new Frame(Type.CLOSE);
        assertThat(frame.getType()).isEqualTo(CLOSE);
        assertThat(frame.getPayload()).isEqualTo(new byte[]{  });
    }

    @Test
    public void writeSmallPayload() throws Exception {
        String payload = createString(1);
        Frame frame = new Frame(payload);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        frame.write(bos);
        assertThat(bos.toByteArray()).isEqualTo(new byte[]{ ((byte) (129)), 1, 65 });
    }

    @Test
    public void writeLargePayload() throws Exception {
        String payload = createString(126);
        Frame frame = new Frame(payload);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        frame.write(bos);
        byte[] bytes = bos.toByteArray();
        assertThat(bytes.length).isEqualTo(130);
        assertThat(bytes[0]).isEqualTo(((byte) (129)));
        assertThat(bytes[1]).isEqualTo(((byte) (126)));
        assertThat(bytes[2]).isEqualTo(((byte) (0)));
        assertThat(bytes[3]).isEqualTo(((byte) (126)));
        assertThat(bytes[4]).isEqualTo(((byte) (65)));
        assertThat(bytes[5]).isEqualTo(((byte) (65)));
    }

    @Test
    public void readFragmentedNotSupported() throws Exception {
        byte[] bytes = new byte[]{ 15 };
        assertThatIllegalStateException().isThrownBy(() -> Frame.read(newConnectionInputStream(bytes))).withMessageContaining("Fragmented frames are not supported");
    }

    @Test
    public void readLargeFramesNotSupported() throws Exception {
        byte[] bytes = new byte[]{ ((byte) (128)), ((byte) (255)) };
        assertThatIllegalStateException().isThrownBy(() -> Frame.read(newConnectionInputStream(bytes))).withMessageContaining("Large frames are not supported");
    }

    @Test
    public void readSmallTextFrame() throws Exception {
        byte[] bytes = new byte[]{ ((byte) (129)), ((byte) (2)), 65, 65 };
        Frame frame = Frame.read(newConnectionInputStream(bytes));
        assertThat(frame.getType()).isEqualTo(TEXT);
        assertThat(frame.getPayload()).isEqualTo(new byte[]{ 65, 65 });
    }

    @Test
    public void readMaskedTextFrame() throws Exception {
        byte[] bytes = new byte[]{ ((byte) (129)), ((byte) (130)), 15, 15, 15, 15, 78, 78 };
        Frame frame = Frame.read(newConnectionInputStream(bytes));
        assertThat(frame.getType()).isEqualTo(TEXT);
        assertThat(frame.getPayload()).isEqualTo(new byte[]{ 65, 65 });
    }

    @Test
    public void readLargeTextFrame() throws Exception {
        byte[] bytes = new byte[134];
        Arrays.fill(bytes, ((byte) (78)));
        bytes[0] = ((byte) (129));
        bytes[1] = ((byte) (254));
        bytes[2] = 0;
        bytes[3] = 126;
        bytes[4] = 15;
        bytes[5] = 15;
        bytes[6] = 15;
        bytes[7] = 15;
        Frame frame = Frame.read(newConnectionInputStream(bytes));
        assertThat(frame.getType()).isEqualTo(TEXT);
        assertThat(frame.getPayload()).isEqualTo(createString(126).getBytes());
    }

    @Test
    public void readContinuation() throws Exception {
        byte[] bytes = new byte[]{ ((byte) (128)), ((byte) (0)) };
        Frame frame = Frame.read(newConnectionInputStream(bytes));
        assertThat(frame.getType()).isEqualTo(CONTINUATION);
    }

    @Test
    public void readBinary() throws Exception {
        byte[] bytes = new byte[]{ ((byte) (130)), ((byte) (0)) };
        Frame frame = Frame.read(newConnectionInputStream(bytes));
        assertThat(frame.getType()).isEqualTo(BINARY);
    }

    @Test
    public void readClose() throws Exception {
        byte[] bytes = new byte[]{ ((byte) (136)), ((byte) (0)) };
        Frame frame = Frame.read(newConnectionInputStream(bytes));
        assertThat(frame.getType()).isEqualTo(CLOSE);
    }

    @Test
    public void readPing() throws Exception {
        byte[] bytes = new byte[]{ ((byte) (137)), ((byte) (0)) };
        Frame frame = Frame.read(newConnectionInputStream(bytes));
        assertThat(frame.getType()).isEqualTo(PING);
    }

    @Test
    public void readPong() throws Exception {
        byte[] bytes = new byte[]{ ((byte) (138)), ((byte) (0)) };
        Frame frame = Frame.read(newConnectionInputStream(bytes));
        assertThat(frame.getType()).isEqualTo(PONG);
    }
}

