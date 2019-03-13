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
package org.apache.hadoop.registry.client.binding;


import PersistencePolicies.APPLICATION_ATTEMPT;
import RegistryUtils.ServiceRecordMarshal;
import ServiceRecord.RECORD_TYPE;
import org.apache.hadoop.registry.RegistryTestHelper;
import org.apache.hadoop.registry.client.exceptions.InvalidRecordException;
import org.apache.hadoop.registry.client.exceptions.NoRecordException;
import org.apache.hadoop.registry.client.types.ServiceRecord;
import org.apache.hadoop.registry.client.types.yarn.PersistencePolicies;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test record marshalling
 */
public class TestMarshalling extends RegistryTestHelper {
    private static final Logger LOG = LoggerFactory.getLogger(TestMarshalling.class);

    @Rule
    public final Timeout testTimeout = new Timeout(10000);

    @Rule
    public TestName methodName = new TestName();

    private static ServiceRecordMarshal marshal;

    @Test
    public void testRoundTrip() throws Throwable {
        String persistence = PersistencePolicies.PERMANENT;
        ServiceRecord record = RegistryTestHelper.createRecord(persistence);
        record.set("customkey", "customvalue");
        record.set("customkey2", "customvalue2");
        RegistryTypeUtils.validateServiceRecord("", record);
        TestMarshalling.LOG.info(TestMarshalling.marshal.toJson(record));
        byte[] bytes = TestMarshalling.marshal.toBytes(record);
        ServiceRecord r2 = TestMarshalling.marshal.fromBytes("", bytes);
        RegistryTestHelper.assertMatches(record, r2);
        RegistryTypeUtils.validateServiceRecord("", r2);
    }

    @Test(expected = NoRecordException.class)
    public void testUnmarshallNoData() throws Throwable {
        TestMarshalling.marshal.fromBytes("src", new byte[]{  });
    }

    @Test(expected = NoRecordException.class)
    public void testUnmarshallNotEnoughData() throws Throwable {
        // this is nominally JSON -but without the service record header
        TestMarshalling.marshal.fromBytes("src", new byte[]{ '{', '}' }, RECORD_TYPE);
    }

    @Test(expected = InvalidRecordException.class)
    public void testUnmarshallNoBody() throws Throwable {
        byte[] bytes = "this is not valid JSON at all and should fail".getBytes();
        TestMarshalling.marshal.fromBytes("src", bytes);
    }

    @Test(expected = InvalidRecordException.class)
    public void testUnmarshallWrongType() throws Throwable {
        byte[] bytes = "{'type':''}".getBytes();
        ServiceRecord serviceRecord = TestMarshalling.marshal.fromBytes("marshalling", bytes);
        RegistryTypeUtils.validateServiceRecord("validating", serviceRecord);
    }

    @Test(expected = NoRecordException.class)
    public void testUnmarshallWrongLongType() throws Throwable {
        ServiceRecord record = new ServiceRecord();
        record.type = "ThisRecordHasALongButNonMatchingType";
        byte[] bytes = TestMarshalling.marshal.toBytes(record);
        ServiceRecord serviceRecord = TestMarshalling.marshal.fromBytes("marshalling", bytes, RECORD_TYPE);
    }

    @Test(expected = NoRecordException.class)
    public void testUnmarshallNoType() throws Throwable {
        ServiceRecord record = new ServiceRecord();
        record.type = "NoRecord";
        byte[] bytes = TestMarshalling.marshal.toBytes(record);
        ServiceRecord serviceRecord = TestMarshalling.marshal.fromBytes("marshalling", bytes, RECORD_TYPE);
    }

    @Test(expected = InvalidRecordException.class)
    public void testRecordValidationWrongType() throws Throwable {
        ServiceRecord record = new ServiceRecord();
        record.type = "NotAServiceRecordType";
        RegistryTypeUtils.validateServiceRecord("validating", record);
    }

    @Test
    public void testUnknownFieldsRoundTrip() throws Throwable {
        ServiceRecord record = RegistryTestHelper.createRecord(APPLICATION_ATTEMPT);
        record.set("key", "value");
        record.set("intval", "2");
        Assert.assertEquals("value", record.get("key"));
        Assert.assertEquals("2", record.get("intval"));
        Assert.assertNull(record.get("null"));
        Assert.assertEquals("defval", record.get("null", "defval"));
        byte[] bytes = TestMarshalling.marshal.toBytes(record);
        ServiceRecord r2 = TestMarshalling.marshal.fromBytes("", bytes);
        Assert.assertEquals("value", r2.get("key"));
        Assert.assertEquals("2", r2.get("intval"));
    }

    @Test
    public void testFieldPropagationInCopy() throws Throwable {
        ServiceRecord record = RegistryTestHelper.createRecord(APPLICATION_ATTEMPT);
        record.set("key", "value");
        record.set("intval", "2");
        ServiceRecord that = new ServiceRecord(record);
        RegistryTestHelper.assertMatches(record, that);
    }
}

