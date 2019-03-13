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
package com.hazelcast.concurrent.atomicreference;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AtomicReferenceInstanceSharingTest extends HazelcastTestSupport {
    private HazelcastInstance local;

    private HazelcastInstance remote;

    @Test
    public void invocationToLocalMember() {
        String localKey = HazelcastTestSupport.generateKeyOwnedBy(local);
        IAtomicReference<AtomicReferenceInstanceSharingTest.DummyObject> ref = local.getAtomicReference(localKey);
        AtomicReferenceInstanceSharingTest.DummyObject inserted = new AtomicReferenceInstanceSharingTest.DummyObject();
        ref.set(inserted);
        AtomicReferenceInstanceSharingTest.DummyObject get1 = ref.get();
        AtomicReferenceInstanceSharingTest.DummyObject get2 = ref.get();
        Assert.assertNotNull(get1);
        Assert.assertNotNull(get2);
        Assert.assertNotSame(get1, get2);
        Assert.assertNotSame(get1, inserted);
        Assert.assertNotSame(get2, inserted);
    }

    @Test
    public void invocationToRemoteMember() {
        String localKey = HazelcastTestSupport.generateKeyOwnedBy(remote);
        IAtomicReference<AtomicReferenceInstanceSharingTest.DummyObject> ref = local.getAtomicReference(localKey);
        AtomicReferenceInstanceSharingTest.DummyObject inserted = new AtomicReferenceInstanceSharingTest.DummyObject();
        ref.set(inserted);
        AtomicReferenceInstanceSharingTest.DummyObject get1 = ref.get();
        AtomicReferenceInstanceSharingTest.DummyObject get2 = ref.get();
        Assert.assertNotNull(get1);
        Assert.assertNotNull(get2);
        Assert.assertNotSame(get1, get2);
        Assert.assertNotSame(get1, inserted);
        Assert.assertNotSame(get2, inserted);
    }

    public static class DummyObject implements Serializable {}
}

