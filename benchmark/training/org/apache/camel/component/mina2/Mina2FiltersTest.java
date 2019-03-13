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
package org.apache.camel.component.mina2;


import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.junit.Test;


/**
 * For unit testing the <tt>filters</tt> option.
 */
public class Mina2FiltersTest extends BaseMina2Test {
    @Test
    public void testFilterListRef() throws Exception {
        testFilter(String.format("mina2:tcp://localhost:%1$s?textline=true&minaLogger=true&sync=false&filters=#myFilters", getPort()));
    }

    @Test
    public void testFilterElementRef() throws Exception {
        testFilter(String.format("mina2:tcp://localhost:%1$s?textline=true&minaLogger=true&sync=false&filters=#myFilter", getPort()));
    }

    public static final class TestFilter extends IoFilterAdapter {
        public static volatile int called;

        @Override
        public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception {
            Mina2FiltersTest.TestFilter.incCalled();
            nextFilter.sessionCreated(session);
        }

        public static synchronized void incCalled() {
            (Mina2FiltersTest.TestFilter.called)++;
        }
    }
}

