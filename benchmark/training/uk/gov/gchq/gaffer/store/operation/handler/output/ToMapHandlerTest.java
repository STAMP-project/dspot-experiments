/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.store.operation.handler.output;


import TestGroups.ENTITY;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.generator.MapGenerator;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.output.ToMap;
import uk.gov.gchq.gaffer.store.Context;


public class ToMapHandlerTest {
    @Test
    public void shouldConvertElementToMap() throws OperationException {
        // Given
        final Entity entity = new Entity.Builder().group(ENTITY).vertex(1).build();
        final Map<String, Object> originalMap = new HashMap<>(1);
        originalMap.put("group", ENTITY);
        originalMap.put("vertex", 1);
        final MapGenerator generator = new MapGenerator.Builder().group("group").vertex("vertex").source("source").destination("destination").build();
        final Iterable originalResults = new uk.gov.gchq.gaffer.commonutil.iterable.WrappedCloseableIterable(Collections.singleton(entity));
        final ToMapHandler handler = new ToMapHandler();
        final ToMap operation = Mockito.mock(ToMap.class);
        BDDMockito.given(operation.getInput()).willReturn(originalResults);
        BDDMockito.given(operation.getElementGenerator()).willReturn(generator);
        // When
        final Iterable<? extends Map<String, Object>> results = handler.doOperation(operation, new Context(), null);
        // Then
        Assert.assertThat(results, contains(originalMap));
    }

    @Test
    public void shouldHandleNullInput() throws OperationException {
        // Given
        final ToMapHandler handler = new ToMapHandler();
        final ToMap operation = Mockito.mock(ToMap.class);
        BDDMockito.given(operation.getInput()).willReturn(null);
        // When
        final Iterable<? extends Map<String, Object>> results = handler.doOperation(operation, new Context(), null);
        // Then
        Assert.assertThat(results, Is.is(Matchers.nullValue()));
    }
}

