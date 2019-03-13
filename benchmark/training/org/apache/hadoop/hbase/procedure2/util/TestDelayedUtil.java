/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.procedure2.util;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, SmallTests.class })
public class TestDelayedUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestDelayedUtil.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestDelayedUtil.class);

    @Test
    public void testDelayedContainerEquals() {
        Object o1 = new Object();
        Object o2 = new Object();
        TestDelayedUtil.ZeroDelayContainer<Long> lnull = new TestDelayedUtil.ZeroDelayContainer(null);
        TestDelayedUtil.ZeroDelayContainer<Long> l10a = new TestDelayedUtil.ZeroDelayContainer<>(10L);
        TestDelayedUtil.ZeroDelayContainer<Long> l10b = new TestDelayedUtil.ZeroDelayContainer(10L);
        TestDelayedUtil.ZeroDelayContainer<Long> l15 = new TestDelayedUtil.ZeroDelayContainer(15L);
        TestDelayedUtil.ZeroDelayContainer<Object> onull = new TestDelayedUtil.ZeroDelayContainer<>(null);
        TestDelayedUtil.ZeroDelayContainer<Object> o1ca = new TestDelayedUtil.ZeroDelayContainer<>(o1);
        TestDelayedUtil.ZeroDelayContainer<Object> o1cb = new TestDelayedUtil.ZeroDelayContainer<>(o1);
        TestDelayedUtil.ZeroDelayContainer<Object> o2c = new TestDelayedUtil.ZeroDelayContainer<>(o2);
        TestDelayedUtil.ZeroDelayContainer[] items = new TestDelayedUtil.ZeroDelayContainer[]{ lnull, l10a, l10b, l15, onull, o1ca, o1cb, o2c };
        assertContainersEquals(lnull, items, lnull, onull);
        assertContainersEquals(l10a, items, l10a, l10b);
        assertContainersEquals(l10b, items, l10a, l10b);
        assertContainersEquals(l15, items, l15);
        assertContainersEquals(onull, items, lnull, onull);
        assertContainersEquals(o1ca, items, o1ca, o1cb);
        assertContainersEquals(o1cb, items, o1ca, o1cb);
        assertContainersEquals(o2c, items, o2c);
    }

    private static class ZeroDelayContainer<T> extends DelayedUtil.DelayedContainer<T> {
        public ZeroDelayContainer(final T object) {
            super(object);
        }

        @Override
        public long getTimeout() {
            return 0;
        }
    }
}

