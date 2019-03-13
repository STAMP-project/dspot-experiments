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


import Mutation.WriteBuilder;
import PipelineResult.State.DONE;
import SpannerIO.FailureMode.REPORT_FAILURES;
import com.google.cloud.spanner.DatabaseAdminClient;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Spanner;
import java.io.Serializable;
import javax.annotation.Nullable;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestPipelineOptions;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Wait;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Predicate;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Predicates;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Throwables;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * End-to-end test of Cloud Spanner Sink.
 */
@RunWith(JUnit4.class)
public class SpannerWriteIT {
    private static final int MAX_DB_NAME_LENGTH = 30;

    @Rule
    public final transient TestPipeline p = TestPipeline.create();

    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    /**
     * Pipeline options for this test.
     */
    public interface SpannerTestPipelineOptions extends TestPipelineOptions {
        @Description("Instance ID to write to in Spanner")
        @Default.String("beam-test")
        String getInstanceId();

        void setInstanceId(String value);

        @Description("Database ID prefix to write to in Spanner")
        @Default.String("beam-testdb")
        String getDatabaseIdPrefix();

        void setDatabaseIdPrefix(String value);

        @Description("Table name")
        @Default.String("users")
        String getTable();

        void setTable(String value);
    }

    private Spanner spanner;

    private DatabaseAdminClient databaseAdminClient;

    private SpannerWriteIT.SpannerTestPipelineOptions options;

    private String databaseName;

    private String project;

    @Test
    public void testWrite() throws Exception {
        int numRecords = 100;
        p.apply(GenerateSequence.from(0).to(numRecords)).apply(ParDo.of(new SpannerWriteIT.GenerateMutations(options.getTable()))).apply(SpannerIO.write().withProjectId(project).withInstanceId(options.getInstanceId()).withDatabaseId(databaseName));
        PipelineResult result = p.run();
        result.waitUntilFinish();
        Assert.assertThat(result.getState(), Matchers.is(DONE));
        Assert.assertThat(countNumberOfRecords(), Matchers.equalTo(((long) (numRecords))));
    }

    @Test
    public void testSequentialWrite() throws Exception {
        int numRecords = 100;
        SpannerWriteResult stepOne = p.apply("first step", GenerateSequence.from(0).to(numRecords)).apply(ParDo.of(new SpannerWriteIT.GenerateMutations(options.getTable()))).apply(SpannerIO.write().withProjectId(project).withInstanceId(options.getInstanceId()).withDatabaseId(databaseName));
        p.apply("second step", GenerateSequence.from(numRecords).to((2 * numRecords))).apply("Gen mutations", ParDo.of(new SpannerWriteIT.GenerateMutations(options.getTable()))).apply(Wait.on(stepOne.getOutput())).apply("write to table2", SpannerIO.write().withProjectId(project).withInstanceId(options.getInstanceId()).withDatabaseId(databaseName));
        PipelineResult result = p.run();
        result.waitUntilFinish();
        Assert.assertThat(result.getState(), Matchers.is(DONE));
        Assert.assertThat(countNumberOfRecords(), Matchers.equalTo((2L * numRecords)));
    }

    @Test
    public void testReportFailures() throws Exception {
        int numRecords = 100;
        p.apply(GenerateSequence.from(0).to((2 * numRecords))).apply(ParDo.of(new SpannerWriteIT.GenerateMutations(options.getTable(), new SpannerWriteIT.DivBy2()))).apply(SpannerIO.write().withProjectId(project).withInstanceId(options.getInstanceId()).withDatabaseId(databaseName).withFailureMode(REPORT_FAILURES));
        PipelineResult result = p.run();
        result.waitUntilFinish();
        Assert.assertThat(result.getState(), Matchers.is(DONE));
        Assert.assertThat(countNumberOfRecords(), Matchers.equalTo(((long) (numRecords))));
    }

    @Test
    public void testFailFast() throws Exception {
        thrown.expect(new SpannerWriteIT.StackTraceContainsString("SpannerException"));
        thrown.expect(new SpannerWriteIT.StackTraceContainsString("Value must not be NULL in table users"));
        int numRecords = 100;
        p.apply(GenerateSequence.from(0).to((2 * numRecords))).apply(ParDo.of(new SpannerWriteIT.GenerateMutations(options.getTable(), new SpannerWriteIT.DivBy2()))).apply(SpannerIO.write().withProjectId(project).withInstanceId(options.getInstanceId()).withDatabaseId(databaseName));
        PipelineResult result = p.run();
        result.waitUntilFinish();
    }

    private static class GenerateMutations extends DoFn<Long, Mutation> {
        private final String table;

        private final int valueSize = 100;

        private final Predicate<Long> injectError;

        public GenerateMutations(String table, Predicate<Long> injectError) {
            this.table = table;
            this.injectError = injectError;
        }

        public GenerateMutations(String table) {
            this(table, Predicates.<Long>alwaysFalse());
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            Mutation.WriteBuilder builder = Mutation.newInsertOrUpdateBuilder(table);
            Long key = c.element();
            builder.set("Key").to(key);
            String value = (injectError.apply(key)) ? null : RandomUtils.randomAlphaNumeric(valueSize);
            builder.set("Value").to(value);
            Mutation mutation = builder.build();
            c.output(mutation);
        }
    }

    private static class DivBy2 implements Serializable , Predicate<Long> {
        @Override
        public boolean apply(@Nullable
        Long input) {
            return (input % 2) == 0;
        }
    }

    static class StackTraceContainsString extends TypeSafeMatcher<Exception> {
        private String str;

        public StackTraceContainsString(String str) {
            this.str = str;
        }

        @Override
        public void describeTo(org.hamcrest.Description description) {
            description.appendText((("stack trace contains string '" + (str)) + "'"));
        }

        @Override
        protected boolean matchesSafely(Exception e) {
            String stacktrace = Throwables.getStackTraceAsString(e);
            return stacktrace.contains(str);
        }
    }
}

