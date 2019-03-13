/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config.serialization;


import com.thoughtworks.go.config.ConfigCache;
import com.thoughtworks.go.config.MagicalGoConfigXmlLoader;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TrackingToolTest {
    private MagicalGoConfigXmlLoader loader;

    private MagicalGoConfigXmlWriter writer;

    private ConfigCache configCache = new ConfigCache();

    @Test
    public void shouldLoadTrackingtool() throws Exception {
        CruiseConfig cruiseConfig = loader.loadConfigHolder(CONFIG_WITH_TRACKINGTOOL).config;
        PipelineConfig pipelineConfig = cruiseConfig.pipelineConfigByName(new CaseInsensitiveString("pipeline1"));
        TrackingTool trackingTool = pipelineConfig.trackingTool();
        Assert.assertThat(trackingTool.getLink(), Matchers.is("http://mingle05/projects/cce/cards/${ID}"));
        Assert.assertThat(trackingTool.getRegex(), Matchers.is("(evo-\\d+)"));
    }

    @Test
    public void shouldWriteTrackingToolToConfig() throws Exception {
        CruiseConfig cruiseConfig = loader.loadConfigHolder(CONFIG_WITH_TRACKINGTOOL).config;
        PipelineConfig pipelineConfig = cruiseConfig.pipelineConfigByName(new CaseInsensitiveString("pipeline1"));
        Assert.assertThat(writer.toXmlPartial(pipelineConfig), Matchers.is(PIPELINE_WITH_TRACKINGTOOL));
    }

    @Test
    public void shouldNotReturnNullWhenTrackingToolIsNotConfigured() throws Exception {
        CruiseConfig cruiseConfig = loader.loadConfigHolder(ONE_PIPELINE).config;
        PipelineConfig pipelineConfig = cruiseConfig.pipelineConfigByName(new CaseInsensitiveString("pipeline1"));
        TrackingTool trackingTool = pipelineConfig.trackingTool();
        Assert.assertThat(trackingTool, Matchers.is(Matchers.not(Matchers.nullValue())));
        Assert.assertThat(trackingTool.getLink(), Matchers.is(""));
        Assert.assertThat(trackingTool.getRegex(), Matchers.is(""));
    }
}

