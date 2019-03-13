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
package com.thoughtworks.go.domain;


import GoConstants.CONFIG_SCHEMA_VERSION;
import com.thoughtworks.go.util.TimeProvider;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GoConfigRevisionTest {
    private Date date;

    private TimeProvider timeProvider;

    @Test
    public void shouldGenerateCommentString() {
        GoConfigRevision configRevision = new GoConfigRevision("config-xml", "my-md5", "loser", "100.3.9.71", timeProvider);
        Assert.assertThat(configRevision.getComment(), Matchers.is(String.format("user:loser|timestamp:%s|schema_version:%s|go_edition:OpenSource|go_version:100.3.9.71|md5:my-md5", date.getTime(), CONFIG_SCHEMA_VERSION)));
    }

    @Test
    public void shouldGenerateCommentStringWithJoinCharacterEscaped() {
        GoConfigRevision configRevision = new GoConfigRevision("config-xml", "my-|md5||", "los|er|", "100.3.|9.71||", timeProvider);
        Assert.assertThat(configRevision.getComment(), Matchers.is(String.format("user:los||er|||timestamp:%s|schema_version:%s|go_edition:OpenSource|go_version:100.3.||9.71|||||md5:my-||md5||||", date.getTime(), CONFIG_SCHEMA_VERSION)));
    }

    @Test
    public void shouldParsePartsFromComment() {
        GoConfigRevision configRevision = new GoConfigRevision("config-xml", String.format("user:los||er|||timestamp:%s|schema_version:20|go_edition:Enterprise|go_version:100.3.||9.71|||||md5:my-||md5||||", date.getTime()));
        Assert.assertThat(configRevision.getContent(), Matchers.is("config-xml"));
        Assert.assertThat(configRevision.getMd5(), Matchers.is("my-|md5||"));
        Assert.assertThat(configRevision.getGoVersion(), Matchers.is("100.3.|9.71||"));
        Assert.assertThat(configRevision.getGoEdition(), Matchers.is("Enterprise"));
        Assert.assertThat(configRevision.getUsername(), Matchers.is("los|er|"));
        Assert.assertThat(configRevision.getTime(), Matchers.is(date));
        Assert.assertThat(configRevision.getSchemaVersion(), Matchers.is(20));
    }

    @Test
    public void shouldThrowExceptionWhenCommentIsInvalid() {
        try {
            new GoConfigRevision("config-xml", "foo");
            Assert.fail("should have failed for invalid comment");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("failed to parse comment [foo]"));
        }
    }

    @Test
    public void shouldUnderstandEquality() {
        GoConfigRevision rev1 = new GoConfigRevision("blah", "md5", "loser", "2.2.2", new TimeProvider());
        GoConfigRevision rev2 = new GoConfigRevision("blah blah", "md5", "loser 2", "2.2.3", new TimeProvider());
        Assert.assertThat(rev1, Matchers.is(rev2));
    }

    @Test
    public void canAnswerIfRevisionContentIsBackedByByteArrayWhenContentIsString() {
        GoConfigRevision rev = new GoConfigRevision("blah", "md5", "loser", "2.2.2", new TimeProvider());
        Assert.assertThat(rev.isByteArrayBacked(), Matchers.is(false));
        Assert.assertThat(rev.getConfigXmlBytes(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(rev.getContent(), Matchers.is("blah"));
    }

    @Test
    public void canAnswerIfRevisionContentIsBackedByByteArrayWhenContentIsAByteArray() {
        GoConfigRevision rev = new GoConfigRevision("blah".getBytes(StandardCharsets.UTF_8), String.format("user:los||er|||timestamp:%s|schema_version:%s|go_edition:OpenSource|go_version:100.3.||9.71|||||md5:my-||md5||||", date.getTime(), CONFIG_SCHEMA_VERSION));
        Assert.assertThat(rev.isByteArrayBacked(), Matchers.is(true));
        Assert.assertThat(Arrays.asList(rev.getConfigXmlBytes()), Matchers.hasItems("blah".getBytes(StandardCharsets.UTF_8)));
        Assert.assertThat(rev.getContent(), Matchers.is("blah"));
    }
}

