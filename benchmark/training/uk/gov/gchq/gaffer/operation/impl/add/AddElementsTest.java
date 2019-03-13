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
package uk.gov.gchq.gaffer.operation.impl.add;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class AddElementsTest extends OperationTest<AddElements> {
    public static final String ADD_ELEMENTS_JSON = String.format(("{%n" + (((((((((((((((((((("  \"class\" : \"uk.gov.gchq.gaffer.operation.impl.add.AddElements\",%n" + "  \"validate\" : true,%n") + "  \"skipInvalidElements\" : false,%n") + "  \"input\" : [ {%n") + "    \"class\" : \"uk.gov.gchq.gaffer.data.element.Entity\",%n") + "    \"properties\" : {%n") + "      \"property 1\" : \"property 1 value\"%n") + "    },%n") + "    \"group\" : \"entity type 1\",%n") + "    \"vertex\" : \"vertex 1\"%n") + "  }, {%n") + "    \"class\" : \"uk.gov.gchq.gaffer.data.element.Edge\",%n") + "    \"properties\" : {%n") + "      \"property 2\" : \"property 2 value\"%n") + "    },%n") + "    \"group\" : \"edge type 2\",%n") + "    \"source\" : \"source vertex 1\",%n") + "    \"destination\" : \"dest vertex 1\",%n") + "    \"directed\" : true%n") + "  } ]%n") + "}")));

    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final AddElements addElements = getTestObject();
        final Map<String, String> options = new HashMap<>();
        options.put("option", "value");
        addElements.setOptions(options);
        // When
        String json = new String(JSONSerialiser.serialise(addElements, true));
        // Then
        JsonAssert.assertEquals(String.format(("{%n" + (((("  \"class\" : \"uk.gov.gchq.gaffer.operation.impl.add.AddElements\",%n" + "  \"validate\" : true,%n") + "  \"options\" : {\"option\": \"value\"},%n") + "  \"skipInvalidElements\" : false%n") + "}"))), json);
    }

    @Test
    public void shouldSerialisePopulatedAddElementsOperation() throws IOException {
        // Given
        final List<Element> elements = Arrays.asList(new Entity.Builder().group("entity type 1").vertex("vertex 1").property("property 1", "property 1 value").build(), new Edge.Builder().group("edge type 2").source("source vertex 1").dest("dest vertex 1").directed(true).property("property 2", "property 2 value").build());
        final AddElements addElements = new AddElements.Builder().input(elements).build();
        // When
        String json = new String(JSONSerialiser.serialise(addElements, true));
        // Then
        JsonAssert.assertEquals(AddElementsTest.ADD_ELEMENTS_JSON, json);
    }

    @Test
    public void shouldDeserialiseAddElementsOperation() throws IOException {
        // Given
        // When
        AddElements addElements = JSONSerialiser.deserialise(AddElementsTest.ADD_ELEMENTS_JSON.getBytes(), AddElements.class);
        // Then
        final Iterator<? extends Element> itr = addElements.getInput().iterator();
        final Entity elm1 = ((Entity) (itr.next()));
        Assert.assertEquals("vertex 1", elm1.getVertex());
        Assert.assertEquals(1, elm1.getProperties().size());
        Assert.assertEquals("property 1 value", elm1.getProperty("property 1"));
        final Edge elm2 = ((Edge) (itr.next()));
        Assert.assertEquals("source vertex 1", elm2.getSource());
        Assert.assertEquals("dest vertex 1", elm2.getDestination());
        Assert.assertTrue(elm2.isDirected());
        Assert.assertEquals(1, elm2.getProperties().size());
        Assert.assertEquals("property 2 value", elm2.getProperty("property 2"));
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        Element element = new Edge.Builder().group("testEdgeGroup").build();
        AddElements addElements = new AddElements.Builder().input(element).skipInvalidElements(true).option("testOption", "true").validate(false).build();
        Assert.assertEquals("true", addElements.getOption("testOption"));
        Assert.assertTrue(addElements.isSkipInvalidElements());
        Assert.assertFalse(addElements.isValidate());
        Assert.assertEquals(element, addElements.getInput().iterator().next());
    }
}

