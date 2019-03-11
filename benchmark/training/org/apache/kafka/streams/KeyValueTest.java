/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams;


import org.junit.Assert;
import org.junit.Test;


public class KeyValueTest {
    @Test
    public void shouldHaveSameEqualsAndHashCode() {
        final KeyValue<String, Long> kv = KeyValue.pair("key1", 1L);
        final KeyValue<String, Long> copyOfKV = KeyValue.pair(kv.key, kv.value);
        // Reflexive
        Assert.assertTrue(kv.equals(kv));
        Assert.assertTrue(((kv.hashCode()) == (kv.hashCode())));
        // Symmetric
        Assert.assertTrue(kv.equals(copyOfKV));
        Assert.assertTrue(((kv.hashCode()) == (copyOfKV.hashCode())));
        Assert.assertTrue(((copyOfKV.hashCode()) == (kv.hashCode())));
        // Transitive
        final KeyValue<String, Long> copyOfCopyOfKV = KeyValue.pair(copyOfKV.key, copyOfKV.value);
        Assert.assertTrue(copyOfKV.equals(copyOfCopyOfKV));
        Assert.assertTrue(((copyOfKV.hashCode()) == (copyOfCopyOfKV.hashCode())));
        Assert.assertTrue(kv.equals(copyOfCopyOfKV));
        Assert.assertTrue(((kv.hashCode()) == (copyOfCopyOfKV.hashCode())));
        // Inequality scenarios
        Assert.assertFalse("must be false for null", kv.equals(null));
        Assert.assertFalse("must be false if key is non-null and other key is null", kv.equals(KeyValue.pair(null, kv.value)));
        Assert.assertFalse("must be false if value is non-null and other value is null", kv.equals(KeyValue.pair(kv.key, null)));
        final KeyValue<Long, Long> differentKeyType = KeyValue.pair(1L, kv.value);
        Assert.assertFalse("must be false for different key types", kv.equals(differentKeyType));
        final KeyValue<String, String> differentValueType = KeyValue.pair(kv.key, "anyString");
        Assert.assertFalse("must be false for different value types", kv.equals(differentValueType));
        final KeyValue<Long, String> differentKeyValueTypes = KeyValue.pair(1L, "anyString");
        Assert.assertFalse("must be false for different key and value types", kv.equals(differentKeyValueTypes));
        Assert.assertFalse("must be false for different types of objects", kv.equals(new Object()));
        final KeyValue<String, Long> differentKey = KeyValue.pair(((kv.key) + "suffix"), kv.value);
        Assert.assertFalse("must be false if key is different", kv.equals(differentKey));
        Assert.assertFalse("must be false if key is different", differentKey.equals(kv));
        final KeyValue<String, Long> differentValue = KeyValue.pair(kv.key, ((kv.value) + 1L));
        Assert.assertFalse("must be false if value is different", kv.equals(differentValue));
        Assert.assertFalse("must be false if value is different", differentValue.equals(kv));
        final KeyValue<String, Long> differentKeyAndValue = KeyValue.pair(((kv.key) + "suffix"), ((kv.value) + 1L));
        Assert.assertFalse("must be false if key and value are different", kv.equals(differentKeyAndValue));
        Assert.assertFalse("must be false if key and value are different", differentKeyAndValue.equals(kv));
    }
}

