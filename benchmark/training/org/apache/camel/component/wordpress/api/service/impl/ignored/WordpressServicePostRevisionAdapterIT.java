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
package org.apache.camel.component.wordpress.api.service.impl.ignored;


import java.util.List;
import org.apache.camel.component.wordpress.api.model.PostRevision;
import org.apache.camel.component.wordpress.api.service.WordpressServicePostRevision;
import org.apache.camel.component.wordpress.api.test.WordpressMockServerTestSupport;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/* TODO fix authentication problem (when implementing global authentication) 
javax.ws.rs.NotAuthorizedException: HTTP 401 Unauthorized
 */
@Ignore("Not implemented yet")
public class WordpressServicePostRevisionAdapterIT extends WordpressMockServerTestSupport {
    private static WordpressServicePostRevision servicePostRevision;

    @Test
    public void testRetrieve() {
        final PostRevision revision = WordpressServicePostRevisionAdapterIT.servicePostRevision.retrieve(1, 1, null);
        Assert.assertThat(revision, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(revision.getId(), CoreMatchers.is(1));
        Assert.assertThat(revision.getGuid(), CoreMatchers.notNullValue());
    }

    @Test
    public void testList() {
        final List<PostRevision> revisions = WordpressServicePostRevisionAdapterIT.servicePostRevision.list(1, null);
        Assert.assertThat(revisions, CoreMatchers.is(CoreMatchers.not(Matchers.emptyCollectionOf(PostRevision.class))));
        Assert.assertThat(revisions.size(), Matchers.greaterThan(0));
    }
}

