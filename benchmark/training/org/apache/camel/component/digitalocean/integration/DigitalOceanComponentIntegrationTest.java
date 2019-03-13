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
package org.apache.camel.component.digitalocean.integration;


import com.myjeeva.digitalocean.pojo.Droplet;
import com.myjeeva.digitalocean.pojo.Image;
import com.myjeeva.digitalocean.pojo.Region;
import com.myjeeva.digitalocean.pojo.Size;
import com.myjeeva.digitalocean.pojo.Tag;
import java.util.List;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Must be manually tested. Provide your own oAuthToken")
public class DigitalOceanComponentIntegrationTest extends DigitalOceanTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint mockResultEndpoint;

    @Test
    public void testGetAccountInfo() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(2);
        Exchange exchange = template.request("direct:getAccountInfo", null);
        assertTrue(isEmailVerified());
        exchange = template.request("direct:getAccountInfo2", null);
        assertTrue(isEmailVerified());
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testGetAllActions() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(1);
        Exchange exchange = template.request("direct:getActions", null);
        assertMockEndpointsSatisfied();
        assertEquals(((List) (exchange.getOut().getBody())).size(), 30);
    }

    @Test
    public void testGetActionInfo() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(1);
        Exchange exchange = template.request("direct:getActionInfo", null);
        assertMockEndpointsSatisfied();
        assertEquals(getId(), new Integer(133459716));
    }

    @Test
    public void testGetDropletInfo() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(2);
        Exchange exchange = template.request("direct:getDroplet", null);
        assertEquals(getId(), new Integer(5428878));
        exchange = template.request("direct:getDroplet2", null);
        assertMockEndpointsSatisfied();
        assertEquals(getId(), new Integer(5428878));
    }

    @Test
    public void testCreateDroplet() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(1);
        Exchange exchange = template.request("direct:createDroplet", null);
        assertMockEndpointsSatisfied();
        Droplet droplet = ((Droplet) (exchange.getOut().getBody()));
        assertNotNull(droplet.getId());
        assertEquals(droplet.getRegion().getSlug(), "fra1");
        assertCollectionSize(droplet.getTags(), 2);
    }

    @Test
    public void testCreateMultipleDroplets() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(1);
        Exchange exchange = template.request("direct:createMultipleDroplets", null);
        assertMockEndpointsSatisfied();
        List<Droplet> droplets = ((List<Droplet>) (exchange.getOut().getBody()));
        assertCollectionSize(droplets, 2);
    }

    @Test
    public void testGetAllDroplets() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(1);
        Exchange exchange = template.request("direct:getDroplets", null);
        assertMockEndpointsSatisfied();
        assertEquals(((List) (exchange.getOut().getBody())).size(), 1);
    }

    @Test
    public void testGetDropletBackups() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(1);
        Exchange exchange = template.request("direct:getDropletBackups", null);
        assertMockEndpointsSatisfied();
        assertCollectionSize(((List) (exchange.getOut().getBody())), 0);
    }

    @Test
    public void testCreateTag() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(1);
        Exchange exchange = template.request("direct:createTag", null);
        assertMockEndpointsSatisfied();
        assertEquals(getName(), "tag1");
    }

    @Test
    public void testGetTags() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(1);
        Exchange exchange = template.request("direct:getTags", null);
        assertMockEndpointsSatisfied();
        assertEquals(((List<Tag>) (exchange.getOut().getBody())).get(0).getName(), "tag1");
    }

    @Test
    public void getImages() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(1);
        Exchange exchange = template.request("direct:getImages", null);
        assertMockEndpointsSatisfied();
        List<Image> images = ((List<Image>) (exchange.getOut().getBody()));
        assertNotEquals(images.size(), 1);
    }

    @Test
    public void getImage() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(1);
        Exchange exchange = template.request("direct:getImage", null);
        assertMockEndpointsSatisfied();
        assertEquals(exchange.getOut().getBody(Image.class).getSlug(), "ubuntu-14-04-x64");
    }

    @Test
    public void getSizes() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(1);
        Exchange exchange = template.request("direct:getSizes", null);
        assertMockEndpointsSatisfied();
        List<Size> sizes = ((List<Size>) (exchange.getOut().getBody()));
        System.out.println(sizes);
        assertNotEquals(sizes.size(), 1);
    }

    @Test
    public void getRegions() throws Exception {
        mockResultEndpoint.expectedMinimumMessageCount(1);
        Exchange exchange = template.request("direct:getRegions", null);
        assertMockEndpointsSatisfied();
        List<Region> regions = ((List<Region>) (exchange.getOut().getBody()));
        System.out.println(regions);
        assertNotEquals(regions.size(), 1);
    }
}

