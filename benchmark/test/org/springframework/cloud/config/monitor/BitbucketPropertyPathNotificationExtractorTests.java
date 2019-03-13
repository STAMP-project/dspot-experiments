/**
 * Copyright 2015-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.config.monitor;


import java.util.Map;
import org.junit.Test;
import org.springframework.http.HttpHeaders;


/**
 *
 *
 * @author Spencer Gibb
 * @author Dave Syer
 * @author Greg Jacobs
 */
public class BitbucketPropertyPathNotificationExtractorTests {
    private BitbucketPropertyPathNotificationExtractor extractor = new BitbucketPropertyPathNotificationExtractor();

    private HttpHeaders headers;

    @Test
    public void bitbucketSample() throws Exception {
        // https://confluence.atlassian.com/bitbucket/event-payloads-740262817.html#EventPayloads-Push
        Map<String, Object> value = readPayload("pathsamples/bitbucket.json");
        setHeaders("repo:push");
        PropertyPathNotification extracted = this.extractor.extract(this.headers, value);
        assertThat(extracted).isNotNull();
        assertThat(extracted.getPaths()[0]).isEqualTo("application.yml");
    }

    @Test
    public void bitbucketPullRequestFulfillmentDetected() throws Exception {
        // https://confluence.atlassian.com/bitbucket/event-payloads-740262817.html#EventPayloads-Merged
        Map<String, Object> value = readPayload("pathsamples/bitbucket.json");
        setHeaders("pullrequest:fulfilled");
        PropertyPathNotification extracted = this.extractor.extract(this.headers, value);
        assertThat(extracted).isNotNull();
        assertThat(extracted.getPaths()[0]).isEqualTo("application.yml");
    }

    @Test
    public void notAPushOrPullRequestNotDetected() throws Exception {
        assertNotExtracted("pathsamples/bitbucket.json", "issue:created");
    }

    @Test
    public void gitlabNotDetected() throws Exception {
        assertNotExtracted("pathsamples/gitlab.json", "repo:push");
    }

    @Test
    public void githubNotDetected() throws Exception {
        assertNotExtracted("pathsamples/github.json", "repo:push");
    }

    @Test
    public void missingUuidHeader() throws Exception {
        // https://confluence.atlassian.com/bitbucket/event-payloads-740262817.html#EventPayloads-Push
        Map<String, Object> value = readPayload("pathsamples/bitbucket.json");
        this.headers.set("X-Event-Key", "repo:push");
        PropertyPathNotification extracted = this.extractor.extract(this.headers, value);
        assertThat(extracted).isNull();
    }

    @Test
    public void missingChanges() throws Exception {
        // https://confluence.atlassian.com/bitbucket/event-payloads-740262817.html#EventPayloads-Push
        Map<String, Object> value = readPayload("pathsamples/bitbucket-invalid.json");
        setHeaders("repo:push");
        PropertyPathNotification extracted = this.extractor.extract(this.headers, value);
        assertThat(extracted).isNull();
    }

    @Test
    public void bitbucketServerSample() throws Exception {
        // https://confluence.atlassian.com/bitbucketserver/event-payload-938025882.html
        Map<String, Object> value = readPayload("pathsamples/bitbucketserver.json");
        setServerHeaders("repo:refs_changed");
        PropertyPathNotification extracted = this.extractor.extract(this.headers, value);
        assertThat(extracted).isNotNull();
        assertThat(extracted.getPaths()[0]).isEqualTo("application.yml");
    }

    @Test
    public void bitbucketServerSamplePullRequest() throws Exception {
        // https://confluence.atlassian.com/bitbucketserver/event-payload-938025882.html
        Map<String, Object> value = readPayload("pathsamples/bitbucketserver.json");
        setServerHeaders("pr:merged");
        PropertyPathNotification extracted = this.extractor.extract(this.headers, value);
        assertThat(extracted).isNotNull();
        assertThat(extracted.getPaths()[0]).isEqualTo("application.yml");
    }

    @Test
    public void notAPushOrPullRequestServer() throws Exception {
        assertNotExtractedServer("pathsamples/bitbucketserver.json", "repo:comment:added");
    }

    @Test
    public void missingUuidHeaderServer() throws Exception {
        // https://confluence.atlassian.com/bitbucketserver/event-payload-938025882.html
        Map<String, Object> value = readPayload("pathsamples/bitbucketserver.json");
        this.headers.set("X-Event-Key", "repo:refs_changed");
        PropertyPathNotification extracted = this.extractor.extract(this.headers, value);
        assertThat(extracted).isNull();
    }

    @Test
    public void missingChangesServer() throws Exception {
        // https://confluence.atlassian.com/bitbucketserver/event-payload-938025882.html
        Map<String, Object> value = readPayload("pathsamples/bitbucketserver-invalid.json");
        setServerHeaders("repo:refs_changed");
        PropertyPathNotification extracted = this.extractor.extract(this.headers, value);
        assertThat(extracted).isNull();
    }
}

