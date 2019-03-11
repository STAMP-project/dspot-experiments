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
package uk.gov.gchq.gaffer.data.elementdefinition.view;


import IdentifierType.SOURCE;
import IdentifierType.VERTEX;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.EDGE_3;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import TestGroups.ENTITY_3;
import TestPropertyNames.COUNT;
import TestPropertyNames.DATE;
import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_2;
import TestPropertyNames.PROP_3;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.element.function.ElementTransformer;
import uk.gov.gchq.gaffer.function.ExampleFilterFunction;
import uk.gov.gchq.gaffer.function.ExampleTransformFunction;
import uk.gov.gchq.koryphe.impl.predicate.Exists;


public class ViewUtilTest {
    @Test
    public void shouldNotRemovePropertiesWhenNotSet() {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().allProperties().build()).entity(ENTITY, new ViewElementDefinition.Builder().properties(PROP_2).build()).build();
        final Edge edge = new Edge.Builder().group(EDGE).property(PROP_1, "1").property(PROP_2, "2").property(PROP_3, "3").build();
        // When
        ViewUtil.removeProperties(view, edge);
        // Then
        Assert.assertEquals(3, edge.getProperties().size());
        Assert.assertEquals("1", edge.getProperties().get(PROP_1));
        Assert.assertEquals("2", edge.getProperties().get(PROP_2));
        Assert.assertEquals("3", edge.getProperties().get(PROP_3));
    }

    @Test
    public void shouldNotRemovePropertiesWhenNoRelevantExcludeProperties() {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().excludeProperties(COUNT).build()).entity(ENTITY, new ViewElementDefinition.Builder().properties(PROP_2).build()).build();
        final Edge edge = new Edge.Builder().group(EDGE).property(PROP_1, "1").property(PROP_2, "2").property(PROP_3, "3").build();
        // When
        ViewUtil.removeProperties(view, edge);
        // Then
        Assert.assertEquals(3, edge.getProperties().size());
        Assert.assertEquals("1", edge.getProperties().get(PROP_1));
        Assert.assertEquals("2", edge.getProperties().get(PROP_2));
        Assert.assertEquals("3", edge.getProperties().get(PROP_3));
    }

    @Test
    public void shouldNotRemoveAllPropertiesWhenNoRelevantProperties() {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(COUNT).build()).entity(ENTITY, new ViewElementDefinition.Builder().properties(PROP_2).build()).build();
        final Edge edge = new Edge.Builder().group(EDGE).property(PROP_1, "1").property(PROP_2, "2").property(PROP_3, "3").build();
        // When
        ViewUtil.removeProperties(view, edge);
        // Then
        Assert.assertEquals(0, edge.getProperties().size());
    }

    @Test
    public void shouldKeepOnlyProvidedProperties() {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().properties(PROP_1).build()).entity(ENTITY, new ViewElementDefinition.Builder().properties(PROP_2).build()).build();
        final Edge edge = new Edge.Builder().group(EDGE).property(PROP_1, "1").property(PROP_2, "2").property(PROP_3, "3").build();
        // When
        ViewUtil.removeProperties(view, edge);
        // Then
        Assert.assertEquals(1, edge.getProperties().size());
        Assert.assertEquals("1", edge.getProperties().get(PROP_1));
    }

    @Test
    public void shouldRemoveExcludedProperties() {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().excludeProperties(PROP_1).build()).entity(ENTITY, new ViewElementDefinition.Builder().excludeProperties(PROP_2).build()).build();
        final Edge edge = new Edge.Builder().group(EDGE).property(PROP_1, "1").property(PROP_2, "2").property(PROP_3, "3").build();
        // When
        ViewUtil.removeProperties(view, edge);
        // Then
        Assert.assertEquals(2, edge.getProperties().size());
        Assert.assertEquals("2", edge.getProperties().get(PROP_2));
        Assert.assertEquals("3", edge.getProperties().get(PROP_3));
    }

    @Test
    public void shouldCreateNewViewWithEdgeAndEntityGroups() {
        // Given
        List<String> entityGroups = new ArrayList<>();
        List<String> edgeGroups = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            entityGroups.add(((TestGroups.ENTITY) + i));
            edgeGroups.add(((TestGroups.EDGE) + i));
        }
        // When
        View view = new View.Builder().entities(entityGroups).edges(edgeGroups).build();
        // Then
        Assert.assertTrue(view.getEntityGroups().containsAll(entityGroups));
        Assert.assertEquals(entityGroups.size(), view.getEntityGroups().size());
        Assert.assertTrue(view.getEdgeGroups().containsAll(edgeGroups));
        Assert.assertEquals(edgeGroups.size(), view.getEdgeGroups().size());
    }

    @Test
    public void shouldBuildView() {
        // Given
        final ViewElementDefinition edgeDef1 = new ViewElementDefinition();
        final ViewElementDefinition edgeDef2 = new ViewElementDefinition();
        final ViewElementDefinition entityDef1 = new ViewElementDefinition();
        final ViewElementDefinition entityDef2 = new ViewElementDefinition();
        // When
        final View view = new View.Builder().edge(EDGE, edgeDef1).entity(ENTITY, entityDef1).entity(ENTITY_2, entityDef2).edge(EDGE_2, edgeDef2).build();
        // Then
        Assert.assertEquals(2, view.getEdges().size());
        Assert.assertSame(edgeDef1, view.getEdge(EDGE));
        Assert.assertSame(edgeDef2, view.getEdge(EDGE_2));
        Assert.assertEquals(2, view.getEntities().size());
        Assert.assertSame(entityDef1, view.getEntity(ENTITY));
        Assert.assertSame(entityDef2, view.getEntity(ENTITY_2));
    }

    @Test
    public void shouldSerialiseToJson() {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().transientProperty(PROP_3, String.class).transformer(new ElementTransformer.Builder().select(PROP_1, PROP_2).execute(new ExampleTransformFunction()).project(PROP_3).build()).postTransformFilter(new ElementFilter.Builder().select(PROP_3).execute(new ExampleFilterFunction()).build()).build()).entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new ExampleFilterFunction()).build()).build()).build();
        // When
        byte[] json = view.toJson(true);
        // Then
        JsonAssert.assertEquals(String.format(("{%n" + (((((((((((((((((((((((((((((("  \"edges\" : {%n" + "    \"BasicEdge\" : {%n") + "      \"transientProperties\" : {%n") + "        \"property3\" : \"java.lang.String\"%n") + "      },%n") + "      \"postTransformFilterFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"property3\" ]%n") + "      } ],%n") + "      \"transformFunctions\" : [ {%n") + "        \"function\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleTransformFunction\"%n") + "        },%n") + "        \"selection\" : [ \"property1\", \"property2\" ],%n") + "        \"projection\" : [ \"property3\" ]%n") + "      } ]%n") + "    }%n") + "  },%n") + "  \"entities\" : {%n") + "    \"BasicEntity\" : {%n") + "      \"preAggregationFilterFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"property1\" ]%n") + "      } ]%n") + "    }%n") + "  }%n") + "}"))), new String(json));
    }

    @Test
    public void shouldJsonSerialiseAndDeserialise() {
        // Given
        final View view = createView();
        // When
        byte[] json = view.toJson(true);
        final View deserialisedView = new View.Builder().json(json).build();
        deserialisedView.expandGlobalDefinitions();
        // Then
        Assert.assertEquals(1, deserialisedView.getEntityGroups().size());
        final ViewElementDefinition entityDef = deserialisedView.getEntity(ENTITY);
        Assert.assertTrue(entityDef.getTransientProperties().isEmpty());
        Assert.assertNull(entityDef.getTransformer());
        Assert.assertEquals(2, entityDef.getPreAggregationFilter().getComponents().size());
        Assert.assertTrue(((entityDef.getPreAggregationFilter().getComponents().get(0).getPredicate()) instanceof ExampleFilterFunction));
        Assert.assertEquals(1, entityDef.getPreAggregationFilter().getComponents().get(0).getSelection().length);
        Assert.assertEquals(PROP_1, entityDef.getPreAggregationFilter().getComponents().get(0).getSelection()[0]);
        Assert.assertEquals(PROP_1, entityDef.getPreAggregationFilter().getComponents().get(1).getSelection()[0]);
        Assert.assertEquals(1, entityDef.getPostAggregationFilter().getComponents().get(0).getSelection().length);
        Assert.assertEquals(VERTEX.name(), entityDef.getPostAggregationFilter().getComponents().get(0).getSelection()[0]);
        final ViewElementDefinition edgeDef = deserialisedView.getEdge(EDGE);
        Assert.assertEquals(1, edgeDef.getTransientProperties().size());
        Assert.assertEquals(String.class, edgeDef.getTransientPropertyMap().get(PROP_3));
        Assert.assertEquals(1, edgeDef.getPreAggregationFilter().getComponents().size());
        Assert.assertTrue(((edgeDef.getPreAggregationFilter().getComponents().get(0).getPredicate()) instanceof ExampleFilterFunction));
        Assert.assertEquals(1, edgeDef.getPreAggregationFilter().getComponents().get(0).getSelection().length);
        Assert.assertEquals(PROP_1, edgeDef.getPreAggregationFilter().getComponents().get(0).getSelection()[0]);
        Assert.assertEquals(1, edgeDef.getTransformer().getComponents().size());
        Assert.assertTrue(((edgeDef.getTransformer().getComponents().get(0).getFunction()) instanceof ExampleTransformFunction));
        Assert.assertEquals(2, edgeDef.getTransformer().getComponents().get(0).getSelection().length);
        Assert.assertEquals(PROP_1, edgeDef.getTransformer().getComponents().get(0).getSelection()[0]);
        Assert.assertEquals(PROP_2, edgeDef.getTransformer().getComponents().get(0).getSelection()[1]);
        Assert.assertEquals(1, edgeDef.getTransformer().getComponents().get(0).getProjection().length);
        Assert.assertEquals(PROP_3, edgeDef.getTransformer().getComponents().get(0).getProjection()[0]);
        Assert.assertEquals(1, edgeDef.getPostTransformFilter().getComponents().size());
        Assert.assertTrue(((edgeDef.getPostTransformFilter().getComponents().get(0).getPredicate()) instanceof ExampleFilterFunction));
        Assert.assertEquals(1, edgeDef.getPostTransformFilter().getComponents().get(0).getSelection().length);
        Assert.assertEquals(PROP_3, edgeDef.getPostTransformFilter().getComponents().get(0).getSelection()[0]);
        Assert.assertEquals(1, edgeDef.getPostAggregationFilter().getComponents().get(0).getSelection().length);
        Assert.assertEquals(SOURCE.name(), edgeDef.getPostAggregationFilter().getComponents().get(0).getSelection()[0]);
    }

    @Test
    public void shouldCreateViewWithGlobalDefinitions() {
        // Given
        final View view = new View.Builder().globalElements(new GlobalViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new ExampleFilterFunction()).build()).groupBy(PROP_1).transientProperty(PROP_2, String.class).build()).globalEntities(new GlobalViewElementDefinition.Builder().postAggregationFilter(new ElementFilter.Builder().select(VERTEX.name()).execute(new ExampleFilterFunction()).build()).groups(ENTITY, ENTITY_2).build()).globalEdges(new GlobalViewElementDefinition.Builder().postTransformFilter(new ElementFilter.Builder().select(SOURCE.name()).execute(new ExampleFilterFunction()).build()).groupBy().groups(EDGE, EDGE_2).build()).entity(ENTITY_3, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(DATE).execute(new ExampleFilterFunction()).build()).groupBy(DATE).build()).entity(ENTITY).entity(ENTITY_2).edge(EDGE).edge(EDGE_2).edge(EDGE_3).build();
        // When
        view.expandGlobalDefinitions();
        JsonAssert.assertEquals(String.format(("{%n" + ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("  \"edges\" : {%n" + "    \"BasicEdge2\" : {%n") + "      \"groupBy\" : [ ],%n") + "      \"transientProperties\" : {%n") + "        \"property2\" : \"java.lang.String\"%n") + "      },%n") + "      \"preAggregationFilterFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"property1\" ]%n") + "      } ],%n") + "      \"postTransformFilterFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"SOURCE\" ]%n") + "      } ]%n") + "    },%n") + "    \"BasicEdge\" : {%n") + "      \"groupBy\" : [ ],%n") + "      \"transientProperties\" : {%n") + "        \"property2\" : \"java.lang.String\"%n") + "      },%n") + "      \"preAggregationFilterFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"property1\" ]%n") + "      } ],%n") + "      \"postTransformFilterFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"SOURCE\" ]%n") + "      } ]%n") + "    },%n") + "    \"BasicEdge3\" : {%n") + "      \"groupBy\" : [ \"property1\" ],%n") + "      \"transientProperties\" : {%n") + "        \"property2\" : \"java.lang.String\"%n") + "      },%n") + "      \"preAggregationFilterFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"property1\" ]%n") + "      } ]%n") + "    }%n") + "  },%n") + "  \"entities\" : {%n") + "    \"BasicEntity2\" : {%n") + "      \"groupBy\" : [ \"property1\" ],%n") + "      \"transientProperties\" : {%n") + "        \"property2\" : \"java.lang.String\"%n") + "      },%n") + "      \"preAggregationFilterFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"property1\" ]%n") + "      } ],%n") + "      \"postAggregationFilterFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"VERTEX\" ]%n") + "      } ]%n") + "    },%n") + "    \"BasicEntity\" : {%n") + "      \"groupBy\" : [ \"property1\" ],%n") + "      \"transientProperties\" : {%n") + "        \"property2\" : \"java.lang.String\"%n") + "      },%n") + "      \"preAggregationFilterFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"property1\" ]%n") + "      } ],%n") + "      \"postAggregationFilterFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"VERTEX\" ]%n") + "      } ]%n") + "    },%n") + "    \"BasicEntity3\" : {%n") + "      \"groupBy\" : [ \"dateProperty\" ],%n") + "      \"transientProperties\" : {%n") + "        \"property2\" : \"java.lang.String\"%n") + "      },%n") + "      \"preAggregationFilterFunctions\" : [ {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"property1\" ]%n") + "      }, {%n") + "        \"predicate\" : {%n") + "          \"class\" : \"uk.gov.gchq.gaffer.function.ExampleFilterFunction\"%n") + "        },%n") + "        \"selection\" : [ \"dateProperty\" ]%n") + "      } ]%n") + "    }%n") + "  }%n") + "}"))), new String(view.toJson(true)));
    }

    @Test
    public void shouldCreateAnIdenticalObjectWhenCloned() {
        // Given
        final ViewElementDefinition edgeDef1 = new ViewElementDefinition();
        final ViewElementDefinition edgeDef2 = new ViewElementDefinition();
        final ViewElementDefinition entityDef1 = new ViewElementDefinition();
        final ViewElementDefinition entityDef2 = new ViewElementDefinition();
        // When
        final View view = new View.Builder().edge(EDGE, edgeDef1).entity(ENTITY, entityDef1).entity(ENTITY_2, entityDef2).edge(EDGE_2, edgeDef2).build();
        // Then
        final View clone = view.clone();
        // Check that the objects are equal
        Assert.assertEquals(view, clone);
        final byte[] viewJson = view.toCompactJson();
        final byte[] cloneJson = clone.toCompactJson();
        // Check that JSON representations of the objects are equal
        Assert.assertArrayEquals(viewJson, cloneJson);
        final View viewFromJson = new View.Builder().json(viewJson).build();
        final View cloneFromJson = new View.Builder().json(cloneJson).build();
        // Check that objects created from JSON representations are equal
        Assert.assertEquals(viewFromJson, cloneFromJson);
        // Check that objects created from JSON representations are equal
        Assert.assertEquals(viewFromJson, view);
        Assert.assertEquals(cloneFromJson, clone);
    }

    @Test
    public void shouldSerialiseToCompactJson() {
        // Given
        final View view = new View();
        // When
        final String compactJson = new String(view.toCompactJson());
        // Then - no description fields or new lines
        Assert.assertFalse(compactJson.contains(String.format("%n")));
    }

    @Test
    public void shouldMergeDifferentViews() {
        // Given
        final View view1 = new View.Builder().entity(ENTITY).edge(EDGE).build();
        final View view2 = new View.Builder().entity(ENTITY).entity(ENTITY_2).edge(EDGE).edge(EDGE_2).build();
        // When
        final View mergedView = new View.Builder().merge(view1).merge(view2).build();
        // Then
        Assert.assertEquals(2, mergedView.getEntities().size());
        Assert.assertEquals(2, mergedView.getEdges().size());
    }

    @Test
    public void shouldGetAllGroups() {
        // Given
        final View view = createView();
        // When
        final Set<String> groups = view.getGroups();
        // Then
        final Set<String> allGroups = new java.util.HashSet(view.getEntityGroups());
        allGroups.addAll(view.getEdgeGroups());
        Assert.assertEquals(allGroups, groups);
    }

    @Test
    public void shouldReturnTrueWhenViewHasPreAggEntityFilters() {
        // Given
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).build()).edge(EDGE).edge(EDGE_2, null).build();
        // When
        final boolean result = view.hasPreAggregationFilters();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnTrueWhenViewHasPreAggEdgeFilters() {
        // Given
        final View view = new View.Builder().entity(ENTITY).edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).build()).edge(EDGE_2, null).build();
        // When
        final boolean result = view.hasPreAggregationFilters();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenViewHasNullPreAggEdgeFilters() {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(null).build()).build();
        // When
        final boolean result = view.hasPreAggregationFilters();
        // Then
        Assert.assertFalse(result);
    }

    @Test
    public void shouldReturnFalseWhenViewHasEmptyPreAggEdgeFilters() {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().build()).build()).build();
        // When
        final boolean result = view.hasPreAggregationFilters();
        // Then
        Assert.assertFalse(result);
    }

    @Test
    public void shouldReturnTrueWhenViewHasPostAggEntityFilters() {
        // Given
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().postAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).build()).edge(EDGE).edge(EDGE_2, null).build();
        // When
        final boolean result = view.hasPostAggregationFilters();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnTrueWhenViewHasPostAggEdgeFilters() {
        // Given
        final View view = new View.Builder().entity(ENTITY).edge(EDGE, new ViewElementDefinition.Builder().postAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).build()).edge(EDGE_2, null).build();
        // When
        final boolean result = view.hasPostAggregationFilters();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenViewHasNullPostAggEdgeFilters() {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().postAggregationFilter(null).build()).build();
        // When
        final boolean result = view.hasPostAggregationFilters();
        // Then
        Assert.assertFalse(result);
    }

    @Test
    public void shouldReturnFalseWhenViewHasEmptyPostAggEdgeFilters() {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().postAggregationFilter(new ElementFilter.Builder().build()).build()).build();
        // When
        final boolean result = view.hasPostAggregationFilters();
        // Then
        Assert.assertFalse(result);
    }

    @Test
    public void shouldReturnTrueWhenViewHasPostTransformEntityFilters() {
        // Given
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().postTransformFilter(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).build()).edge(EDGE).edge(EDGE_2, null).build();
        // When
        final boolean result = view.hasPostTransformFilters();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnTrueWhenViewHasPostTransformEdgeFilters() {
        // Given
        final View view = new View.Builder().entity(ENTITY).edge(EDGE, new ViewElementDefinition.Builder().postTransformFilter(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).build()).edge(EDGE_2, null).build();
        // When
        final boolean result = view.hasPostTransformFilters();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenViewHasNullPostTransformEdgeFilters() {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().postTransformFilter(null).build()).build();
        // When
        final boolean result = view.hasPostTransformFilters();
        // Then
        Assert.assertFalse(result);
    }

    @Test
    public void shouldReturnFalseWhenViewHasEmptyPostTransformEdgeFilters() {
        // Given
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().postTransformFilter(new ElementFilter.Builder().build()).build()).build();
        // When
        final boolean result = view.hasPostTransformFilters();
        // Then
        Assert.assertFalse(result);
    }

    @Test
    public void shouldRemoveGroupFromView() {
        // Given
        View view = new View.Builder().edge(EDGE).entity(ENTITY).build();
        // Then
        Assert.assertEquals(2, view.getGroups().size());
        // When
        view = ViewUtil.removeGroups(view, EDGE);
        // Then
        Assert.assertFalse(view.getGroups().contains(EDGE));
        Assert.assertEquals(1, view.getGroups().size());
        // When
        view = ViewUtil.removeGroups(view, ENTITY);
        // Then
        Assert.assertFalse(view.getGroups().contains(ENTITY));
        Assert.assertEquals(0, view.getGroups().size());
    }

    @Test
    public void shouldIgnoreRemovingGroupFromViewWhenNotSet() {
        // Given
        View view = new View.Builder().edge(EDGE).build();
        // When
        View viewAfterRemove = ViewUtil.removeGroups(view, ENTITY);
        // Then - views identical
        JsonAssert.assertEquals(view.toJson(false), viewAfterRemove.toJson(false));
    }

    @Test
    public void shouldThrowExceptionOnRemovalOfNullGroups() {
        // Given
        View view = new View.Builder().edge(EDGE).build();
        // When / Then
        try {
            ViewUtil.removeGroups(view, null);
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().equals("Specified group(s) to remove is null"));
        }
    }

    @Test
    public void shouldThrowExceptionOnWhenRemovingGroupFromNullView() {
        // When / Then
        try {
            ViewUtil.removeGroups(null, EDGE);
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().equals("View cannot be null"));
        }
    }
}

