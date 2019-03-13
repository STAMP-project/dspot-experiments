/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.submitted.heavy_initial_load;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class HeavyInitialLoadTest {
    private static SqlSessionFactory sqlSessionFactory;

    private static final int THREAD_COUNT = 5;

    /**
     * Test to demonstrate the effect of the
     * https://issues.apache.org/jira/browse/OGNL-121 issue in ognl on mybatis.
     *
     * Use the thing mapper for the first time in multiple threads. The mapper contains
     * a lot of ognl references to static final class members like:
     *
     * <code>@org.apache.ibatis.submitted.heavy_initial_load.Code@_1.equals(code)</code>
     *
     * Handling of these references is optimized in ognl (because they never change), but
     * version 2.6.9 has a bug in caching the result . As a result the reference is
     * translated to a 'null' value, which is used to invoke the 'equals' method on
     * (hence the 'target is null for method equals' exception).
     */
    @Test
    public void selectThingsConcurrently_mybatis_issue_224() throws Exception {
        final List<Throwable> throwables = Collections.synchronizedList(new ArrayList<Throwable>());
        Thread[] threads = new Thread[HeavyInitialLoadTest.THREAD_COUNT];
        for (int i = 0; i < (HeavyInitialLoadTest.THREAD_COUNT); i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        selectThing();
                    } catch (Exception exception) {
                        throwables.add(exception);
                    }
                }
            };
            threads[i].start();
        }
        for (int i = 0; i < (HeavyInitialLoadTest.THREAD_COUNT); i++) {
            threads[i].join();
        }
        Assert.assertTrue(("There were exceptions: " + throwables), throwables.isEmpty());
    }
}

