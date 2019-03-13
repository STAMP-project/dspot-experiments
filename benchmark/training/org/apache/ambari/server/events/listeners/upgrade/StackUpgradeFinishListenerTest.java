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
package org.apache.ambari.server.events.listeners.upgrade;


import com.google.inject.Provider;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.events.publishers.VersionEventPublisher;
import org.apache.ambari.server.metadata.RoleCommandOrderProvider;
import org.apache.ambari.server.state.Cluster;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * StackVersionListener tests.
 */
@RunWith(EasyMockRunner.class)
public class StackUpgradeFinishListenerTest extends EasyMockSupport {
    public static final String STACK_NAME = "HDP-2.4.0.0";

    public static final String STACK_VERSION = "2.4.0.0";

    private VersionEventPublisher publisher = new VersionEventPublisher();

    private Cluster cluster;

    @TestSubject
    private StackUpgradeFinishListener listener = new StackUpgradeFinishListener(publisher);

    @Mock(type = MockType.NICE, fieldName = "roleCommandOrderProvider")
    private Provider<RoleCommandOrderProvider> roleCommandOrderProviderProviderMock;

    @Mock(type = MockType.NICE, fieldName = "ambariMetaInfo")
    private Provider<AmbariMetaInfo> ambariMetaInfoProvider = null;

    @Test
    public void testupdateComponentInfo() throws AmbariException {
        replayAll();
        sendEventAndVerify();
    }
}

