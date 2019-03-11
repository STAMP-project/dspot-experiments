/**
 * Copyright 2014 Google Inc. All Rights Reserved.
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
package com.google.security.zynamics.binnavi.API.disassembly;


import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class AddressTest {
    @Test
    public void testConstructor() {
        final Address address = new Address(291);
        Assert.assertEquals(291, address.toLong());
        Assert.assertEquals("123", address.toHexString());
        Assert.assertEquals("123", address.toString());
    }

    @Test
    public void testPythonFunctions() {
        final Address address1 = new Address(100);
        final Address address2 = new Address(50);
        Assert.assertEquals((100 + 50), address1.__add__(address2).toLong());
        Assert.assertEquals((100 + 50), address1.__add__(50).toLong());
        Assert.assertEquals((100 + 50), address1.__add__(BigInteger.valueOf(50)).toLong());
        Assert.assertEquals((100 & 50), address1.__and__(address2).toLong());
        Assert.assertFalse(address1.__eq__(address2));
        Assert.assertTrue(address1.__eq__(100));
        Assert.assertTrue(address1.__ge__(address2));
        Assert.assertFalse(address1.__ge__(200));
        Assert.assertTrue(address1.__gt__(address2));
        Assert.assertFalse(address1.__gt__(address1));
        Assert.assertFalse(address1.__le__(address2));
        Assert.assertTrue(address1.__le__(address1));
        Assert.assertFalse(address1.__lt__(address2));
        Assert.assertTrue(address1.__lt__(200));
        Assert.assertEquals(100, address1.__long__());
        Assert.assertEquals((100 << 1), address1.__lshift__(1).toLong());
        Assert.assertEquals((100 * 50), address1.__mul__(address2).toLong());
        Assert.assertTrue(address1.__ne__(address2));
        Assert.assertFalse(address1.__ne__(address1));
        Assert.assertEquals((100 | 50), address1.__or__(address2).toLong());
        Assert.assertEquals((100 + 50), address1.__radd__(address2).toLong());
        Assert.assertEquals((100 & 50), address1.__rand__(address2).toLong());
        Assert.assertEquals((100 * 50), address1.__rmul__(address2).toLong());
        Assert.assertEquals((100 | 50), address1.__ror__(address2).toLong());
        Assert.assertEquals((100 >> 1), address1.__rshift__(1).toLong());
        Assert.assertEquals((-50), address1.__rsub__(address2).toLong());
        Assert.assertEquals(50, address1.__sub__(address2).toLong());
        Assert.assertEquals((100 ^ 50), address1.__xor__(address2).toLong());
        Assert.assertEquals((100 ^ 50), address1.__rxor__(address2).toLong());
    }
}

