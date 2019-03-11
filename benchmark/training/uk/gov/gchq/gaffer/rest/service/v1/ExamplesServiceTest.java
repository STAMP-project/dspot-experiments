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
package uk.gov.gchq.gaffer.rest.service.v1;


import View.Builder;
import com.google.common.collect.Sets;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.rest.factory.GraphFactory;
import uk.gov.gchq.gaffer.rest.factory.UserFactory;
import uk.gov.gchq.gaffer.rest.service.v1.example.ExamplesService;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.ViewValidator;


@RunWith(MockitoJUnitRunner.class)
public class ExamplesServiceTest {
    @InjectMocks
    private ExamplesService service;

    @Mock
    private GraphFactory graphFactory;

    @Mock
    private UserFactory userFactory;

    private Schema schema;

    @Test
    public void shouldSerialiseAndDeserialiseAddElements() throws IOException {
        shouldSerialiseAndDeserialiseOperation(service.addElements());
    }

    @Test
    public void shouldSerialiseAndDeserialiseGetElementsBySeed() throws IOException {
        shouldSerialiseAndDeserialiseOperation(service.getElementsBySeed());
    }

    @Test
    public void shouldSerialiseAndDeserialiseGetRelatedElements() throws IOException {
        shouldSerialiseAndDeserialiseOperation(service.getRelatedElements());
    }

    @Test
    public void shouldSerialiseAndDeserialiseGetAllElements() throws IOException {
        shouldSerialiseAndDeserialiseOperation(service.getAllElements());
    }

    @Test
    public void shouldSerialiseAndDeserialiseGenerateObjects() throws IOException {
        shouldSerialiseAndDeserialiseOperation(service.generateObjects());
    }

    @Test
    public void shouldSerialiseAndDeserialiseGenerateElements() throws IOException {
        shouldSerialiseAndDeserialiseOperation(service.generateElements());
    }

    @Test
    public void shouldSerialiseAndDeserialiseOperationChain() throws IOException {
        // Given
        final OperationChain opChain = service.execute();
        // When
        byte[] bytes = JSONSerialiser.serialise(opChain);
        final OperationChain deserialisedOp = JSONSerialiser.deserialise(bytes, opChain.getClass());
        // Then
        Assert.assertNotNull(deserialisedOp);
    }

    @Test
    public void shouldCreateViewForEdges() {
        final View.Builder builder = service.generateViewBuilder();
        final View view = builder.build();
        Assert.assertNotNull(view);
        final ViewValidator viewValidator = new ViewValidator();
        Assert.assertTrue(viewValidator.validate(view, schema, Sets.newHashSet(StoreTrait.values())).isValid());
    }
}

