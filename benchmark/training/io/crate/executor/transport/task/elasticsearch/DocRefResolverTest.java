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
package io.crate.executor.transport.task.elasticsearch;


import DocSysColumns.COLUMN_IDENTS;
import DocSysColumns.ID;
import DocSysColumns.RAW;
import DocSysColumns.VERSION;
import RowGranularity.DOC;
import XContentType.JSON;
import com.google.common.collect.Lists;
import io.crate.execution.engine.collect.CollectExpression;
import io.crate.expression.reference.Doc;
import io.crate.expression.reference.DocRefResolver;
import io.crate.metadata.Reference;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.TestingHelpers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.hamcrest.Matchers;
import org.junit.Test;


public class DocRefResolverTest extends CrateUnitTest {
    private static final BytesReference SOURCE = new BytesArray("{\"x\": 1}".getBytes());

    private static final DocRefResolver REF_RESOLVER = new DocRefResolver(Collections.emptyList());

    private static final Doc GET_RESULT = new Doc("t1", "abc", 1L, XContentHelper.convertToMap(DocRefResolverTest.SOURCE, false, JSON).v2(), DocRefResolverTest.SOURCE::utf8ToString);

    @Test
    public void testSystemColumnsCollectExpressions() throws Exception {
        List<Reference> references = Lists.newArrayList(TestingHelpers.refInfo("t1._id", COLUMN_IDENTS.get(ID), DOC), TestingHelpers.refInfo("t1._version", COLUMN_IDENTS.get(VERSION), DOC), TestingHelpers.refInfo("t1._doc", COLUMN_IDENTS.get(DocSysColumns.DOC), DOC), TestingHelpers.refInfo("t1._raw", COLUMN_IDENTS.get(RAW), DOC));
        List<CollectExpression<Doc, ?>> collectExpressions = new ArrayList<>(4);
        for (Reference reference : references) {
            CollectExpression<Doc, ?> collectExpression = DocRefResolverTest.REF_RESOLVER.getImplementation(reference);
            collectExpression.setNextRow(DocRefResolverTest.GET_RESULT);
            collectExpressions.add(collectExpression);
        }
        assertThat(collectExpressions.get(0).value(), Matchers.is("abc"));
        assertThat(collectExpressions.get(1).value(), Matchers.is(1L));
        assertThat(collectExpressions.get(2).value(), Matchers.is(XContentHelper.convertToMap(DocRefResolverTest.SOURCE, false, JSON).v2()));
        assertThat(collectExpressions.get(3).value(), Matchers.is(DocRefResolverTest.SOURCE.utf8ToString()));
    }
}

