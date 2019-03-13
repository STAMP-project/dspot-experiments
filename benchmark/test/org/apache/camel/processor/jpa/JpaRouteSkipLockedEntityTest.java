/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.processor.jpa;


import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.examples.VersionedItem;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Need the fix of OPENJPA-2461")
public class JpaRouteSkipLockedEntityTest extends AbstractJpaTest {
    protected static final String SELECT_ALL_STRING = ("select x from " + (VersionedItem.class.getName())) + " x";

    private int count;

    private final ReentrantLock lock = new ReentrantLock();

    private Condition cond1 = lock.newCondition();

    @Test
    public void testRouteJpa() throws Exception {
        MockEndpoint mock1 = getMockEndpoint("mock:result1");
        mock1.expectedMessageCount(2);
        MockEndpoint mock2 = getMockEndpoint("mock:result2");
        mock2.expectedMessageCount(2);
        template.sendBody(("jpa://" + (VersionedItem.class.getName())), new VersionedItem("one"));
        template.sendBody(("jpa://" + (VersionedItem.class.getName())), new VersionedItem("two"));
        template.sendBody(("jpa://" + (VersionedItem.class.getName())), new VersionedItem("three"));
        template.sendBody(("jpa://" + (VersionedItem.class.getName())), new VersionedItem("four"));
        this.context.getRouteController().startRoute("second");
        this.context.getRouteController().startRoute("first");
        assertMockEndpointsSatisfied();
        // force test to wait till finished
        this.context.getRouteController().stopRoute("first");
        this.context.getRouteController().stopRoute("second");
        setLockTimeout(60);
        List<?> list = entityManager.createQuery(selectAllString()).getResultList();
        assertEquals(0, list.size());
    }

    public class WaitLatch {
        public void onMessage(VersionedItem body) throws Exception {
            lock.lock();
            try {
                (count)++;
                // if (count != 1) {
                cond1.signal();
                // }
                // if not last
                if ((count) != 4) {
                    cond1.await();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}

