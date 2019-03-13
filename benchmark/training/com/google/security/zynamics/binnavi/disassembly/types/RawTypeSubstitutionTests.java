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
package com.google.security.zynamics.binnavi.disassembly.types;


import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.IAddress;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * This class contains tests related to {@link RawTypeSubstitution raw type substitutions}.
 */
@RunWith(JUnit4.class)
public class RawTypeSubstitutionTests {
    private static final IAddress ADDRESS = new CAddress(4096);

    private static final int POSITION = 1;

    private static final int EXPRESSION_ID = 2;

    private static final int BASE_TYPE_ID = 3;

    private static final Integer[] PATH = new Integer[]{ 1, 2, 3 };

    private static final Integer OFFSET = 10;

    @Test
    public void testConstruction() {
        final RawTypeSubstitution rawSubstitution = new RawTypeSubstitution(RawTypeSubstitutionTests.ADDRESS, RawTypeSubstitutionTests.POSITION, RawTypeSubstitutionTests.EXPRESSION_ID, RawTypeSubstitutionTests.BASE_TYPE_ID, RawTypeSubstitutionTests.PATH, RawTypeSubstitutionTests.OFFSET);
        Assert.assertEquals(RawTypeSubstitutionTests.ADDRESS, rawSubstitution.getAddress());
        Assert.assertEquals(RawTypeSubstitutionTests.POSITION, rawSubstitution.getPosition());
        Assert.assertEquals(RawTypeSubstitutionTests.EXPRESSION_ID, rawSubstitution.getExpressionId());
        Assert.assertEquals(RawTypeSubstitutionTests.BASE_TYPE_ID, rawSubstitution.getBaseTypeId());
        Assert.assertArrayEquals(RawTypeSubstitutionTests.PATH, rawSubstitution.getPath());
        Assert.assertEquals(RawTypeSubstitutionTests.OFFSET, rawSubstitution.getOffset());
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidConstruction0() {
        new RawTypeSubstitution(null, RawTypeSubstitutionTests.POSITION, RawTypeSubstitutionTests.EXPRESSION_ID, RawTypeSubstitutionTests.BASE_TYPE_ID, RawTypeSubstitutionTests.PATH, RawTypeSubstitutionTests.OFFSET);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConstruction1() {
        new RawTypeSubstitution(RawTypeSubstitutionTests.ADDRESS, (-1), RawTypeSubstitutionTests.EXPRESSION_ID, RawTypeSubstitutionTests.BASE_TYPE_ID, RawTypeSubstitutionTests.PATH, RawTypeSubstitutionTests.OFFSET);
    }
}

