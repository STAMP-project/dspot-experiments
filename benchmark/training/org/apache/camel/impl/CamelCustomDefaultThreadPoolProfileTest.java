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
package org.apache.camel.impl;


import ThreadPoolRejectedPolicy.Abort;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.spi.ThreadPoolProfile;
import org.junit.Assert;
import org.junit.Test;


public class CamelCustomDefaultThreadPoolProfileTest extends ContextTestSupport {
    @Test
    public void testCamelCustomDefaultThreadPoolProfile() throws Exception {
        DefaultExecutorServiceManager manager = ((DefaultExecutorServiceManager) (context.getExecutorServiceManager()));
        ThreadPoolProfile profile = manager.getDefaultThreadPoolProfile();
        Assert.assertEquals(5, profile.getPoolSize().intValue());
        Assert.assertEquals(15, profile.getMaxPoolSize().intValue());
        Assert.assertEquals(25, profile.getKeepAliveTime().longValue());
        Assert.assertEquals(250, profile.getMaxQueueSize().intValue());
        Assert.assertEquals(true, profile.getAllowCoreThreadTimeOut().booleanValue());
        Assert.assertEquals(Abort, profile.getRejectedPolicy());
    }
}

