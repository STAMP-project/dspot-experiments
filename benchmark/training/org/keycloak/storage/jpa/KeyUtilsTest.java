/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.storage.jpa;


import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author hmlnarik
 */
public class KeyUtilsTest {
    @Test
    public void testValidKeys() {
        Assert.assertTrue(KeyUtils.isValidKey(UUID.randomUUID().toString()));
        Assert.assertTrue(KeyUtils.isValidKey("01234567-1234-1234-aAAa-123456789012"));
        Assert.assertTrue(KeyUtils.isValidKey("01234567-1234-1234-aAAf-123456789012"));
        Assert.assertTrue(KeyUtils.isValidKey((("f:" + (UUID.randomUUID())) + ":dsadsada")));
        Assert.assertTrue(KeyUtils.isValidKey("f:01234567-1234-1234-aAAa-123456789012:dsadsada"));
        Assert.assertTrue(KeyUtils.isValidKey("f:a1234567-1234-1234-aAAa-123456789012:dsadsada"));
    }

    @Test
    public void testInvalidKeys() {
        Assert.assertFalse(KeyUtils.isValidKey("any string"));
        Assert.assertFalse(KeyUtils.isValidKey("0"));
        Assert.assertFalse(KeyUtils.isValidKey("01234567-1234-1234-aAAg-123456789012a"));
        Assert.assertFalse(KeyUtils.isValidKey("z1234567-1234-1234-aAAa-123456789012"));
        Assert.assertFalse(KeyUtils.isValidKey("f:g1234567-1234-1234-aAAa-123456789012:dsadsada"));
        Assert.assertFalse(KeyUtils.isValidKey("g:a1234567-1234-1234-aAAa-123456789012:dsadsada"));
        Assert.assertFalse(KeyUtils.isValidKey("f:a1234567-1234-1234-aAAa-123456789012"));
    }
}

