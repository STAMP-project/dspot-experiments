/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.security;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class KeystoreWrapperTest {
    @Test
    public void testSetValue() throws Exception {
        KeystoreWrapper keystoreWrapper = KeystoreWrapper.newStore().build();
        keystoreWrapper.setSecureSetting("key", "swordfish");
        Assert.assertThat(keystoreWrapper.getSecureSetting("key"), Matchers.is("swordfish"));
    }

    @Test
    public void testEmptyKeystore() throws Exception {
        Assert.assertThat(KeystoreWrapper.newStore().build().getSecureSetting("anything"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testStoreLoad() throws Exception {
        KeystoreWrapper keystoreWrapper = KeystoreWrapper.newStore().build();
        keystoreWrapper.setSecureSetting("key", "swordfish");
        Assert.assertThat(keystoreWrapper.getSecureSetting("key"), Matchers.is("swordfish"));
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
        keystoreWrapper.saveKeystore(stream);
        byte[] data = stream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        KeystoreWrapper loaded = KeystoreWrapper.loadStore(inputStream).build();
        Assert.assertThat(loaded.getSecureSetting("key"), Matchers.is("swordfish"));
    }
}

