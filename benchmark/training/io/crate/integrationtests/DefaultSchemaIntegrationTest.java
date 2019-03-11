/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
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


import java.io.File;
import java.nio.file.Paths;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DefaultSchemaIntegrationTest extends SQLTransportIntegrationTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testSelectFromFooSchemaWithRequestHeaders() throws Exception {
        // this test uses all kind of different statements that involve a table to make sure the schema is applied in each case.
        execute("create table foobar (x string) with (number_of_replicas = 0)", "foo");
        ensureYellow();
        waitNoPendingTasksOnAll();
        execute("alter table foobar set (number_of_replicas = '0-1')", "foo");
        assertThat(getTableCount("foo", "foobar"), Is.is(1L));
        assertThat(getTableCount("doc", "foobar"), Is.is(0L));
        execute("insert into foobar (x) values ('a'), ('b')", "foo");
        execute("refresh table foobar", "foo");
        execute("update foobar set x = 'c'", "foo");
        assertThat(response.rowCount(), Is.is(2L));
        execute("select * from foobar", "foo");
        assertThat(response.rowCount(), Is.is(2L));
        File foobarExport = tmpFolder.newFolder("foobar_export");
        String uriTemplate = Paths.get(foobarExport.toURI()).toUri().toString();
        execute("copy foobar to directory ?", new Object[]{ uriTemplate }, "foo");
        refresh();
        execute("delete from foobar", "foo");
        refresh();
        execute("select * from foobar", "foo");
        assertThat(response.rowCount(), Is.is(0L));
        execute("copy foobar from ? with (shared=True)", new Object[]{ uriTemplate + "*" }, "foo");
        execute("refresh table foobar", "foo");
        execute("select * from foobar", "foo");
        assertThat(response.rowCount(), Is.is(2L));
        execute("insert into foobar (x) (select x from foobar)", "foo");
        execute("refresh table foobar", "foo");
        execute("select * from foobar", "foo");
        assertThat(response.rowCount(), Is.is(4L));
        execute("drop table foobar", "foo");
        assertThat(getTableCount("foo", "foobar"), Is.is(0L));
    }
}

