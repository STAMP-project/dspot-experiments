/**
 * Copyright 2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package brave.mysql8;


import Sampler.ALWAYS_SAMPLE;
import Span.Kind.CLIENT;
import brave.ScopedSpan;
import brave.Tracing;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import zipkin2.Span;


@RunWith(Parameterized.class)
public class ITTracingQueryInterceptor {
    static final String QUERY = "select 'hello world'";

    static final String ERROR_QUERY = "select unknown_field FROM unknown_table";

    @Parameterized.Parameter
    public boolean exceptionsTraced;

    /**
     * JDBC is synchronous and we aren't using thread pools: everything happens on the main thread
     */
    ArrayList<Span> spans = new ArrayList<>();

    Tracing tracing = tracingBuilder(ALWAYS_SAMPLE).build();

    Connection connection;

    @Test
    public void makesChildOfCurrentSpan() throws Exception {
        ScopedSpan parent = tracing.tracer().startScopedSpan("test");
        try {
            prepareExecuteSelect(ITTracingQueryInterceptor.QUERY);
        } finally {
            parent.finish();
        }
        assertThat(spans).hasSize(2);
    }

    @Test
    public void reportsClientKindToZipkin() throws Exception {
        prepareExecuteSelect(ITTracingQueryInterceptor.QUERY);
        assertThat(spans).extracting(Span::kind).containsExactly(CLIENT);
    }

    @Test
    public void defaultSpanNameIsOperationName() throws Exception {
        prepareExecuteSelect(ITTracingQueryInterceptor.QUERY);
        assertThat(spans).extracting(Span::name).containsExactly("select");
    }

    /**
     * This intercepts all SQL, not just queries. This ensures single-word statements work
     */
    @Test
    public void defaultSpanNameIsOperationName_oneWord() throws Exception {
        connection.setAutoCommit(false);
        connection.commit();
        assertThat(spans).extracting(Span::name).contains("commit");
    }

    @Test
    public void addsQueryTag() throws Exception {
        prepareExecuteSelect(ITTracingQueryInterceptor.QUERY);
        assertThat(spans).flatExtracting(( s) -> s.tags().entrySet()).containsExactly(entry("sql.query", ITTracingQueryInterceptor.QUERY));
    }

    @Test
    public void reportsServerAddress() throws Exception {
        prepareExecuteSelect(ITTracingQueryInterceptor.QUERY);
        assertThat(spans).extracting(Span::remoteServiceName).contains("myservice");
    }

    @Test
    public void sqlError() throws Exception {
        assertThatThrownBy(() -> prepareExecuteSelect(ERROR_QUERY)).isInstanceOf(SQLException.class);
        assertThat(spans).isNotEmpty();
        if (exceptionsTraced) {
            assertThat(spans).anySatisfy(( span) -> assertThat(span.tags()).containsEntry("error", "1046"));
        }
    }
}

