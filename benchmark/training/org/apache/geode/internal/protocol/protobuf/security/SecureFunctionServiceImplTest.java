/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.protocol.protobuf.security;


import java.util.Arrays;
import java.util.Collections;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.security.NotAuthorizedException;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import static Operation.WRITE;
import static Resource.CLUSTER;


@Category({ ClientServerTest.class })
public class SecureFunctionServiceImplTest {
    public static final String REGION = "TestRegion";

    public static final String FUNCTION_ID = "id";

    private SecureFunctionServiceImpl functionService;

    private InternalCache cache;

    private Security security;

    private Region region;

    private Function function;

    @Test
    public void executeFunctionOnRegionWithoutAuthorization() throws Exception {
        Mockito.when(function.getRequiredPermissions(SecureFunctionServiceImplTest.REGION, null)).thenReturn(Collections.singleton(new org.apache.geode.security.ResourcePermission(CLUSTER, WRITE, SecureFunctionServiceImplTest.REGION, ALL)));
        assertThatThrownBy(() -> functionService.executeFunctionOnRegion(FUNCTION_ID, REGION, null, null)).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void executeFunctionOnMemberWithoutAuthorization() throws Exception {
        Mockito.when(function.getRequiredPermissions(null, null)).thenReturn(Collections.singleton(new org.apache.geode.security.ResourcePermission(CLUSTER, WRITE, SecureFunctionServiceImplTest.REGION, ALL)));
        assertThatThrownBy(() -> functionService.executeFunctionOnMember(FUNCTION_ID, null, Arrays.asList("member"))).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void executeFunctionOnGroupsWithoutAuthorization() throws Exception {
        Mockito.when(function.getRequiredPermissions(null, null)).thenReturn(Collections.singleton(new org.apache.geode.security.ResourcePermission(CLUSTER, WRITE, SecureFunctionServiceImplTest.REGION, ALL)));
        assertThatThrownBy(() -> functionService.executeFunctionOnGroups(FUNCTION_ID, null, Arrays.asList("group"))).isInstanceOf(NotAuthorizedException.class);
    }
}

