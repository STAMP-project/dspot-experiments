/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.client.impl.protocol;


import AuthenticationStatus.AUTHENTICATED;
import AuthenticationStatus.CREDENTIALS_FAILED;
import AuthenticationStatus.SERIALIZATION_VERSION_MISMATCH;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AuthenticationStatusTest {
    @Test
    public void testGetId() throws Exception {
        Assert.assertEquals(0, AUTHENTICATED.getId());
        Assert.assertEquals(1, CREDENTIALS_FAILED.getId());
        Assert.assertEquals(2, SERIALIZATION_VERSION_MISMATCH.getId());
    }

    @Test
    public void testGetById() throws Exception {
        AuthenticationStatus status = AuthenticationStatus.getById(AUTHENTICATED.getId());
        Assert.assertEquals(AUTHENTICATED, status);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetById_invalidId() throws Exception {
        AuthenticationStatus.getById((-1));
    }
}

