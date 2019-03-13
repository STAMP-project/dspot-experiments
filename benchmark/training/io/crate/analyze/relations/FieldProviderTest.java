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
package io.crate.analyze.relations;


import Operation.INSERT;
import Operation.READ;
import com.google.common.collect.ImmutableMap;
import io.crate.exceptions.AmbiguousColumnException;
import io.crate.exceptions.ColumnUnknownException;
import io.crate.exceptions.RelationUnknown;
import io.crate.expression.symbol.Field;
import io.crate.sql.tree.QualifiedName;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.DummyRelation;
import java.util.Arrays;
import java.util.Map;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class FieldProviderTest extends CrateUnitTest {
    private AnalyzedRelation dummyRelation = new DummyRelation("name");

    private Map<QualifiedName, AnalyzedRelation> dummySources = ImmutableMap.of(FieldProviderTest.newQN("dummy.t"), dummyRelation);

    @Test
    public void testInvalidSources() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        AnalyzedRelation relation = new DummyRelation("name");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.of(FieldProviderTest.newQN("too.many.parts"), relation));
        resolver.resolveField(FieldProviderTest.newQN("name"), null, READ);
    }

    @Test
    public void testUnknownSchema() throws Exception {
        expectedException.expect(RelationUnknown.class);
        expectedException.expectMessage("Relation 'invalid.table' unknown");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(dummySources);
        resolver.resolveField(FieldProviderTest.newQN("invalid.table.name"), null, READ);
    }

    @Test
    public void testUnknownTable() throws Exception {
        expectedException.expect(RelationUnknown.class);
        expectedException.expectMessage("Relation 'dummy.invalid' unknown");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(dummySources);
        resolver.resolveField(FieldProviderTest.newQN("dummy.invalid.name"), null, READ);
    }

    @Test
    public void testSysColumnWithoutSourceRelation() throws Exception {
        expectedException.expect(RelationUnknown.class);
        expectedException.expectMessage("Relation 'sys.nodes' unknown");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(dummySources);
        resolver.resolveField(FieldProviderTest.newQN("sys.nodes.name"), null, READ);
    }

    @Test
    public void testRegularColumnUnknown() throws Exception {
        expectedException.expect(ColumnUnknownException.class);
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(dummySources);
        resolver.resolveField(FieldProviderTest.newQN("age"), null, READ);
    }

    @Test
    public void testResolveDynamicReference() throws Exception {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column age unknown");
        AnalyzedRelation barT = new DummyRelation("name");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.of(FieldProviderTest.newQN("bar.t"), barT));
        resolver.resolveField(FieldProviderTest.newQN("t.age"), null, READ);
    }

    @Test
    public void testMultipleSourcesWithDynamicReferenceAndReference() throws Exception {
        AnalyzedRelation barT = new DummyRelation("name");
        AnalyzedRelation fooT = new DummyRelation("name");
        AnalyzedRelation fooA = new DummyRelation("name");
        AnalyzedRelation customT = new DummyRelation("tags");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.of(FieldProviderTest.newQN("bar.t"), barT, FieldProviderTest.newQN("foo.t"), fooT, FieldProviderTest.newQN("foo.a"), fooA, FieldProviderTest.newQN("custom.t"), customT));
        Field field = resolver.resolveField(FieldProviderTest.newQN("foo.t.name"), null, READ);
        assertThat(field.relation(), Matchers.equalTo(fooT));
        // reference > dynamicReference - not ambiguous
        Field tags = resolver.resolveField(FieldProviderTest.newQN("tags"), null, READ);
        assertThat(tags.relation(), Matchers.equalTo(customT));
        field = resolver.resolveField(FieldProviderTest.newQN("a.name"), null, READ);
        assertThat(field.relation(), Matchers.equalTo(fooA));
    }

    @Test
    public void testRelationOutputFromAlias() throws Exception {
        // t.name from doc.foo t
        AnalyzedRelation relation = new DummyRelation("name");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.of(new QualifiedName(Arrays.asList("t")), relation));
        Field field = resolver.resolveField(FieldProviderTest.newQN("t.name"), null, READ);
        assertThat(field.relation(), Matchers.equalTo(relation));
        assertThat(field.path().outputName(), Is.is("name"));
    }

    @Test
    public void testRelationOutputFromSingleColumnName() throws Exception {
        // select name from t
        AnalyzedRelation relation = new DummyRelation("name");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.of(FieldProviderTest.newQN("doc.t"), relation));
        Field field = resolver.resolveField(FieldProviderTest.newQN("name"), null, READ);
        assertThat(field.relation(), Matchers.equalTo(relation));
        assertThat(field.path().outputName(), Is.is("name"));
    }

    @Test
    public void testRelationOutputFromSchemaTableColumnName() throws Exception {
        // doc.t.name from t.name
        AnalyzedRelation relation = new DummyRelation("name");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.of(FieldProviderTest.newQN("doc.t"), relation));
        Field field = resolver.resolveField(FieldProviderTest.newQN("doc.t.name"), null, INSERT);
        assertThat(field.relation(), Matchers.equalTo(relation));
        assertThat(field.path().outputName(), Is.is("name"));
    }

    @Test
    public void testTooManyParts() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(dummySources);
        resolver.resolveField(new QualifiedName(Arrays.asList("a", "b", "c", "d")), null, READ);
    }

    @Test
    public void testTooManyPartsNameFieldResolver() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Column reference \"a.b\" has too many parts. A column must not have a schema or a table here.");
        FieldProvider<Field> resolver = new NameFieldProvider(dummyRelation);
        resolver.resolveField(new QualifiedName(Arrays.asList("a", "b")), null, READ);
    }

    @Test
    public void testRelationFromTwoTablesWithSameNameDifferentSchemaIsAmbiguous() throws Exception {
        // select t.name from custom.t.name, doc.t.name
        expectedException.expect(AmbiguousColumnException.class);
        expectedException.expectMessage("Column \"name\" is ambiguous");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.<QualifiedName, AnalyzedRelation>of(new QualifiedName(Arrays.asList("custom", "t")), new DummyRelation("name"), new QualifiedName(Arrays.asList("doc", "t")), new DummyRelation("name")));
        resolver.resolveField(new QualifiedName(Arrays.asList("t", "name")), null, READ);
    }

    @Test
    public void testRelationFromTwoTables() throws Exception {
        // select name from doc.t, custom.t
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.<QualifiedName, AnalyzedRelation>of(new QualifiedName(Arrays.asList("custom", "t")), new DummyRelation("address"), new QualifiedName(Arrays.asList("doc", "t")), new DummyRelation("name")));
        resolver.resolveField(new QualifiedName(Arrays.asList("t", "name")), null, READ);
    }

    @Test
    public void testSimpleFieldResolver() throws Exception {
        // select name from doc.t
        AnalyzedRelation relation = new DummyRelation("name");
        FieldProvider<Field> resolver = new NameFieldProvider(relation);
        Field field = resolver.resolveField(new QualifiedName(Arrays.asList("name")), null, READ);
        assertThat(field.relation(), Matchers.equalTo(relation));
    }

    @Test
    public void testSimpleResolverUnknownColumn() throws Exception {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column unknown unknown");
        AnalyzedRelation relation = new DummyRelation("name");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.of(FieldProviderTest.newQN("doc.t"), relation));
        resolver.resolveField(new QualifiedName(Arrays.asList("unknown")), null, READ);
    }

    @Test
    public void testColumnSchemaResolver() throws Exception {
        AnalyzedRelation barT = new DummyRelation("\"Name\"");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.of(FieldProviderTest.newQN("\"Foo\".\"Bar\""), barT));
        Field field = resolver.resolveField(FieldProviderTest.newQN("\"Foo\".\"Bar\".\"Name\""), null, READ);
        assertThat(field.relation(), Matchers.equalTo(barT));
    }

    @Test
    public void testColumnSchemaResolverFail() throws Exception {
        expectedException.expect(ColumnUnknownException.class);
        expectedException.expectMessage("Column name unknown");
        AnalyzedRelation barT = new DummyRelation("\"Name\"");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.of(FieldProviderTest.newQN("bar"), barT));
        resolver.resolveField(FieldProviderTest.newQN("bar.name"), null, READ);
    }

    @Test
    public void testAliasRelationNameResolver() throws Exception {
        AnalyzedRelation barT = new DummyRelation("name");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.of(FieldProviderTest.newQN("\"Bar\""), barT));
        Field field = resolver.resolveField(FieldProviderTest.newQN("\"Bar\".name"), null, READ);
        assertThat(field.relation(), Matchers.equalTo(barT));
    }

    @Test
    public void testAliasRelationNameResolverFail() throws Exception {
        expectedException.expect(RelationUnknown.class);
        expectedException.expectMessage("Relation \'doc.\"Bar\"\' unknown");
        AnalyzedRelation barT = new DummyRelation("name");
        FieldProvider<Field> resolver = FieldProviderTest.newFQFieldProvider(ImmutableMap.of(FieldProviderTest.newQN("bar"), barT));
        resolver.resolveField(FieldProviderTest.newQN("\"Bar\".name"), null, READ);
    }
}

