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
package com.thoughtworks.go.config;


import MingleConfig.BASE_URL;
import MingleConfig.MQL_GROUPING_CONDITIONS;
import MingleConfig.PROJECT_IDENTIFIER;
import MqlCriteria.MQL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MingleConfigTest {
    @Test
    public void shouldBuildUrlGivenPath() throws Exception {
        MingleConfig mingleConfig = new MingleConfig("http://foo.bar:7019/baz", "go-project");
        Assert.assertThat(mingleConfig.urlFor("/cards/123"), Matchers.is("http://foo.bar:7019/baz/cards/123"));
        Assert.assertThat(mingleConfig.urlFor("cards/123"), Matchers.is("http://foo.bar:7019/bazcards/123"));
        mingleConfig = new MingleConfig("http://foo.bar:7019/baz/", "go-project");
        Assert.assertThat(mingleConfig.urlFor("/cards/123"), Matchers.is("http://foo.bar:7019/baz/cards/123"));
        Assert.assertThat(mingleConfig.urlFor("cards/123"), Matchers.is("http://foo.bar:7019/baz/cards/123"));
        mingleConfig = new MingleConfig("http://foo.bar:7019", "go-project");
        Assert.assertThat(mingleConfig.urlFor("/cards/123"), Matchers.is("http://foo.bar:7019/cards/123"));
        mingleConfig = new MingleConfig("http://foo.bar:7019/quux?hi=there", "go-project");
        Assert.assertThat(mingleConfig.urlFor("/cards/123"), Matchers.is("http://foo.bar:7019/quux/cards/123?hi=there"));
    }

    @Test
    public void shouldUnderstandGettingMql() {
        MingleConfig mingleConfig = new MingleConfig("http://foo.bar:7019/baz", "go-project", "foo = bar");
        Assert.assertThat(mingleConfig.getQuotedMql(), Matchers.is("\"foo = bar\""));
        mingleConfig = new MingleConfig("http://foo.bar:7019/baz", "go-project");
        Assert.assertThat(mingleConfig.getQuotedMql(), Matchers.is("\"\""));
        mingleConfig = new MingleConfig("http://foo.bar:7019/baz", "go-project", "\"foo\" = \'bar\'");
        Assert.assertThat(mingleConfig.getQuotedMql(), Matchers.is("\"\\\"foo\\\" = \'bar\'\""));
        mingleConfig = new MingleConfig("http://foo.bar:7019/baz", "go-project", "\\\"foo\\\" = \'bar\'");
        Assert.assertThat(mingleConfig.getQuotedMql(), Matchers.is("\"\\\\\\\"foo\\\\\\\" = \'bar\'\""));
    }

    @Test
    public void shouldSetMingleConfigFromConfigAttributes() {
        MingleConfig mingleConfig = new MingleConfig();
        Map configMap = new HashMap();
        configMap.put(BASE_URL, "http://mingle01.tw.com/mingle");
        configMap.put(PROJECT_IDENTIFIER, "go");
        configMap.put(MQL_GROUPING_CONDITIONS, Collections.singletonMap(MQL, "Some MQL"));
        mingleConfig.setConfigAttributes(configMap);
        Assert.assertThat(mingleConfig.getBaseUrl(), Matchers.is("http://mingle01.tw.com/mingle"));
        Assert.assertThat(mingleConfig.getProjectIdentifier(), Matchers.is("go"));
        Assert.assertThat(mingleConfig.getMqlCriteria().getMql(), Matchers.is("Some MQL"));
    }

    @Test
    public void shouldReturnInvalidIfTheURLIsNotAHTTPSURL() {
        MingleConfig mingleConfig = new MingleConfig("http://some-mingle-instance", "go");
        mingleConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(mingleConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(mingleConfig.errors().on(PROJECT_IDENTIFIER), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(mingleConfig.errors().on(BASE_URL), Matchers.is("Should be a URL starting with https://"));
    }

    @Test
    public void shouldReturnInvalidIfTheProjectIdentifierIsInvalid() {
        MingleConfig mingleConfig = new MingleConfig("https://some-mingle-instance", "wrong project identifier");
        mingleConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(mingleConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(mingleConfig.errors().on(BASE_URL), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(mingleConfig.errors().on(PROJECT_IDENTIFIER), Matchers.is("Should be a valid mingle identifier."));
    }

    @Test
    public void shouldValidateEmptyConfig() {
        MingleConfig config = new MingleConfig();
        config.validate(null);
        Assert.assertThat(config.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldGetQuotedProjectIdentifier() {
        MingleConfig mingleConfig = new MingleConfig("baseUrl", "Ethan's_Project");
        Assert.assertThat(mingleConfig.getQuotedProjectIdentifier(), Matchers.is("\"Ethan\'s_Project\""));
        mingleConfig = new MingleConfig("baseUrl", "Ethan\"s_Project");
        Assert.assertThat(mingleConfig.getQuotedProjectIdentifier(), Matchers.is("\"Ethan\\\"s_Project\""));
    }

    @Test
    public void shouldRenderStringWithSpecifiedRegexAndLink() throws Exception {
        MingleConfig config = new MingleConfig("http://mingle05", "cce");
        String result = config.render("#111: checkin message");
        Assert.assertThat(result, Matchers.is(("<a href=\"" + "http://mingle05/projects/cce/cards/111\" target=\"story_tracker\">#111</a>: checkin message")));
    }

    @Test
    public void shouldReturnOriginalStringIfRegexDoesNotMatch() throws Exception {
        String toRender = "evo-abc: checkin message";
        MingleConfig config = new MingleConfig("http://mingle05", "cce");
        Assert.assertThat(config.render(toRender), Matchers.is(toRender));
    }

    @Test
    public void shouldGetMingleConfigAsTrackingTool() {
        MingleConfig mingleConfig = new MingleConfig("http://foo.bar:7019/baz", "go-project");
        TrackingTool expectedTrackingTool = new TrackingTool("http://foo.bar:7019/baz/projects/go-project/cards/${ID}", "#(\\d+)");
        Assert.assertThat(mingleConfig.asTrackingTool(), Matchers.is(expectedTrackingTool));
    }
}

