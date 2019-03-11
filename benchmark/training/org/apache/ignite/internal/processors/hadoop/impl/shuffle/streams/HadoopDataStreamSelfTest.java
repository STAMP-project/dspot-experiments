/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.hadoop.impl.shuffle.streams;


import java.io.IOException;
import java.util.Arrays;
import org.apache.ignite.internal.processors.hadoop.shuffle.direct.HadoopDirectDataInput;
import org.apache.ignite.internal.processors.hadoop.shuffle.direct.HadoopDirectDataOutput;
import org.apache.ignite.internal.processors.hadoop.shuffle.streams.HadoopDataInStream;
import org.apache.ignite.internal.processors.hadoop.shuffle.streams.HadoopDataOutStream;
import org.apache.ignite.internal.util.offheap.unsafe.GridUnsafeMemory;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class HadoopDataStreamSelfTest extends GridCommonAbstractTest {
    private static final int BUFF_SIZE = 4 * 1024;

    /**
     *
     *
     * @throws IOException
     * 		If failed.
     */
    @Test
    public void testStreams() throws IOException {
        GridUnsafeMemory mem = new GridUnsafeMemory(0);
        HadoopDataOutStream out = new HadoopDataOutStream(mem);
        final long ptr = mem.allocate(HadoopDataStreamSelfTest.BUFF_SIZE);
        out.buffer().set(ptr, HadoopDataStreamSelfTest.BUFF_SIZE);
        write(out);
        HadoopDataInStream in = new HadoopDataInStream(mem);
        in.buffer().set(ptr, ((out.buffer().pointer()) - ptr));
        checkRead(in);
    }

    /**
     *
     *
     * @throws IOException
     * 		If failed.
     */
    @Test
    public void testDirectStreams() throws IOException {
        HadoopDirectDataOutput out = new HadoopDirectDataOutput(HadoopDataStreamSelfTest.BUFF_SIZE);
        write(out);
        byte[] inBuf = Arrays.copyOf(out.buffer(), out.position());
        HadoopDirectDataInput in = new HadoopDirectDataInput(inBuf);
        checkRead(in);
    }

    /**
     *
     *
     * @throws IOException
     * 		If failed.
     */
    @Test
    public void testReadline() throws IOException {
        checkReadLine("String1\rString2\r\nString3\nString4");
        checkReadLine("String1\rString2\r\nString3\nString4\r\n");
        checkReadLine("String1\rString2\r\nString3\nString4\r");
        checkReadLine("\nA\rB\r\nC\nD\n");
        checkReadLine("\rA\rB\r\nC\nD\n");
        checkReadLine("\r\nA\rB\r\nC\nD\n");
        checkReadLine("\r\r\nA\r\r\nC\nD\n");
        checkReadLine("\r\r\r\n\n\n");
        checkReadLine("\r\n");
        checkReadLine("\r");
        checkReadLine("\n");
    }
}

