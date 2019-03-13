/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static StreamUtils.BUFFER_SIZE;


/**
 * Tests for {@link StreamUtils}.
 *
 * @author Phillip Webb
 */
public class StreamUtilsTests {
    private byte[] bytes = new byte[(BUFFER_SIZE) + 10];

    private String string = "";

    @Test
    public void copyToByteArray() throws Exception {
        InputStream inputStream = Mockito.spy(new ByteArrayInputStream(bytes));
        byte[] actual = StreamUtils.copyToByteArray(inputStream);
        Assert.assertThat(actual, equalTo(bytes));
        Mockito.verify(inputStream, Mockito.never()).close();
    }

    @Test
    public void copyToString() throws Exception {
        Charset charset = Charset.defaultCharset();
        InputStream inputStream = Mockito.spy(new ByteArrayInputStream(string.getBytes(charset)));
        String actual = StreamUtils.copyToString(inputStream, charset);
        Assert.assertThat(actual, equalTo(string));
        Mockito.verify(inputStream, Mockito.never()).close();
    }

    @Test
    public void copyBytes() throws Exception {
        ByteArrayOutputStream out = Mockito.spy(new ByteArrayOutputStream());
        StreamUtils.copy(bytes, out);
        Assert.assertThat(out.toByteArray(), equalTo(bytes));
        Mockito.verify(out, Mockito.never()).close();
    }

    @Test
    public void copyString() throws Exception {
        Charset charset = Charset.defaultCharset();
        ByteArrayOutputStream out = Mockito.spy(new ByteArrayOutputStream());
        StreamUtils.copy(string, charset, out);
        Assert.assertThat(out.toByteArray(), equalTo(string.getBytes(charset)));
        Mockito.verify(out, Mockito.never()).close();
    }

    @Test
    public void copyStream() throws Exception {
        ByteArrayOutputStream out = Mockito.spy(new ByteArrayOutputStream());
        StreamUtils.copy(new ByteArrayInputStream(bytes), out);
        Assert.assertThat(out.toByteArray(), equalTo(bytes));
        Mockito.verify(out, Mockito.never()).close();
    }

    @Test
    public void copyRange() throws Exception {
        ByteArrayOutputStream out = Mockito.spy(new ByteArrayOutputStream());
        StreamUtils.copyRange(new ByteArrayInputStream(bytes), out, 0, 100);
        byte[] range = Arrays.copyOfRange(bytes, 0, 101);
        Assert.assertThat(out.toByteArray(), equalTo(range));
        Mockito.verify(out, Mockito.never()).close();
    }

    @Test
    public void nonClosingInputStream() throws Exception {
        InputStream source = Mockito.mock(InputStream.class);
        InputStream nonClosing = StreamUtils.nonClosing(source);
        nonClosing.read();
        nonClosing.read(bytes);
        nonClosing.read(bytes, 1, 2);
        nonClosing.close();
        InOrder ordered = Mockito.inOrder(source);
        ordered.verify(source).read();
        ordered.verify(source).read(bytes, 0, bytes.length);
        ordered.verify(source).read(bytes, 1, 2);
        ordered.verify(source, Mockito.never()).close();
    }

    @Test
    public void nonClosingOutputStream() throws Exception {
        OutputStream source = Mockito.mock(OutputStream.class);
        OutputStream nonClosing = StreamUtils.nonClosing(source);
        nonClosing.write(1);
        nonClosing.write(bytes);
        nonClosing.write(bytes, 1, 2);
        nonClosing.close();
        InOrder ordered = Mockito.inOrder(source);
        ordered.verify(source).write(1);
        ordered.verify(source).write(bytes, 0, bytes.length);
        ordered.verify(source).write(bytes, 1, 2);
        ordered.verify(source, Mockito.never()).close();
    }
}

