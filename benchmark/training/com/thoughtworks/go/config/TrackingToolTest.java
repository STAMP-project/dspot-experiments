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


import TrackingTool.LINK;
import TrackingTool.REGEX;
import com.thoughtworks.go.domain.ConfigErrors;
import java.util.HashMap;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TrackingToolTest {
    private TrackingTool trackingTool;

    @Test
    public void shouldSetTrackingToolAttributesFromConfigMap() {
        trackingTool = new TrackingTool();
        HashMap attributeMap = new HashMap();
        String expectedLink = "http://blah.com";
        attributeMap.put(LINK, expectedLink);
        String expectedRegex = "[a-z]*";
        attributeMap.put(REGEX, expectedRegex);
        trackingTool.setConfigAttributes(attributeMap);
        Assert.assertThat(trackingTool.getLink(), Matchers.is(expectedLink));
        Assert.assertThat(trackingTool.getRegex(), Matchers.is(expectedRegex));
    }

    @Test
    public void shouldEnsureTrackingToolLinkContainsIDForTheMatchingRegexGroup() {
        TrackingTool trackingTool = new TrackingTool("http://no-id.com", "some-regex");
        trackingTool.validate(null);
        ConfigErrors configErrors = trackingTool.errors();
        List<String> errors = configErrors.getAllOn(LINK);
        Assert.assertThat(errors.size(), Matchers.is(1));
        Assert.assertThat(errors, Matchers.hasItem("Link must be a URL containing '${ID}'. Go will replace the string '${ID}' with the first matched group from the regex at run-time."));
    }

    @Test
    public void shouldPopulateErrorsWhenOnlyLinkOrOnlyRegexIsSpecified() {
        trackingTool = new TrackingTool("link", "");
        trackingTool.validate(null);
        Assert.assertThat(trackingTool.errors().on(REGEX), Matchers.is("Regex should be populated"));
        trackingTool = new TrackingTool("", "regex");
        trackingTool.validate(null);
        Assert.assertThat(trackingTool.errors().on(LINK), Matchers.is("Link should be populated"));
    }

    @Test
    public void shouldNotPopulateErrorsWhenTimerSpecIsValid() {
        trackingTool = new TrackingTool("http://myLink-${ID}", "myRegex");
        trackingTool.validate(null);
        Assert.assertThat(trackingTool.errors().isEmpty(), Matchers.is(true));
        trackingTool = new TrackingTool("https://myLink-${ID}", "myRegex");
        trackingTool.validate(null);
        Assert.assertThat(trackingTool.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldRenderStringWithSpecifiedRegexAndLink() throws Exception {
        TrackingTool config = new TrackingTool("http://mingle05/projects/cce/cards/${ID}", "#(\\d+)");
        String result = config.render("#111: checkin message");
        Assert.assertThat(result, Matchers.is(("<a href=\"" + "http://mingle05/projects/cce/cards/111\" target=\"story_tracker\">#111</a>: checkin message")));
    }

    @Test
    public void shouldReturnOriginalStringIfRegexDoesNotMatch() throws Exception {
        String toRender = "evo-abc: checkin message";
        TrackingTool config = new TrackingTool("http://mingle05/projects/cce/cards/${ID}", "#(\\d+)");
        Assert.assertThat(config.render(toRender), Matchers.is(toRender));
    }

    @Test
    public void shouldValidate() {
        TrackingTool tool = new TrackingTool();
        tool.validateTree(null);
        Assert.assertThat(tool.errors().on(LINK), Matchers.is("Link should be populated"));
        Assert.assertThat(tool.errors().on(REGEX), Matchers.is("Regex should be populated"));
    }

    @Test
    public void shouldValidateLinkProtocol() {
        TrackingTool tool = new TrackingTool("file:///home/user/${ID}", "");
        tool.validate(null);
        Assert.assertThat(tool.errors().on(LINK), Matchers.is("Link must be a URL starting with https:// or http://"));
        tool = new TrackingTool("javascript:alert(${ID})", "");
        tool.validate(null);
        Assert.assertThat(tool.errors().on(LINK), Matchers.is("Link must be a URL starting with https:// or http://"));
    }
}

