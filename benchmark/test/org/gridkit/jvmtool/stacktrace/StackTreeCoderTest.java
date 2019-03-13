/**
 * Copyright 2014 Alexey Ragozin
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
package org.gridkit.jvmtool.stacktrace;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


public class StackTreeCoderTest {
    @Test
    public void test() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2014, 1, 1);
        long time = cal.getTimeInMillis();
        System.out.println(time);
    }

    @Test
    public void verifyVarLongBoundaries() throws IOException {
        ByteArrayOutputStream bos;
        DataOutputStream dos;
        bos = new ByteArrayOutputStream((4 << 20));
        dos = new DataOutputStream(bos);
        StackTraceCodec.writeVarLong(dos, 0);
        StackTraceCodec.writeVarLong(dos, 1);
        StackTraceCodec.writeVarLong(dos, (-1));
        StackTraceCodec.writeVarLong(dos, Long.MAX_VALUE);
        StackTraceCodec.writeVarLong(dos, Long.MIN_VALUE);
        StackTraceCodec.writeVarLong(dos, (4294967295L & (Integer.MAX_VALUE)));
        StackTraceCodec.writeVarLong(dos, (4294967295L & (Integer.MIN_VALUE)));
        byte[] buf = bos.toByteArray();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buf));
        Assert.assertEquals(0, StackTraceCodec.readVarLong(dis));
        Assert.assertEquals(1, StackTraceCodec.readVarLong(dis));
        Assert.assertEquals((-1), StackTraceCodec.readVarLong(dis));
        Assert.assertEquals(Long.MAX_VALUE, StackTraceCodec.readVarLong(dis));
        Assert.assertEquals(Long.MIN_VALUE, StackTraceCodec.readVarLong(dis));
        Assert.assertEquals((4294967295L & (Integer.MAX_VALUE)), StackTraceCodec.readVarLong(dis));
        Assert.assertEquals((4294967295L & (Integer.MIN_VALUE)), StackTraceCodec.readVarLong(dis));
    }
}

