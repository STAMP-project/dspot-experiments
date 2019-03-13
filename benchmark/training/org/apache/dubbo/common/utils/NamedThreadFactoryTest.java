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
package org.apache.dubbo.common.utils;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class NamedThreadFactoryTest {
    @Test
    public void testNewThread() throws Exception {
        NamedThreadFactory factory = new NamedThreadFactory();
        Thread t = factory.newThread(Mockito.mock(Runnable.class));
        MatcherAssert.assertThat(t.getName(), Matchers.allOf(Matchers.containsString("pool-"), Matchers.containsString("-thread-")));
        Assertions.assertFalse(t.isDaemon());
        // since security manager is not installed.
        Assertions.assertSame(t.getThreadGroup(), Thread.currentThread().getThreadGroup());
    }

    @Test
    public void testPrefixAndDaemon() throws Exception {
        NamedThreadFactory factory = new NamedThreadFactory("prefix", true);
        Thread t = factory.newThread(Mockito.mock(Runnable.class));
        MatcherAssert.assertThat(t.getName(), Matchers.allOf(Matchers.containsString("prefix-"), Matchers.containsString("-thread-")));
        Assertions.assertTrue(t.isDaemon());
    }
}

