/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.accumulostore.inputformat;


import TestGroups.EDGE;
import TestGroups.ENTITY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;


public class InputFormatTest {
    private enum KeyPackage {

        BYTE_ENTITY_KEY_PACKAGE,
        CLASSIC_KEY_PACKAGE;}

    private static final int NUM_ENTRIES = 1000;

    private static final List<Element> DATA = new ArrayList<>();

    private static final List<Element> DATA_WITH_VISIBILITIES = new ArrayList<>();

    static {
        for (int i = 0; i < (InputFormatTest.NUM_ENTRIES); i++) {
            final Entity entity = new Entity.Builder().group(ENTITY).vertex(("" + i)).property("property1", 1).build();
            final Edge edge = new Edge.Builder().group(EDGE).source(("" + i)).dest("B").directed(true).property("property1", 2).build();
            final Edge edge2 = new Edge.Builder().group(EDGE).source(("" + i)).dest("C").directed(true).property("property2", 3).build();
            InputFormatTest.DATA.add(edge);
            InputFormatTest.DATA.add(edge2);
            InputFormatTest.DATA.add(entity);
        }
        for (int i = 0; i < (InputFormatTest.NUM_ENTRIES); i++) {
            final Entity entity = new Entity.Builder().group(ENTITY).vertex(("" + i)).property("property1", 1).property("visibility", "public").build();
            final Edge edge = new Edge.Builder().group(EDGE).source(("" + i)).dest("B").directed(true).property("property1", 2).property("visibility", "private").build();
            final Edge edge2 = new Edge.Builder().group(EDGE).source(("" + i)).dest("C").directed(true).property("property2", 3).property("visibility", "public").build();
            InputFormatTest.DATA_WITH_VISIBILITIES.add(edge);
            InputFormatTest.DATA_WITH_VISIBILITIES.add(edge2);
            InputFormatTest.DATA_WITH_VISIBILITIES.add(entity);
        }
    }

    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    @Test
    public void shouldReturnCorrectDataToMapReduceJob() throws Exception {
        final GetElements op = new GetElements.Builder().view(new View()).build();
        final Set<String> expectedResults = new HashSet<>();
        for (final Element element : InputFormatTest.DATA) {
            expectedResults.add(InputFormatTest.getJsonString(element));
        }
        shouldReturnCorrectDataToMapReduceJob(getSchema(), InputFormatTest.KeyPackage.CLASSIC_KEY_PACKAGE, InputFormatTest.DATA, op, new User(), "instance1", expectedResults);
        shouldReturnCorrectDataToMapReduceJob(getSchema(), InputFormatTest.KeyPackage.BYTE_ENTITY_KEY_PACKAGE, InputFormatTest.DATA, op, new User(), "instance2", expectedResults);
    }

    @Test
    public void shouldReturnCorrectDataToMapReduceJobWithView() throws Exception {
        final Schema schema = getSchema();
        final GetElements op = new GetElements.Builder().view(new View.Builder().edge(EDGE).build()).build();
        final Set<String> expectedResults = new HashSet<>();
        for (final Element element : InputFormatTest.DATA) {
            if (element.getGroup().equals(EDGE)) {
                expectedResults.add(InputFormatTest.getJsonString(element));
            }
        }
        shouldReturnCorrectDataToMapReduceJob(schema, InputFormatTest.KeyPackage.BYTE_ENTITY_KEY_PACKAGE, InputFormatTest.DATA, op, new User(), "instance3", expectedResults);
        shouldReturnCorrectDataToMapReduceJob(schema, InputFormatTest.KeyPackage.CLASSIC_KEY_PACKAGE, InputFormatTest.DATA, op, new User(), "instance4", expectedResults);
    }

    @Test
    public void shouldReturnCorrectDataToMapReduceJobRespectingAuthorizations() throws Exception {
        final Schema schema = getSchemaWithVisibilities();
        final GetElements op = new GetElements.Builder().view(new View()).build();
        final Set<String> expectedResultsPublicNotPrivate = new HashSet<>();
        final Set<String> expectedResultsPrivate = new HashSet<>();
        for (final Element element : InputFormatTest.DATA_WITH_VISIBILITIES) {
            expectedResultsPrivate.add(InputFormatTest.getJsonString(element));
            if (element.getProperty("visibility").equals("public")) {
                expectedResultsPublicNotPrivate.add(InputFormatTest.getJsonString(element));
            }
        }
        final Set<String> privateAuth = new HashSet<>();
        privateAuth.add("public");
        privateAuth.add("private");
        final Set<String> publicNotPrivate = new HashSet<>();
        publicNotPrivate.add("public");
        final User userWithPrivate = new User("user1", privateAuth);
        final User userWithPublicNotPrivate = new User("user1", publicNotPrivate);
        shouldReturnCorrectDataToMapReduceJob(schema, InputFormatTest.KeyPackage.BYTE_ENTITY_KEY_PACKAGE, InputFormatTest.DATA_WITH_VISIBILITIES, op, userWithPublicNotPrivate, "instance5", expectedResultsPublicNotPrivate);
        shouldReturnCorrectDataToMapReduceJob(schema, InputFormatTest.KeyPackage.BYTE_ENTITY_KEY_PACKAGE, InputFormatTest.DATA_WITH_VISIBILITIES, op, userWithPrivate, "instance6", expectedResultsPrivate);
        shouldReturnCorrectDataToMapReduceJob(schema, InputFormatTest.KeyPackage.CLASSIC_KEY_PACKAGE, InputFormatTest.DATA_WITH_VISIBILITIES, op, userWithPublicNotPrivate, "instance7", expectedResultsPublicNotPrivate);
        shouldReturnCorrectDataToMapReduceJob(schema, InputFormatTest.KeyPackage.CLASSIC_KEY_PACKAGE, InputFormatTest.DATA_WITH_VISIBILITIES, op, userWithPrivate, "instance8", expectedResultsPrivate);
    }

    private class Driver extends Configured implements Tool {
        private final String outputDir;

        Driver(final String outputDir) {
            this.outputDir = outputDir;
        }

        @Override
        public int run(final String[] args) throws Exception {
            final Configuration conf = getConf();
            final Job job = new Job(conf);
            job.setJarByClass(getClass());
            job.setInputFormatClass(ElementInputFormat.class);
            job.setMapperClass(InputFormatTest.AMapper.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(NullWritable.class);
            job.setNumReduceTasks(0);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(NullWritable.class);
            job.setOutputFormatClass(SequenceFileOutputFormat.class);
            SequenceFileOutputFormat.setOutputPath(job, new Path(outputDir));
            job.setNumReduceTasks(0);
            job.waitForCompletion(true);
            return job.isSuccessful() ? 0 : 1;
        }
    }

    private static class AMapper extends Mapper<Element, NullWritable, Text, NullWritable> {
        @Override
        protected void map(final Element key, final NullWritable nw, final Context context) throws IOException, InterruptedException {
            context.write(new Text(InputFormatTest.getJsonString(key)), nw);
        }
    }
}

