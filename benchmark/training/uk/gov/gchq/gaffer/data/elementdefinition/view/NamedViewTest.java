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
package uk.gov.gchq.gaffer.data.elementdefinition.view;


import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import TestPropertyNames.PROP_1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.commonutil.StringUtil;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.function.ExampleFilterFunction;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;


public class NamedViewTest {
    private static final String TEST_VIEW_NAME = "testViewName";

    private static final String TEST_PARAM_KEY = "testParamKey";

    private static final Object TEST_PARAM = 1L;

    private final Map<String, Object> testParameters = new HashMap<>();

    private final ViewElementDefinition edgeDef1 = new ViewElementDefinition();

    private final ViewElementDefinition entityDef1 = new ViewElementDefinition();

    private final ViewElementDefinition edgeDef2 = new ViewElementDefinition.Builder().groupBy(EDGE).preAggregationFilter(new ElementFilter.Builder().select("count").execute(new IsMoreThan("${IS_MORE_THAN_X}")).build()).build();

    private final ViewElementDefinition entityDef2 = new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new ExampleFilterFunction()).build()).build();

    @Test
    public void shouldCreateEmptyNamedViewWithBasicConstructor() {
        // When
        NamedView namedView = new NamedView();
        // Then
        Assert.assertTrue(namedView.getName().isEmpty());
        Assert.assertTrue(namedView.getMergedNamedViewNames().isEmpty());
        Assert.assertTrue(namedView.getParameters().isEmpty());
        Assert.assertTrue(namedView.getEdges().isEmpty());
        Assert.assertTrue(namedView.getEntities().isEmpty());
    }

    @Test
    public void shouldThrowExceptionWithNoName() {
        try {
            new NamedView.Builder().edge(EDGE).build();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Name must be set"));
        }
    }

    @Test
    public void shouldNotExpandGlobalDefinitions() {
        // Given
        final GlobalViewElementDefinition globalDef = Mockito.mock(GlobalViewElementDefinition.class);
        final NamedView view = new NamedView.Builder().name("name").globalElements(globalDef).build();
        // When
        view.expandGlobalDefinitions();
        // Then
        Assert.assertEquals(1, view.getGlobalElements().size());
        Assert.assertSame(globalDef, view.getGlobalElements().get(0));
    }

    @Test
    public void shouldCreateNewNamedViewWithEdgesAndEntities() {
        // Given
        List<String> entityGroups = new ArrayList<>();
        List<String> edgeGroups = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            entityGroups.add(((TestGroups.ENTITY) + i));
            edgeGroups.add(((TestGroups.EDGE) + i));
        }
        // When
        NamedView namedView = new NamedView.Builder().name(NamedViewTest.TEST_VIEW_NAME).entities(entityGroups).edges(edgeGroups).build();
        // Then
        Assert.assertTrue(namedView.getEntityGroups().containsAll(entityGroups));
        Assert.assertEquals(entityGroups.size(), namedView.getEntityGroups().size());
        Assert.assertTrue(namedView.getEdgeGroups().containsAll(edgeGroups));
        Assert.assertEquals(edgeGroups.size(), namedView.getEdgeGroups().size());
    }

    @Test
    public void shouldBuildFullNamedView() {
        // When
        NamedView namedView = new NamedView.Builder().edge(EDGE, edgeDef1).entity(ENTITY, entityDef1).name(NamedViewTest.TEST_VIEW_NAME).parameters(testParameters).build();
        // Then
        Assert.assertEquals(NamedViewTest.TEST_VIEW_NAME, namedView.getName());
        Assert.assertEquals(testParameters, namedView.getParameters());
        Assert.assertEquals(1, namedView.getEdges().size());
        Assert.assertSame(edgeDef1, namedView.getEdge(EDGE));
        Assert.assertEquals(1, namedView.getEntities().size());
        Assert.assertSame(entityDef1, namedView.getEntity(ENTITY));
    }

    @Test
    public void shouldSerialiseToJson() {
        // Given
        final NamedView namedView = new NamedView.Builder().name(NamedViewTest.TEST_VIEW_NAME).entity(ENTITY, entityDef2).parameters(testParameters).build();
        // When
        byte[] json = namedView.toJson(true);
        // Then
        JsonAssert.assertEquals(String.format(("{%n" + ((((((((((((((("  \"class\" : \"uk.gov.gchq.gaffer.data.elementdefinition.view.NamedView\"," + "  \"entities\" : {\n") + "    \"BasicEntity\" : {\n") + "       \"preAggregationFilterFunctions\" : [ {\n") + "          \"predicate\" : {\n") + "             \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"") + "           },") + "           \"selection\" : [ \"property1\" ]") + "          } ]") + "        }") + "      },") + "      \"name\": \"testViewName\",") + "       \"parameters\": {") + "           \"testParamKey\" : 1") + "         }") + "    }"))), new String(json));
    }

    @Test
    public void shouldJsonSerialiseAndDeserialise() {
        // Given
        NamedView namedView = new NamedView.Builder().edge(EDGE, edgeDef1).entity(ENTITY, entityDef1).name(NamedViewTest.TEST_VIEW_NAME).parameters(testParameters).build();
        // When
        byte[] json = namedView.toJson(true);
        final NamedView deserialisedView = new NamedView.Builder().json(json).build();
        // Then
        Assert.assertEquals(NamedViewTest.TEST_VIEW_NAME, deserialisedView.getName());
        Assert.assertEquals(testParameters, namedView.getParameters());
        Assert.assertEquals(1, namedView.getEdges().size());
        Assert.assertSame(edgeDef1, namedView.getEdge(EDGE));
        Assert.assertEquals(1, namedView.getEntities().size());
        Assert.assertSame(entityDef1, namedView.getEntity(ENTITY));
    }

    @Test
    public void shouldDefaultDeserialiseToView() throws SerialisationException {
        final byte[] emptyJson = StringUtil.toBytes("{}");
        View view = JSONSerialiser.deserialise(emptyJson, View.class);
        Assert.assertEquals(View.class, view.getClass());
    }

    @Test
    public void shouldMergeNamedViews() {
        // Given / When
        NamedView namedView = new NamedView.Builder().edge(EDGE, edgeDef1).entity(ENTITY, entityDef1).name(NamedViewTest.TEST_VIEW_NAME).parameters(testParameters).build();
        NamedView namedView2 = new NamedView.Builder().edge(EDGE, edgeDef2).entity(ENTITY_2, entityDef2).name(((NamedViewTest.TEST_VIEW_NAME) + 2)).parameters(new HashMap()).merge(namedView).build();
        // Then
        Assert.assertEquals(((NamedViewTest.TEST_VIEW_NAME) + 2), namedView2.getName());
        Assert.assertEquals(testParameters, namedView2.getParameters());
        Assert.assertEquals(2, namedView2.getEntities().size());
        Assert.assertEquals(entityDef1, namedView2.getEntity(ENTITY));
        Assert.assertEquals(entityDef2, namedView2.getEntity(ENTITY_2));
        Assert.assertEquals(1, namedView2.getEdges().size());
        Assert.assertEquals(edgeDef2, namedView2.getEdge(EDGE));
    }

    @Test
    public void shouldMergeEmptyNamedViewWithPopulatedNamedView() {
        // When
        NamedView namedView = new NamedView.Builder().edge(EDGE, edgeDef1).entity(ENTITY, entityDef1).name(NamedViewTest.TEST_VIEW_NAME).parameters(testParameters).merge(new NamedView()).build();
        // Then
        Assert.assertEquals(NamedViewTest.TEST_VIEW_NAME, namedView.getName());
        Assert.assertEquals(testParameters, namedView.getParameters());
        Assert.assertEquals(1, namedView.getEdges().size());
        Assert.assertEquals(edgeDef1, namedView.getEdge(EDGE));
        Assert.assertEquals(1, namedView.getEntities().size());
        Assert.assertEquals(entityDef1, namedView.getEntity(ENTITY));
    }

    @Test
    public void shouldMultipleMergeNamedViewsCorrectly() {
        // Given
        List<String> entityGroups = new ArrayList<>();
        List<String> edgeGroups = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            entityGroups.add(((TestGroups.ENTITY) + i));
            edgeGroups.add(((TestGroups.EDGE) + i));
        }
        // When
        NamedView namedView1 = new NamedView.Builder().edges(edgeGroups).name(((NamedViewTest.TEST_VIEW_NAME) + 1)).parameters(testParameters).merge(new NamedView()).build();
        NamedView namedView2 = new NamedView.Builder().entities(entityGroups).name(((NamedViewTest.TEST_VIEW_NAME) + 2)).parameters(testParameters).merge(namedView1).build();
        NamedView namedView3 = new NamedView.Builder().name(((NamedViewTest.TEST_VIEW_NAME) + 3)).parameters(testParameters).merge(namedView2).build();
        // Then
        Assert.assertEquals(((NamedViewTest.TEST_VIEW_NAME) + 3), namedView3.getName());
        Assert.assertEquals(testParameters, namedView3.getParameters());
        Assert.assertTrue(namedView3.getEntityGroups().containsAll(entityGroups));
        Assert.assertEquals(entityGroups.size(), namedView3.getEntityGroups().size());
        Assert.assertTrue(namedView3.getEdgeGroups().containsAll(edgeGroups));
        Assert.assertEquals(edgeGroups.size(), namedView3.getEdgeGroups().size());
        Assert.assertEquals(2, namedView3.getMergedNamedViewNames().size());
        Assert.assertTrue(namedView3.getMergedNamedViewNames().containsAll(Arrays.asList(((NamedViewTest.TEST_VIEW_NAME) + 1), ((NamedViewTest.TEST_VIEW_NAME) + 2))));
    }

    @Test
    public void shouldMergeViewToNamedViewsCorrectly() {
        // Given
        List<String> entityGroups = new ArrayList<>();
        List<String> edgeGroups = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            entityGroups.add(((TestGroups.ENTITY) + i));
            edgeGroups.add(((TestGroups.EDGE) + i));
        }
        // When
        View view = new View.Builder().entities(entityGroups).build();
        NamedView namedView = new NamedView.Builder().name(((NamedViewTest.TEST_VIEW_NAME) + 3)).edges(edgeGroups).parameters(testParameters).merge(view).build();
        // Then
        Assert.assertEquals(((NamedViewTest.TEST_VIEW_NAME) + 3), namedView.getName());
        Assert.assertEquals(testParameters, namedView.getParameters());
        Assert.assertTrue(namedView.getEntityGroups().containsAll(entityGroups));
        Assert.assertEquals(entityGroups.size(), namedView.getEntityGroups().size());
        Assert.assertTrue(namedView.getEdgeGroups().containsAll(edgeGroups));
        Assert.assertEquals(edgeGroups.size(), namedView.getEdgeGroups().size());
    }

    @Test
    public void showAllowMergingOfNamedViewIntoAViewWhenNameIsEmpty() {
        // When / Then
        try {
            new View.Builder().merge(new NamedView()).build();
        } catch (final IllegalArgumentException e) {
            Assert.fail("Exception not expected");
        }
    }

    @Test
    public void shouldThrowExceptionWhenMergingNamedViewIntoAViewWhenNameIsSet() {
        // Given
        final NamedView namedView = new NamedView.Builder().name(NamedViewTest.TEST_VIEW_NAME).build();
        // When / Then
        try {
            new View.Builder().merge(namedView).build();
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("A NamedView cannot be merged into a View"));
        }
    }

    @Test
    public void shouldNotAddNameToMergedNamedViewsListIfNameIsTheSameAsTheNamedViewName() {
        final String namedViewName = "namedViewName";
        final NamedView namedViewToMerge = new NamedView.Builder().name(namedViewName).edge(EDGE).build();
        final NamedView namedViewMerged = new NamedView.Builder().name(namedViewName).merge(namedViewToMerge).build();
        Assert.assertFalse(namedViewMerged.getMergedNamedViewNames().contains(namedViewName));
    }

    @Test
    public void shouldAddAllMergedNamedViewNamesToTopLevelNamedView() {
        final String namedViewName = "namedViewName";
        final NamedView namedViewToMerge3 = new NamedView.Builder().name((namedViewName + 3)).edge(EDGE_2).build();
        final NamedView namedViewToMerge2 = new NamedView.Builder().name((namedViewName + 2)).merge(namedViewToMerge3).edge(EDGE).build();
        final NamedView namedViewToMerge1 = new NamedView.Builder().name((namedViewName + 1)).merge(namedViewToMerge2).entity(ENTITY).build();
        final NamedView namedViewMerged = new NamedView.Builder().name(namedViewName).merge(namedViewToMerge1).build();
        Assert.assertEquals(3, namedViewMerged.getMergedNamedViewNames().size());
        Assert.assertEquals(Arrays.asList((namedViewName + 1), (namedViewName + 2), (namedViewName + 3)), namedViewMerged.getMergedNamedViewNames());
    }
}

