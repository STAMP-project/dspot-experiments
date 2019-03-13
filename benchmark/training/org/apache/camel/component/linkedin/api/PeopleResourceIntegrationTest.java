/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.linkedin.api;


import MembershipStateCode.MEMBER;
import Order.RECENCY;
import PostCategoryCode.DISCUSSION;
import PostRole.FOLLOWER;
import java.util.Date;
import org.apache.camel.component.linkedin.api.model.GroupMemberships;
import org.apache.camel.component.linkedin.api.model.JobSuggestions;
import org.apache.camel.component.linkedin.api.model.Person;
import org.apache.camel.component.linkedin.api.model.Posts;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration test for {@link PeopleResource}.
 */
public class PeopleResourceIntegrationTest extends AbstractResourceIntegrationTest {
    private static PeopleResource peopleResource;

    @Test
    public void testGetPerson() throws Exception {
        execute(new Runnable() {
            @Override
            public void run() {
                final Person person = PeopleResourceIntegrationTest.peopleResource.getPerson(":(id)", true);
                Assert.assertNotNull(person);
                Assert.assertNotNull(person.getId());
                AbstractResourceIntegrationTest.LOG.debug("getPerson result: {}", person);
            }
        });
    }

    @Test
    public void testGetPosts() throws Exception {
        execute(new Runnable() {
            @Override
            public void run() {
                final GroupMemberships groupMemberships = PeopleResourceIntegrationTest.peopleResource.getGroupMemberships(MEMBER, "", null, null);
                Assert.assertNotNull(groupMemberships);
                Assert.assertNotNull(groupMemberships.getGroupMembershipList());
                Assert.assertFalse(groupMemberships.getGroupMembershipList().isEmpty());
                final Posts posts = PeopleResourceIntegrationTest.peopleResource.getPosts(Long.parseLong(groupMemberships.getGroupMembershipList().get(0).getGroup().getId()), null, null, RECENCY, FOLLOWER, DISCUSSION, null, ":(id)");
                Assert.assertNotNull(posts);
                AbstractResourceIntegrationTest.LOG.debug("getPosts result: {}", posts);
            }
        });
    }

    @Test(expected = LinkedInException.class)
    public void testLinkedInError() throws Exception {
        execute(new Runnable() {
            @Override
            public void run() {
                PeopleResourceIntegrationTest.peopleResource.getPerson("bad_fields_selector", true);
            }
        });
    }

    @Test
    public void testOAuthTokenRefresh() throws Exception {
        PeopleResourceIntegrationTest.peopleResource.getPerson("", false);
        // mark OAuth token as expired
        final OAuthToken oAuthToken = AbstractResourceIntegrationTest.requestFilter.getOAuthToken();
        final long expiryTime = oAuthToken.getExpiryTime();
        oAuthToken.setExpiryTime(new Date().getTime());
        try {
            PeopleResourceIntegrationTest.peopleResource.getPerson("", false);
        } finally {
            AbstractResourceIntegrationTest.token.setExpiryTime(expiryTime);
        }
    }

    @Test
    public void testGetSuggestedJobs() throws Exception {
        execute(new Runnable() {
            @Override
            public void run() {
                final JobSuggestions suggestedJobs = PeopleResourceIntegrationTest.peopleResource.getSuggestedJobs(AbstractResourceIntegrationTest.DEFAULT_FIELDS);
                Assert.assertNotNull(suggestedJobs);
                AbstractResourceIntegrationTest.LOG.debug("Suggested Jobs {}", suggestedJobs.getJobs());
            }
        });
    }
}

