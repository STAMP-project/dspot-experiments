/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.access.configrepo;


import ConfigRepoExtension.REQUEST_CAPABILITIES;
import ConfigRepoExtension.REQUEST_PARSE_DIRECTORY;
import ConfigRepoExtension.REQUEST_PIPELINE_EXPORT;
import com.thoughtworks.go.plugin.access.ExtensionsRegistry;
import com.thoughtworks.go.plugin.access.common.AbstractExtension;
import com.thoughtworks.go.plugin.access.configrepo.v1.JsonMessageHandler1_0;
import com.thoughtworks.go.plugin.access.configrepo.v2.JsonMessageHandler2_0;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.configrepo.codec.GsonCodec;
import com.thoughtworks.go.plugin.configrepo.contract.CRParseResult;
import com.thoughtworks.go.plugin.configrepo.contract.CRPipeline;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.domain.configrepo.Capabilities;
import com.thoughtworks.go.plugin.infra.PluginManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ConfigRepoExtensionTest {
    public static final String PLUGIN_ID = "plugin-id";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private PluginManager pluginManager;

    @Mock
    private JsonMessageHandler1_0 jsonMessageHandler1;

    @Mock
    private JsonMessageHandler2_0 jsonMessageHandler2;

    @Mock
    ExtensionsRegistry extensionsRegistry;

    private ConfigRepoExtension extension;

    private String responseBody = "expected-response";

    private Map<String, String> responseHeaders = new HashMap<String, String>() {
        {
            put("Content-Type", "text/plain");
            put("X-Export-Filename", "foo.txt");
        }
    };

    private ArgumentCaptor<GoPluginApiRequest> requestArgumentCaptor;

    @Test
    public void shouldExtendAbstractExtension() {
        Assert.assertTrue(((extension) instanceof AbstractExtension));
    }

    @Test
    public void shouldTalkToPluginToGetParsedDirectory() {
        CRParseResult deserializedResponse = new CRParseResult();
        Mockito.when(jsonMessageHandler1.responseMessageForParseDirectory(responseBody)).thenReturn(deserializedResponse);
        CRParseResult response = extension.parseDirectory(ConfigRepoExtensionTest.PLUGIN_ID, "dir", null);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.CONFIG_REPO_EXTENSION, "1.0", REQUEST_PARSE_DIRECTORY, null);
        Mockito.verify(jsonMessageHandler1).responseMessageForParseDirectory(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToGetPipelineExport() {
        CRPipeline pipeline = new CRPipeline();
        String serialized = new GsonCodec().getGson().toJson(pipeline);
        Mockito.when(jsonMessageHandler2.responseMessageForPipelineExport(responseBody, responseHeaders)).thenReturn(ExportedConfig.from(serialized, responseHeaders));
        Mockito.when(pluginManager.resolveExtensionVersion(ConfigRepoExtensionTest.PLUGIN_ID, PluginConstants.CONFIG_REPO_EXTENSION, new ArrayList(Arrays.asList("1.0", "2.0")))).thenReturn("2.0");
        ExportedConfig response = extension.pipelineExport(ConfigRepoExtensionTest.PLUGIN_ID, pipeline);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.CONFIG_REPO_EXTENSION, "2.0", REQUEST_PIPELINE_EXPORT, null);
        Mockito.verify(jsonMessageHandler2).responseMessageForPipelineExport(responseBody, responseHeaders);
        Assert.assertSame(response.getContent(), serialized);
    }

    @Test
    public void shouldRequestCapabilities() {
        Capabilities capabilities = new Capabilities(true, true);
        Mockito.when(jsonMessageHandler2.getCapabilitiesFromResponse(responseBody)).thenReturn(capabilities);
        Mockito.when(pluginManager.resolveExtensionVersion(ConfigRepoExtensionTest.PLUGIN_ID, PluginConstants.CONFIG_REPO_EXTENSION, new ArrayList(Arrays.asList("1.0", "2.0")))).thenReturn("2.0");
        Capabilities res = extension.getCapabilities(ConfigRepoExtensionTest.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.CONFIG_REPO_EXTENSION, "2.0", REQUEST_CAPABILITIES, null);
        Assert.assertSame(capabilities, res);
    }

    @Test
    public void shouldRequestCapabilitiesV1() {
        Capabilities capabilities = new Capabilities(false, false);
        Capabilities res = extension.getCapabilities(ConfigRepoExtensionTest.PLUGIN_ID);
        Assert.assertThat(capabilities, Matchers.is(res));
    }
}

