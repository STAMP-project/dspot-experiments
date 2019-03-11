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
package uk.gov.gchq.gaffer.store.operation.handler;


import TestGroups.EDGE;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.graph.Walk;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.impl.GetWalks;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;


public class GetWalksHandlerTest {
    @Test
    public void shouldHandleNullInput() throws Exception {
        // Given
        final GetElements getElements = new GetElements.Builder().view(new View.Builder().edge(EDGE).build()).build();
        final GetWalks operation = new GetWalks.Builder().operations(getElements).build();
        final GetWalksHandler handler = new GetWalksHandler();
        // When
        final Iterable<Walk> result = handler.doOperation(operation, null, null);
        // Then
        MatcherAssert.assertThat(result, Is.is(Matchers.nullValue()));
    }

    @Test
    public void shouldSerialiseDeserialise() throws JsonProcessingException, SerialisationException {
        // Given
        final GetWalksHandler obj = new GetWalksHandler();
        obj.setPrune(true);
        // When
        final byte[] json = JSONSerialiser.serialise(obj, true);
        final GetWalksHandler deserialisedObj = JSONSerialiser.deserialise(json, GetWalksHandler.class);
        // Then
        Assert.assertNotNull(deserialisedObj);
    }
}

