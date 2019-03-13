/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core;


import java.io.IOException;
import java.io.Writer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class WriterOutputStreamTest {
    Writer writer = Mockito.mock(Writer.class);

    final String encoding = "UTF8";

    @Test
    public void testConstructor() {
        WriterOutputStream stream = new WriterOutputStream(writer);
        Assert.assertSame(writer, stream.getWriter());
        stream = new WriterOutputStream(writer, encoding);
        Assert.assertSame(writer, stream.getWriter());
        Assert.assertSame(encoding, stream.getEncoding());
    }

    @Test
    public void testWrite() throws IOException {
        WriterOutputStream stream = new WriterOutputStream(writer);
        stream.write(68);
        stream.write("value".getBytes(), 1, 3);
        stream.write("value".getBytes());
        stream.flush();
        stream.close();
        Mockito.verify(writer).append(new String(new byte[]{ ((byte) (68)) }));
        Mockito.verify(writer).append("alu");
        Mockito.verify(writer).append("value");
        Mockito.verify(writer).flush();
        Mockito.verify(writer).close();
        Assert.assertNull(stream.getWriter());
        writer = Mockito.mock(Writer.class);
        WriterOutputStream streamWithEncoding = new WriterOutputStream(writer, encoding);
        streamWithEncoding.write("value".getBytes(encoding));
        Mockito.verify(writer).append("value");
    }
}

