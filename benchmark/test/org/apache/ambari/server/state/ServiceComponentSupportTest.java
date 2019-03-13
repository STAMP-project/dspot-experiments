/**
 * * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package org.apache.ambari.server.state;


import java.util.Set;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class ServiceComponentSupportTest extends EasyMockSupport {
    private static final String STACK_NAME = "HDP";

    private static final String VERSION = "3.0";

    @Mock
    private AmbariMetaInfo ambariMetaInfo;

    private ServiceComponentSupport componentSupport;

    @Test
    public void testNoUnsupportedIfAllExistsInTargetStack() throws Exception {
        targetStackWith("SERVICE1", "SERVICE2");
        Set<String> unsupported = unsupportedServices(clusterWith("SERVICE1", "SERVICE2"));
        Assert.assertThat(unsupported, Matchers.hasSize(0));
        verifyAll();
    }

    @Test
    public void testUnsupportedIfDoesntExistInTargetStack() throws Exception {
        targetStackWith("SERVICE1");
        Set<String> unsupported = unsupportedServices(clusterWith("SERVICE1", "SERVICE2"));
        Assert.assertThat(unsupported, ServiceComponentSupportTest.hasOnlyItems(Is.is("SERVICE2")));
        verifyAll();
    }

    @Test
    public void testUnsupportedIfDeletedFromTargetStack() throws Exception {
        targetStackWith("SERVICE1", "SERVICE2");
        markAsDeleted("SERVICE2");
        Set<String> unsupported = unsupportedServices(clusterWith("SERVICE1", "SERVICE2"));
        Assert.assertThat(unsupported, ServiceComponentSupportTest.hasOnlyItems(Is.is("SERVICE2")));
        verifyAll();
    }
}

