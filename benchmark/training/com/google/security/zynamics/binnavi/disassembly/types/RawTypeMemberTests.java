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


import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * This class contains tests related to {@link RawTypeMember raw type members}.
 */
@RunWith(JUnit4.class)
public class RawTypeMemberTests {
    private static final int ID = 1;

    private static final String NAME = "test_raw_type_member";

    private static final int BASE_TYPE_ID = 2;

    private static final Integer PARENT_ID = 3;

    private static final Integer OFFSET = 20;

    private static final Integer ARGUMENT = 30;

    private static final int NUMBER_OF_ELEMENTS = 100;

    @Test
    public void testConstruction() {
        final RawTypeMember rawMember = new RawTypeMember(RawTypeMemberTests.ID, RawTypeMemberTests.NAME, RawTypeMemberTests.BASE_TYPE_ID, RawTypeMemberTests.PARENT_ID, RawTypeMemberTests.OFFSET, null, RawTypeMemberTests.NUMBER_OF_ELEMENTS);
        Assert.assertEquals(RawTypeMemberTests.ID, rawMember.getId());
        Assert.assertEquals(RawTypeMemberTests.NAME, rawMember.getName());
        Assert.assertEquals(RawTypeMemberTests.BASE_TYPE_ID, rawMember.getBaseTypeId());
        Assert.assertEquals(RawTypeMemberTests.PARENT_ID, rawMember.getParentId());
        Assert.assertEquals(Optional.<Integer>of(RawTypeMemberTests.OFFSET), rawMember.getOffset());
        Assert.assertEquals(Optional.<Integer>absent(), rawMember.getArgumentIndex());
        Assert.assertEquals(Optional.<Integer>of(RawTypeMemberTests.NUMBER_OF_ELEMENTS), rawMember.getNumberOfElements());
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidConstruction0() {
        new RawTypeMember(RawTypeMemberTests.ID, null, RawTypeMemberTests.BASE_TYPE_ID, RawTypeMemberTests.PARENT_ID, RawTypeMemberTests.OFFSET, RawTypeMemberTests.ARGUMENT, RawTypeMemberTests.NUMBER_OF_ELEMENTS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConstruction1() {
        new RawTypeMember(RawTypeMemberTests.ID, RawTypeMemberTests.NAME, RawTypeMemberTests.BASE_TYPE_ID, RawTypeMemberTests.PARENT_ID, (-10), RawTypeMemberTests.ARGUMENT, RawTypeMemberTests.NUMBER_OF_ELEMENTS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConstruction2() {
        new RawTypeMember(RawTypeMemberTests.ID, RawTypeMemberTests.NAME, RawTypeMemberTests.BASE_TYPE_ID, RawTypeMemberTests.PARENT_ID, RawTypeMemberTests.OFFSET, RawTypeMemberTests.ARGUMENT, (-1));
    }
}

