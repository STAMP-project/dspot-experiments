/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.rocketmq.spout.scheme;


import StringScheme.STRING_SCHEME_KEY;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import org.apache.storm.tuple.Fields;
import org.junit.Assert;
import org.junit.Test;


public class StringKeyValueSchemeTest {
    private StringKeyValueScheme scheme = new StringKeyValueScheme();

    @Test
    public void testDeserialize() throws Exception {
        Assert.assertEquals(Collections.singletonList("test"), scheme.deserialize(StringKeyValueSchemeTest.wrapString("test")));
    }

    @Test
    public void testGetOutputFields() throws Exception {
        Fields outputFields = scheme.getOutputFields();
        Assert.assertTrue(outputFields.contains(STRING_SCHEME_KEY));
        Assert.assertEquals(1, outputFields.size());
    }

    @Test
    public void testDeserializeWithNullKeyAndValue() throws Exception {
        Assert.assertEquals(Collections.singletonList("test"), scheme.deserializeKeyAndValue(null, StringKeyValueSchemeTest.wrapString("test")));
    }

    @Test
    public void testDeserializeWithKeyAndValue() throws Exception {
        Assert.assertEquals(Collections.singletonList(ImmutableMap.of("key", "test")), scheme.deserializeKeyAndValue(StringKeyValueSchemeTest.wrapString("key"), StringKeyValueSchemeTest.wrapString("test")));
    }
}

