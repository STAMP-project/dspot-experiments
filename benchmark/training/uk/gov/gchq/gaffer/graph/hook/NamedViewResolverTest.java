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
package uk.gov.gchq.gaffer.graph.hook;


import IdentifierType.VERTEX;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.EDGE_3;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import TestPropertyNames.PROP_1;
import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.elementdefinition.view.NamedView;
import uk.gov.gchq.gaffer.data.elementdefinition.view.NamedViewDetail;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewParameterDetail;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.function.ExampleFilterFunction;
import uk.gov.gchq.gaffer.named.operation.cache.exception.CacheOperationFailedException;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.operation.handler.named.cache.NamedViewCache;
import uk.gov.gchq.gaffer.user.User;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;


public class NamedViewResolverTest {
    private static final String NAMED_VIEW_NAME = "namedViewName";

    private static final String NESTED_NAMED_VIEW_NAME = "nestedNamedViewName";

    private static final String IS_MORE_THAN_X_PARAM_KEY = "IS_MORE_THAN_X";

    private static final String SELECTION_PARAM_KEY = "SELECTION";

    private static final String EDGE_NAME_PARAM_KEY = "EDGE_NAME";

    private static final String VALUE_JSON_STRING = "\"value\":";

    private static final NamedViewCache CACHE = Mockito.mock(NamedViewCache.class);

    private static final Context CONTEXT = new Context(Mockito.mock(User.class));

    private static final NamedViewResolver RESOLVER = new NamedViewResolver(NamedViewResolverTest.CACHE);

    private static final View FULL_VIEW = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new ExampleFilterFunction()).build()).build()).build();

    private static final NamedViewDetail FULL_NAMED_VIEW_DETAIL = new NamedViewDetail.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).view(NamedViewResolverTest.FULL_VIEW).build();

    @Test
    public void shouldResolveNamedView() throws SerialisationException, CacheOperationFailedException {
        // Given
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(NamedViewResolverTest.NAMED_VIEW_NAME)).willReturn(NamedViewResolverTest.FULL_NAMED_VIEW_DETAIL);
        final OperationChain<?> opChain = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).build()).build()).build();
        // When
        NamedViewResolverTest.RESOLVER.preExecute(opChain, NamedViewResolverTest.CONTEXT);
        GetElements getElements = ((GetElements) (opChain.getOperations().get(0)));
        // Then
        JsonAssert.assertEquals(NamedViewResolverTest.FULL_VIEW.toCompactJson(), getElements.getView().toCompactJson());
    }

    @Test
    public void shouldResolveNamedViewAndMergeAnotherView() throws SerialisationException, CacheOperationFailedException {
        // Given
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(NamedViewResolverTest.NAMED_VIEW_NAME)).willReturn(NamedViewResolverTest.FULL_NAMED_VIEW_DETAIL);
        final View viewToMerge = new View.Builder().edge(EDGE).build();
        final View mergedView = new View.Builder().merge(NamedViewResolverTest.FULL_VIEW).merge(viewToMerge).build();
        final OperationChain<?> opChain = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).merge(viewToMerge).build()).build()).build();
        // When
        NamedViewResolverTest.RESOLVER.preExecute(opChain, NamedViewResolverTest.CONTEXT);
        GetElements getElements = ((GetElements) (opChain.getOperations().get(0)));
        // Then
        JsonAssert.assertEquals(mergedView.toCompactJson(), getElements.getView().toCompactJson());
    }

    @Test
    public void shouldResolveNamedViewAndMergeAnotherNamedView() throws SerialisationException, CacheOperationFailedException {
        // Given
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(NamedViewResolverTest.NAMED_VIEW_NAME)).willReturn(NamedViewResolverTest.FULL_NAMED_VIEW_DETAIL);
        final NamedView namedViewToMerge = new NamedView.Builder().name(((NamedViewResolverTest.NAMED_VIEW_NAME) + 1)).edge(EDGE).build();
        final NamedViewDetail namedViewDetailToMerge = new NamedViewDetail.Builder().name(namedViewToMerge.getName()).view(namedViewToMerge).build();
        final View mergedView = new View.Builder().merge(NamedViewResolverTest.FULL_VIEW).edge(EDGE).build();
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(((NamedViewResolverTest.NAMED_VIEW_NAME) + 1))).willReturn(namedViewDetailToMerge);
        final OperationChain<?> opChain = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).merge(namedViewToMerge).build()).build()).build();
        // When
        NamedViewResolverTest.RESOLVER.preExecute(opChain, NamedViewResolverTest.CONTEXT);
        GetElements getElements = ((GetElements) (opChain.getOperations().get(0)));
        // Then
        JsonAssert.assertEquals(mergedView.toCompactJson(), getElements.getView().toCompactJson());
    }

    @Test
    public void shouldResolveNestedNamedViews() throws SerialisationException, CacheOperationFailedException {
        // Given
        final NamedView nestedNamedView = new NamedView.Builder().name(NamedViewResolverTest.NESTED_NAMED_VIEW_NAME).entity(ENTITY_2).build();
        final NamedViewDetail nestedNamedViewDetail = new NamedViewDetail.Builder().name(nestedNamedView.getName()).view(new View.Builder().entity(ENTITY_2).build()).build();
        final NamedView nestedNamedView1 = new NamedView.Builder().name(((NamedViewResolverTest.NESTED_NAMED_VIEW_NAME) + 1)).edge(EDGE).merge(nestedNamedView).build();
        final NamedViewDetail nestedNamedView1Detail = new NamedViewDetail.Builder().name(nestedNamedView.getName()).view(new View.Builder().edge(EDGE).entity(ENTITY_2).build()).build();
        final NamedView namedViewWithNestedNamedView = new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).merge(nestedNamedView1).entity(ENTITY, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new ExampleFilterFunction()).build()).build()).build();
        final NamedViewDetail namedViewWithNestedNamedViewDetail = new NamedViewDetail.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).view(namedViewWithNestedNamedView).build();
        Assert.assertEquals(2, namedViewWithNestedNamedView.getMergedNamedViewNames().size());
        Assert.assertTrue(namedViewWithNestedNamedView.getMergedNamedViewNames().contains(NamedViewResolverTest.NESTED_NAMED_VIEW_NAME));
        Assert.assertTrue(namedViewWithNestedNamedView.getMergedNamedViewNames().contains(((NamedViewResolverTest.NESTED_NAMED_VIEW_NAME) + 1)));
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(NamedViewResolverTest.NAMED_VIEW_NAME)).willReturn(namedViewWithNestedNamedViewDetail);
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(NamedViewResolverTest.NESTED_NAMED_VIEW_NAME)).willReturn(nestedNamedViewDetail);
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(((NamedViewResolverTest.NESTED_NAMED_VIEW_NAME) + 1))).willReturn(nestedNamedView1Detail);
        final OperationChain<?> opChain = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).build()).build()).build();
        // When
        NamedViewResolverTest.RESOLVER.preExecute(opChain, NamedViewResolverTest.CONTEXT);
        GetElements getElements = ((GetElements) (opChain.getOperations().get(0)));
        namedViewWithNestedNamedView.setName(null);
        final View mergedView = new View.Builder().merge(namedViewWithNestedNamedView).build();
        // Then
        JsonAssert.assertEquals(mergedView.toCompactJson(), getElements.getView().toCompactJson());
    }

    @Test
    public void shouldResolveNamedViewWithParameter() throws CacheOperationFailedException {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put(NamedViewResolverTest.EDGE_NAME_PARAM_KEY, EDGE_2);
        ViewParameterDetail param = new ViewParameterDetail.Builder().defaultValue(EDGE).description("edge name param").valueClass(String.class).build();
        Map<String, ViewParameterDetail> paramDetailMap = Maps.newHashMap();
        paramDetailMap.put(NamedViewResolverTest.EDGE_NAME_PARAM_KEY, param);
        // Make a real View with a parameter
        final View extendedView = new View.Builder().edge((("${" + (NamedViewResolverTest.EDGE_NAME_PARAM_KEY)) + "}"), new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(VERTEX.name()).execute(new ExampleFilterFunction()).build()).build()).build();
        final NamedViewDetail extendedNamedViewDetail = new NamedViewDetail.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).view(extendedView).parameters(paramDetailMap).build();
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(NamedViewResolverTest.NAMED_VIEW_NAME)).willReturn(extendedNamedViewDetail);
        final OperationChain<?> opChain = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).build()).build()).build();
        // When
        NamedViewResolverTest.RESOLVER.preExecute(opChain, NamedViewResolverTest.CONTEXT);
        GetElements getElements = ((GetElements) (opChain.getOperations().get(0)));
        // Then
        Assert.assertTrue(((getElements.getView().getEdge(EDGE)) != null));
        Assert.assertTrue(((getElements.getView().getEdge(EDGE_2)) == null));
        final OperationChain<?> opChain1 = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).parameters(paramMap).build()).build()).build();
        // When
        NamedViewResolverTest.RESOLVER.preExecute(opChain1, NamedViewResolverTest.CONTEXT);
        GetElements getElements1 = ((GetElements) (opChain1.getOperations().get(0)));
        // Then
        Assert.assertTrue(((getElements1.getView().getEdge(EDGE_2)) != null));
    }

    @Test
    public void shouldResolveNamedViewWithParametersToMakeCompleteFilter() throws CacheOperationFailedException {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put(NamedViewResolverTest.IS_MORE_THAN_X_PARAM_KEY, 7L);
        ViewParameterDetail param = new ViewParameterDetail.Builder().defaultValue(2L).description("more than filter").valueClass(Long.class).build();
        Map<String, ViewParameterDetail> paramDetailMap = Maps.newHashMap();
        paramDetailMap.put(NamedViewResolverTest.IS_MORE_THAN_X_PARAM_KEY, param);
        // Make a real View with a parameter
        final View extendedView = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(VERTEX.name()).execute(new IsMoreThan((("${" + (NamedViewResolverTest.IS_MORE_THAN_X_PARAM_KEY)) + "}"))).build()).build()).build();
        final NamedViewDetail extendedNamedViewDetail = new NamedViewDetail.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).view(extendedView).parameters(paramDetailMap).build();
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(NamedViewResolverTest.NAMED_VIEW_NAME)).willReturn(extendedNamedViewDetail);
        final OperationChain<?> opChain = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).build()).build()).build();
        // When
        NamedViewResolverTest.RESOLVER.preExecute(opChain, NamedViewResolverTest.CONTEXT);
        GetElements getElements = ((GetElements) (opChain.getOperations().get(0)));
        // Then
        Assert.assertTrue(new String(getElements.getView().toCompactJson()).contains(((NamedViewResolverTest.VALUE_JSON_STRING) + 2)));
        final OperationChain<?> opChain1 = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).parameters(paramMap).build()).build()).build();
        // When
        NamedViewResolverTest.RESOLVER.preExecute(opChain1, NamedViewResolverTest.CONTEXT);
        GetElements getElements1 = ((GetElements) (opChain1.getOperations().get(0)));
        // Then
        Assert.assertTrue(new String(getElements1.getView().toCompactJson()).contains(((NamedViewResolverTest.VALUE_JSON_STRING) + 7)));
    }

    @Test
    public void shouldResolveNamedViewJsonWithParameters() throws CacheOperationFailedException {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put(NamedViewResolverTest.IS_MORE_THAN_X_PARAM_KEY, 7L);
        paramMap.put(NamedViewResolverTest.SELECTION_PARAM_KEY, "VERTEX");
        ViewParameterDetail param = new ViewParameterDetail.Builder().defaultValue(2L).description("more than filter").valueClass(Long.class).build();
        ViewParameterDetail param2 = new ViewParameterDetail.Builder().description("selection filter").valueClass(String.class).required(true).build();
        Map<String, ViewParameterDetail> paramDetailMap = Maps.newHashMap();
        paramDetailMap.put(NamedViewResolverTest.IS_MORE_THAN_X_PARAM_KEY, param);
        paramDetailMap.put(NamedViewResolverTest.SELECTION_PARAM_KEY, param2);
        // real View json with a parameter
        final String extendedViewString = "{\n" + ((((((((((((("  \"edges\" : {\n" + "    \"BasicEdge\" : {\n") + "      \"preAggregationFilterFunctions\" : [ {\n") + "        \"predicate\" : {\n") + "          \"class\" : \"uk.gov.gchq.koryphe.impl.predicate.IsMoreThan\",\n") + "          \"orEqualTo\" : false,\n") + "          \"value\" : \"${IS_MORE_THAN_X}\"\n") + "        },\n") + "        \"selection\" : [ \"${SELECTION}\" ]\n") + "      } ]\n") + "    }\n") + "  },\n") + "  \"entities\" : { }\n") + "}\n");
        final NamedViewDetail extendedNamedViewDetail = new NamedViewDetail.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).view(extendedViewString).parameters(paramDetailMap).build();
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(NamedViewResolverTest.NAMED_VIEW_NAME)).willReturn(extendedNamedViewDetail);
        final OperationChain<?> opChain = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).parameters(paramMap).build()).build()).build();
        // When
        NamedViewResolverTest.RESOLVER.preExecute(opChain, NamedViewResolverTest.CONTEXT);
        GetElements getElements = ((GetElements) (opChain.getOperations().get(0)));
        // Then
        Assert.assertTrue(new String(getElements.getView().toCompactJson()).contains(((NamedViewResolverTest.VALUE_JSON_STRING) + 7)));
        Assert.assertTrue(new String(getElements.getView().toCompactJson()).contains("VERTEX"));
    }

    @Test
    public void shouldResolveMultipleNestedNamedViews() throws CacheOperationFailedException {
        // Given
        final NamedView nestedNamedView = new NamedView.Builder().name(NamedViewResolverTest.NESTED_NAMED_VIEW_NAME).entity(ENTITY_2).build();
        final NamedViewDetail nestedNamedViewDetail = new NamedViewDetail.Builder().name(nestedNamedView.getName()).view(nestedNamedView).build();
        final NamedView nestedNamedView1 = new NamedView.Builder().name(((NamedViewResolverTest.NESTED_NAMED_VIEW_NAME) + 1)).edge(EDGE).merge(nestedNamedView).build();
        final NamedViewDetail nestedNamedView1Detail = new NamedViewDetail.Builder().name(nestedNamedView1.getName()).view(nestedNamedView1).build();
        final NamedView nestedNamedView2 = new NamedView.Builder().name(((NamedViewResolverTest.NESTED_NAMED_VIEW_NAME) + 2)).edge(EDGE_2).merge(nestedNamedView1).build();
        final NamedViewDetail nestedNamedView2Detail = new NamedViewDetail.Builder().name(nestedNamedView2.getName()).view(nestedNamedView2).build();
        final NamedView nestedNamedView3 = new NamedView.Builder().name(((NamedViewResolverTest.NESTED_NAMED_VIEW_NAME) + 3)).edge(EDGE_3).merge(nestedNamedView2).build();
        final NamedViewDetail nestedNamedView3Detail = new NamedViewDetail.Builder().name(nestedNamedView3.getName()).view(nestedNamedView3).build();
        Assert.assertEquals(3, nestedNamedView3.getMergedNamedViewNames().size());
        Assert.assertTrue(nestedNamedView3.getMergedNamedViewNames().contains(NamedViewResolverTest.NESTED_NAMED_VIEW_NAME));
        Assert.assertTrue(nestedNamedView3.getMergedNamedViewNames().contains(((NamedViewResolverTest.NESTED_NAMED_VIEW_NAME) + 1)));
        Assert.assertTrue(nestedNamedView3.getMergedNamedViewNames().contains(((NamedViewResolverTest.NESTED_NAMED_VIEW_NAME) + 2)));
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(NamedViewResolverTest.NESTED_NAMED_VIEW_NAME)).willReturn(nestedNamedViewDetail);
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(((NamedViewResolverTest.NESTED_NAMED_VIEW_NAME) + 1))).willReturn(nestedNamedView1Detail);
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(((NamedViewResolverTest.NESTED_NAMED_VIEW_NAME) + 2))).willReturn(nestedNamedView2Detail);
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(((NamedViewResolverTest.NESTED_NAMED_VIEW_NAME) + 3))).willReturn(nestedNamedView3Detail);
        final OperationChain<?> opChain = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(nestedNamedView3.getName()).build()).build()).build();
        // When
        NamedViewResolverTest.RESOLVER.preExecute(opChain, NamedViewResolverTest.CONTEXT);
        GetElements getElements = ((GetElements) (opChain.getOperations().get(0)));
        nestedNamedView3.setName(null);
        final View mergedView = new View.Builder().merge(nestedNamedView3).build();
        // Then
        JsonAssert.assertEquals(mergedView.toCompactJson(), getElements.getView().toCompactJson());
    }

    @Test
    public void shouldBuildFullViewWhenAViewToBeMergedIsSupplied() throws SerialisationException, CacheOperationFailedException {
        // Given
        final View viewToMerge = new View.Builder().edge(EDGE).build();
        final View finalExpectedView = new View.Builder().edge(EDGE).merge(NamedViewResolverTest.FULL_VIEW).build();
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(NamedViewResolverTest.NAMED_VIEW_NAME)).willReturn(NamedViewResolverTest.FULL_NAMED_VIEW_DETAIL);
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(((NamedViewResolverTest.NAMED_VIEW_NAME) + 1))).willReturn(null);
        final OperationChain<?> opChain = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).merge(viewToMerge).build()).build()).build();
        // When
        NamedViewResolverTest.RESOLVER.preExecute(opChain, NamedViewResolverTest.CONTEXT);
        // Then
        JsonAssert.assertEquals(finalExpectedView.toCompactJson(), getView().toCompactJson());
    }

    @Test
    public void shouldBuildFullViewWhenANamedViewNeedingToBeResolvedAndMergedIsSupplied() throws SerialisationException, CacheOperationFailedException {
        // Given
        final View viewToMerge = new View.Builder().edge(EDGE).build();
        final NamedViewDetail namedViewDetailToMerge = new NamedViewDetail.Builder().name(((NamedViewResolverTest.NAMED_VIEW_NAME) + 2)).view(viewToMerge).build();
        final View finalExpectedView = new View.Builder().edge(EDGE).merge(NamedViewResolverTest.FULL_VIEW).build();
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(NamedViewResolverTest.NAMED_VIEW_NAME)).willReturn(NamedViewResolverTest.FULL_NAMED_VIEW_DETAIL);
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(((NamedViewResolverTest.NAMED_VIEW_NAME) + 2))).willReturn(namedViewDetailToMerge);
        final OperationChain<?> opChain = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).merge(new NamedView.Builder().name(((NamedViewResolverTest.NAMED_VIEW_NAME) + 2)).build()).build()).build()).build();
        // When
        NamedViewResolverTest.RESOLVER.preExecute(opChain, NamedViewResolverTest.CONTEXT);
        // Then
        JsonAssert.assertEquals(finalExpectedView.toCompactJson(), getView().toCompactJson());
    }

    @Test
    public void shouldThrowExceptionWhenNamedViewToBeMergedIsNotInCache() throws CacheOperationFailedException {
        // Given
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(NamedViewResolverTest.NAMED_VIEW_NAME)).willReturn(NamedViewResolverTest.FULL_NAMED_VIEW_DETAIL);
        BDDMockito.given(NamedViewResolverTest.CACHE.getNamedView(((NamedViewResolverTest.NAMED_VIEW_NAME) + 1))).willThrow(new CacheOperationFailedException("No NamedView with the name namedViewName1 exists in the cache"));
        final OperationChain<?> opChain = new OperationChain.Builder().first(new GetElements.Builder().view(new NamedView.Builder().name(NamedViewResolverTest.NAMED_VIEW_NAME).merge(new NamedView.Builder().name(((NamedViewResolverTest.NAMED_VIEW_NAME) + 1)).build()).build()).build()).build();
        // When / Then
        try {
            NamedViewResolverTest.RESOLVER.preExecute(opChain, NamedViewResolverTest.CONTEXT);
            Assert.fail("Exception expected");
        } catch (final RuntimeException e) {
            assert e.getMessage().contains("No NamedView with the name namedViewName1 exists in the cache");
        }
    }
}

