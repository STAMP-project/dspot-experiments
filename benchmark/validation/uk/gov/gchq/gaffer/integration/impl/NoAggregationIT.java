/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.integration.impl;


import TestGroups.EDGE;
import TestGroups.ENTITY;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.util.ElementUtil;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.ElementSeed;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;


public class NoAggregationIT extends AbstractStoreIT {
    @Test
    public void shouldReturnDuplicateEntitiesWhenNoAggregationIsUsed() throws OperationException {
        // Given
        final ArrayList<Entity> expected = Lists.newArrayList(getEntity(), getEntity());
        // When
        final CloseableIterable<? extends Element> result = AbstractStoreIT.graph.execute(new GetElements.Builder().input(ElementSeed.createSeed(getEntity())).view(new View.Builder().entity(ENTITY).build()).build(), getUser());
        // Then
        ElementUtil.assertElementEquals(expected, result);
    }

    @Test
    public void shouldReturnDuplicateEdgesWhenNoAggregationIsUsed() throws OperationException {
        // Given
        final ArrayList<Edge> expected = Lists.newArrayList(getEdge(), getEdge());
        // When
        final CloseableIterable<? extends Element> result = AbstractStoreIT.graph.execute(new GetElements.Builder().input(ElementSeed.createSeed(getEdge())).view(new View.Builder().edge(EDGE).build()).build(), getUser());
        // Then
        ElementUtil.assertElementEquals(expected, result);
    }
}

