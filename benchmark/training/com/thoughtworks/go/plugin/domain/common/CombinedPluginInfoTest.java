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
package com.thoughtworks.go.plugin.domain.common;


import com.thoughtworks.go.plugin.api.info.PluginDescriptor;
import com.thoughtworks.go.plugin.domain.analytics.AnalyticsPluginInfo;
import com.thoughtworks.go.plugin.domain.authorization.AuthorizationPluginInfo;
import com.thoughtworks.go.plugin.domain.elastic.ElasticAgentPluginInfo;
import com.thoughtworks.go.plugin.domain.notification.NotificationPluginInfo;
import com.thoughtworks.go.plugin.domain.pluggabletask.PluggableTaskPluginInfo;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class CombinedPluginInfoTest {
    @Test
    public void shouldGetExtensionNamesOfAllExtensionsContainedWithin() throws Exception {
        CombinedPluginInfo pluginInfo = new CombinedPluginInfo(Arrays.asList(new PluggableTaskPluginInfo(null, null, null), new NotificationPluginInfo(null, null)));
        Assert.assertThat(pluginInfo.extensionNames().size(), Matchers.is(2));
        Assert.assertThat(pluginInfo.extensionNames(), Matchers.hasItems(NOTIFICATION_EXTENSION, PLUGGABLE_TASK_EXTENSION));
    }

    @Test
    public void shouldGetDescriptorOfPluginUsingAnyPluginInfo() throws Exception {
        PluginDescriptor descriptor = Mockito.mock(PluginDescriptor.class);
        NotificationPluginInfo notificationPluginInfo = new NotificationPluginInfo(descriptor, null);
        PluggableTaskPluginInfo pluggableTaskPluginInfo = new PluggableTaskPluginInfo(descriptor, null, null);
        CombinedPluginInfo pluginInfo = new CombinedPluginInfo(Arrays.asList(pluggableTaskPluginInfo, notificationPluginInfo));
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
    }

    @Test
    public void shouldFailWhenThereIsNoPluginInfoToGetTheDescriptorFrom() throws Exception {
        CombinedPluginInfo pluginInfo = new CombinedPluginInfo();
        try {
            pluginInfo.getDescriptor();
            Assert.fail("Should have failed since there are no plugins found.");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Cannot get descriptor"));
        }
    }

    @Test
    public void shouldGetAllIndividualExtensionInfos() throws Exception {
        NotificationPluginInfo notificationPluginInfo = new NotificationPluginInfo(null, null);
        PluggableTaskPluginInfo pluggableTaskPluginInfo = new PluggableTaskPluginInfo(null, null, null);
        CombinedPluginInfo pluginInfo = new CombinedPluginInfo(Arrays.asList(pluggableTaskPluginInfo, notificationPluginInfo));
        Assert.assertThat(pluginInfo.getExtensionInfos(), Matchers.containsInAnyOrder(notificationPluginInfo, pluggableTaskPluginInfo));
    }

    @Test
    public void shouldFindFirstExtensionWithImageIfPluginImplementsAtleastOneExtensionWithImage() throws Exception {
        Image image1 = new Image("c1", "d1", "hash1");
        Image image2 = new Image("c2", "d2", "hash2");
        Image image3 = new Image("c3", "d3", "hash3");
        ElasticAgentPluginInfo elasticAgentPluginInfo = new ElasticAgentPluginInfo(null, null, image1, null, null);
        AuthorizationPluginInfo authorizationPluginInfo = new AuthorizationPluginInfo(null, null, null, image2, null);
        AnalyticsPluginInfo analyticsPluginInfo = new AnalyticsPluginInfo(null, image3, null, null);
        Assert.assertThat(getImage(), Matchers.is(image1));
        Assert.assertThat(getImage(), Matchers.is(image2));
        Assert.assertThat(getImage(), Matchers.is(image3));
        Assert.assertThat(getImage(), Matchers.anyOf(Matchers.is(image1), Matchers.is(image2)));
        Assert.assertThat(getImage(), Matchers.anyOf(Matchers.is(image2), Matchers.is(image3)));
    }

    @Test
    public void shouldNotFindImageIfPluginDoesNotImplementAnExtensionWhichHasImages() throws Exception {
        NotificationPluginInfo notificationPluginInfo = new NotificationPluginInfo(null, null);
        PluggableTaskPluginInfo pluggableTaskPluginInfo = new PluggableTaskPluginInfo(null, null, null);
        Assert.assertThat(getImage(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(getImage(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(getImage(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldFindAnExtensionOfAGivenTypeIfItExists() throws Exception {
        NotificationPluginInfo notificationPluginInfo = new NotificationPluginInfo(null, null);
        PluggableTaskPluginInfo pluggableTaskPluginInfo = new PluggableTaskPluginInfo(null, null, null);
        CombinedPluginInfo pluginInfo = new CombinedPluginInfo(Arrays.asList(pluggableTaskPluginInfo, notificationPluginInfo));
        Assert.assertThat(pluginInfo.extensionFor(NOTIFICATION_EXTENSION), Matchers.is(notificationPluginInfo));
        Assert.assertThat(pluginInfo.extensionFor(PLUGGABLE_TASK_EXTENSION), Matchers.is(pluggableTaskPluginInfo));
        Assert.assertThat(pluginInfo.extensionFor(ANALYTICS_EXTENSION), Matchers.is(Matchers.nullValue()));
    }
}

