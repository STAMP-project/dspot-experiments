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
package org.kairosdb.core.http.rest.json;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.kairosdb.core.KairosDataPointFactory;
import org.kairosdb.core.TestDataPointFactory;
import org.kairosdb.core.datapoints.StringDataPoint;
import org.kairosdb.core.datastore.Datastore;
import org.kairosdb.core.datastore.DatastoreMetricQuery;
import org.kairosdb.core.datastore.QueryCallback;
import org.kairosdb.core.datastore.ServiceKeyStore;
import org.kairosdb.core.datastore.ServiceKeyValue;
import org.kairosdb.core.datastore.TagSet;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.eventbus.FilterEventBus;
import org.kairosdb.eventbus.Publisher;
import org.kairosdb.eventbus.Subscribe;
import org.kairosdb.events.DataPointEvent;


@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class DataPointsParserTest {
    private static KairosDataPointFactory dataPointFactory = new TestDataPointFactory();

    private FilterEventBus eventBus;

    private Publisher<DataPointEvent> publisher;

    @Test
    public void test_emptyJson_Invalid() throws IOException, DatastoreException {
        String json = "";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getFirstError(), CoreMatchers.equalTo("Invalid json. No content due to end of input."));
    }

    @Test
    public void test_nullMetricName_Invalid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"timestamp\": 1234, \"value\": 456, \"datapoints\": [[1,2]], \"tags\":{\"foo\":\"bar\"}}, {\"datapoints\": [[1,2]], \"tags\":{\"foo\":\"bar\"}}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getFirstError(), CoreMatchers.equalTo("metric[1].name may not be null."));
    }

    @Test
    public void test_timestampButNoValue_Invalid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"timestamp\": 1234, \"tags\": {\"foo\":\"bar\"}}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getFirstError(), CoreMatchers.equalTo("metric[0](name=metric1).value may not be null."));
    }

    @Test
    public void test_valueButNoTimestamp_Invalid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"value\": 1234, \"tags\":{\"foo\":\"bar\"}}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getFirstError(), CoreMatchers.equalTo("metric[0](name=metric1).timestamp may not be null."));
    }

    @Test
    public void test_timestamp_Zero_Valid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"timestamp\": 0, \"value\": 1234, \"tags\":{\"foo\":\"bar\"}}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(0));
        MatcherAssert.assertThat(parser.getDataPointCount(), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_Timestamp_Negative_Valid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"timestamp\": -1, \"value\": 1234, \"tags\":{\"foo\":\"bar\"}}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(0));
        MatcherAssert.assertThat(parser.getDataPointCount(), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_datapoints_empty_Invalid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"tags\":{\"foo\":\"bar\"}, \"datapoints\": [[]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getFirstError(), CoreMatchers.equalTo("metric[0](name=metric1).datapoints[0].timestamp cannot be null or empty."));
    }

    @Test
    public void test_datapoints_empty_value_Invalid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"tags\":{\"foo\":\"bar\"}, \"datapoints\": [[2,]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getFirstError(), CoreMatchers.equalTo("metric[0](name=metric1).datapoints[0].value may not be empty."));
    }

    @Test
    public void test_datapoints_empty_timestamp_Invalid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"tags\":{\"foo\":\"bar\"}, \"datapoints\": [[,2]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getFirstError(), CoreMatchers.equalTo("metric[0](name=metric1).datapoints[0].timestamp may not be null."));
    }

    @Test
    public void test_emptyMetricName_Invalid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"\", \"tags\":{\"foo\":\"bar\"}, \"datapoints\": [[1,2]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getFirstError(), CoreMatchers.equalTo("metric[0].name may not be empty."));
    }

    @Test
    public void test_metricName_validCharacters() throws IOException, DatastoreException {
        String json = "[{\"name\": \"bad:\u4f60\u597dname\", \"tags\":{\"foo\":\"bar\"}, \"datapoints\": [[1,2]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(0));
        MatcherAssert.assertThat(parser.getDataPointCount(), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_emptyTags_Invalid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metricName\", \"timestamp\": 12345, \"value\": 456, \"datapoints\": [[1,2]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getFirstError(), CoreMatchers.equalTo("metric[0](name=metricName).tags count must be greater than or equal to 1."));
    }

    @Test
    public void test_datapoints_timestamp_zero_Valid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"tags\":{\"foo\":\"bar\"}, \"datapoints\": [[0,2]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(0));
        MatcherAssert.assertThat(parser.getDataPointCount(), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_datapoints_timestamp_negative_Valid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"tags\":{\"foo\":\"bar\"}, \"datapoints\": [[-1,2]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(0));
        MatcherAssert.assertThat(parser.getDataPointCount(), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_emptyTagName_Invalid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metricName\", \"tags\":{\"\":\"bar\"}, \"datapoints\": [[1,2]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getFirstError(), CoreMatchers.equalTo("metric[0](name=metricName).tag[0].name may not be empty."));
        MatcherAssert.assertThat(parser.getDataPointCount(), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_tagName_withColon() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metricName\", \"tags\":{\"bad:name\":\"bar\"}, \"datapoints\": [[1,2]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_emptyTagValue_Invalid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metricName\", \"tags\":{\"foo\":\"\"}, \"datapoints\": [[1,2]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getFirstError(), CoreMatchers.equalTo("metric[0](name=metricName).tag[foo].value may not be empty."));
    }

    @Test
    public void test_tagValue_withColon() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metricName\", \"tags\":{\"foo\":\"bad:value\"}, \"datapoints\": [[1,2]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_multipleValidationFailures() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metricName\", \"timestamp\": 456, \"value\":\"\", \"tags\":{\"name\":\"\"}}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(validationErrors.getErrors(), CoreMatchers.hasItem("metric[0](name=metricName).tag[name].value may not be empty."));
        MatcherAssert.assertThat(validationErrors.getErrors(), CoreMatchers.hasItem("metric[0](name=metricName).value may not be empty."));
    }

    /**
     * Zero is a special case.
     */
    @Test
    public void test_value_decimal_with_zeros() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metricName\", \"tags\":{\"foo\":\"bar\"}, \"datapoints\": [[1, \"0.000000\"]]}]";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void test_validJsonWithTimestampValue() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"timestamp\": 1234, \"value\": 4321, \"tags\":{\"foo\":\"bar\"}}]";
        DataPointsParserTest.FakeDataStore fakeds = new DataPointsParserTest.FakeDataStore();
        eventBus.register(fakeds);
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.hasErrors(), CoreMatchers.equalTo(false));
        List<DataPointSet> dataPointSetList = fakeds.getDataPointSetList();
        MatcherAssert.assertThat(dataPointSetList.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getName(), CoreMatchers.equalTo("metric1"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().get("foo"), CoreMatchers.equalTo("bar"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getTimestamp(), CoreMatchers.equalTo(1234L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getLongValue(), CoreMatchers.equalTo(4321L));
    }

    @Test(expected = JsonSyntaxException.class)
    public void test_invalidJson() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"timestamp\": 1234, \"value\": }]";
        DataPointsParserTest.FakeDataStore fakeds = new DataPointsParserTest.FakeDataStore();
        eventBus.register(fakeds);
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.hasErrors(), CoreMatchers.equalTo(false));
        List<DataPointSet> dataPointSetList = fakeds.getDataPointSetList();
        MatcherAssert.assertThat(dataPointSetList.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getName(), CoreMatchers.equalTo("metric1"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().size(), CoreMatchers.equalTo(0));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getTimestamp(), CoreMatchers.equalTo(1234L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getLongValue(), CoreMatchers.equalTo(4321L));
    }

    @Test
    public void test_validJsonWithTimestampValueAndDataPoints() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\", \"time\": 1234, \"value\": 4321, \"datapoints\": [[456, 654]], \"tags\":{\"foo\":\"bar\"}}]";
        DataPointsParserTest.FakeDataStore fakeds = new DataPointsParserTest.FakeDataStore();
        eventBus.register(fakeds);
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.hasErrors(), CoreMatchers.equalTo(false));
        List<DataPointSet> dataPointSetList = fakeds.getDataPointSetList();
        MatcherAssert.assertThat(dataPointSetList.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getName(), CoreMatchers.equalTo("metric1"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().get("foo"), CoreMatchers.equalTo("bar"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getTimestamp(), CoreMatchers.equalTo(1234L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getLongValue(), CoreMatchers.equalTo(4321L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(1).getTimestamp(), CoreMatchers.equalTo(456L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(1).getLongValue(), CoreMatchers.equalTo(654L));
        MatcherAssert.assertThat(parser.getDataPointCount(), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_validJsonWithDatapoints() throws IOException, DatastoreException {
        String json = Resources.toString(Resources.getResource("json-metric-parser-multiple-metric.json"), Charsets.UTF_8);
        DataPointsParserTest.FakeDataStore fakeds = new DataPointsParserTest.FakeDataStore();
        eventBus.register(fakeds);
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.hasErrors(), CoreMatchers.equalTo(false));
        List<DataPointSet> dataPointSetList = fakeds.getDataPointSetList();
        MatcherAssert.assertThat(dataPointSetList.size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(dataPointSetList.get(0).getName(), CoreMatchers.equalTo("archive_file_tracked"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().get("host"), CoreMatchers.equalTo("server1"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().size(), CoreMatchers.equalTo(3));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getTimestamp(), CoreMatchers.equalTo(1349109376L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getLongValue(), CoreMatchers.equalTo(123L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(1).getTimestamp(), CoreMatchers.equalTo(1349109377L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(1).getDoubleValue(), CoreMatchers.equalTo(13.2));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(2).getTimestamp(), CoreMatchers.equalTo(1349109378L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(2).getDoubleValue(), CoreMatchers.equalTo(23.1));
        MatcherAssert.assertThat(dataPointSetList.get(1).getName(), CoreMatchers.equalTo("archive_file_search"));
        MatcherAssert.assertThat(dataPointSetList.get(1).getTags().size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(dataPointSetList.get(1).getTags().get("host"), CoreMatchers.equalTo("server2"));
        MatcherAssert.assertThat(dataPointSetList.get(1).getTags().get("customer"), CoreMatchers.equalTo("Acme"));
        MatcherAssert.assertThat(dataPointSetList.get(1).getDataPoints().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(1).getDataPoints().get(0).getTimestamp(), CoreMatchers.equalTo(1349109378L));
        MatcherAssert.assertThat(dataPointSetList.get(1).getDataPoints().get(0).getLongValue(), CoreMatchers.equalTo(321L));
        MatcherAssert.assertThat(parser.getDataPointCount(), CoreMatchers.equalTo(4));
    }

    @Test
    public void test_validJsonWithTypes() throws IOException, DatastoreException {
        String json = Resources.toString(Resources.getResource("json-metric-parser-metrics-with-type.json"), Charsets.UTF_8);
        DataPointsParserTest.FakeDataStore fakeds = new DataPointsParserTest.FakeDataStore();
        eventBus.register(fakeds);
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.hasErrors(), CoreMatchers.equalTo(false));
        List<DataPointSet> dataPointSetList = fakeds.getDataPointSetList();
        MatcherAssert.assertThat(dataPointSetList.size(), CoreMatchers.equalTo(3));
        MatcherAssert.assertThat(dataPointSetList.get(0).getName(), CoreMatchers.equalTo("archive_file_tracked"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().get("host"), CoreMatchers.equalTo("server1"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().size(), CoreMatchers.equalTo(4));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getTimestamp(), CoreMatchers.equalTo(1349109376L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getLongValue(), CoreMatchers.equalTo(123L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(1).getTimestamp(), CoreMatchers.equalTo(1349109377L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(1).getDoubleValue(), CoreMatchers.equalTo(13.2));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(2).getTimestamp(), CoreMatchers.equalTo(1349109378L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(2).getDoubleValue(), CoreMatchers.equalTo(23.1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(3).getTimestamp(), CoreMatchers.equalTo(1349109378L));
        DataPoint dataPoint = dataPointSetList.get(0).getDataPoints().get(3);
        MatcherAssert.assertThat(dataPoint, CoreMatchers.instanceOf(StringDataPoint.class));
        MatcherAssert.assertThat(getValue(), CoreMatchers.equalTo("string_data"));
        MatcherAssert.assertThat(dataPointSetList.get(1).getName(), CoreMatchers.equalTo("archive_file_search"));
        MatcherAssert.assertThat(dataPointSetList.get(1).getTags().size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(dataPointSetList.get(1).getTags().get("host"), CoreMatchers.equalTo("server2"));
        MatcherAssert.assertThat(dataPointSetList.get(1).getTags().get("customer"), CoreMatchers.equalTo("Acme"));
        MatcherAssert.assertThat(dataPointSetList.get(1).getDataPoints().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(1).getDataPoints().get(0).getTimestamp(), CoreMatchers.equalTo(1349109378L));
        MatcherAssert.assertThat(dataPointSetList.get(1).getDataPoints().get(0).getLongValue(), CoreMatchers.equalTo(321L));
        MatcherAssert.assertThat(dataPointSetList.get(2).getName(), CoreMatchers.equalTo("archive_file_search_text"));
        MatcherAssert.assertThat(dataPointSetList.get(2).getTags().size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(dataPointSetList.get(2).getTags().get("host"), CoreMatchers.equalTo("server2"));
        MatcherAssert.assertThat(dataPointSetList.get(2).getTags().get("customer"), CoreMatchers.equalTo("Acme"));
        MatcherAssert.assertThat(dataPointSetList.get(2).getDataPoints().size(), CoreMatchers.equalTo(1));
        DataPoint stringData = dataPointSetList.get(2).getDataPoints().get(0);
        MatcherAssert.assertThat(stringData.getTimestamp(), CoreMatchers.equalTo(1349109378L));
        MatcherAssert.assertThat(getValue(), CoreMatchers.equalTo("sweet"));
        MatcherAssert.assertThat(parser.getDataPointCount(), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_justObjectNoArray_valid() throws IOException, DatastoreException {
        String json = "{\"name\": \"metric1\", \"timestamp\": 1234, \"value\": 4321, \"tags\":{\"foo\":\"bar\"}}";
        DataPointsParserTest.FakeDataStore fakeds = new DataPointsParserTest.FakeDataStore();
        eventBus.register(fakeds);
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.hasErrors(), CoreMatchers.equalTo(false));
        List<DataPointSet> dataPointSetList = fakeds.getDataPointSetList();
        MatcherAssert.assertThat(dataPointSetList.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getName(), CoreMatchers.equalTo("metric1"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().get("foo"), CoreMatchers.equalTo("bar"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getTimestamp(), CoreMatchers.equalTo(1234L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getLongValue(), CoreMatchers.equalTo(4321L));
    }

    @Test
    public void test_stringWithNoType_valid() throws IOException, DatastoreException {
        String json = "{\"name\": \"metric1\", \"timestamp\": 1234, \"value\": \"The Value\", \"tags\":{\"foo\":\"bar\"}}";
        DataPointsParserTest.FakeDataStore fakeds = new DataPointsParserTest.FakeDataStore();
        eventBus.register(fakeds);
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.hasErrors(), CoreMatchers.equalTo(false));
        List<DataPointSet> dataPointSetList = fakeds.getDataPointSetList();
        MatcherAssert.assertThat(dataPointSetList.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getName(), CoreMatchers.equalTo("metric1"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().get("foo"), CoreMatchers.equalTo("bar"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getTimestamp(), CoreMatchers.equalTo(1234L));
        MatcherAssert.assertThat(getValue(), CoreMatchers.equalTo("The Value"));
    }

    @Test
    public void test_stringWithNoTypeAsArray_valid() throws IOException, DatastoreException {
        String json = "[{\"name\": \"metric1\",\"datapoints\": [[1234, \"The Value\"]],\"tags\": {\"foo\": \"bar\"}}]";
        DataPointsParserTest.FakeDataStore fakeds = new DataPointsParserTest.FakeDataStore();
        eventBus.register(fakeds);
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.hasErrors(), CoreMatchers.equalTo(false));
        List<DataPointSet> dataPointSetList = fakeds.getDataPointSetList();
        MatcherAssert.assertThat(dataPointSetList.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getName(), CoreMatchers.equalTo("metric1"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().get("foo"), CoreMatchers.equalTo("bar"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getTimestamp(), CoreMatchers.equalTo(1234L));
        MatcherAssert.assertThat(getValue(), CoreMatchers.equalTo("The Value"));
    }

    @Test
    public void test_stringContainsInteger_valid() throws IOException, DatastoreException {
        String json = "{\"name\": \"metric1\", \"timestamp\": 1234, \"value\": \"123\", \"tags\":{\"foo\":\"bar\"}}";
        DataPointsParserTest.FakeDataStore fakeds = new DataPointsParserTest.FakeDataStore();
        eventBus.register(fakeds);
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.hasErrors(), CoreMatchers.equalTo(false));
        List<DataPointSet> dataPointSetList = fakeds.getDataPointSetList();
        MatcherAssert.assertThat(dataPointSetList.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getName(), CoreMatchers.equalTo("metric1"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().get("foo"), CoreMatchers.equalTo("bar"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getTimestamp(), CoreMatchers.equalTo(1234L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getLongValue(), CoreMatchers.equalTo(123L));
    }

    @Test
    public void test_stringContainsDouble_valid() throws IOException, DatastoreException {
        String json = "{\"name\": \"metric1\", \"timestamp\": 1234, \"value\": \"123.3\", \"tags\":{\"foo\":\"bar\"}}";
        DataPointsParserTest.FakeDataStore fakeds = new DataPointsParserTest.FakeDataStore();
        eventBus.register(fakeds);
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.hasErrors(), CoreMatchers.equalTo(false));
        List<DataPointSet> dataPointSetList = fakeds.getDataPointSetList();
        MatcherAssert.assertThat(dataPointSetList.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getName(), CoreMatchers.equalTo("metric1"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getTags().get("foo"), CoreMatchers.equalTo("bar"));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getTimestamp(), CoreMatchers.equalTo(1234L));
        MatcherAssert.assertThat(dataPointSetList.get(0).getDataPoints().get(0).getDoubleValue(), CoreMatchers.equalTo(123.3));
        MatcherAssert.assertThat(parser.getDataPointCount(), CoreMatchers.equalTo(1));
    }

    @Test
    public void test_valueType_invalid() throws IOException, DatastoreException {
        // Value is a map which is not valid
        String json = ("{\"name\": \"metric1\", \"timestamp\": 1234, \"value\": " + (new HashMap())) + ", \"tags\":{\"foo\":\"bar\"}}";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.hasErrors(), CoreMatchers.equalTo(true));
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getErrors().get(0), CoreMatchers.equalTo("metric[0](name=metric1) value is an invalid type"));
    }

    @Test
    public void test_valueType_dataPointArray_invalid() throws IOException, DatastoreException {
        // Value is a map which is not valid
        String json = ("{\"name\": \"metric1\", \"datapoints\": [[1349109376, " + (new HashMap())) + "]], \"tags\":{\"foo\":\"bar\"}}";
        DataPointsParser parser = new DataPointsParser(publisher, new StringReader(json), new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        MatcherAssert.assertThat(validationErrors.hasErrors(), CoreMatchers.equalTo(true));
        MatcherAssert.assertThat(validationErrors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(validationErrors.getErrors().get(0), CoreMatchers.equalTo("metric[0](name=metric1) value is an invalid type"));
    }

    @Test
    public void test_parserSpeed() throws IOException, DatastoreException {
        Reader skipReader = new InputStreamReader(new GZIPInputStream(ClassLoader.getSystemResourceAsStream("large_import_skip.gz")));
        Reader reader = new InputStreamReader(new GZIPInputStream(ClassLoader.getSystemResourceAsStream("large_import.gz")));
        DataPointsParser parser = new DataPointsParser(publisher, skipReader, new Gson(), DataPointsParserTest.dataPointFactory);
        ValidationErrors validationErrors = parser.parse();
        System.out.println(parser.getDataPointCount());
        System.out.println("No ValidationProperty.java");
        System.out.println(parser.getIngestTime());
        parser = new DataPointsParser(publisher, reader, new Gson(), DataPointsParserTest.dataPointFactory);
        validationErrors = parser.parse();
        System.out.println("With ValidationProperty.java");
        System.out.println(parser.getIngestTime());
    }

    private static class FakeDataStore implements Datastore , ServiceKeyStore {
        List<DataPointSet> dataPointSetList = new ArrayList<>();

        private DataPointSet lastDataPointSet;

        protected FakeDataStore() throws DatastoreException {
        }

        public List<DataPointSet> getDataPointSetList() {
            return dataPointSetList;
        }

        @Override
        public void close() throws InterruptedException, DatastoreException {
        }

        @Subscribe
        public void putDataPoint(DataPointEvent event) throws DatastoreException {
            if ((((lastDataPointSet) == null) || (!(lastDataPointSet.getName().equals(event.getMetricName())))) || (!(lastDataPointSet.getTags().equals(event.getTags())))) {
                lastDataPointSet = new DataPointSet(event.getMetricName(), event.getTags(), Collections.<DataPoint>emptyList());
                dataPointSetList.add(lastDataPointSet);
            }
            lastDataPointSet.addDataPoint(event.getDataPoint());
        }

        /* @Override
        public void putDataPoints(DataPointSet dps) throws DatastoreException
        {
        dataPointSetList.add(dps);
        }
         */
        @Override
        public Iterable<String> getMetricNames(String prefix) throws DatastoreException {
            return null;
        }

        @Override
        public Iterable<String> getTagNames() throws DatastoreException {
            return null;
        }

        @Override
        public Iterable<String> getTagValues() throws DatastoreException {
            return null;
        }

        @Override
        public void queryDatabase(DatastoreMetricQuery query, QueryCallback queryCallback) throws DatastoreException {
        }

        @Override
        public void deleteDataPoints(DatastoreMetricQuery deleteQuery) throws DatastoreException {
        }

        @Override
        public TagSet queryMetricTags(DatastoreMetricQuery query) throws DatastoreException {
            return null;
        }

        @Override
        public void setValue(String service, String serviceKey, String key, String value) throws DatastoreException {
        }

        @Override
        public ServiceKeyValue getValue(String service, String serviceKey, String key) throws DatastoreException {
            return null;
        }

        @Override
        public Iterable<String> listServiceKeys(String service) throws DatastoreException {
            return null;
        }

        @Override
        public Iterable<String> listKeys(String service, String serviceKey) throws DatastoreException {
            return null;
        }

        @Override
        public Iterable<String> listKeys(String service, String serviceKey, String keyStartsWith) throws DatastoreException {
            return null;
        }

        @Override
        public void deleteKey(String service, String serviceKey, String key) throws DatastoreException {
        }

        @Override
        public Date getServiceKeyLastModifiedTime(String service, String serviceKey) throws DatastoreException {
            return null;
        }
    }
}

