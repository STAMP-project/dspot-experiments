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
package org.kairosdb.core;


import com.google.inject.Injector;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.json.JSONException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.kairosdb.core.datastore.DataPointGroup;
import org.kairosdb.core.datastore.DatastoreQuery;
import org.kairosdb.core.datastore.KairosDatastore;
import org.kairosdb.core.datastore.QueryMetric;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.util.ValidationException;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExportTest {
    public static final String METRIC_NAME = "kairos.import_export_unit_test";

    private static Main s_main;

    private static Injector s_injector;

    public static final long LOAD = 1000L;

    @Test
    public void test1_testExport() throws IOException, InterruptedException, JSONException, DatastoreException, ValidationException {
        verifyDataPoints();
        Writer ps = new OutputStreamWriter(new FileOutputStream("build/export.json"), "UTF-8");
        ExportTest.s_main.runExport(ps, Collections.singletonList(ExportTest.METRIC_NAME));
        ps.flush();
        ps.close();
    }

    @Test
    public void test2_testImport() throws IOException, InterruptedException, JSONException, DatastoreException, ValidationException {
        ExportTest.deleteData();
        KairosDatastore ds = ExportTest.s_injector.getInstance(KairosDatastore.class);
        // Assert data is not there.
        QueryMetric queryMetric = new QueryMetric(0, 0, ExportTest.METRIC_NAME);
        DatastoreQuery query = ds.createQuery(queryMetric);
        List<DataPointGroup> results = query.execute();
        MatcherAssert.assertThat(results.size(), IsEqual.equalTo(1));
        MatcherAssert.assertThat(results.get(0).hasNext(), IsEqual.equalTo(false));
        query.close();
        InputStream export = new FileInputStream("build/export.json");
        ExportTest.s_main.runImport(export);
        export.close();
        Thread.sleep(2000);
        verifyDataPoints();
    }
}

