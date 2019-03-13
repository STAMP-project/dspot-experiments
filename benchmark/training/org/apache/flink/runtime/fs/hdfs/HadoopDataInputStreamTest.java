/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.fs.hdfs;


import java.io.IOException;
import org.apache.flink.core.memory.ByteArrayInputStreamWithPos;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.PositionedReadable;
import org.apache.hadoop.fs.Seekable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static HadoopDataInputStream.MIN_SKIP_BYTES;


/**
 * Tests for the {@link HadoopDataInputStream}.
 */
public class HadoopDataInputStreamTest {
    private FSDataInputStream verifyInputStream;

    private HadoopDataInputStream testInputStream;

    @Test
    public void testSeekSkip() throws IOException {
        verifyInputStream = Mockito.spy(new FSDataInputStream(new HadoopDataInputStreamTest.SeekableByteArrayInputStream(new byte[2 * (MIN_SKIP_BYTES)])));
        testInputStream = new HadoopDataInputStream(verifyInputStream);
        seekAndAssert(10);
        seekAndAssert(((10 + (MIN_SKIP_BYTES)) + 1));
        seekAndAssert(((testInputStream.getPos()) - 1));
        seekAndAssert(((testInputStream.getPos()) + 1));
        seekAndAssert(((testInputStream.getPos()) - (MIN_SKIP_BYTES)));
        seekAndAssert(testInputStream.getPos());
        seekAndAssert(0);
        seekAndAssert(((testInputStream.getPos()) + (MIN_SKIP_BYTES)));
        seekAndAssert((((testInputStream.getPos()) + (MIN_SKIP_BYTES)) - 1));
        try {
            seekAndAssert((-1));
            Assert.fail();
        } catch (Exception ignore) {
        }
        try {
            seekAndAssert(((-(MIN_SKIP_BYTES)) - 1));
            Assert.fail();
        } catch (Exception ignore) {
        }
    }

    private static final class SeekableByteArrayInputStream extends ByteArrayInputStreamWithPos implements PositionedReadable , Seekable {
        public SeekableByteArrayInputStream(byte[] buffer) {
            super(buffer);
        }

        @Override
        public void seek(long pos) throws IOException {
            setPosition(((int) (pos)));
        }

        @Override
        public long getPos() throws IOException {
            return getPosition();
        }

        @Override
        public boolean seekToNewSource(long targetPos) throws IOException {
            return false;
        }

        @Override
        public int read(long position, byte[] buffer, int offset, int length) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void readFully(long position, byte[] buffer, int offset, int length) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void readFully(long position, byte[] buffer) throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}

