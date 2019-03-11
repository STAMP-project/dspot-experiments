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
package org.apache.ignite.internal.processors.resource;


import java.io.Externalizable;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test for injected logger category.
 */
public class GridLoggerInjectionSelfTest extends GridCommonAbstractTest implements Externalizable {
    /**
     * Test that closure gets right log category injected on all nodes using field injection.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureField() throws Exception {
        Ignite ignite = grid(0);
        ignite.compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @LoggerResource(categoryClass = GridLoggerInjectionSelfTest.class)
            private IgniteLogger log;

            @Override
            public Object call() throws Exception {
                if ((log) instanceof org.apache.ignite.internal.GridLoggerProxy) {
                    Object category = org.apache.ignite.internal.util.typedef.internal.U.field(log, "ctgr");
                    assertTrue("Logger created for the wrong category.", category.toString().contains(GridLoggerInjectionSelfTest.class.getName()));
                } else
                    fail("This test should be run with proxy logger.");

                return null;
            }
        });
    }

    /**
     * Test that closure gets right log category injected on all nodes using method injection.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureMethod() throws Exception {
        Ignite ignite = grid(0);
        ignite.compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @LoggerResource(categoryClass = GridLoggerInjectionSelfTest.class)
            private void log(IgniteLogger log) {
                if (log instanceof org.apache.ignite.internal.GridLoggerProxy) {
                    Object category = org.apache.ignite.internal.util.typedef.internal.U.field(log, "ctgr");
                    assertTrue("Logger created for the wrong category.", category.toString().contains(GridLoggerInjectionSelfTest.class.getName()));
                } else
                    fail("This test should be run with proxy logger.");

            }

            @Override
            public Object call() throws Exception {
                return null;
            }
        });
    }

    /**
     * Test that closure gets right log category injected through {@link org.apache.ignite.resources.LoggerResource#categoryName()}.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testStringCategory() throws Exception {
        Ignite ignite = grid(0);
        ignite.compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @LoggerResource(categoryName = "GridLoggerInjectionSelfTest")
            private void log(IgniteLogger log) {
                if (log instanceof org.apache.ignite.internal.GridLoggerProxy) {
                    Object category = org.apache.ignite.internal.util.typedef.internal.U.field(log, "ctgr");
                    assertTrue("Logger created for the wrong category.", "GridLoggerInjectionSelfTest".equals(category.toString()));
                } else
                    fail("This test should be run with proxy logger.");

            }

            @Override
            public Object call() throws Exception {
                return null;
            }
        });
    }
}

