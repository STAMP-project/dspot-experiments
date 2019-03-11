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
package uk.gov.gchq.gaffer.sparkaccumulo.generator;


import TestGroups.EDGE;
import TestGroups.ENTITY;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.util.ElementUtil;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.Map;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateElements;
import uk.gov.gchq.gaffer.spark.data.generator.RowToElementGenerator;
import uk.gov.gchq.gaffer.spark.function.GraphFrameToIterableRow;
import uk.gov.gchq.gaffer.spark.operation.graphframe.GetGraphFrameOfElements;
import uk.gov.gchq.gaffer.user.User;


public class RowToElementGeneratorTest {
    @Test
    public void checkGetCorrectElementsInGraphFrame() throws OperationException {
        // Given
        final Graph graph = getGraph("/schema-GraphFrame/elements.json", getElements());
        final GetGraphFrameOfElements gfOperation = new GetGraphFrameOfElements.Builder().view(new View.Builder().edge(EDGE).entity(ENTITY).build()).build();
        final OperationChain<Iterable<? extends Element>> opChain = new OperationChain.Builder().first(gfOperation).then(new Map.Builder<>().first(new GraphFrameToIterableRow()).build()).then(new GenerateElements.Builder<org.apache.spark.sql.Row>().generator(new RowToElementGenerator()).build()).build();
        // When
        final Iterable<? extends Element> result = graph.execute(opChain, new User());
        // Then
        ElementUtil.assertElementEquals(getElements(), result);
    }
}

