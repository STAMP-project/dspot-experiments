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
package org.apache.beam.sdk.io.gcp.spanner;


import SpannerSchema.Column;
import SpannerSchema.KeyPart;
import com.google.cloud.spanner.ReadOnlyTransaction;
import com.google.cloud.spanner.Type;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.transforms.DoFnTester;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


/**
 * A test of {@link ReadSpannerSchemaTest}.
 */
public class ReadSpannerSchemaTest {
    @Rule
    public final transient ExpectedException thrown = ExpectedException.none();

    private FakeServiceFactory serviceFactory;

    private ReadOnlyTransaction mockTx;

    @Test
    public void simple() throws Exception {
        // Simplest schema: a table with int64 key
        ReadOnlyTransaction tx = Mockito.mock(ReadOnlyTransaction.class);
        Mockito.when(serviceFactory.mockDatabaseClient().readOnlyTransaction()).thenReturn(tx);
        preparePkMetadata(tx, Arrays.asList(ReadSpannerSchemaTest.pkMetadata("test", "key", "ASC")));
        prepareColumnMetadata(tx, Arrays.asList(ReadSpannerSchemaTest.columnMetadata("test", "key", "INT64")));
        SpannerConfig config = SpannerConfig.create().withProjectId("test-project").withInstanceId("test-instance").withDatabaseId("test-database").withServiceFactory(serviceFactory);
        DoFnTester<Void, SpannerSchema> tester = DoFnTester.of(new ReadSpannerSchema(config));
        List<SpannerSchema> schemas = tester.processBundle(Arrays.asList(((Void) (null))));
        Assert.assertEquals(1, schemas.size());
        SpannerSchema schema = schemas.get(0);
        Assert.assertEquals(1, schema.getTables().size());
        SpannerSchema.Column column = Column.create("key", Type.int64());
        SpannerSchema.KeyPart keyPart = KeyPart.create("key", false);
        Assert.assertThat(schema.getColumns("test"), Matchers.contains(column));
        Assert.assertThat(schema.getKeyParts("test"), Matchers.contains(keyPart));
    }
}

