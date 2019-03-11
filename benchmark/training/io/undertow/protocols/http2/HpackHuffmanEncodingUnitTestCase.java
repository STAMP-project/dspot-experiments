/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.protocols.http2;


import io.undertow.testutils.category.UnitTest;
import java.nio.ByteBuffer;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 *
 *
 * @author Stuart Douglas
 */
@Category(UnitTest.class)
public class HpackHuffmanEncodingUnitTestCase {
    @Test
    public void testHuffmanEncoding() throws HpackException {
        runTest("Hello World", ByteBuffer.allocate(100), true);
        runTest("Hello World", ByteBuffer.allocate(3), false);
        runTest("\\randomSpecialsChars~\u001d", ByteBuffer.allocate(100), true);
        runTest("\\~\u001d", ByteBuffer.allocate(100), false);// encoded form is larger than the original string

    }

    @Test
    public void testHuffmanEncodingLargeString() throws HpackException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; ++i) {
            sb.append("Hello World");
        }
        runTest(sb.toString(), ByteBuffer.allocate(10000), true);// encoded form is larger than the original string

    }
}

