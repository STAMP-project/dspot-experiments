/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.plugin.infra;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PluginManagerReferenceTest {
    @Test
    public void testGetPluginManager() throws Exception {
        PluginManagerReference reference = PluginManagerReference.reference();
        try {
            reference.getPluginManager();
            Assert.fail("should throw exception");
        } catch (IllegalStateException ignored) {
        }
        PluginManager mockManager = Mockito.mock(PluginManager.class);
        reference.setPluginManager(mockManager);
        Assert.assertThat(reference.getPluginManager(), Matchers.is(mockManager));
    }
}

