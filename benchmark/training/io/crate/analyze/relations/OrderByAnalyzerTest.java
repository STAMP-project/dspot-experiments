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
package io.crate.analyze.relations;


import SortItem.NullOrdering;
import SortItem.Ordering;
import io.crate.analyze.OrderBy;
import io.crate.expression.symbol.Literal;
import io.crate.expression.symbol.Symbol;
import io.crate.sql.tree.QualifiedName;
import io.crate.sql.tree.QualifiedNameReference;
import io.crate.sql.tree.SortItem;
import io.crate.testing.SymbolMatchers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


public class OrderByAnalyzerTest {
    @Test
    public void analyzeEmptySortItemsReturnsNull() {
        Assert.assertThat(OrderyByAnalyzer.analyzeSortItems(Collections.emptyList(), null), Is.is(IsNull.nullValue()));
    }

    @Test
    public void analyzeSortItems() {
        List<SortItem> sortItems = new ArrayList<>(2);
        QualifiedName tx = QualifiedName.of("t", "x");
        SortItem firstSort = new SortItem(new QualifiedNameReference(tx), Ordering.ASCENDING, NullOrdering.FIRST);
        sortItems.add(firstSort);
        QualifiedName ty = QualifiedName.of("t", "y");
        SortItem second = new SortItem(new QualifiedNameReference(ty), Ordering.DESCENDING, NullOrdering.LAST);
        sortItems.add(second);
        OrderBy orderBy = OrderyByAnalyzer.analyzeSortItems(sortItems, ( e) -> Literal.of(((QualifiedNameReference) (e)).getName().toString()));
        Assert.assertThat(orderBy, Is.is(IsNull.notNullValue()));
        List<Symbol> orderBySymbols = orderBy.orderBySymbols();
        Assert.assertThat(orderBySymbols.size(), Is.is(2));
        Assert.assertThat(orderBySymbols.get(0), SymbolMatchers.isLiteral("t.x"));
        Assert.assertThat(orderBySymbols.get(1), SymbolMatchers.isLiteral("t.y"));
        boolean[] reverseFlags = orderBy.reverseFlags();
        Assert.assertThat(reverseFlags.length, Is.is(2));
        Assert.assertThat(reverseFlags[0], Is.is(false));
        Assert.assertThat(reverseFlags[1], Is.is(true));
        Boolean[] nullsFirst = orderBy.nullsFirst();
        Assert.assertThat(nullsFirst.length, Is.is(2));
        Assert.assertThat(nullsFirst[0], Is.is(true));
        Assert.assertThat(nullsFirst[1], Is.is(false));
    }
}

