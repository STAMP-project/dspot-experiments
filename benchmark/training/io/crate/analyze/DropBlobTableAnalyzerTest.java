/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.analyze;


import io.crate.exceptions.RelationUnknown;
import io.crate.metadata.RelationName;
import io.crate.metadata.Schemas;
import io.crate.metadata.blob.BlobSchemaInfo;
import io.crate.sql.tree.QualifiedName;
import io.crate.sql.tree.Table;
import io.crate.test.integration.CrateUnitTest;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DropBlobTableAnalyzerTest extends CrateUnitTest {
    private static final String IRRELEVANT = "Irrelevant";

    private RelationName relationName = new RelationName(BlobSchemaInfo.NAME, DropBlobTableAnalyzerTest.IRRELEVANT);

    private Table table = new Table(new QualifiedName(DropBlobTableAnalyzerTest.IRRELEVANT));

    @Mock
    public Schemas schemas;

    private DropBlobTableAnalyzer analyzer;

    @Test
    public void testDeletingNoExistingTableSetsNoopIfIgnoreNonExistentTablesIsSet() throws Exception {
        Mockito.when(schemas.getTableInfo(relationName)).thenThrow(new RelationUnknown(relationName));
        DropBlobTableAnalyzedStatement statement = analyzer.analyze(new io.crate.sql.tree.DropBlobTable(table, true));
        assertThat(statement.noop(), Is.is(true));
    }

    @Test
    public void testDeletingNonExistingTableRaisesException() throws Exception {
        Mockito.when(schemas.getTableInfo(relationName)).thenThrow(new RelationUnknown(relationName));
        expectedException.expect(RelationUnknown.class);
        expectedException.expectMessage("Relation 'blob.Irrelevant' unknown");
        analyzer.analyze(new io.crate.sql.tree.DropBlobTable(table, false));
    }
}

