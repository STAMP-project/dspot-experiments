/**
 * Copyright (C) 2016 Electronic Arts Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2.  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
 * its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package cloud.orbit.actors.test;


import cloud.orbit.actors.Actor;
import cloud.orbit.actors.Stage;
import cloud.orbit.actors.cloner.ExecutionObjectCloner;
import cloud.orbit.actors.cloner.JavaSerializationCloner;
import cloud.orbit.actors.test.actors.CacheResponse;
import cloud.orbit.actors.test.dto.TestDto1;
import cloud.orbit.actors.test.dto.TestDto2;
import java.util.Arrays;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests cloning functionality of the CacheResponse system, using
 * different clone implementations.
 */
@RunWith(Parameterized.class)
public class CacheResponseCloneTest extends ActorBaseTest {
    private Stage stage;

    private ExecutionObjectCloner objectCloner;

    public CacheResponseCloneTest(ExecutionObjectCloner objectCloner) {
        this.objectCloner = objectCloner;
    }

    @Test
    public void testCloneIntegrity() {
        CacheResponse actor = Actor.getReference(CacheResponse.class, UUID.randomUUID().toString());
        // Initialize a dto
        int someIdValue = 3214;
        TestDto1 a = new TestDto1();
        a.setSampleInt(someIdValue);
        a.getSampleIntList().addAll(Arrays.asList(10, 20, 20, 30, 40, 50));
        a.setSampleActor(actor);
        // Place dto in actor
        actor.setDto1(a).join();
        // Get dto from actor, caching it
        TestDto1 a1 = actor.getDto1().join();
        // Modify our copy of the cached dto
        a1.setSampleInt((someIdValue + 10));
        a1.getSampleIntList().remove(((Object) (30)));
        a1.getSampleIntList().add(60);
        a1.getSampleIntList().add(70);
        // Get dto from cache
        TestDto1 a2 = actor.getDto1().join();
        // Verify that the cached dto is unchanged
        Assert.assertEquals(someIdValue, a2.getSampleInt());
        if ((stage.getObjectCloner()) instanceof JavaSerializationCloner) {
            Assert.assertEquals(a1.getId(), a2.getId());
        } else {
            Assert.assertSame(a1.getId(), a2.getId());
        }
        Assert.assertNotEquals(a1.getSampleIntList().size(), a2.getSampleIntList().size());
        Assert.assertTrue(a2.getSampleIntList().contains(30));
        Assert.assertFalse(a2.getSampleIntList().contains(60));
        Assert.assertFalse(a2.getSampleIntList().contains(70));
        Assert.assertNotSame(a1, a2);
    }

    @Test
    public void testCloneCyclicReference() {
        // Set up cyclic reference
        TestDto1 a = new TestDto1();
        TestDto2 b = new TestDto2();
        a.setDto2(b);
        b.setDto1(a);
        Assert.assertSame(a, b.getDto1());
        // Send to actor
        CacheResponse actor = Actor.getReference(CacheResponse.class, UUID.randomUUID().toString());
        actor.setDto1(a).join();
        // Get dto from cache and test cyclic reference
        TestDto1 a1 = actor.getDto1().join();
        Assert.assertSame(a1, a1.getDto2().getDto1());
        Assert.assertNotSame(a, a1);
    }
}

