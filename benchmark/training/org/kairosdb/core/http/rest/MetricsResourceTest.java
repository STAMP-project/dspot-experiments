/**
 * Copyright 2016 KairosDB Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.core.http.rest;


import HttpHeaders.ACCEPT_ENCODING;
import Level.OFF;
import ServerType.DELETE;
import ServerType.INGEST;
import ServerType.QUERY;
import ch.qos.logback.classic.Level;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.EnumSet;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.core.exception.InvalidServerTypeException;
import org.kairosdb.testing.Client;
import org.kairosdb.testing.JsonResponse;
import org.kairosdb.util.LoggingUtils;


public class MetricsResourceTest extends ResourceBase {
    private static final String ADD_METRIC_URL = "http://localhost:9001/api/v1/datapoints";

    private static final String GET_METRIC_URL = "http://localhost:9001/api/v1/datapoints/query";

    private static final String METRIC_NAMES_URL = "http://localhost:9001/api/v1/metricnames";

    private static final String DELETE_DATAPOINTS_URL = "http://localhost:9001/api/v1/datapoints/delete";

    private static final String DELETE_METRIC_URL = "http://localhost:9001/api/v1/metric/";

    private static final String VERSION_URL = "http://localhost:9001/api/v1/version";

    @Test
    public void testAddEmptyBody() throws Exception {
        JsonResponse response = ResourceBase.client.post("", MetricsResourceTest.ADD_METRIC_URL);
        MetricsResourceTest.assertResponse(response, 400, "{\"errors\":[\"Invalid json. No content due to end of input.\"]}");
    }

    @Test
    public void testAddSingleMetricLongValueSuccess() throws Exception {
        String json = Resources.toString(Resources.getResource("single-metric-long.json"), Charsets.UTF_8);
        JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.ADD_METRIC_URL);
        MetricsResourceTest.assertResponse(response, 204);
    }

    @Test
    public void testAddSingleMetricDoubleValueSuccess() throws Exception {
        String json = Resources.toString(Resources.getResource("single-metric-double.json"), Charsets.UTF_8);
        JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.ADD_METRIC_URL);
        MetricsResourceTest.assertResponse(response, 204);
    }

    @Test
    public void testAddMutipleDatapointSuccess() throws Exception {
        String json = Resources.toString(Resources.getResource("multiple-datapoints-metric.json"), Charsets.UTF_8);
        JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.ADD_METRIC_URL);
        MetricsResourceTest.assertResponse(response, 204);
    }

    @Test
    public void testAddMultipleMetricLongValueSuccess() throws Exception {
        String json = Resources.toString(Resources.getResource("multi-metric-long.json"), Charsets.UTF_8);
        JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.ADD_METRIC_URL);
        Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(204));
    }

    @Test
    public void testAddMissingName() throws Exception {
        String json = Resources.toString(Resources.getResource("single-metric-missing-name.json"), Charsets.UTF_8);
        JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.ADD_METRIC_URL);
        MetricsResourceTest.assertResponse(response, 400, "{\"errors\":[\"metric[0].name may not be empty.\"]}");
    }

    @Test
    public void testAddTimestampZeroValid() throws Exception {
        String json = Resources.toString(Resources.getResource("multi-metric-timestamp-zero.json"), Charsets.UTF_8);
        JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.ADD_METRIC_URL);
        MetricsResourceTest.assertResponse(response, 204);
    }

    @Test
    public void testQuery() throws IOException {
        String json = Resources.toString(Resources.getResource("query-metric-absolute-dates.json"), Charsets.UTF_8);
        JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.GET_METRIC_URL);
        MetricsResourceTest.assertResponse(response, 200, ("{\"queries\":" + ("[{\"sample_size\":10,\"results\":" + "[{\"name\":\"abc.123\",\"group_by\":[{\"name\":\"type\",\"type\":\"number\"}],\"tags\":{\"server\":[\"server1\",\"server2\"]},\"values\":[[1,60.2],[2,30.200000000000003],[3,20.1]]}]}]}")));
    }

    @Test
    public void testQueryWithBeanValidationException() throws IOException {
        String json = Resources.toString(Resources.getResource("invalid-query-metric-relative-unit.json"), Charsets.UTF_8);
        JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.GET_METRIC_URL);
        MetricsResourceTest.assertResponse(response, 400, "{\"errors\":[\"query.bogus is not a valid time unit, must be one of MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS,WEEKS,MONTHS,YEARS\"]}");
    }

    @Test
    public void testQueryWithJsonMapperParsingException() throws IOException {
        String json = Resources.toString(Resources.getResource("invalid-query-metric-json.json"), Charsets.UTF_8);
        JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.GET_METRIC_URL);
        MetricsResourceTest.assertResponse(response, 400, "{\"errors\":[\"com.google.gson.stream.MalformedJsonException: Use JsonReader.setLenient(true) to accept malformed JSON at line 2 column 22\"]}");
    }

    @Test
    public void testMetricNames() throws IOException {
        JsonResponse response = ResourceBase.client.get(MetricsResourceTest.METRIC_NAMES_URL);
        MetricsResourceTest.assertResponse(response, 200, "{\"results\":[\"cpu\",\"memory\",\"disk\",\"network\"]}");
    }

    /**
     * Verify that the web server will gzip the response if the Accept-Encoding header is set to "gzip".
     */
    @Test
    public void testGzippedResponse() throws IOException {
        Client client = new Client(true);
        client.addHeader(ACCEPT_ENCODING, "gzip");
        byte[] response = client.getAsBytes(MetricsResourceTest.VERSION_URL);
        Assert.assertThat(decompress(response), CoreMatchers.equalTo("{\"version\": \"null null\"}\n"));
    }

    @Test
    public void test_datastoreThrowsException() throws IOException, DatastoreException {
        Level previousLogLevel = LoggingUtils.setLogLevel(OFF);
        try {
            ResourceBase.datastore.throwException(new DatastoreException("bogus"));
            String json = Resources.toString(Resources.getResource("query-metric-absolute-dates.json"), Charsets.UTF_8);
            JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.GET_METRIC_URL);
            ResourceBase.datastore.throwException(null);
            Assert.assertThat(response.getStatusCode(), CoreMatchers.equalTo(500));
            Assert.assertThat(response.getJson(), CoreMatchers.equalTo("{\"errors\":[\"org.kairosdb.core.exception.DatastoreException: bogus\"]}"));
            Assert.assertEquals(3, ResourceBase.queuingManager.getAvailableThreads());
        } finally {
            LoggingUtils.setLogLevel(previousLogLevel);
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test_checkServerTypeStaticIngestDisabled() throws InvalidServerTypeException {
        thrown.expect(InvalidServerTypeException.class);
        thrown.expectMessage("{\"errors\": [\"Forbidden: INGEST API methods are disabled on this KairosDB instance.\"]}");
        MetricsResource.checkServerTypeStatic(EnumSet.of(QUERY, DELETE), INGEST, "/datapoints", "POST");
    }

    @Test
    public void test_checkServerTypeStaticQueryDisabled() throws InvalidServerTypeException {
        thrown.expect(InvalidServerTypeException.class);
        thrown.expectMessage("{\"errors\": [\"Forbidden: QUERY API methods are disabled on this KairosDB instance.\"]}");
        MetricsResource.checkServerTypeStatic(EnumSet.of(INGEST, DELETE), QUERY, "/datapoints/query", "POST");
    }

    @Test
    public void test_checkServerTypeStaticDeleteDisabled() throws InvalidServerTypeException {
        thrown.expect(InvalidServerTypeException.class);
        thrown.expectMessage("{\"errors\": [\"Forbidden: DELETE API methods are disabled on this KairosDB instance.\"]}");
        MetricsResource.checkServerTypeStatic(EnumSet.of(INGEST, QUERY), DELETE, "/datapoints/delete", "POST");
    }

    @Test
    public void test_checkServerTypeStaticIngestEnabled() throws InvalidServerTypeException {
        MetricsResource.checkServerTypeStatic(EnumSet.of(INGEST), INGEST, "/datapoints", "POST");
    }

    @Test
    public void test_checkServerTypeStaticQueryEnabled() throws InvalidServerTypeException {
        MetricsResource.checkServerTypeStatic(EnumSet.of(QUERY), QUERY, "/datapoints/query", "POST");
    }

    @Test
    public void test_checkServerTypeStaticDeleteEnabled() throws InvalidServerTypeException {
        MetricsResource.checkServerTypeStatic(EnumSet.of(DELETE), DELETE, "/datapoints/delete", "POST");
    }

    @Test
    public void testAddMetricIngestDisabled() throws IOException {
        ResourceBase.resource.setServerType("QUERY");
        String json = Resources.toString(Resources.getResource("single-metric-long.json"), Charsets.UTF_8);
        JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.ADD_METRIC_URL);
        MetricsResourceTest.assertResponse(response, 403, "{\"errors\": [\"Forbidden: INGEST API methods are disabled on this KairosDB instance.\"]}");
        ResourceBase.resource.setServerType("INGEST,QUERY,DELETE");
    }

    @Test
    public void testGetMetricQueryDisabled() throws IOException {
        ResourceBase.resource.setServerType("INGEST");
        String json = Resources.toString(Resources.getResource("invalid-query-metric-relative-unit.json"), Charsets.UTF_8);
        JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.GET_METRIC_URL);
        MetricsResourceTest.assertResponse(response, 403, "{\"errors\": [\"Forbidden: QUERY API methods are disabled on this KairosDB instance.\"]}");
        ResourceBase.resource.setServerType("INGEST,QUERY,DELETE");
    }

    @Test
    public void testMetricNamesQueryDisabled() throws IOException {
        ResourceBase.resource.setServerType("INGEST");
        JsonResponse response = ResourceBase.client.get(MetricsResourceTest.METRIC_NAMES_URL);
        MetricsResourceTest.assertResponse(response, 403, "{\"errors\": [\"Forbidden: QUERY API methods are disabled on this KairosDB instance.\"]}");
        ResourceBase.resource.setServerType("INGEST,QUERY,DELETE");
    }

    @Test
    public void testDeleteDatapointsDeleteDisabled() throws IOException {
        ResourceBase.resource.setServerType("INGEST");
        String json = Resources.toString(Resources.getResource("query-metric-absolute-dates.json"), Charsets.UTF_8);
        JsonResponse response = ResourceBase.client.post(json, MetricsResourceTest.DELETE_DATAPOINTS_URL);
        MetricsResourceTest.assertResponse(response, 403, "{\"errors\": [\"Forbidden: DELETE API methods are disabled on this KairosDB instance.\"]}");
        ResourceBase.resource.setServerType("INGEST,QUERY,DELETE");
    }

    @Test
    public void testDeleteMetricDeleteDisabled() throws IOException {
        ResourceBase.resource.setServerType("INGEST");
        String metricName = "Some.Metric.Name";
        JsonResponse response = ResourceBase.client.delete(((MetricsResourceTest.DELETE_METRIC_URL) + metricName));
        MetricsResourceTest.assertResponse(response, 403, "{\"errors\": [\"Forbidden: DELETE API methods are disabled on this KairosDB instance.\"]}");
    }
}

