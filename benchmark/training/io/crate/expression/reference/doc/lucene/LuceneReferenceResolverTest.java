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
package io.crate.expression.reference.doc.lucene;


import DocCollectorExpression.ChildDocCollectorExpression;
import io.crate.metadata.Reference;
import io.crate.metadata.RelationName;
import io.crate.metadata.RowGranularity;
import io.crate.test.integration.CrateUnitTest;
import io.crate.types.DataTypes;
import org.hamcrest.Matchers;
import org.junit.Test;


public class LuceneReferenceResolverTest extends CrateUnitTest {
    // just return any fieldType to get passt the null check
    private LuceneReferenceResolver luceneReferenceResolver = new LuceneReferenceResolver(( i) -> KeywordFieldMapper.Defaults.FIELD_TYPE, null);

    @Test
    public void testGetImplementationWithColumnsOfTypeCollection() {
        Reference arrayRef = new Reference(new io.crate.metadata.ReferenceIdent(new RelationName("s", "t"), "a"), RowGranularity.DOC, DataTypes.DOUBLE_ARRAY);
        assertThat(luceneReferenceResolver.getImplementation(arrayRef), Matchers.instanceOf(ChildDocCollectorExpression.class));
        Reference setRef = new Reference(new io.crate.metadata.ReferenceIdent(new RelationName("s", "t"), "a"), RowGranularity.DOC, new io.crate.types.SetType(DataTypes.DOUBLE));
        assertThat(luceneReferenceResolver.getImplementation(setRef), Matchers.instanceOf(ChildDocCollectorExpression.class));
    }
}

