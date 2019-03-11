/**
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
package org.jctools.queues.atomic;


import org.jctools.queues.matchers.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SpscAtomicArrayQueueTest {
    @Test
    public void shouldWorkAfterWrap() {
        // Arrange
        final SpscAtomicArrayQueue<Object> q = new SpscAtomicArrayQueue<Object>(1024);
        // starting point for empty queue at max long, next offer will wrap the producerIndex
        q.soConsumerIndex(Long.MAX_VALUE);
        q.soProducerIndex(Long.MAX_VALUE);
        q.producerLimit = Long.MAX_VALUE;
        // valid starting point
        Assert.assertThat(q, Matchers.emptyAndZeroSize());
        // Act
        // assert offer is successful
        final Object e = new Object();
        Assert.assertTrue(q.offer(e));
        // size is computed correctly after wrap
        Assert.assertThat(q, not(Matchers.emptyAndZeroSize()));
        Assert.assertThat(q, hasSize(1));
        // now consumer index wraps
        final Object poll = q.poll();
        Assert.assertThat(poll, sameInstance(e));
        Assert.assertThat(q, Matchers.emptyAndZeroSize());
        // let's go again
        Assert.assertTrue(q.offer(e));
        Assert.assertThat(q, not(Matchers.emptyAndZeroSize()));
        final Object poll2 = q.poll();
        Assert.assertThat(poll2, sameInstance(e));
        Assert.assertThat(q, Matchers.emptyAndZeroSize());
    }
}

