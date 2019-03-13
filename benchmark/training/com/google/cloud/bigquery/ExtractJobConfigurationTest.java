/**
 * Copyright 2016 Google LLC
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
package com.google.cloud.bigquery;


import JobConfiguration.Type.EXTRACT;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ExtractJobConfigurationTest {
    private static final String TEST_PROJECT_ID = "test-project-id";

    private static final List<String> DESTINATION_URIS = ImmutableList.of("uri1", "uri2");

    private static final String DESTINATION_URI = "uri1";

    private static final TableId TABLE_ID = TableId.of("dataset", "table");

    private static final String FIELD_DELIMITER = ",";

    private static final String FORMAT = "CSV";

    private static final String JSON_FORMAT = "NEWLINE_DELIMITED_JSON";

    private static final Boolean PRINT_HEADER = true;

    private static final String COMPRESSION = "GZIP";

    private static final ExtractJobConfiguration EXTRACT_CONFIGURATION = ExtractJobConfiguration.newBuilder(ExtractJobConfigurationTest.TABLE_ID, ExtractJobConfigurationTest.DESTINATION_URIS).setPrintHeader(ExtractJobConfigurationTest.PRINT_HEADER).setFieldDelimiter(ExtractJobConfigurationTest.FIELD_DELIMITER).setCompression(ExtractJobConfigurationTest.COMPRESSION).setFormat(ExtractJobConfigurationTest.FORMAT).build();

    private static final ExtractJobConfiguration EXTRACT_CONFIGURATION_ONE_URI = ExtractJobConfiguration.newBuilder(ExtractJobConfigurationTest.TABLE_ID, ExtractJobConfigurationTest.DESTINATION_URI).setPrintHeader(ExtractJobConfigurationTest.PRINT_HEADER).setFieldDelimiter(ExtractJobConfigurationTest.FIELD_DELIMITER).setCompression(ExtractJobConfigurationTest.COMPRESSION).setFormat(ExtractJobConfigurationTest.FORMAT).build();

    @Test
    public void testToBuilder() {
        compareExtractJobConfiguration(ExtractJobConfigurationTest.EXTRACT_CONFIGURATION, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.toBuilder().build());
        ExtractJobConfiguration job = ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.toBuilder().setSourceTable(TableId.of("dataset", "newTable")).build();
        Assert.assertEquals("newTable", job.getSourceTable().getTable());
        job = job.toBuilder().setSourceTable(ExtractJobConfigurationTest.TABLE_ID).build();
        compareExtractJobConfiguration(ExtractJobConfigurationTest.EXTRACT_CONFIGURATION, job);
    }

    @Test
    public void testOf() {
        ExtractJobConfiguration job = ExtractJobConfiguration.of(ExtractJobConfigurationTest.TABLE_ID, ExtractJobConfigurationTest.DESTINATION_URIS);
        Assert.assertEquals(ExtractJobConfigurationTest.TABLE_ID, job.getSourceTable());
        Assert.assertEquals(ExtractJobConfigurationTest.DESTINATION_URIS, job.getDestinationUris());
        job = ExtractJobConfiguration.of(ExtractJobConfigurationTest.TABLE_ID, ExtractJobConfigurationTest.DESTINATION_URI);
        Assert.assertEquals(ExtractJobConfigurationTest.TABLE_ID, job.getSourceTable());
        Assert.assertEquals(ImmutableList.of(ExtractJobConfigurationTest.DESTINATION_URI), job.getDestinationUris());
        job = ExtractJobConfiguration.of(ExtractJobConfigurationTest.TABLE_ID, ExtractJobConfigurationTest.DESTINATION_URIS, ExtractJobConfigurationTest.JSON_FORMAT);
        Assert.assertEquals(ExtractJobConfigurationTest.TABLE_ID, job.getSourceTable());
        Assert.assertEquals(ExtractJobConfigurationTest.DESTINATION_URIS, job.getDestinationUris());
        Assert.assertEquals(ExtractJobConfigurationTest.JSON_FORMAT, job.getFormat());
        job = ExtractJobConfiguration.of(ExtractJobConfigurationTest.TABLE_ID, ExtractJobConfigurationTest.DESTINATION_URI, ExtractJobConfigurationTest.JSON_FORMAT);
        Assert.assertEquals(ExtractJobConfigurationTest.TABLE_ID, job.getSourceTable());
        Assert.assertEquals(ImmutableList.of(ExtractJobConfigurationTest.DESTINATION_URI), job.getDestinationUris());
        Assert.assertEquals(ExtractJobConfigurationTest.JSON_FORMAT, job.getFormat());
    }

    @Test
    public void testToBuilderIncomplete() {
        ExtractJobConfiguration job = ExtractJobConfiguration.of(ExtractJobConfigurationTest.TABLE_ID, ExtractJobConfigurationTest.DESTINATION_URIS);
        compareExtractJobConfiguration(job, job.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(ExtractJobConfigurationTest.TABLE_ID, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.getSourceTable());
        Assert.assertEquals(ExtractJobConfigurationTest.DESTINATION_URIS, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.getDestinationUris());
        Assert.assertEquals(ExtractJobConfigurationTest.FIELD_DELIMITER, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.getFieldDelimiter());
        Assert.assertEquals(ExtractJobConfigurationTest.COMPRESSION, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.getCompression());
        Assert.assertEquals(ExtractJobConfigurationTest.PRINT_HEADER, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.printHeader());
        Assert.assertEquals(ExtractJobConfigurationTest.FORMAT, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.getFormat());
        Assert.assertEquals(ExtractJobConfigurationTest.TABLE_ID, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION_ONE_URI.getSourceTable());
        Assert.assertEquals(ImmutableList.of(ExtractJobConfigurationTest.DESTINATION_URI), ExtractJobConfigurationTest.EXTRACT_CONFIGURATION_ONE_URI.getDestinationUris());
        Assert.assertEquals(ExtractJobConfigurationTest.FIELD_DELIMITER, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION_ONE_URI.getFieldDelimiter());
        Assert.assertEquals(ExtractJobConfigurationTest.COMPRESSION, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION_ONE_URI.getCompression());
        Assert.assertEquals(ExtractJobConfigurationTest.PRINT_HEADER, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION_ONE_URI.printHeader());
        Assert.assertEquals(ExtractJobConfigurationTest.FORMAT, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION_ONE_URI.getFormat());
    }

    @Test
    public void testToPbAndFromPb() {
        Assert.assertNotNull(ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.toPb().getExtract());
        Assert.assertNull(ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.toPb().getCopy());
        Assert.assertNull(ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.toPb().getLoad());
        Assert.assertNull(ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.toPb().getQuery());
        compareExtractJobConfiguration(ExtractJobConfigurationTest.EXTRACT_CONFIGURATION, ExtractJobConfiguration.fromPb(ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.toPb()));
        compareExtractJobConfiguration(ExtractJobConfigurationTest.EXTRACT_CONFIGURATION_ONE_URI, ExtractJobConfiguration.fromPb(ExtractJobConfigurationTest.EXTRACT_CONFIGURATION_ONE_URI.toPb()));
        ExtractJobConfiguration job = ExtractJobConfiguration.of(ExtractJobConfigurationTest.TABLE_ID, ExtractJobConfigurationTest.DESTINATION_URIS);
        compareExtractJobConfiguration(job, ExtractJobConfiguration.fromPb(job.toPb()));
    }

    @Test
    public void testSetProjectId() {
        ExtractJobConfiguration configuration = ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.setProjectId(ExtractJobConfigurationTest.TEST_PROJECT_ID);
        Assert.assertEquals(ExtractJobConfigurationTest.TEST_PROJECT_ID, configuration.getSourceTable().getProject());
    }

    @Test
    public void testSetProjectIdDoNotOverride() {
        ExtractJobConfiguration configuration = ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.toBuilder().setSourceTable(ExtractJobConfigurationTest.TABLE_ID.setProjectId(ExtractJobConfigurationTest.TEST_PROJECT_ID)).build().setProjectId("do-not-update");
        Assert.assertEquals(ExtractJobConfigurationTest.TEST_PROJECT_ID, configuration.getSourceTable().getProject());
    }

    @Test
    public void testGetType() {
        Assert.assertEquals(EXTRACT, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION.getType());
        Assert.assertEquals(EXTRACT, ExtractJobConfigurationTest.EXTRACT_CONFIGURATION_ONE_URI.getType());
    }
}

