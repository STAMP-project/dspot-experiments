/**
 * Copyright 2017-present Open Networking Foundation
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
package io.atomix.protocols.raft.session.impl;


import io.atomix.cluster.MemberId;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Member selector manager test.
 */
public class MemberSelectorManagerTest {
    /**
     * Tests the member selector manager.
     */
    @Test
    public void testMemberSelectorManager() throws Exception {
        MemberSelectorManager selectorManager = new MemberSelectorManager();
        Assert.assertNull(selectorManager.leader());
        Assert.assertEquals(0, selectorManager.members().size());
        selectorManager.resetAll();
        Assert.assertNull(selectorManager.leader());
        Assert.assertEquals(0, selectorManager.members().size());
        selectorManager.resetAll(MemberId.from("a"), Arrays.asList(MemberId.from("a"), MemberId.from("b"), MemberId.from("c")));
        Assert.assertEquals(MemberId.from("a"), selectorManager.leader());
        Assert.assertEquals(3, selectorManager.members().size());
    }
}

