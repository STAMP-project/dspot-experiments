/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.index.query;


import java.util.Collection;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.ObjectType;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.index.select.QueryCallback;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class QueryableIndexTest {
    private QueryableIndex queryableIndex;

    @Mock
    private QueryCallback<Collection<Rule>> rulesQueryCallback;

    @Mock
    private QueryCallback<Column> firstColumnQueryCallback;

    @Mock
    private QueryCallback<ObjectType> objectTypeQueryCallback;

    @Captor
    private ArgumentCaptor<Collection<Rule>> rulesArgumentCaptor;

    @Captor
    private ArgumentCaptor<Column> firstColumnArgumentCaptor;

    @Captor
    private ArgumentCaptor<ObjectType> objectTypeArgumentCaptor;

    private AnalyzerConfiguration configuration;

    private Column firstColumn;

    @Test
    public void queryAllRules() throws Exception {
        queryableIndex.getRules().where(Rule.index().any()).select().all(rulesQueryCallback);
        Mockito.verify(rulesQueryCallback).callback(rulesArgumentCaptor.capture());
        Assert.assertEquals(3, rulesArgumentCaptor.getValue().size());
    }

    @Test
    public void queryFirstColumn() throws Exception {
        queryableIndex.getColumns().where(Column.index().any()).select().first(firstColumnQueryCallback);
        Mockito.verify(firstColumnQueryCallback).callback(firstColumnArgumentCaptor.capture());
        Assert.assertEquals(firstColumn, firstColumnArgumentCaptor.getValue());
    }

    @Test
    public void makeSureFirstAndLastObjectTypesAreTheSame() throws Exception {
        queryableIndex.getObjectTypes().where(ObjectType.type().is("Person")).select().first(objectTypeQueryCallback);
        Mockito.verify(objectTypeQueryCallback).callback(objectTypeArgumentCaptor.capture());
        final ObjectType first = objectTypeArgumentCaptor.getValue();
        Mockito.reset(objectTypeQueryCallback);
        queryableIndex.getObjectTypes().where(ObjectType.type().is("Person")).select().last(objectTypeQueryCallback);
        Mockito.verify(objectTypeQueryCallback).callback(objectTypeArgumentCaptor.capture());
        final ObjectType last = objectTypeArgumentCaptor.getValue();
        Assert.assertEquals("Person", first.getType());
        Assert.assertEquals(first, last);
    }
}

