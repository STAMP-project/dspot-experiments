/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.datastore;


import DatastoreOptions.Builder;
import com.google.cloud.TransportOptions;
import com.google.cloud.datastore.spi.DatastoreRpcFactory;
import com.google.cloud.datastore.spi.v1.DatastoreRpc;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class DatastoreOptionsTest {
    private static final String PROJECT_ID = "project-id";

    private static final int PORT = 8080;

    private DatastoreRpcFactory datastoreRpcFactory;

    private DatastoreRpc datastoreRpc;

    private Builder options;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testProjectId() throws Exception {
        Assert.assertEquals(DatastoreOptionsTest.PROJECT_ID, options.build().getProjectId());
    }

    @Test
    public void testHost() throws Exception {
        Assert.assertEquals(("http://localhost:" + (DatastoreOptionsTest.PORT)), options.build().getHost());
    }

    @Test
    public void testNamespace() throws Exception {
        Assert.assertTrue(options.build().getNamespace().isEmpty());
        Assert.assertEquals("ns1", options.setNamespace("ns1").build().getNamespace());
    }

    @Test
    public void testDatastore() throws Exception {
        Assert.assertSame(datastoreRpc, options.build().getRpc());
    }

    @Test
    public void testToBuilder() throws Exception {
        DatastoreOptions original = options.setNamespace("ns1").build();
        DatastoreOptions copy = original.toBuilder().build();
        Assert.assertEquals(original.getProjectId(), copy.getProjectId());
        Assert.assertEquals(original.getNamespace(), copy.getNamespace());
        Assert.assertEquals(original.getHost(), copy.getHost());
        Assert.assertEquals(original.getRetrySettings(), copy.getRetrySettings());
        Assert.assertEquals(original.getCredentials(), copy.getCredentials());
    }

    @Test
    public void testInvalidTransport() {
        thrown.expect(IllegalArgumentException.class);
        DatastoreOptions.newBuilder().setTransportOptions(EasyMock.createMock(TransportOptions.class));
    }
}

