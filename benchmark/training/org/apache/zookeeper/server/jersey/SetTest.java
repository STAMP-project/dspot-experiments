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
import MediaType.APPLICATION_OCTET_STREAM;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import java.util.Arrays;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.jersey.jaxb.ZStat;
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
public class SetTest extends Base {
    protected static final Logger LOG = LoggerFactory.getLogger(SetTest.class);

    private String accept;

    private String path;

    private String encoding;

    private Status expectedStatus;

    private ZStat expectedStat;

    private byte[] data;

    public static class MyWatcher implements Watcher {
        public void process(WatchedEvent event) {
            // FIXME ignore for now
        }
    }

    public SetTest(String accept, String path, String encoding, ClientResponse.Status status, ZStat expectedStat, byte[] data) {
        this.accept = accept;
        this.path = path;
        this.encoding = encoding;
        this.expectedStatus = status;
        this.expectedStat = expectedStat;
        this.data = data;
    }

    @Test
    public void testSet() throws Exception {
        if ((expectedStat) != null) {
            zk.create(expectedStat.path, "initial".getBytes(), OPEN_ACL_UNSAFE, PERSISTENT);
        }
        WebResource wr = znodesr.path(path).queryParam("dataformat", encoding);
        if ((data) == null) {
            wr = wr.queryParam("null", "true");
        }
        Builder builder = wr.accept(accept).type(APPLICATION_OCTET_STREAM);
        ClientResponse cr;
        if ((data) == null) {
            cr = builder.put(ClientResponse.class);
        } else {
            // this shouldn't be necessary (wrapping data with string)
            // but without it there are problems on the server - ie it
            // hangs for 30 seconds and doesn't get the data.
            // TODO investigate
            cr = builder.put(ClientResponse.class, new String(data));
        }
        Assert.assertEquals(expectedStatus, cr.getClientResponseStatus());
        if ((expectedStat) == null) {
            return;
        }
        ZStat zstat = cr.getEntity(ZStat.class);
        Assert.assertEquals(expectedStat, zstat);
        // use out-of-band method to verify
        byte[] data = zk.getData(zstat.path, false, new Stat());
        if ((data == null) && ((this.data) == null)) {
            return;
        } else
            if ((data == null) || ((this.data) == null)) {
                Assert.fail((((data == null ? null : new String(data)) + " == ") + ((this.data) == null ? null : new String(this.data))));
            } else {
                Assert.assertTrue((((new String(data)) + " == ") + (new String(this.data))), Arrays.equals(data, this.data));
            }

    }
}

