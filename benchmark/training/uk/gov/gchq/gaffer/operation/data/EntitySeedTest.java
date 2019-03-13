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
package uk.gov.gchq.gaffer.operation.data;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.JSONSerialisationTest;
import uk.gov.gchq.gaffer.data.element.id.EdgeId;
import uk.gov.gchq.gaffer.data.element.id.ElementId;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;


public class EntitySeedTest extends JSONSerialisationTest<EntitySeed> {
    @Test
    public void shouldBeRelatedToEdgeIdWhenSourceEqualsVertex() {
        // Given
        final String source = "source";
        final String destination = "destination";
        final EntityId seed = new EntitySeed(source);
        final EdgeId relatedSeed = Mockito.mock(EdgeId.class);
        BDDMockito.given(relatedSeed.getSource()).willReturn(source);
        BDDMockito.given(relatedSeed.getDestination()).willReturn(destination);
        // When
        final boolean isRelated = seed.isRelated(((ElementId) (relatedSeed))).isMatch();
        // Then
        Assert.assertTrue(isRelated);
    }

    @Test
    public void shouldBeRelatedToEdgeIdWhenDestinationEqualsVertex() {
        // Given
        final String source = "source";
        final String destination = "destination";
        final EntityId seed = new EntitySeed(destination);
        final EdgeId relatedSeed = Mockito.mock(EdgeId.class);
        BDDMockito.given(relatedSeed.getSource()).willReturn(source);
        BDDMockito.given(relatedSeed.getDestination()).willReturn(destination);
        // When
        final boolean isRelated = seed.isRelated(((ElementId) (relatedSeed))).isMatch();
        // Then
        Assert.assertTrue(isRelated);
    }

    @Test
    public void shouldBeRelatedToEdgeIdWhenSourceAndVertexAreNull() {
        // Given
        final String source = null;
        final String destination = "destination";
        final EntityId seed = new EntitySeed(destination);
        final EdgeId relatedSeed = Mockito.mock(EdgeId.class);
        BDDMockito.given(relatedSeed.getSource()).willReturn(source);
        BDDMockito.given(relatedSeed.getDestination()).willReturn(destination);
        // When
        final boolean isRelated = seed.isRelated(((ElementId) (relatedSeed))).isMatch();
        // Then
        Assert.assertTrue(isRelated);
    }

    @Test
    public void shouldBeRelatedToEdgeIdWhenDestinationAndVertexAreNull() {
        // Given
        final String source = "source";
        final String destination = null;
        final EntityId seed = new EntitySeed(destination);
        final EdgeId relatedSeed = Mockito.mock(EdgeId.class);
        BDDMockito.given(relatedSeed.getSource()).willReturn(source);
        BDDMockito.given(relatedSeed.getDestination()).willReturn(destination);
        // When
        final boolean isRelated = seed.isRelated(((ElementId) (relatedSeed))).isMatch();
        // Then
        Assert.assertTrue(isRelated);
    }

    @Test
    public void shouldNotBeRelatedToEdgeIdWhenVertexNotEqualToSourceOrDestination() {
        // Given
        final String source = "source";
        final String destination = "destination";
        final boolean directed = true;
        final EntityId seed = new EntitySeed("other vertex");
        final EdgeId relatedSeed = Mockito.mock(EdgeId.class);
        BDDMockito.given(relatedSeed.getSource()).willReturn(source);
        BDDMockito.given(relatedSeed.getDestination()).willReturn(destination);
        BDDMockito.given(relatedSeed.isDirected()).willReturn(directed);
        // When
        final boolean isRelated = seed.isRelated(((ElementId) (relatedSeed))).isMatch();
        // Then
        Assert.assertFalse(isRelated);
    }

    @Test
    public void shouldBeRelatedToEntityId() {
        // Given
        final EntityId seed1 = new EntitySeed("vertex");
        final EntityId seed2 = new EntitySeed("vertex");
        // When
        final boolean isRelated = seed1.isRelated(seed2).isMatch();
        // Then
        Assert.assertTrue(isRelated);
    }

    @Test
    public void shouldBeEqualWhenVerticesEqual() {
        // Given
        final String vertex = "vertex";
        final EntityId seed1 = new EntitySeed(vertex);
        final EntityId seed2 = new EntitySeed(vertex);
        // When
        final boolean isEqual = seed1.equals(seed2);
        // Then
        Assert.assertTrue(isEqual);
        Assert.assertEquals(seed1.hashCode(), seed2.hashCode());
    }

    @Test
    public void shouldNotBeEqualWhenVerticesEqual() {
        // Given
        final EntityId seed1 = new EntitySeed("vertex");
        final EntityId seed2 = new EntitySeed("other vertex");
        // When
        final boolean isEqual = seed1.equals(seed2);
        // Then
        Assert.assertFalse(isEqual);
        Assert.assertNotEquals(seed1.hashCode(), seed2.hashCode());
    }

    @Test
    public void shouldSerialiseAndDeserialiseIntegersAndLongs() throws SerialisationException {
        // Given
        final Long vertex1 = 1L;
        final Integer vertex2 = 2;
        final EntityId seed1 = new EntitySeed(vertex1);
        final EntityId seed2 = new EntitySeed(vertex2);
        // When
        final byte[] bytes1 = JSONSerialiser.serialise(seed1);
        final byte[] bytes2 = JSONSerialiser.serialise(seed2);
        final EntityId seed1Deserialised = JSONSerialiser.deserialise(bytes1, EntityId.class);
        final EntityId seed2Deserialised = JSONSerialiser.deserialise(bytes2, EntityId.class);
        // Then
        Assert.assertEquals(seed1, seed1Deserialised);
        Assert.assertEquals(seed2, seed2Deserialised);
        Assert.assertTrue(((seed1Deserialised.getVertex()) instanceof Long));
        Assert.assertTrue(((seed2Deserialised.getVertex()) instanceof Integer));
    }

    @Test
    public void shouldSerialiseAndDeserialiseCustomVertexObjects() throws SerialisationException {
        // Given
        final CustomVertex vertex = new CustomVertex();
        vertex.setType("type");
        vertex.setValue("value");
        final EntityId seed = new EntitySeed(vertex);
        // When
        final byte[] bytes = JSONSerialiser.serialise(seed);
        final EntityId seedDeserialised = JSONSerialiser.deserialise(bytes, EntityId.class);
        // Then
        Assert.assertTrue(((seedDeserialised.getVertex()) instanceof CustomVertex));
        Assert.assertEquals("type", ((CustomVertex) (seedDeserialised.getVertex())).getType());
        Assert.assertEquals("value", ((CustomVertex) (seedDeserialised.getVertex())).getValue());
    }
}

