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
import java.nio.charset.StandardCharsets;
import junit.framework.TestCase;


public class ByteCountingInputStreamTest extends TestCase {
    public void testReset() throws Exception {
        final ByteArrayInputStream reader = new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(StandardCharsets.UTF_8));
        final ByteCountingInputStream bcis = new ByteCountingInputStream(reader);
        int tmp;
        /* verify first 2 bytes */
        tmp = bcis.read();
        TestCase.assertEquals(tmp, 97);
        tmp = bcis.read();
        TestCase.assertEquals(tmp, 98);
        /* save bytes read and place mark */
        final long bytesAtMark = bcis.getBytesRead();
        bcis.mark(0);
        /* verify next 2 bytes */
        tmp = bcis.read();
        TestCase.assertEquals(tmp, 99);
        tmp = bcis.read();
        TestCase.assertEquals(tmp, 100);
        /* verify reset returns to position when mark was placed */
        bcis.reset();
        TestCase.assertEquals(bytesAtMark, bcis.getBytesRead());
        /* verify that the reset bug has been fixed (bug would reduce bytes read count) */
        bcis.reset();
        TestCase.assertEquals(bytesAtMark, bcis.getBytesRead());
    }

    public void testAvailableShouldReturnCorrectCount() throws Exception {
        // Arrange
        final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(ALPHABET.getBytes(StandardCharsets.UTF_8));
        final ByteCountingInputStream bcis = new ByteCountingInputStream(inputStream);
        int tmp;
        int initialAvailableBytes = bcis.available();
        TestCase.assertEquals(ALPHABET.length(), initialAvailableBytes);
        // Act
        /* verify first 2 bytes */
        tmp = bcis.read();
        TestCase.assertEquals(tmp, 97);
        tmp = bcis.read();
        TestCase.assertEquals(tmp, 98);
        int availableBytes = bcis.available();
        TestCase.assertEquals(((ALPHABET.length()) - 2), availableBytes);
        bcis.skip(24);
        // Assert
        int finalAvailableBytes = bcis.available();
        TestCase.assertEquals(0, finalAvailableBytes);
    }
}

