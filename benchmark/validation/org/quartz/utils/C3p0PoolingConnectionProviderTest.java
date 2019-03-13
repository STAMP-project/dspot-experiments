/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.quartz.utils;


import org.junit.Test;
import org.quartz.integrations.tests.QuartzDatabaseTestSupport;


/**
 * A integration test to ensure PoolConnectionProvider is working properly.
 */
public class C3p0PoolingConnectionProviderTest extends QuartzDatabaseTestSupport {
    boolean testConnectionProviderClass = false;

    @Test
    public void testC3p0PoolProviderWithExtraProps() throws Exception {
        validateC3p0PoolProviderClassWithExtraProps();
        // Turn flag on for next test.
        testConnectionProviderClass = true;
    }

    @Test
    public void testC3p0PoolProviderClassWithExtraProps() throws Exception {
        validateC3p0PoolProviderClassWithExtraProps();
        // Turn flag off for next test.
        testConnectionProviderClass = false;
    }
}

