/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.ipc.specific;


import java.io.IOException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Protocol;
import org.junit.Assert;
import org.junit.Test;


public class TestSpecificRequestor {
    public interface SampleSpecificProtocol {
        public static final Protocol PROTOCOL = Protocol.parse("{\"protocol\":\"SampleSpecificProtocol\",\"namespace\":\"org.apache.avro.ipc.specific\",\"types\":[],\"messages\":{}}");
    }

    static Object proxy;

    @Test
    public void testHashCode() throws IOException {
        try {
            TestSpecificRequestor.proxy.hashCode();
        } catch (AvroRuntimeException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testEquals() throws IOException {
        try {
            TestSpecificRequestor.proxy.equals(TestSpecificRequestor.proxy);
        } catch (AvroRuntimeException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testToString() throws IOException {
        try {
            TestSpecificRequestor.proxy.toString();
        } catch (AvroRuntimeException e) {
            Assert.fail(e.getMessage());
        }
    }
}

