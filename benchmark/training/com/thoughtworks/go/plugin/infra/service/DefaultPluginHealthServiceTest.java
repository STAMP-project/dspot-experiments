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
package com.thoughtworks.go.plugin.infra.service;


import com.thoughtworks.go.plugin.infra.plugininfo.PluginRegistry;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultPluginHealthServiceTest {
    private DefaultPluginHealthService serviceDefault;

    private PluginRegistry pluginRegistry;

    @Test
    public void shouldMarkPluginAsInvalidWhenServiceReportsAnError() throws Exception {
        String pluginId = "plugin-id";
        String message = "plugin is broken beyond repair";
        List<String> reasons = Arrays.asList(message);
        Mockito.doNothing().when(pluginRegistry).markPluginInvalid(pluginId, reasons);
        serviceDefault.reportErrorAndInvalidate(pluginId, reasons);
        Mockito.verify(pluginRegistry).markPluginInvalid(pluginId, reasons);
    }

    @Test
    public void shouldNotThrowExceptionWhenPluginIsNotFound() throws Exception {
        String pluginId = "invalid-plugin";
        String message = "some msg";
        List<String> reasons = Arrays.asList(message);
        Mockito.doThrow(new RuntimeException()).when(pluginRegistry).markPluginInvalid(pluginId, reasons);
        try {
            serviceDefault.reportErrorAndInvalidate(pluginId, reasons);
        } catch (Exception e) {
            Assert.fail("Should not have thrown exception");
        }
    }
}

