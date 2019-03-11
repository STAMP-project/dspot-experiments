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
package org.apache.ambari.server.controller.internal;


import RootComponent.AMBARI_AGENT;
import RootComponent.AMBARI_SERVER;
import org.junit.Test;


public class RootServiceComponentPropertyProviderTest {
    @Test
    public void testPopulateResources_AmbariServer_None() throws Exception {
        testPopulateResources(AMBARI_SERVER.name(), false, false, false, false);
    }

    @Test
    public void testPopulateResources_AmbariServer_CiphersAndJCEPolicy() throws Exception {
        testPopulateResources(AMBARI_SERVER.name(), true, true, true, true);
    }

    @Test
    public void testPopulateResources_AmbariServer_JCEPolicy() throws Exception {
        testPopulateResources(AMBARI_SERVER.name(), false, true, false, true);
    }

    @Test
    public void testPopulateResources_AmbariServer_Ciphers() throws Exception {
        testPopulateResources(AMBARI_SERVER.name(), true, false, true, false);
    }

    @Test
    public void testPopulateResources_AmbariAgent_CiphersAndJCEPolicy() throws Exception {
        testPopulateResources(AMBARI_AGENT.name(), true, true, false, false);
    }
}

