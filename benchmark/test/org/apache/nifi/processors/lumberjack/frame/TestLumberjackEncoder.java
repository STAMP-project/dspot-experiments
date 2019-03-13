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
package org.apache.nifi.processors.lumberjack.frame;


import java.nio.ByteBuffer;
import javax.xml.bind.DatatypeConverter;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class TestLumberjackEncoder {
    private LumberjackEncoder encoder;

    @Test
    public void testEncode() {
        LumberjackFrame frame = new LumberjackFrame.Builder().version(((byte) (49))).frameType(((byte) (65))).payload(ByteBuffer.allocate(8).putLong(123).array()).build();
        byte[] encoded = encoder.encode(frame);
        Assert.assertArrayEquals(DatatypeConverter.parseHexBinary("3141000000000000007B"), encoded);
    }
}

