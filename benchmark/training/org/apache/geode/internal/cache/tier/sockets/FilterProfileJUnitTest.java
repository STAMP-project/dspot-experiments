/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.tier.sockets;


import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.geode.internal.cache.FilterProfile;
import org.apache.geode.internal.cache.LocalRegion;
import org.apache.geode.test.junit.categories.ClientSubscriptionTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientSubscriptionTest.class })
public class FilterProfileJUnitTest {
    private LocalRegion mockRegion;

    private FilterProfile fprofile;

    @Test
    public void testUnregisterKey() {
        unregisterKey(false);
    }

    @Test
    public void testUnregisterKeyInv() {
        unregisterKey(true);
    }

    @Test
    public void testUnregisterTwoKeys() {
        unregisterTwoKeys(false);
    }

    @Test
    public void testUnregisterTwoKeysInv() {
        unregisterTwoKeys(true);
    }

    @Test
    public void testUnregisterAllKey() {
        unregisterAllKey(false);
    }

    @Test
    public void testUnregisterAllKeyInv() {
        unregisterAllKey(true);
    }

    @Test
    public void testUnregisterRegex() {
        unregisterRegex(false);
    }

    @Test
    public void testUnregisterRegexInv() {
        unregisterRegex(true);
    }

    @Test
    public void testUnregisterAllRegex() {
        unregisterAllRegex(false);
    }

    @Test
    public void testUnregisterAllRegexInv() {
        unregisterAllRegex(true);
    }

    @Test
    public void testUnregisterAllKeys() {
        unregisterAllKeys(false);
    }

    @Test
    public void testUnregisterAllKeysInv() {
        unregisterAllKeys(true);
    }

    @Test
    public void testUnregisterFilterClass() {
        unregisterFilterClass(false);
    }

    @Test
    public void testUnregisterFilterClassInv() {
        unregisterFilterClass(true);
    }

    @Test
    public void testUnregisterAllFilterClass() {
        unregisterAllFilterClass(false);
    }

    @Test
    public void testUnregisterAllFilterClassInv() {
        unregisterAllFilterClass(true);
    }

    @Test
    public void testUnregisterRegexNotRegistered() {
        unregisterRegexNotRegistered(false);
    }

    @Test
    public void testUnregisterRegexNotRegisteredInv() {
        unregisterRegexNotRegistered(true);
    }

    @Test
    public void testUnregisterKeyNotRegistered() {
        unregisterKeyNotRegistered(false);
    }

    @Test
    public void testUnregisterKeyNotRegisteredInv() {
        unregisterKeyNotRegistered(true);
    }

    @Test
    public void testUnregisterFilterNotRegistered() {
        unregisterFilterNotRegistered(false);
    }

    @Test
    public void testUnregisterFilterNotRegisteredInv() {
        unregisterFilterNotRegistered(true);
    }

    @Test
    public void testUnregisterAllKeysNotRegistered() {
        unregisterAllKeysNotRegistered(false);
    }

    @Test
    public void testUnregisterAllKeysNotRegisteredInv() {
        unregisterAllKeysNotRegistered(true);
    }

    @Test
    public void testUnregisterAllFilterNotRegistered() {
        unregisterAllFilterNotRegistered(false);
    }

    @Test
    public void testUnregisterAllFilterNotRegisteredInv() {
        unregisterAllFilterNotRegistered(true);
    }

    @Test
    public void testRegisterUnregisterClientInterestListAndVerifyKeysRegistered() {
        registerUnregisterClientInterestListAndVerifyKeysRegistered(false);
    }

    @Test
    public void testRegisterUnregisterClientInterestListInvAndVerifyKeysRegistered() {
        registerUnregisterClientInterestListAndVerifyKeysRegistered(true);
    }

    @Test
    public void testRegisterUnregisterClientInterestListsAndVerifyKeysRegistered() {
        String clientId = "client";
        List<String> keys = Arrays.asList("K1", "K2");
        Set registeredKeys = fprofile.registerClientInterestList(clientId, keys, false);
        // Register interest with invalidates.
        keys = Arrays.asList("K3", "K4");
        registeredKeys = fprofile.registerClientInterestList(clientId, keys, true);
        // Unregister keys from both list.
        keys = Arrays.asList("K2", "K3", "K5");// K5 is not registered.

        registeredKeys = fprofile.unregisterClientInterestList(clientId, keys);
        Assert.assertEquals(2, registeredKeys.size());
        Assert.assertFalse("Expected key not found in registered list.", registeredKeys.contains("K5"));
        Set keySet = fprofile.getKeysOfInterest(clientId);
        Assert.assertEquals(1, keySet.size());
        Assert.assertTrue("Expected key not found in registered list.", keySet.contains("K1"));
        keySet = fprofile.getKeysOfInterestInv(clientId);
        Assert.assertEquals(1, keySet.size());
        Assert.assertTrue("Expected key not found in registered list.", keySet.contains("K4"));
    }
}

