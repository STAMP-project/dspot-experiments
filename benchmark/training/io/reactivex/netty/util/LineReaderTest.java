/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.netty.util;


import UnpooledByteBufAllocator.DEFAULT;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;


public class LineReaderTest {
    @Rule
    public final LineReaderTest.ReaderRule readerRule = new LineReaderTest.ReaderRule();

    @Test(timeout = 60000)
    public void testSingleLine() throws Exception {
        String msg = "Hello";
        ByteBuf data = Unpooled.buffer().writeBytes((msg + "\n").getBytes());
        List<Object> out = new ArrayList<>();
        readerRule.lineReader.decode(data, out, DEFAULT);
        MatcherAssert.assertThat("Unexpected output size.", out, hasSize(1));
        MatcherAssert.assertThat("Unexpected output message.", out, contains(((Object) (msg))));
        MatcherAssert.assertThat("Input buffer not consumed.", data.isReadable(), is(false));
    }

    @Test(timeout = 60000)
    public void testEmptyInputBuffer() throws Exception {
        ByteBuf data = Unpooled.buffer();
        List<Object> out = new ArrayList<>();
        readerRule.lineReader.decode(data, out, DEFAULT);
        MatcherAssert.assertThat("Unexpected output size.", out, is(empty()));
        MatcherAssert.assertThat("Input buffer not consumed.", data.isReadable(), is(false));
    }

    @Test(timeout = 60000)
    public void testEmptyLine() throws Exception {
        ByteBuf data = Unpooled.buffer().writeByte('\n');
        List<Object> out = new ArrayList<>();
        readerRule.lineReader.decode(data, out, DEFAULT);
        MatcherAssert.assertThat("Unexpected output size.", out, hasSize(1));
        MatcherAssert.assertThat("Unexpected output message.", out, contains(((Object) (""))));
        MatcherAssert.assertThat("Input buffer not consumed.", data.isReadable(), is(false));
    }

    @Test(timeout = 60000)
    public void testSplitData() throws Exception {
        String msg1 = "Hell";
        String msg2 = "o";
        ByteBuf data1 = Unpooled.buffer().writeBytes(msg1.getBytes());
        ByteBuf data2 = Unpooled.buffer().writeBytes((msg2 + "\n").getBytes());
        List<Object> out = new ArrayList<>();
        readerRule.lineReader.decode(data1, out, DEFAULT);
        MatcherAssert.assertThat("Unexpected output size post first decode.", out, is(empty()));
        MatcherAssert.assertThat("Input buffer not consumed.", data1.isReadable(), is(false));
        readerRule.lineReader.decode(data2, out, DEFAULT);
        MatcherAssert.assertThat("Unexpected output size post second decode.", out, hasSize(1));
        MatcherAssert.assertThat("Unexpected output message post second decode.", out, contains(((Object) (msg1 + msg2))));
        MatcherAssert.assertThat("Input buffer not consumed.", data2.isReadable(), is(false));
    }

    @Test(timeout = 60000)
    public void testUnreadDataDispose() throws Exception {
        String msg = "Hell";
        ByteBuf data1 = Unpooled.buffer().writeBytes(msg.getBytes());
        List<Object> out = new ArrayList<>();
        readerRule.lineReader.decode(data1, out, DEFAULT);
        MatcherAssert.assertThat("Unexpected output size post first decode.", out, is(empty()));
        MatcherAssert.assertThat("Input buffer not consumed.", data1.isReadable(), is(false));
        MatcherAssert.assertThat("Reader does not have incomplete buffer.", readerRule.lineReader.getIncompleteBuffer(), is(notNullValue()));
        MatcherAssert.assertThat("Reader's incomplete buffer is not readable.", readerRule.lineReader.getIncompleteBuffer().isReadable(), is(true));
        readerRule.lineReader.dispose();
        MatcherAssert.assertThat("Reader does not have incomplete buffer.", readerRule.lineReader.getIncompleteBuffer(), is(notNullValue()));
        MatcherAssert.assertThat("Reader did not release incomplete buffer.", readerRule.lineReader.getIncompleteBuffer().refCnt(), is(0));
    }

    public static class ReaderRule extends ExternalResource {
        private LineReader lineReader;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    lineReader = new LineReader();
                    base.evaluate();
                }
            };
        }
    }
}

