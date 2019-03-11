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
package org.apache.hadoop.fs.s3a.s3guard;


import PathMetadataDynamoDBTranslation.IGNORED_FIELDS;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.LambdaTestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.Mockito;


/**
 * Test the PathMetadataDynamoDBTranslation is able to translate between domain
 * model objects and DynamoDB items.
 */
public class TestPathMetadataDynamoDBTranslation extends Assert {
    private static final Path TEST_DIR_PATH = new Path("s3a://test-bucket/myDir");

    private static final Item TEST_DIR_ITEM = new Item();

    private static DDBPathMetadata testDirPathMetadata;

    private static final long TEST_FILE_LENGTH = 100;

    private static final long TEST_MOD_TIME = 9999;

    private static final long TEST_BLOCK_SIZE = 128;

    private static final Path TEST_FILE_PATH = new Path(TestPathMetadataDynamoDBTranslation.TEST_DIR_PATH, "myFile");

    private static final Item TEST_FILE_ITEM = new Item();

    private static DDBPathMetadata testFilePathMetadata;

    /**
     * It should not take long time as it doesn't involve remote server operation.
     */
    @Rule
    public final Timeout timeout = new Timeout((30 * 1000));

    @Test
    public void testKeySchema() {
        final Collection<KeySchemaElement> keySchema = PathMetadataDynamoDBTranslation.PathMetadataDynamoDBTranslation.keySchema();
        Assert.assertNotNull(keySchema);
        Assert.assertEquals("There should be HASH and RANGE key in key schema", 2, keySchema.size());
        for (KeySchemaElement element : keySchema) {
            Assert.assertThat(element.getAttributeName(), CoreMatchers.anyOf(CoreMatchers.is(PARENT), CoreMatchers.is(CHILD)));
            Assert.assertThat(element.getKeyType(), CoreMatchers.anyOf(CoreMatchers.is(HASH.toString()), CoreMatchers.is(RANGE.toString())));
        }
    }

    @Test
    public void testAttributeDefinitions() {
        final Collection<AttributeDefinition> attrs = PathMetadataDynamoDBTranslation.PathMetadataDynamoDBTranslation.attributeDefinitions();
        Assert.assertNotNull(attrs);
        Assert.assertEquals("There should be HASH and RANGE attributes", 2, attrs.size());
        for (AttributeDefinition definition : attrs) {
            Assert.assertThat(definition.getAttributeName(), CoreMatchers.anyOf(CoreMatchers.is(PARENT), CoreMatchers.is(CHILD)));
            Assert.assertEquals(S.toString(), definition.getAttributeType());
        }
    }

    @Test
    public void testItemToPathMetadata() throws IOException {
        final String user = UserGroupInformation.getCurrentUser().getShortUserName();
        Assert.assertNull(itemToPathMetadata(null, user));
        TestPathMetadataDynamoDBTranslation.verify(TestPathMetadataDynamoDBTranslation.TEST_DIR_ITEM, itemToPathMetadata(TestPathMetadataDynamoDBTranslation.TEST_DIR_ITEM, user));
        TestPathMetadataDynamoDBTranslation.verify(TestPathMetadataDynamoDBTranslation.TEST_FILE_ITEM, itemToPathMetadata(TestPathMetadataDynamoDBTranslation.TEST_FILE_ITEM, user));
    }

    @Test
    public void testPathMetadataToItem() {
        TestPathMetadataDynamoDBTranslation.verify(pathMetadataToItem(TestPathMetadataDynamoDBTranslation.testDirPathMetadata), TestPathMetadataDynamoDBTranslation.testDirPathMetadata);
        TestPathMetadataDynamoDBTranslation.verify(pathMetadataToItem(TestPathMetadataDynamoDBTranslation.testFilePathMetadata), TestPathMetadataDynamoDBTranslation.testFilePathMetadata);
    }

    @Test
    public void testPathToParentKeyAttribute() {
        TestPathMetadataDynamoDBTranslation.doTestPathToParentKeyAttribute(TestPathMetadataDynamoDBTranslation.TEST_DIR_PATH);
        TestPathMetadataDynamoDBTranslation.doTestPathToParentKeyAttribute(TestPathMetadataDynamoDBTranslation.TEST_FILE_PATH);
    }

    @Test
    public void testPathToKey() throws Exception {
        LambdaTestUtils.intercept(IllegalArgumentException.class, new Callable<PrimaryKey>() {
            @Override
            public PrimaryKey call() throws Exception {
                return pathToKey(new Path("/"));
            }
        });
        TestPathMetadataDynamoDBTranslation.doTestPathToKey(TestPathMetadataDynamoDBTranslation.TEST_DIR_PATH);
        TestPathMetadataDynamoDBTranslation.doTestPathToKey(TestPathMetadataDynamoDBTranslation.TEST_FILE_PATH);
    }

    @Test
    public void testVersionRoundTrip() throws Throwable {
        final Item marker = createVersionMarker(DynamoDBMetadataStore.VERSION_MARKER, DynamoDBMetadataStore.VERSION, 0);
        Assert.assertEquals(("Extracted version from " + marker), DynamoDBMetadataStore.VERSION, extractVersionFromMarker(marker));
    }

    @Test
    public void testVersionMarkerNotStatusIllegalPath() throws Throwable {
        final Item marker = createVersionMarker(DynamoDBMetadataStore.VERSION_MARKER, DynamoDBMetadataStore.VERSION, 0);
        Assert.assertNull(("Path metadata fromfrom " + marker), itemToPathMetadata(marker, "alice"));
    }

    /**
     * Test when translating an {@link Item} to {@link DDBPathMetadata} works
     * if {@code IS_AUTHORITATIVE} flag is ignored.
     */
    @Test
    public void testIsAuthoritativeCompatibilityItemToPathMetadata() throws Exception {
        Item item = Mockito.spy(TestPathMetadataDynamoDBTranslation.TEST_DIR_ITEM);
        item.withBoolean(IS_AUTHORITATIVE, true);
        IGNORED_FIELDS.add(IS_AUTHORITATIVE);
        final String user = UserGroupInformation.getCurrentUser().getShortUserName();
        DDBPathMetadata meta = itemToPathMetadata(item, user);
        Mockito.verify(item, Mockito.never()).getBoolean(IS_AUTHORITATIVE);
        Assert.assertFalse(meta.isAuthoritativeDir());
    }

    /**
     * Test when translating an {@link DDBPathMetadata} to {@link Item} works
     * if {@code IS_AUTHORITATIVE} flag is ignored.
     */
    @Test
    public void testIsAuthoritativeCompatibilityPathMetadataToItem() {
        DDBPathMetadata meta = Mockito.spy(TestPathMetadataDynamoDBTranslation.testFilePathMetadata);
        meta.setAuthoritativeDir(true);
        IGNORED_FIELDS.add(IS_AUTHORITATIVE);
        Item item = pathMetadataToItem(meta);
        Mockito.verify(meta, Mockito.never()).isAuthoritativeDir();
        Assert.assertFalse(item.hasAttribute(IS_AUTHORITATIVE));
    }

    /**
     * Test when translating an {@link Item} to {@link DDBPathMetadata} works
     * if {@code LAST_UPDATED} flag is ignored.
     */
    @Test
    public void testIsLastUpdatedCompatibilityItemToPathMetadata() throws Exception {
        Item item = Mockito.spy(TestPathMetadataDynamoDBTranslation.TEST_DIR_ITEM);
        item.withLong(LAST_UPDATED, 100);
        IGNORED_FIELDS.add(LAST_UPDATED);
        final String user = UserGroupInformation.getCurrentUser().getShortUserName();
        DDBPathMetadata meta = itemToPathMetadata(item, user);
        Mockito.verify(item, Mockito.never()).getLong(LAST_UPDATED);
        Assert.assertFalse(meta.isAuthoritativeDir());
    }

    /**
     * Test when translating an {@link DDBPathMetadata} to {@link Item} works
     * if {@code LAST_UPDATED} flag is ignored.
     */
    @Test
    public void testIsLastUpdatedCompatibilityPathMetadataToItem() {
        DDBPathMetadata meta = Mockito.spy(TestPathMetadataDynamoDBTranslation.testFilePathMetadata);
        meta.setLastUpdated(100);
        IGNORED_FIELDS.add(LAST_UPDATED);
        Item item = pathMetadataToItem(meta);
        Mockito.verify(meta, Mockito.never()).getLastUpdated();
        Assert.assertFalse(item.hasAttribute(LAST_UPDATED));
    }
}

