/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.rest.server.resources;


import HttpStatus.TEMPORARY_REDIRECT_307;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author andy
created 17/04/2018
 */
public class RootDocumentTest {
    @Test
    public void shouldRedirectToInfoIfNoUI() throws Exception {
        // Given:
        final RootDocument doc = new RootDocument();
        final UriInfo uriInfo = uriInfo("http://some/proxy");
        // When:
        final Response response = doc.get(uriInfo);
        // Then:
        Assert.assertThat(response.getStatus(), CoreMatchers.is(TEMPORARY_REDIRECT_307));
        Assert.assertThat(response.getLocation().toString(), CoreMatchers.is("http://some/proxy/info"));
    }
}

