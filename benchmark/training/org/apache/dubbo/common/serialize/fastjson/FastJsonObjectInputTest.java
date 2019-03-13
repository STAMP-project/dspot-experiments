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
package org.apache.dubbo.common.serialize.fastjson;


import com.alibaba.fastjson.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;
import org.apache.dubbo.common.serialize.model.Person;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class FastJsonObjectInputTest {
    private FastJsonObjectInput fastJsonObjectInput;

    @Test
    public void testReadBool() throws IOException {
        fastJsonObjectInput = new FastJsonObjectInput(new ByteArrayInputStream("true".getBytes()));
        boolean result = fastJsonObjectInput.readBool();
        MatcherAssert.assertThat(result, Is.is(true));
        fastJsonObjectInput = new FastJsonObjectInput(new StringReader("false"));
        result = fastJsonObjectInput.readBool();
        MatcherAssert.assertThat(result, Is.is(false));
    }

    @Test
    public void testReadByte() throws IOException {
        fastJsonObjectInput = new FastJsonObjectInput(new ByteArrayInputStream("123".getBytes()));
        Byte result = fastJsonObjectInput.readByte();
        MatcherAssert.assertThat(result, Is.is(Byte.parseByte("123")));
    }

    @Test
    public void testReadBytes() throws IOException {
        fastJsonObjectInput = new FastJsonObjectInput(new ByteArrayInputStream("123456".getBytes()));
        byte[] result = fastJsonObjectInput.readBytes();
        MatcherAssert.assertThat(result, Is.is("123456".getBytes()));
    }

    @Test
    public void testReadShort() throws IOException {
        fastJsonObjectInput = new FastJsonObjectInput(new StringReader("1"));
        short result = fastJsonObjectInput.readShort();
        MatcherAssert.assertThat(result, Is.is(((short) (1))));
    }

    @Test
    public void testReadInt() throws IOException {
        fastJsonObjectInput = new FastJsonObjectInput(new StringReader("1"));
        Integer result = fastJsonObjectInput.readInt();
        MatcherAssert.assertThat(result, Is.is(1));
    }

    @Test
    public void testReadDouble() throws IOException {
        fastJsonObjectInput = new FastJsonObjectInput(new StringReader("1.88"));
        Double result = fastJsonObjectInput.readDouble();
        MatcherAssert.assertThat(result, Is.is(1.88));
    }

    @Test
    public void testReadLong() throws IOException {
        fastJsonObjectInput = new FastJsonObjectInput(new StringReader("10"));
        Long result = fastJsonObjectInput.readLong();
        MatcherAssert.assertThat(result, Is.is(10L));
    }

    @Test
    public void testReadFloat() throws IOException {
        fastJsonObjectInput = new FastJsonObjectInput(new StringReader("1.66"));
        Float result = fastJsonObjectInput.readFloat();
        MatcherAssert.assertThat(result, Is.is(1.66F));
    }

    @Test
    public void testReadUTF() throws IOException {
        fastJsonObjectInput = new FastJsonObjectInput(new StringReader("\"wording\""));
        String result = fastJsonObjectInput.readUTF();
        MatcherAssert.assertThat(result, Is.is("wording"));
    }

    @Test
    public void testReadObject() throws IOException, ClassNotFoundException {
        fastJsonObjectInput = new FastJsonObjectInput(new StringReader("{ \"name\":\"John\", \"age\":30 }"));
        Person result = fastJsonObjectInput.readObject(Person.class);
        MatcherAssert.assertThat(result, CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(result.getName(), Is.is("John"));
        MatcherAssert.assertThat(result.getAge(), Is.is(30));
    }

    @Test
    public void testEmptyLine() throws IOException, ClassNotFoundException {
        Assertions.assertThrows(EOFException.class, () -> {
            fastJsonObjectInput = new FastJsonObjectInput(new StringReader(""));
            fastJsonObjectInput.readObject();
        });
    }

    @Test
    public void testEmptySpace() throws IOException, ClassNotFoundException {
        Assertions.assertThrows(EOFException.class, () -> {
            fastJsonObjectInput = new FastJsonObjectInput(new StringReader("  "));
            fastJsonObjectInput.readObject();
        });
    }

    @Test
    public void testReadObjectWithoutClass() throws IOException, ClassNotFoundException {
        fastJsonObjectInput = new FastJsonObjectInput(new StringReader("{ \"name\":\"John\", \"age\":30 }"));
        JSONObject readObject = ((JSONObject) (fastJsonObjectInput.readObject()));
        MatcherAssert.assertThat(readObject, CoreMatchers.not(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(readObject.getString("name"), Is.is("John"));
        MatcherAssert.assertThat(readObject.getInteger("age"), Is.is(30));
    }
}

