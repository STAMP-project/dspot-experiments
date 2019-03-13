/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pentaho.repository.importexport;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.platform.api.repository2.unified.Converter;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
@RunWith(Parameterized.class)
public class StreamToNodeConvertersPrivateDatabasesTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static final String FILE_ID = "fileId";

    private final Converter converter;

    private final String metaTag;

    private final AbstractMeta meta;

    public StreamToNodeConvertersPrivateDatabasesTest(Converter converter, String metaTag, AbstractMeta meta) {
        this.converter = converter;
        this.metaTag = metaTag;
        this.meta = meta;
    }

    @Test
    public void removesSharedDatabases() throws Exception {
        List<DatabaseMeta> dbs = new ArrayList<DatabaseMeta>(Arrays.asList(createDb("meta1"), createDb("private"), createDb("meta2")));
        meta.setDatabases(dbs);
        meta.setPrivateDatabases(Collections.singleton("private"));
        InputStream stream = converter.convert(StreamToNodeConvertersPrivateDatabasesTest.FILE_ID);
        assertDatabaseNodes(stream, "private");
    }

    @Test
    public void removesAll_IfPrivateSetIsEmpty() throws Exception {
        List<DatabaseMeta> dbs = new ArrayList<DatabaseMeta>(Arrays.asList(createDb("meta1"), createDb("meta2")));
        meta.setDatabases(dbs);
        meta.setPrivateDatabases(Collections.<String>emptySet());
        InputStream stream = converter.convert(StreamToNodeConvertersPrivateDatabasesTest.FILE_ID);
        assertDatabaseNodes(stream);
    }

    @Test
    public void keepsAll_IfPrivateSetIsNull() throws Exception {
        List<DatabaseMeta> dbs = new ArrayList<DatabaseMeta>(Arrays.asList(createDb("meta1"), createDb("meta2")));
        meta.setDatabases(dbs);
        meta.setPrivateDatabases(null);
        InputStream stream = converter.convert(StreamToNodeConvertersPrivateDatabasesTest.FILE_ID);
        assertDatabaseNodes(stream, "meta1", "meta2");
    }
}

