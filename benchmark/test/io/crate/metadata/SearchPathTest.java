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
package io.crate.metadata;


import PgCatalogSchemaInfo.NAME;
import Schemas.DOC_SCHEMA_NAME;
import java.util.Iterator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SearchPathTest {
    @Test
    public void testEmptyConstructorSetsDefaultSchema() {
        Iterable<String> searchPath = SearchPath.pathWithPGCatalogAndDoc();
        Iterator<String> pathIterator = searchPath.iterator();
        pathIterator.next();
        String secondInPath = pathIterator.next();
        Assert.assertThat(secondInPath, Matchers.is(DOC_SCHEMA_NAME));
    }

    @Test
    public void testCurrentSchemaIsFirstSchemaInSearchPath() {
        SearchPath searchPath = SearchPath.createSearchPathFrom("firstSchema", "secondSchema");
        Assert.assertThat(searchPath.currentSchema(), Matchers.is("firstSchema"));
    }

    @Test
    public void testPgCatalogIsFirstInTheSearchPathIfNotExplicitlySet() {
        SearchPath searchPath = SearchPath.createSearchPathFrom("firstSchema", "secondSchema");
        Assert.assertThat(searchPath.iterator().next(), Matchers.is(NAME));
    }

    @Test
    public void testPgCatalogKeepsPositionInSearchPathWhenExplicitlySet() {
        SearchPath searchPath = SearchPath.createSearchPathFrom("firstSchema", "secondSchema", NAME);
        Iterator<String> pathIterator = searchPath.iterator();
        pathIterator.next();
        pathIterator.next();
        String thirdInPath = pathIterator.next();
        Assert.assertThat(thirdInPath, Matchers.is(NAME));
    }

    @Test
    public void testPgCatalogISCurrentSchemaIfSetFirstInPath() {
        SearchPath searchPath = SearchPath.createSearchPathFrom(NAME, "secondSchema");
        Assert.assertThat(searchPath.currentSchema(), Matchers.is(NAME));
    }
}

