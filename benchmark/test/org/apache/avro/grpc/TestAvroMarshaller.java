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
package org.apache.avro.grpc;


import Kind.FOO;
import Protocol.Message;
import TestService.PROTOCOL;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import org.apache.avro.grpc.test.MD5;
import org.apache.avro.grpc.test.TestRecord;
import org.junit.Assert;
import org.junit.Test;


public class TestAvroMarshaller {
    private final TestRecord record = TestRecord.newBuilder().setName("foo").setKind(FOO).setArrayOfLongs(Arrays.asList(42L, 424L, 4242L)).setHash(new MD5(new byte[]{ 4, 2, 4, 2 })).setNullableHash(null).build();

    private final Message message = PROTOCOL.getMessages().get("echo");

    private Random random = new Random();

    @Test
    public void testAvroRequestReadPartialAndDrain() throws IOException {
        AvroRequestMarshaller requestMarshaller = new AvroRequestMarshaller(message);
        InputStream requestInputStream = requestMarshaller.stream(new Object[]{ record });
        ByteArrayOutputStream requestOutputStream = new ByteArrayOutputStream();
        readPratialAndDrain(((random.nextInt(7)) + 1), requestInputStream, requestOutputStream);
        InputStream serialized = new ByteArrayInputStream(requestOutputStream.toByteArray());
        Object[] parsedArgs = requestMarshaller.parse(serialized);
        Assert.assertEquals(1, parsedArgs.length);
        Assert.assertEquals(record, parsedArgs[0]);
    }

    @Test
    public void testAvroResponseReadPartialAndDrain() throws IOException {
        AvroResponseMarshaller responseMarshaller = new AvroResponseMarshaller(message);
        InputStream responseInputStream = responseMarshaller.stream(record);
        ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
        readPratialAndDrain(((random.nextInt(7)) + 1), responseInputStream, responseOutputStream);
        InputStream serialized = new ByteArrayInputStream(responseOutputStream.toByteArray());
        Object parsedResponse = responseMarshaller.parse(serialized);
        Assert.assertEquals(record, parsedResponse);
    }
}

