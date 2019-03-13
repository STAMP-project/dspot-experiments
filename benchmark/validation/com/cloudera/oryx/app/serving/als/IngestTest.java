/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.serving.als;


import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.junit.Test;


public final class IngestTest extends AbstractALSServingTest {
    static final String INGEST_DATA = "a,B,1\nc,B\nc,D,5.5\nc,D,\na,C,2,123456789";

    private static final String[][] EXPECTED_TOPIC = new String[][]{ new String[]{ "a", "B", "1.0" }, new String[]{ "c", "B", "1" }, new String[]{ "c", "D", "5.5" }, new String[]{ "c", "D", "" }, new String[]{ "a", "C", "2.0" } };

    @Test
    public void testSimpleIngest() {
        try (Response response = target("/ingest").request().post(Entity.text(IngestTest.INGEST_DATA))) {
            IngestTest.checkResponse(response);
        }
    }

    @Test
    public void testGZippedIngest() {
        byte[] compressed = compress(IngestTest.INGEST_DATA, GZIPOutputStream.class);
        Entity<byte[]> entity = Entity.entity(compressed, IngestTest.compressedVariant("gzip"));
        try (Response response = target("/ingest").request().post(entity)) {
            IngestTest.checkResponse(response);
        }
    }

    @Test
    public void testDeflateIngest() {
        byte[] compressed = compress(IngestTest.INGEST_DATA, DeflaterOutputStream.class);
        Entity<byte[]> entity = Entity.entity(compressed, IngestTest.compressedVariant("deflate"));
        try (Response response = target("/ingest").request().post(entity)) {
            IngestTest.checkResponse(response);
        }
    }

    @Test
    public void testFormIngest() throws Exception {
        IngestTest.checkResponse(getFormPostResponse(IngestTest.INGEST_DATA, "/ingest", null, null));
    }

    @Test
    public void testGzippedFormIngest() throws Exception {
        IngestTest.checkResponse(getFormPostResponse(IngestTest.INGEST_DATA, "/ingest", GZIPOutputStream.class, "gzip"));
    }

    @Test
    public void testZippedFormIngest() throws Exception {
        IngestTest.checkResponse(getFormPostResponse(IngestTest.INGEST_DATA, "/ingest", ZipOutputStream.class, "zip"));
    }
}

