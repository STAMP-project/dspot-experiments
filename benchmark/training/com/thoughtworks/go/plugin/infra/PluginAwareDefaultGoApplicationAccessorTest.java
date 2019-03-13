/**
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.plugin.infra;


import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PluginAwareDefaultGoApplicationAccessorTest {
    @Test
    public void shouldHandleExceptionThrownByProcessor() throws Exception {
        String api = "api-uri";
        GoPluginApiRequestProcessor processor = Mockito.mock(GoPluginApiRequestProcessor.class);
        GoApiRequest goApiRequest = Mockito.mock(GoApiRequest.class);
        GoPluginDescriptor descriptor = Mockito.mock(GoPluginDescriptor.class);
        Mockito.when(goApiRequest.api()).thenReturn(api);
        Throwable cause = new RuntimeException("error");
        Mockito.when(processor.process(descriptor, goApiRequest)).thenThrow(cause);
        PluginRequestProcessorRegistry pluginRequestProcessorRegistry = new PluginRequestProcessorRegistry();
        pluginRequestProcessorRegistry.registerProcessorFor(api, processor);
        PluginAwareDefaultGoApplicationAccessor accessor = new PluginAwareDefaultGoApplicationAccessor(descriptor, pluginRequestProcessorRegistry);
        try {
            accessor.submit(goApiRequest);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is(String.format("Error while processing request api %s", api)));
            Assert.assertThat(e.getCause(), Matchers.is(cause));
        }
    }
}

