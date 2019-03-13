/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.reflect;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import org.apache.avro.Schema;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestByteBuffer {
    @Rule
    public TemporaryFolder DIR = new TemporaryFolder();

    static class X {
        String name = "";

        ByteBuffer content;
    }

    File content;

    @Test
    public void test() throws Exception {
        Schema schema = ReflectData.get().getSchema(TestByteBuffer.X.class);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        writeOneXAsAvro(schema, bout);
        TestByteBuffer.X record = readOneXFromAvro(schema, bout);
        String expected = getmd5(content);
        String actual = getmd5(record.content);
        Assert.assertEquals("md5 for result differed from input", expected, actual);
    }
}

