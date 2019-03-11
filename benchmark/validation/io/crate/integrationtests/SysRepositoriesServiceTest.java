/**
 * Licensed to CRATE.IO GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.integrationtests;


import ESIntegTestCase.ClusterScope;
import io.crate.testing.UseJdbc;
import io.crate.types.DataType;
import io.crate.types.ObjectType;
import io.crate.types.StringType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


// missing column types
@ClusterScope
@UseJdbc(0)
public class SysRepositoriesServiceTest extends SQLTransportIntegrationTest {
    @ClassRule
    public static TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    private List<String> repositories = new ArrayList<>();

    @Test
    public void testQueryAllColumns() throws Exception {
        execute("select * from sys.repositories");
        assertThat(response.rowCount(), Matchers.is(1L));
        assertThat(response.cols().length, Matchers.is(3));
        assertThat(response.cols(), Matchers.is(new String[]{ "name", "settings", "type" }));
        assertThat(response.columnTypes(), Matchers.is(new DataType[]{ StringType.INSTANCE, ObjectType.untyped(), StringType.INSTANCE }));
        assertThat(((String) (response.rows()[0][0])), Matchers.is("test-repo"));
        Map<String, Object> settings = ((Map<String, Object>) (response.rows()[0][1]));
        assertThat(settings.size(), Matchers.is(3));
        assertThat(((String) (settings.get("location"))), Matchers.is(new File(SysRepositoriesServiceTest.TEMP_FOLDER.getRoot(), "backup").getAbsolutePath()));
        assertThat(((String) (settings.get("chunk_size"))), Matchers.is("5k"));
        assertThat(((String) (settings.get("compress"))), Matchers.is("false"));
        assertThat(((String) (response.rows()[0][2])), Matchers.is("fs"));
    }
}

