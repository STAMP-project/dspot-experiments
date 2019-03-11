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
package com.google.security.zynamics.zylib.disassembly;


import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 *
 * @author cblichmann@google.com (Christian Blichmann)
 */
@RunWith(JUnit4.class)
public final class AddressTests {
    @Test
    public void testCompare() {
        // Values smaller than 2^63
        Assert.assertTrue(((CAddress.compare(268435456L, 2271560481L)) < 0));
        Assert.assertTrue(((CAddress.compare(268435456L, 268435456L)) == 0));
        Assert.assertTrue(((CAddress.compare(268435456L, 161061273L)) > 0));
        // Test large values with sign-bits set
        Assert.assertTrue(((CAddress.compare(-9223372036854775808L, -9223372036586340352L)) < 0));
        Assert.assertTrue(((CAddress.compare(-9223372036854775808L, -9223372036854775808L)) == 0));
        Assert.assertTrue(((CAddress.compare(-9223372036586340352L, -9223372036854775808L)) > 0));
        // Test large values where sign-bits differ
        Assert.assertTrue(((CAddress.compare(8070450532247928832L, -9223372036586340352L)) < 0));
        Assert.assertTrue(((CAddress.compare(-9223372036586340352L, 8070450532247928832L)) > 0));
    }

    @Test
    public void testCompareToLarge() {
        // Test large values with sign-bits set
        final CAddress compareLarge = new CAddress(-9223372036586340352L);
        Assert.assertTrue(((compareLarge.compareTo(-9223372036317904896L)) < 0));
        Assert.assertTrue(((compareLarge.compareTo(-9223372036586340352L)) == 0));
        Assert.assertTrue(((compareLarge.compareTo(-9223372036854775808L)) > 0));
        Assert.assertTrue(((compareLarge.compareTo(new CAddress(-9223372036317904896L))) < 0));
        Assert.assertTrue(((compareLarge.compareTo(new CAddress(-9223372036586340352L))) == 0));
        Assert.assertTrue(((compareLarge.compareTo(new CAddress(-9223372036854775808L))) > 0));
        // MockAddress is always 0x100, so we have mixed sign-bits
        Assert.assertFalse(((compareLarge.compareTo(new MockAddress())) < 0));
        Assert.assertFalse(((compareLarge.compareTo(new MockAddress())) == 0));
        Assert.assertTrue(((compareLarge.compareTo(new MockAddress())) > 0));
        // Test large values where sign-bits differ
        Assert.assertTrue(((new CAddress(8070450532247928832L).compareTo(-9223372036586340352L)) < 0));
        Assert.assertTrue(((new CAddress(-9223372036586340352L).compareTo(8070450532247928832L)) > 0));
        Assert.assertTrue(((new CAddress(8070450532247928832L).compareTo(new CAddress(-9223372036586340352L))) < 0));
        Assert.assertTrue(((new CAddress(-9223372036586340352L).compareTo(new CAddress(8070450532247928832L))) > 0));
    }

    @Test
    public void testCompareToSmall() {
        // Values smaller than 2^63
        final CAddress compareSmall = new CAddress(268435456L);
        Assert.assertTrue(((compareSmall.compareTo(2271560481L)) < 0));
        Assert.assertTrue(((compareSmall.compareTo(268435456L)) == 0));
        Assert.assertTrue(((compareSmall.compareTo(161061273L)) > 0));
        Assert.assertTrue(((compareSmall.compareTo(new CAddress(2271560481L))) < 0));
        Assert.assertTrue(((compareSmall.compareTo(new CAddress(268435456L))) == 0));
        Assert.assertTrue(((compareSmall.compareTo(new CAddress(161061273L))) > 0));
        // MockAddress is always 0x100
        Assert.assertFalse(((compareSmall.compareTo(new MockAddress())) < 0));
        Assert.assertFalse(((compareSmall.compareTo(new MockAddress())) == 0));
        Assert.assertTrue(((compareSmall.compareTo(new MockAddress())) > 0));
    }

    @Test
    public void testConstruction() {
        final CAddress fromPrimitiveLong = new CAddress(2271560481L);
        Assert.assertEquals(fromPrimitiveLong.toLong(), 2271560481L);
        final IAddress anIAddress = new MockAddress();
        final CAddress fromIAddress = new CAddress(anIAddress);
        Assert.assertEquals(fromIAddress.toLong(), 256);
        final CAddress fromCAddress = new CAddress(new CAddress(2271560481L));
        Assert.assertEquals(fromCAddress.toLong(), 2271560481L);
        final CAddress fromBigInteger = new CAddress(new BigInteger("87654321", 16));
        Assert.assertEquals(fromBigInteger.toLong(), 2271560481L);
        final CAddress fromString = new CAddress("87654321", 16);
        Assert.assertEquals(fromString.toLong(), 2271560481L);
    }

    @Test
    public void testEquals() {
        final CAddress address = new CAddress(2271560481L);
        Assert.assertFalse(address.equals(null));
        Assert.assertFalse(address.equals("SOMESTRING"));
        Assert.assertTrue(address.equals(2271560481L));
        Assert.assertTrue(address.equals(new CAddress(2271560481L)));
        final CAddress addressEqualsMock = new CAddress(256L);
        Assert.assertTrue(addressEqualsMock.equals(new MockAddress()));
    }

    @Test
    public void testToHexString() {
        // Test conversion
        Assert.assertTrue(new CAddress(2271560619L).toHexString().equalsIgnoreCase("876543ab"));
        Assert.assertTrue(new CAddress(-81985529216486896L).toHexString().equalsIgnoreCase("fedcba9876543210"));
        // Test left-padding with zeroes
        Assert.assertEquals(new CAddress(256L).toHexString().length(), 8);
        Assert.assertEquals(new CAddress(4294967296L).toHexString().length(), 16);
    }

    @Test
    public void testToString() {
        // This test is to make sure toString doesn't diverge from
        // toHexString(). While not strictly necessary, some parts of BinNavi
        // and REIL rely on the assumption of the two methods always returning
        // the same result.
        final CAddress small = new CAddress(2271560619L);
        Assert.assertEquals(small.toString(), small.toHexString());
        final CAddress larger = new CAddress(4294967296L);
        Assert.assertEquals(larger.toString(), larger.toHexString());
    }
}

