/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server.jersey;


import ClientResponse.Status;
import CreateMode.PERSISTENT;
import Ids.OPEN_ACL_UNSAFE;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import com.sun.jersey.api.client.ClientResponse;
import java.util.Collections;
import java.util.List;
import org.apache.zookeeper.server.jersey.jaxb.ZChildren;
import org.apache.zookeeper.server.jersey.jaxb.ZChildrenJSON;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test stand-alone server.
 */
@RunWith(Parameterized.class)
public class GetChildrenTest extends Base {
    protected static final Logger LOG = LoggerFactory.getLogger(GetChildrenTest.class);

    private String accept;

    private String path;

    private Status expectedStatus;

    private String expectedPath;

    private List<String> expectedChildren;

    public GetChildrenTest(String accept, String path, ClientResponse.Status status, String expectedPath, List<String> expectedChildren) {
        this.accept = accept;
        this.path = path;
        this.expectedStatus = status;
        this.expectedPath = expectedPath;
        this.expectedChildren = expectedChildren;
    }

    @Test
    public void testGetChildren() throws Exception {
        if ((expectedChildren) != null) {
            for (String child : expectedChildren) {
                zk.create((((expectedPath) + "/") + child), null, OPEN_ACL_UNSAFE, PERSISTENT);
            }
        }
        ClientResponse cr = znodesr.path(path).queryParam("view", "children").accept(accept).get(ClientResponse.class);
        Assert.assertEquals(expectedStatus, cr.getClientResponseStatus());
        if ((expectedChildren) == null) {
            return;
        }
        if (accept.equals(APPLICATION_JSON)) {
            ZChildrenJSON zchildren = cr.getEntity(ZChildrenJSON.class);
            Collections.sort(expectedChildren);
            Collections.sort(zchildren.children);
            Assert.assertEquals(expectedChildren, zchildren.children);
            Assert.assertEquals(znodesr.path(path).toString(), zchildren.uri);
            Assert.assertEquals(((znodesr.path(path).toString()) + "/{child}"), zchildren.child_uri_template);
        } else
            if (accept.equals(APPLICATION_XML)) {
                ZChildren zchildren = cr.getEntity(ZChildren.class);
                Collections.sort(expectedChildren);
                Collections.sort(zchildren.children);
                Assert.assertEquals(expectedChildren, zchildren.children);
                Assert.assertEquals(znodesr.path(path).toString(), zchildren.uri);
                Assert.assertEquals(((znodesr.path(path).toString()) + "/{child}"), zchildren.child_uri_template);
            } else {
                Assert.fail("unknown accept type");
            }

    }
}

