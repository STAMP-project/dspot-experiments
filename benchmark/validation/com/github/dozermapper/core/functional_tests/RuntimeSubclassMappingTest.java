/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.vo.runtimesubclass.SpecialUserGroup;
import com.github.dozermapper.core.vo.runtimesubclass.SpecialUserGroupPrime;
import com.github.dozermapper.core.vo.runtimesubclass.User;
import com.github.dozermapper.core.vo.runtimesubclass.UserGroup;
import com.github.dozermapper.core.vo.runtimesubclass.UserPrime;
import org.junit.Assert;
import org.junit.Test;


public class RuntimeSubclassMappingTest extends AbstractFunctionalTest {
    @Test
    public void testSpecialMapping() {
        // SpecialUserGroup(!)
        SpecialUserGroup userGroup = newInstance(SpecialUserGroup.class);
        userGroup.setName("special user group");
        // User in SpecialUserGroup
        User user = newInstance(User.class);
        user.setUserGroup(userGroup);
        // do mapping to UserPrime
        UserPrime userPrime = mapper.map(user, UserPrime.class);
        // check class type of mapped group, should be SpecialUserGroupPrime!
        Assert.assertNotNull(userPrime.getUserGroup());
        Assert.assertEquals("special user group", userPrime.getUserGroup().getName());
        Assert.assertEquals(SpecialUserGroupPrime.class, userPrime.getUserGroup().getClass());
    }

    @Test
    public void testNormalMapping() {
        // normal UserGroup
        UserGroup userGroup = newInstance(UserGroup.class);
        userGroup.setName("user group");
        // User in normal UserGroup
        User user = newInstance(User.class);
        user.setUserGroup(userGroup);
        // do mapping to UserPrime
        UserPrime userPrime = mapper.map(user, UserPrime.class);
        // check class type of mapped group, should NOT be SpecialUserGroupPrime!
        Assert.assertNotNull(userPrime.getUserGroup());
        Assert.assertFalse(((userPrime.getUserGroup()) instanceof SpecialUserGroupPrime));
    }
}

