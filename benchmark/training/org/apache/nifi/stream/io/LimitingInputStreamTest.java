/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.stream.io;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import junit.framework.TestCase;


public class LimitingInputStreamTest extends TestCase {
    private static final byte[] TEST_BUFFER = new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    public void testReadLimitNotReached() throws IOException {
        final LimitingInputStream is = new LimitingInputStream(new ByteArrayInputStream(LimitingInputStreamTest.TEST_BUFFER), 50);
        long bytesRead = StreamUtils.copy(is, new ByteArrayOutputStream());
        TestCase.assertEquals(bytesRead, LimitingInputStreamTest.TEST_BUFFER.length);
        TestCase.assertFalse(is.hasReachedLimit());
    }

    public void testReadLimitMatched() throws IOException {
        final LimitingInputStream is = new LimitingInputStream(new ByteArrayInputStream(LimitingInputStreamTest.TEST_BUFFER), 10);
        long bytesRead = StreamUtils.copy(is, new ByteArrayOutputStream());
        TestCase.assertEquals(bytesRead, LimitingInputStreamTest.TEST_BUFFER.length);
        TestCase.assertTrue(is.hasReachedLimit());
    }

    public void testReadLimitExceeded() throws IOException {
        final LimitingInputStream is = new LimitingInputStream(new ByteArrayInputStream(LimitingInputStreamTest.TEST_BUFFER), 9);
        final long bytesRead = StreamUtils.copy(is, new ByteArrayOutputStream());
        TestCase.assertEquals(bytesRead, 9);
        TestCase.assertTrue(is.hasReachedLimit());
    }
}

