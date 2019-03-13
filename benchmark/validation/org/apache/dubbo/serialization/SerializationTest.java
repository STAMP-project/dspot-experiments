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
package org.apache.dubbo.serialization;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.dubbo.common.serialize.ObjectInput;
import org.apache.dubbo.common.serialize.ObjectOutput;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class SerializationTest {
    private MySerialization mySerialization;

    private MyObjectOutput myObjectOutput;

    private MyObjectInput myObjectInput;

    private ByteArrayOutputStream byteArrayOutputStream;

    private ByteArrayInputStream byteArrayInputStream;

    @Test
    public void testContentType() {
        MatcherAssert.assertThat(mySerialization.getContentType(), Matchers.is("x-application/my"));
    }

    @Test
    public void testContentTypeId() {
        MatcherAssert.assertThat(mySerialization.getContentTypeId(), Matchers.is(((byte) (101))));
    }

    @Test
    public void testObjectOutput() throws IOException {
        ObjectOutput objectOutput = mySerialization.serialize(null, Mockito.mock(OutputStream.class));
        MatcherAssert.assertThat(objectOutput, Matchers.<ObjectOutput>instanceOf(MyObjectOutput.class));
    }

    @Test
    public void testObjectInput() throws IOException {
        ObjectInput objectInput = mySerialization.deserialize(null, Mockito.mock(InputStream.class));
        MatcherAssert.assertThat(objectInput, Matchers.<ObjectInput>instanceOf(MyObjectInput.class));
    }

    @Test
    public void testWriteUTF() throws IOException {
        myObjectOutput.writeUTF("Pace");
        myObjectOutput.writeUTF("??");
        myObjectOutput.writeUTF(" ???");
        flushToInput();
        MatcherAssert.assertThat(myObjectInput.readUTF(), CoreMatchers.is("Pace"));
        MatcherAssert.assertThat(myObjectInput.readUTF(), CoreMatchers.is("??"));
        MatcherAssert.assertThat(myObjectInput.readUTF(), CoreMatchers.is(" ???"));
    }
}

