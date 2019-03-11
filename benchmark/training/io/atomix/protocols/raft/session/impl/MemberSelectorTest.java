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


import CommunicationStrategy.ANY;
import CommunicationStrategy.FOLLOWERS;
import CommunicationStrategy.LEADER;
import io.atomix.cluster.MemberId;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Member selector test.
 */
public class MemberSelectorTest {
    /**
     * Tests selecting members using the ANY selector.
     */
    @Test
    public void testSelectAny() throws Exception {
        MemberSelectorManager selectorManager = new MemberSelectorManager();
        MemberSelector selector = selectorManager.createSelector(ANY);
        Assert.assertNull(selector.leader());
        Assert.assertFalse(selector.hasNext());
        selectorManager.resetAll(null, Arrays.asList(MemberId.from("a"), MemberId.from("b"), MemberId.from("c")));
        Assert.assertNull(selector.leader());
        Assert.assertTrue(selector.hasNext());
        selector.hasNext();
        Assert.assertTrue(selector.hasNext());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertFalse(selector.hasNext());
        selector.reset();
        Assert.assertTrue(selector.hasNext());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertFalse(selector.hasNext());
        selectorManager.resetAll(MemberId.from("a"), Arrays.asList(MemberId.from("a"), MemberId.from("b"), MemberId.from("c")));
        Assert.assertNotNull(selector.leader());
        Assert.assertTrue(selector.hasNext());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertFalse(selector.hasNext());
        selector.reset();
        Assert.assertTrue(selector.hasNext());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertFalse(selector.hasNext());
    }

    /**
     * Tests selecting members using the FOLLOWER selector.
     */
    @Test
    public void testSelectFollower() throws Exception {
        MemberSelectorManager selectorManager = new MemberSelectorManager();
        MemberSelector selector = selectorManager.createSelector(FOLLOWERS);
        Assert.assertNull(selector.leader());
        Assert.assertFalse(selector.hasNext());
        selectorManager.resetAll(null, Arrays.asList(MemberId.from("a"), MemberId.from("b"), MemberId.from("c")));
        Assert.assertNull(selector.leader());
        Assert.assertTrue(selector.hasNext());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertFalse(selector.hasNext());
        selector.reset();
        Assert.assertTrue(selector.hasNext());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertFalse(selector.hasNext());
        selectorManager.resetAll(MemberId.from("a"), Arrays.asList(MemberId.from("a"), MemberId.from("b"), MemberId.from("c")));
        Assert.assertNotNull(selector.leader());
        Assert.assertTrue(selector.hasNext());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertFalse(selector.hasNext());
    }

    /**
     * Tests the member selector.
     */
    @Test
    public void testSelectLeader() throws Exception {
        MemberSelectorManager selectorManager = new MemberSelectorManager();
        MemberSelector selector = selectorManager.createSelector(LEADER);
        Assert.assertNull(selector.leader());
        Assert.assertFalse(selector.hasNext());
        selectorManager.resetAll(null, Arrays.asList(MemberId.from("a"), MemberId.from("b"), MemberId.from("c")));
        Assert.assertNull(selector.leader());
        Assert.assertTrue(selector.hasNext());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertFalse(selector.hasNext());
        selector.reset();
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertNotNull(selector.next());
        Assert.assertFalse(selector.hasNext());
        selectorManager.resetAll(MemberId.from("a"), Arrays.asList(MemberId.from("a"), MemberId.from("b"), MemberId.from("c")));
        Assert.assertEquals(MemberId.from("a"), selector.leader());
        Assert.assertEquals(3, selector.members().size());
        Assert.assertTrue(selector.hasNext());
        Assert.assertNotNull(selector.next());
        Assert.assertFalse(selector.hasNext());
        selectorManager.resetAll(null, Arrays.asList(MemberId.from("a"), MemberId.from("b"), MemberId.from("c")));
        Assert.assertNull(selector.leader());
        Assert.assertTrue(selector.hasNext());
        selectorManager.resetAll(MemberId.from("a"), Arrays.asList(MemberId.from("b"), MemberId.from("c")));
        Assert.assertNull(selector.leader());
        Assert.assertTrue(selector.hasNext());
    }
}

