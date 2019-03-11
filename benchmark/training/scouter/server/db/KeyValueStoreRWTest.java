/**
 * Copyright 2015 the original author or authors.
 *  @https://github.com/scouter-project/scouter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package scouter.server.db;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gun Lee (gunlee01@gmail.com) on 2018. 3. 1.
 */
public class KeyValueStoreRWTest {
    private static final String vutDiv = "junit-test";

    private static final String vutKey1 = "testkey-01";

    private static final String vutValue1 = "testvalue-01";

    private static final String vutKey2 = "testkey-02";

    private static final String vutValue2 = "testvalue-02";

    @Test
    public void set() {
        KeyValueStoreRW.set(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1, KeyValueStoreRWTest.vutValue1);
        String getValue = KeyValueStoreRW.get(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1);
        Assert.assertEquals(KeyValueStoreRWTest.vutValue1, getValue);
    }

    @Test
    public void delete() {
        KeyValueStoreRW.set(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1, KeyValueStoreRWTest.vutValue1);
        String getValue = KeyValueStoreRW.get(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1);
        Assert.assertEquals(KeyValueStoreRWTest.vutValue1, getValue);
        KeyValueStoreRW.delete(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1);
        Assert.assertNull(KeyValueStoreRWTest.vutValue2, KeyValueStoreRW.get(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1));
    }

    @Test
    public void set_and_set_on_the_same_key() {
        // given : there is one key(vutKey1) set with value - (vutValue1)
        KeyValueStoreRW.set(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1, KeyValueStoreRWTest.vutValue1);
        String getValue = KeyValueStoreRW.get(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1);
        Assert.assertEquals(KeyValueStoreRWTest.vutValue1, getValue);
        // when : if set different value(vutValue2) on the same key(vutKey1)
        KeyValueStoreRW.set(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1, KeyValueStoreRWTest.vutValue2);
        // then : the value would be changed
        Assert.assertEquals(KeyValueStoreRWTest.vutValue2, KeyValueStoreRW.get(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1));
    }

    @Test
    public void set_value_and_ttl() {
        // when : if set value and ttl
        KeyValueStoreRW.set(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1, KeyValueStoreRWTest.vutValue1, 3);
        // then : the value would be acquired
        Assert.assertEquals(KeyValueStoreRWTest.vutValue1, KeyValueStoreRW.get(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1));
    }

    @Test
    public void set_value_and_ttl_and_check_expired() {
        // when : if set value and ttl
        KeyValueStoreRW.set(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1, KeyValueStoreRWTest.vutValue1, 2);
        // then : after ttl the value is null
        sleep(3000);
        Assert.assertNull(KeyValueStoreRW.get(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1));
    }

    @Test
    public void set_ttl_after_set_value() {
        // given : there is one key(vutKey1) set with value - (vutValue1)
        KeyValueStoreRW.set(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1, KeyValueStoreRWTest.vutValue1);
        // when : if set ttl
        KeyValueStoreRW.setTTL(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1, 3);
        // then : the value would be acquired
        Assert.assertEquals(KeyValueStoreRWTest.vutValue1, KeyValueStoreRW.get(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1));
    }

    @Test
    public void set_ttl_after_set_value_and_check_expired() {
        // given : there is one key(vutKey1) set with value - (vutValue1)
        KeyValueStoreRW.set(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1, KeyValueStoreRWTest.vutValue1);
        // when : if set ttl
        KeyValueStoreRW.setTTL(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1, 2);
        // then : after ttl the value is null
        sleep(3000);
        Assert.assertNull(KeyValueStoreRW.get(KeyValueStoreRWTest.vutDiv, KeyValueStoreRWTest.vutKey1));
    }
}

