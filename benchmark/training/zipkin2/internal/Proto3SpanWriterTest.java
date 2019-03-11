/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.internal;


import java.util.Arrays;
import org.junit.Test;
import zipkin2.TestObjects;


public class Proto3SpanWriterTest {
    Buffer buf = new Buffer(2048);// bigger than needed to test sizeOf


    Proto3SpanWriter writer = new Proto3SpanWriter();

    /**
     * proto messages always need a key, so the non-list form is just a single-field
     */
    @Test
    public void write_startsWithSpanKeyAndLengthPrefix() {
        byte[] buff = writer.write(TestObjects.CLIENT_SPAN);
        assertThat(buff).hasSize(writer.sizeInBytes(TestObjects.CLIENT_SPAN)).startsWith(((byte) (10)), Proto3ZipkinFields.SPAN.sizeOfValue(TestObjects.CLIENT_SPAN));
    }

    @Test
    public void writeList_startsWithSpanKeyAndLengthPrefix() {
        byte[] buff = writer.writeList(Arrays.asList(TestObjects.CLIENT_SPAN));
        assertThat(buff).hasSize(writer.sizeInBytes(TestObjects.CLIENT_SPAN)).startsWith(((byte) (10)), Proto3ZipkinFields.SPAN.sizeOfValue(TestObjects.CLIENT_SPAN));
    }

    @Test
    public void writeList_multiple() {
        byte[] buff = writer.writeList(Arrays.asList(TestObjects.CLIENT_SPAN, TestObjects.CLIENT_SPAN));
        assertThat(buff).hasSize(((writer.sizeInBytes(TestObjects.CLIENT_SPAN)) * 2)).startsWith(((byte) (10)), Proto3ZipkinFields.SPAN.sizeOfValue(TestObjects.CLIENT_SPAN));
    }

    @Test
    public void writeList_empty() {
        assertThat(writer.writeList(Arrays.asList())).isEmpty();
    }

    @Test
    public void writeList_offset_startsWithSpanKeyAndLengthPrefix() {
        writer.writeList(Arrays.asList(TestObjects.CLIENT_SPAN, TestObjects.CLIENT_SPAN), buf.toByteArray(), 0);
        assertThat(buf.toByteArray()).startsWith(((byte) (10)), Proto3ZipkinFields.SPAN.sizeOfValue(TestObjects.CLIENT_SPAN));
    }
}

