/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.net.buffer;


import bt.TestUtil;
import java.nio.ByteBuffer;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;


public class BuffersTest {
    @Test
    public void testSearchPattern_SingleByte_MatchesAllBuffer() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 1 });
        byte[] pattern = new byte[]{ 1 };
        Assert.assertTrue(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(buf.limit(), buf.position());
    }

    @Test
    public void testSearchPattern_SingleByte_MatchesBeginningOfBuffer() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 1, 1, 1, 1, 1 });
        byte[] pattern = new byte[]{ 1 };
        Assert.assertTrue(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(1, buf.position());
    }

    @Test
    public void testSearchPattern_SingleByte_MatchesMiddleOfBuffer() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 0, 0, 1, 1, 1 });
        byte[] pattern = new byte[]{ 1 };
        Assert.assertTrue(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(3, buf.position());
    }

    @Test
    public void testSearchPattern_SingleByte_MatchesEndOfBuffer() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 0, 0, 0, 0, 1 });
        byte[] pattern = new byte[]{ 1 };
        Assert.assertTrue(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(buf.limit(), buf.position());
    }

    @Test
    public void testSearchPattern_SingleByte_NoMatch_1() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 0 });
        byte[] pattern = new byte[]{ 1 };
        Assert.assertFalse(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(0, buf.position());
    }

    @Test
    public void testSearchPattern_SingleByte_NoMatch_2() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 0, 0, 0, 0, 0 });
        byte[] pattern = new byte[]{ 1 };
        Assert.assertFalse(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(0, buf.position());
    }

    @Test
    public void testSearchPattern_MultiByte_MatchesAllBuffer() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 1, 1, 1, 1, 1 });
        byte[] pattern = new byte[]{ 1, 1, 1, 1, 1 };
        Assert.assertTrue(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(buf.limit(), buf.position());
    }

    @Test
    public void testSearchPattern_MultiByte_MatchesBeginningOfBuffer() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
        byte[] pattern = new byte[]{ 1, 1, 1, 1, 1 };
        Assert.assertTrue(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(5, buf.position());
    }

    @Test
    public void testSearchPattern_MultiByte_MatchesMiddleOfBuffer() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
        byte[] pattern = new byte[]{ 1, 1, 1, 1, 1 };
        Assert.assertTrue(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(10, buf.position());
    }

    @Test
    public void testSearchPattern_MultiByte_MatchesEndOfBuffer() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 0, 0, 0, 0, 0, 1, 1, 1, 1, 1 });
        byte[] pattern = new byte[]{ 1, 1, 1, 1, 1 };
        Assert.assertTrue(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(buf.limit(), buf.position());
    }

    @Test
    public void testSearchPattern_MultiByte_NoMatch_1() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 0, 0, 0, 0, 0 });
        byte[] pattern = new byte[]{ 1, 1, 1, 1, 1 };
        Assert.assertFalse(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(0, buf.position());
    }

    @Test
    public void testSearchPattern_MultiByte_NoMatch_2() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        byte[] pattern = new byte[]{ 1, 1, 1, 1, 1 };
        Assert.assertFalse(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(0, buf.position());
    }

    @Test
    public void testSearchPattern_SingleByte_EmptyBuffer() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[0]);
        byte[] pattern = new byte[]{ 1 };
        Assert.assertFalse(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(0, buf.position());
    }

    @Test
    public void testSearchPattern_MultiByte_EmptyBuffer() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[0]);
        byte[] pattern = new byte[]{ 1, 1, 1, 1, 1 };
        Assert.assertFalse(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(0, buf.position());
    }

    @Test
    public void testSearchPattern_MultiByte_LongerThanBuffer() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 0, 0, 0 });
        byte[] pattern = new byte[]{ 1, 1, 1, 1, 1 };
        Assert.assertFalse(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(0, buf.position());
    }

    @Test
    public void testSearchPattern_MultiByte_LongerThanBuffer_BufferContainsPrefix() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[]{ 1, 1, 1 });
        byte[] pattern = new byte[]{ 1, 1, 1, 1, 1 };
        Assert.assertFalse(Buffers.searchPattern(buf, pattern));
        Assert.assertEquals(0, buf.position());
    }

    @Test
    public void testSearchPattern_EmptyPattern() {
        ByteBuffer buf = ByteBuffer.wrap(new byte[0]);
        byte[] pattern = new byte[0];
        TestUtil.assertExceptionWithMessage(( it) -> Buffers.searchPattern(buf, pattern), "Empty pattern");
    }
}

