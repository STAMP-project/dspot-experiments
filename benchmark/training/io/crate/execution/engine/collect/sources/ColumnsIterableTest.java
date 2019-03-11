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
package io.crate.execution.engine.collect.sources;


import InformationSchemaIterables.ColumnsIterable;
import com.google.common.collect.ImmutableList;
import io.crate.expression.reference.information.ColumnContext;
import io.crate.testing.T3;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ColumnsIterableTest {
    @Test
    public void testColumnsIteratorCanBeMaterializedToList() throws Exception {
        InformationSchemaIterables.ColumnsIterable columns = new InformationSchemaIterables.ColumnsIterable(T3.T1_INFO);
        ImmutableList<ColumnContext> contexts = ImmutableList.copyOf(((Iterable<ColumnContext>) (columns)));
        Assert.assertThat(contexts.stream().map(( c) -> c.info.column().name()).collect(Collectors.toList()), Matchers.contains("a", "x", "i"));
    }

    @Test
    public void testColumnsIterableCanBeConsumedTwice() throws Exception {
        List<String> names = new ArrayList<>(6);
        InformationSchemaIterables.ColumnsIterable columns = new InformationSchemaIterables.ColumnsIterable(T3.T1_INFO);
        for (ColumnContext column : columns) {
            names.add(column.info.column().name());
        }
        for (ColumnContext column : columns) {
            names.add(column.info.column().name());
        }
        Assert.assertThat(names, Matchers.contains("a", "x", "i", "a", "x", "i"));
    }

    @Test
    public void testOrdinalIsNullOnSubColumns() throws Exception {
        InformationSchemaIterables.ColumnsIterable columns = new InformationSchemaIterables.ColumnsIterable(T3.T4_INFO);
        ImmutableList<ColumnContext> contexts = ImmutableList.copyOf(columns);
        // sub columns must have NULL ordinal value
        Assert.assertThat(contexts.get(1).ordinal, Matchers.is(new Short("2")));
        Assert.assertThat(contexts.get(2).ordinal, Matchers.nullValue());
        // array of object sub columns also
        Assert.assertThat(contexts.get(3).ordinal, Matchers.is(new Short("3")));
        Assert.assertThat(contexts.get(4).ordinal, Matchers.nullValue());
    }
}

