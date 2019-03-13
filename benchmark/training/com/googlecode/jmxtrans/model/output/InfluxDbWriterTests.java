/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.model.output;


import ResultAttributes.ATTRIBUTE_NAME;
import ResultAttributes.CLASS_NAME;
import ResultAttributes.OBJ_DOMAIN;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.googlecode.jmxtrans.cli.JmxTransConfiguration;
import com.googlecode.jmxtrans.guice.JmxTransModule;
import com.googlecode.jmxtrans.model.JmxProcess;
import com.googlecode.jmxtrans.model.QueryFixtures;
import com.googlecode.jmxtrans.model.Result;
import com.googlecode.jmxtrans.model.ResultAttribute;
import com.googlecode.jmxtrans.model.ResultAttributes;
import com.googlecode.jmxtrans.model.ServerFixtures;
import com.googlecode.jmxtrans.util.ProcessConfigUtils;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link InfluxDbWriter}.
 *
 * @author <a href="https://github.com/sihutch">github.com/sihutch</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class InfluxDbWriterTests {
    private static final String DATABASE_NAME = "database";

    private static final String HOST = "host.example.net";

    private static final ImmutableMap<String, String> DEFAULT_CUSTOM_TAGS = ImmutableMap.of();

    private static final ImmutableList<String> DEFAULT_TYPE_NAMES = ImmutableList.of();

    @Mock
    private InfluxDB influxDB;

    @Captor
    private ArgumentCaptor<BatchPoints> messageCaptor;

    private static final ConsistencyLevel DEFAULT_CONSISTENCY_LEVEL = ConsistencyLevel.ALL;

    private static final String DEFAULT_RETENTION_POLICY = "default";

    private static final ImmutableSet<ResultAttribute> DEFAULT_RESULT_ATTRIBUTES = ImmutableSet.copyOf(ResultAttributes.values());

    Result result = new Result(2L, "attributeName", "className", "objDomain", "keyAlias", "type=test,name=name", ImmutableList.of("key"), 1);

    Result resultStr = new Result(2L, "attributeName", "className", "objDomain", "keyAlias", "type=test,name=name", ImmutableList.of("key"), ((Object) ("hello")));

    ImmutableList<Result> results = ImmutableList.of(result);

    ImmutableList<Result> resultsStr = ImmutableList.of(resultStr);

    @Test
    public void pointsAreWrittenToInfluxDb() throws Exception {
        BatchPoints batchPoints = writeToInfluxDb(getTestInfluxDbWriterWithDefaultSettings());
        // The database name is present
        assertThat(batchPoints.getDatabase()).isEqualTo(InfluxDbWriterTests.DATABASE_NAME);
        // Point only exposes its state via a line protocol so we have to
        // make assertions against this.
        // Format is:
        // measurement,<comma separated key=val tags>" " <comma separated
        // key=val fields>" "time
        Map<String, String> expectedTags = new TreeMap<String, String>();
        expectedTags.put(ATTRIBUTE_NAME.getName(), result.getAttributeName());
        expectedTags.put(CLASS_NAME.getName(), result.getClassName());
        expectedTags.put(OBJ_DOMAIN.getName(), result.getObjDomain());
        expectedTags.put(InfluxDbWriter.TAG_HOSTNAME, InfluxDbWriterTests.HOST);
        String lineProtocol = buildLineProtocol(result.getKeyAlias(), expectedTags);
        List<Point> points = batchPoints.getPoints();
        assertThat(points).hasSize(1);
        Point point = points.get(0);
        assertThat(point.lineProtocol()).startsWith(lineProtocol);
    }

    @Test
    public void customTagsAreWrittenToDb() throws Exception {
        ImmutableMap<String, String> tags = ImmutableMap.of("customTag", "customValue");
        BatchPoints batchPoints = writeToInfluxDb(getTestInfluxDbWriterWithCustomTags(tags));
        assertThat(batchPoints.getPoints().get(0).lineProtocol()).contains("customTag=customValue");
    }

    @Test
    public void attributeNameIncludesTypeNames() throws Exception {
        ImmutableList<String> typeNames = ImmutableList.of("name");
        BatchPoints batchPoints = writeToInfluxDb(getTestInfluxDbWriterWithTypeNames(typeNames));
        assertThat(batchPoints.getPoints().get(0).lineProtocol()).contains("name.attributeName_key");
    }

    @Test
    public void attributeNameIncludesTypeNamesAsTags() throws Exception {
        ImmutableList<String> typeNames = ImmutableList.of("name");
        InfluxDbWriter writer = getTestInfluxDbWriterWithTypeNamesTags(typeNames);
        writer.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), results);
        Mockito.verify(influxDB).write(messageCaptor.capture());
        BatchPoints batchPoints = messageCaptor.getValue();
        assertThat(batchPoints.getDatabase()).isEqualTo(InfluxDbWriterTests.DATABASE_NAME);
        List<Point> points = batchPoints.getPoints();
        assertThat(points).hasSize(1);
        Point point = points.get(0);
        assertThat(point.lineProtocol()).contains(",name=name,");
    }

    @Test
    public void writeConsistencyLevelsAreAppliedToBatchPointsBeingWritten() throws Exception {
        for (ConsistencyLevel consistencyLevel : ConsistencyLevel.values()) {
            BatchPoints batchPoints = writeAtLeastOnceToInfluxDb(getTestInfluxDbWriterWithWriteConsistency(consistencyLevel));
            assertThat(batchPoints.getConsistency()).isEqualTo(consistencyLevel);
        }
    }

    @Test
    public void onlyRequestedResultPropertiesAreAppliedAsTags() throws Exception {
        for (ResultAttribute expectedResultTag : ResultAttributes.values()) {
            ImmutableSet<ResultAttribute> expectedResultTags = ImmutableSet.of(expectedResultTag);
            BatchPoints batchPoints = writeAtLeastOnceToInfluxDb(getTestInfluxDbWriterWithResultTags(expectedResultTags));
            String lineProtocol = batchPoints.getPoints().get(0).lineProtocol();
            assertThat(lineProtocol).contains(((expectedResultTag.getName()) + "="));
            Set<ResultAttribute> unexpectedResultTags = new HashSet<ResultAttribute>(ResultAttributes.values());
            unexpectedResultTags.remove(expectedResultTag);
            for (ResultAttribute unexpectedResultTag : unexpectedResultTags) {
                assertThat(lineProtocol).doesNotContain(((unexpectedResultTag.getName()) + "="));
            }
        }
    }

    @Test
    public void databaseIsCreated() throws Exception {
        writeToInfluxDb(getTestInfluxDbWriterWithDefaultSettings());
        Mockito.verify(influxDB).createDatabase(InfluxDbWriterTests.DATABASE_NAME);
    }

    @Test
    public void databaseIsNotCreated() throws Exception {
        writeToInfluxDb(getTestInfluxDbWriterNoDatabaseCreation());
        Mockito.verify(influxDB, Mockito.never()).createDatabase(ArgumentMatchers.anyString());
    }

    @Test
    public void jmxPortIsReportedAsFieldByDefault() throws Exception {
        BatchPoints batchPoints = writeToInfluxDb(getTestInfluxDbWriterWithDefaultSettings());
        /* Fields token */
        verifyJMXPortOnlyInToken(batchPoints.getPoints().get(0).lineProtocol(), 1);
    }

    @Test
    public void jmxPortIsReportedAsTag() throws Exception {
        BatchPoints batchPoints = writeToInfluxDb(getTestInfluxDbWriterWithReportJmxPortAsTag());
        /* Tags token */
        verifyJMXPortOnlyInToken(batchPoints.getPoints().get(0).lineProtocol(), 0);
    }

    @Test
    public void loadingFromFile() throws IOException, URISyntaxException {
        File input = new File(InfluxDbWriterTests.class.getResource("/influxDB.json").toURI());
        Injector injector = JmxTransModule.createInjector(new JmxTransConfiguration());
        ProcessConfigUtils processConfigUtils = injector.getInstance(ProcessConfigUtils.class);
        JmxProcess process = processConfigUtils.parseProcess(input);
        assertThat(process.getName()).isEqualTo("influxDB.json");
    }

    @Test
    public void valueString() throws Exception {
        InfluxDbWriter writer = getTestInfluxDbWriterWithStringValue();
        writer.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), resultsStr);
        Mockito.verify(influxDB).write(messageCaptor.capture());
        BatchPoints batchPoints = messageCaptor.getValue();
        assertThat(batchPoints.getDatabase()).isEqualTo(InfluxDbWriterTests.DATABASE_NAME);
        List<Point> points = batchPoints.getPoints();
        assertThat(points).hasSize(1);
        Point point = points.get(0);
        assertThat(point.lineProtocol()).contains("key=\"hello\"");
    }
}

