/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.examples.feedcombiner.resources;


import MediaType.APPLICATION_JSON;
import Status.CREATED;
import Status.NOT_FOUND;
import Status.NO_CONTENT;
import Status.OK;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.examples.feedcombiner.model.CombinedFeed;
import org.glassfish.jersey.examples.feedcombiner.model.FeedEntry;
import org.glassfish.jersey.examples.feedcombiner.store.ReadWriteLockDataStore;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Petr Bouda
 */
public class CombinedFeedResourceTest extends JerseyTest {
    private static final String RESOURCE_URI = "feeds";

    private WebTarget target;

    private ReadWriteLockDataStore datastore;

    @Test
    public void testCreate() {
        CombinedFeed feed = CombinedFeedTestHelper.combinedFeed("1");
        Entity<CombinedFeed> entity = Entity.entity(feed, APPLICATION_JSON_TYPE);
        Response response = target.request(APPLICATION_JSON).post(entity);
        Assert.assertEquals(CREATED.getStatusCode(), response.getStatus());
        Assert.assertEquals(feed, response.readEntity(CombinedFeed.class));
    }

    @Test
    public void testDelete() {
        // Saving entity
        CombinedFeed feed = CombinedFeedTestHelper.combinedFeed("1");
        datastore.put(feed.getId(), feed);
        // Deleting entity
        Response deleteResp = target.path(feed.getId()).request().delete();
        Assert.assertEquals(NO_CONTENT.getStatusCode(), deleteResp.getStatus());
    }

    @Test
    public void testDeleteNotFound() {
        // Saving entity
        CombinedFeed feed = CombinedFeedTestHelper.combinedFeed("1");
        datastore.put(feed.getId(), feed);
        // Deleting entity
        Response deleteResp = target.path("wrong_id").request().delete();
        Assert.assertEquals(NOT_FOUND.getStatusCode(), deleteResp.getStatus());
    }

    @Test
    public void testGetEntriesJSON() {
        Response response = callGetEntries(APPLICATION_JSON_TYPE);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        List<FeedEntry> restoredEntities = response.readEntity(new javax.ws.rs.core.GenericType<List<FeedEntry>>() {});
        Assert.assertEquals(CombinedFeedTestHelper.feedEntries(), restoredEntities);
    }

    @Test
    public void testGetEntriesATOM() {
        Response response = callGetEntries(APPLICATION_ATOM_XML_TYPE);
        Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(APPLICATION_ATOM_XML_TYPE, response.getMediaType());
    }

    @Test
    public void testGetEntriesNotFound() {
        Response response = target.path("1").path("entries").request(APPLICATION_JSON_TYPE).get();
        Assert.assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
    }
}

