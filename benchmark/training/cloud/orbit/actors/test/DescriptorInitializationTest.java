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
import cloud.orbit.actors.annotation.StatelessWorker;
import cloud.orbit.actors.runtime.AbstractActor;
import cloud.orbit.concurrent.Task;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;


public class DescriptorInitializationTest extends ActorBaseTest {
    @StatelessWorker
    public interface MyActor extends Actor {
        Task<Void> ping();
    }

    public static class MyActorImpl extends AbstractActor implements DescriptorInitializationTest.MyActor {
        @Override
        public Task<Void> ping() {
            return Task.done();
        }
    }

    @Test
    public void testConcurrentDescriptorInitialization() throws InterruptedException, ExecutionException {
        // this test forces the state to create actor descriptors concurrently.
        // using this test problems were detected with ClassPathSearch
        for (int j = 0; j < 100; j++) {
            // using a different cluster to test the stage initialization
            clusterName = UUID.randomUUID().toString();
            Stage stage = createStage();
            DescriptorInitializationTest.MyActor ref = Actor.getReference(DescriptorInitializationTest.MyActor.class, clusterName);
            List<Task<Void>> list = IntStream.range(1, 10).parallel().mapToObj(( i) -> ref.ping()).collect(Collectors.toList());
            Task.allOf(list).join();
        }
    }
}

