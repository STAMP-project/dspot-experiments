package uk.gov.gchq.gaffer.store.util;


import AggregatorUtil.ToIngestElementKey;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import TestGroups.NON_AGG_ENTITY;
import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_3;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.function.ElementAggregator;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.koryphe.impl.binaryoperator.Product;


public class AggregatorUtilTest {
    @Test
    public void shouldThrowExceptionWhenIngestAggregatedIfSchemaIsNull() {
        // given
        final Schema schema = null;
        // When / Then
        try {
            AggregatorUtil.ingestAggregate(Collections.emptyList(), schema);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldIngestAggregateElementsWhenProvidedIterableCanOnlyBeConsumedOnce() {
        // given
        final Schema schema = Schema.fromJson(StreamUtil.openStreams(getClass(), "schema-groupby"));
        final List<Element> elements = Arrays.asList(new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 1).build(), new Entity.Builder().group(NON_AGG_ENTITY).vertex("vertex1").property("count", 2).build());
        final Iterable<Element> onlyConsumingOnceIterable = new Iterable<Element>() {
            private boolean firstTime = true;

            @Override
            public Iterator<Element> iterator() {
                if (firstTime) {
                    firstTime = false;
                    return elements.iterator();
                }
                throw new NoSuchElementException();
            }
        };
        // when
        final CloseableIterable<Element> aggregatedElements = AggregatorUtil.ingestAggregate(onlyConsumingOnceIterable, schema);
        // then
        assertElementEquals(elements, aggregatedElements);
    }

    @Test
    public void shouldIngestAggregateElementsWithNoGroupBy() {
        // given
        final Schema schema = Schema.fromJson(StreamUtil.openStreams(getClass(), "schema-groupby"));
        final List<Element> elements = Arrays.asList(new Entity.Builder().group(NON_AGG_ENTITY).vertex("vertex1").property("count", 1).build(), new Entity.Builder().group(NON_AGG_ENTITY).vertex("vertex1").property("count", 2).build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 1).build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 2).build(), new Entity.Builder().group(ENTITY).vertex("vertex2").property("count", 10).build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 100).build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 200).build());
        final Set<Element> expected = Sets.newHashSet(new Entity.Builder().group(NON_AGG_ENTITY).vertex("vertex1").property("count", 1).build(), new Entity.Builder().group(NON_AGG_ENTITY).vertex("vertex1").property("count", 2).build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 3).build(), new Entity.Builder().group(ENTITY).vertex("vertex2").property("count", 10).build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 300).build());
        // when
        final CloseableIterable<Element> aggregatedElements = AggregatorUtil.ingestAggregate(elements, schema);
        // then
        assertElementEquals(expected, aggregatedElements);
    }

    @Test
    public void shouldIngestAggregateElementsWithGroupBy() {
        // given
        final Schema schema = Schema.fromJson(StreamUtil.openStreams(getClass(), "schema-groupby"));
        final List<Element> elements = Arrays.asList(new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 1).property("property2", "value1").property("visibility", "vis1").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 1).property("property2", "value1").property("visibility", "vis2").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 2).property("property2", "value1").property("visibility", "vis1").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 2).property("property2", "value2").property("visibility", "vis1").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 2).property("property2", "value2").property("visibility", "vis2").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 10).property("property2", "value2").property("visibility", "vis1").build(), new Entity.Builder().group(ENTITY).vertex("vertex2").property("count", 20).property("property2", "value10").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 100).property("property2", "value1").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 100).property("property2", "value1").property("visibility", "vis2").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 100).property("property2", "value1").property("visibility", "vis2").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 200).property("property2", "value1").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 1000).property("property2", "value2").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 2000).property("property2", "value2").property("visibility", "vis1").build());
        final Set<Element> expected = Sets.newHashSet(new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 3).property("property2", "value1").property("visibility", "vis1").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 1).property("property2", "value1").property("visibility", "vis2").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 12).property("property2", "value2").property("visibility", "vis1").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 2).property("property2", "value2").property("visibility", "vis2").build(), new Entity.Builder().group(ENTITY).vertex("vertex2").property("count", 20).property("property2", "value10").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 300).property("property2", "value1").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 200).property("property2", "value1").property("visibility", "vis2").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 3000).property("property2", "value2").property("visibility", "vis1").build());
        // when
        final CloseableIterable<Element> aggregatedElements = AggregatorUtil.ingestAggregate(elements, schema);
        // then
        assertElementEquals(expected, aggregatedElements);
    }

    @Test
    public void shouldThrowExceptionWhenQueryAggregatedIfSchemaIsNull() {
        // given
        final Schema schema = null;
        final View view = new View();
        // When / Then
        try {
            AggregatorUtil.queryAggregate(Collections.emptyList(), schema, view);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Schema"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenQueryAggregatedIfViewIsNull() {
        // given
        final Schema schema = new Schema();
        final View view = null;
        // When / Then
        try {
            AggregatorUtil.queryAggregate(Collections.emptyList(), schema, view);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("View"));
        }
    }

    @Test
    public void shouldQueryAggregateElementsWithGroupBy() {
        // given
        final Schema schema = Schema.fromJson(StreamUtil.openStreams(getClass(), "schema-groupby"));
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().groupBy().build()).entity(EDGE, new ViewElementDefinition.Builder().groupBy().build()).build();
        final List<Element> elements = Arrays.asList(new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 1).property("property2", "value1").property("visibility", "vis1").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 2).property("property2", "value1").property("visibility", "vis2").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 2).property("property2", "value2").property("visibility", "vis1").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 10).property("property2", "value2").property("visibility", "vis2").build(), new Entity.Builder().group(ENTITY).vertex("vertex2").property("count", 20).property("property2", "value10").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 100).property("property2", "value1").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 200).property("property2", "value1").property("visibility", "vis2").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 1000).property("property2", "value2").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 2000).property("property2", "value2").property("visibility", "vis2").build());
        final Set<Element> expected = Sets.newHashSet(new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 15).property("property2", "value1").property("visibility", "vis1").build(), new Entity.Builder().group(ENTITY).vertex("vertex2").property("count", 20).property("property2", "value10").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 3300).property("property2", "value1").property("visibility", "vis1").build());
        // when
        final CloseableIterable<Element> aggregatedElements = AggregatorUtil.queryAggregate(elements, schema, view);
        // then
        assertElementEquals(expected, aggregatedElements);
    }

    @Test
    public void shouldQueryAggregateElementsWithGroupByAndViewAggregator() {
        // given
        final Schema schema = Schema.fromJson(StreamUtil.openStreams(getClass(), "schema-groupby"));
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().groupBy().aggregator(new ElementAggregator.Builder().select("count").execute(new Product()).build()).build()).entity(EDGE, new ViewElementDefinition.Builder().groupBy().build()).build();
        final List<Element> elements = Arrays.asList(new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 1).property("property2", "value1").property("visibility", "vis1").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 2).property("property2", "value1").property("visibility", "vis2").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 2).property("property2", "value2").property("visibility", "vis1").build(), new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 10).property("property2", "value2").property("visibility", "vis2").build(), new Entity.Builder().group(ENTITY).vertex("vertex2").property("count", 20).property("property2", "value10").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 100).property("property2", "value1").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 200).property("property2", "value1").property("visibility", "vis2").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 1000).property("property2", "value2").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 2000).property("property2", "value2").property("visibility", "vis2").build());
        final Set<Element> expected = Sets.newHashSet(new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 40).property("property2", "value1").property("visibility", "vis1").build(), new Entity.Builder().group(ENTITY).vertex("vertex2").property("count", 20).property("property2", "value10").property("visibility", "vis1").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").property("count", 3300).property("property2", "value1").property("visibility", "vis1").build());
        // when
        final CloseableIterable<Element> aggregatedElements = AggregatorUtil.queryAggregate(elements, schema, view);
        // then
        assertElementEquals(expected, aggregatedElements);
    }

    @Test
    public void shouldQueryAggregateElementsWhenProvidedIterableCanOnlyBeConsumedOnce() {
        // given
        final Schema schema = Schema.fromJson(StreamUtil.openStreams(getClass(), "schema-groupby"));
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().groupBy().build()).entity(EDGE, new ViewElementDefinition.Builder().groupBy().build()).build();
        final List<Element> elements = Arrays.asList(new Entity.Builder().group(ENTITY).vertex("vertex1").property("count", 1).build(), new Entity.Builder().group(NON_AGG_ENTITY).vertex("vertex1").property("count", 2).build());
        final Iterable<Element> onlyConsumingOnceIterable = new Iterable<Element>() {
            private boolean firstTime = true;

            @Override
            public Iterator<Element> iterator() {
                if (firstTime) {
                    firstTime = false;
                    return elements.iterator();
                }
                throw new NoSuchElementException();
            }
        };
        // when
        final CloseableIterable<Element> aggregatedElements = AggregatorUtil.queryAggregate(onlyConsumingOnceIterable, schema, view);
        // then
        assertElementEquals(elements, aggregatedElements);
    }

    @Test
    public void shouldCreateIngestElementKeyUsingVertex() {
        // given
        final Schema schema = Schema.fromJson(StreamUtil.openStream(getClass(), "/schema/elements.json"));
        final List<Element> input = Arrays.asList(new Entity.Builder().group(ENTITY).vertex("vertex1").build(), new Entity.Builder().group(ENTITY).vertex("vertex2").build(), new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").build());
        // when
        final Function<Element, Element> fn = new AggregatorUtil.ToIngestElementKey(schema);
        // then
        Assert.assertEquals(new Entity.Builder().group(ENTITY).vertex("vertex1").build(), fn.apply(input.get(0)));
        Assert.assertEquals(new Entity.Builder().group(ENTITY).vertex("vertex2").build(), fn.apply(input.get(1)));
        Assert.assertEquals(new Edge.Builder().group(EDGE).source("vertex2").dest("vertex1").build(), fn.apply(input.get(2)));
        final Map<Element, List<Element>> results = input.stream().collect(Collectors.groupingBy(fn));
        final Map<Element, List<Element>> expected = new HashMap<>();
        expected.put(input.get(0), Lists.newArrayList(input.get(0)));
        expected.put(input.get(1), Lists.newArrayList(input.get(1)));
        expected.put(input.get(2), Lists.newArrayList(input.get(2)));
        Assert.assertEquals(expected, results);
    }

    @Test
    public void shouldCreateIngestElementKeyUsingGroup() {
        // given
        final Schema schema = createSchema();
        final List<Element> input = Arrays.asList(new Entity.Builder().group(ENTITY).vertex("vertex1").build(), new Entity.Builder().group(ENTITY_2).vertex("vertex1").build());
        // when
        final AggregatorUtil.ToIngestElementKey fn = new AggregatorUtil.ToIngestElementKey(schema);
        // then
        Assert.assertEquals(new Entity.Builder().group(ENTITY).vertex("vertex1").build(), fn.apply(input.get(0)));
        Assert.assertEquals(new Entity.Builder().group(ENTITY_2).vertex("vertex1").build(), fn.apply(input.get(1)));
        final Map<Element, List<Element>> results = input.stream().collect(Collectors.groupingBy(fn));
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(input.get(0), results.get(input.get(0)).get(0));
        Assert.assertEquals(input.get(1), results.get(input.get(1)).get(0));
    }

    @Test
    public void shouldCreateIngestElementKeyUsingGroupByProperties() {
        // given
        final Schema schema = Schema.fromJson(StreamUtil.openStreams(getClass(), "schema-groupby"));
        // when
        final Function<Element, Element> fn = new AggregatorUtil.ToIngestElementKey(schema);
        // then
        Assert.assertEquals(new Entity.Builder().group(ENTITY).vertex("vertex1").property("property2", "value2").property("property3", "value3").property("visibility", "vis1").build(), fn.apply(new Entity.Builder().group(ENTITY).vertex("vertex1").property("property1", "value1").property("property2", "value2").property("property3", "value3").property("visibility", "vis1").build()));
        Assert.assertEquals(new Edge.Builder().group(EDGE).source("vertex1").dest("vertex2").directed(true).property("property2", "value2").property("property3", "value3").property("visibility", "vis1").build(), fn.apply(new Edge.Builder().group(EDGE).source("vertex1").dest("vertex2").directed(true).property("property1", "value1").property("property2", "value2").property("property3", "value3").property("visibility", "vis1").build()));
    }

    @Test
    public void shouldCreateQueryElementKeyUsingViewGroupByProperties() {
        // given
        final Schema schema = Schema.fromJson(StreamUtil.openStreams(getClass(), "schema-groupby"));
        final View view = new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().groupBy("property2").build()).entity(EDGE, new ViewElementDefinition.Builder().groupBy("property2").build()).build();
        // when
        final Function<Element, Element> fn = new AggregatorUtil.ToQueryElementKey(schema, view);
        // then
        Assert.assertEquals(new Entity.Builder().group(ENTITY).vertex("vertex1").property("property2", "value2").build(), fn.apply(new Entity.Builder().group(ENTITY).vertex("vertex1").property("property1", "value1").property("property2", "value2").property("property3", "value3").property("visibility", "vis1").build()));
        Assert.assertEquals(new Edge.Builder().group(EDGE).source("vertex1").dest("vertex2").directed(true).property("property2", "value2").build(), fn.apply(new Edge.Builder().group(EDGE).source("vertex1").dest("vertex2").directed(true).property("property1", "value1").property("property2", "value2").property("property3", "value3").property("visibility", "vis1").build()));
    }

    @Test
    public void shouldThrowExceptionWhenCreateIngestElementKeyIfElementBelongsToGroupThatDoesntExistInSchema() {
        // given
        final Schema schema = createSchema();
        final Element element = new Entity.Builder().group("Unknown group").vertex("vertex1").property("Meaning of life", 42).build();
        // when
        final Function<Element, Element> fn = new AggregatorUtil.ToIngestElementKey(schema);
        // then
        try {
            fn.apply(element);
            Assert.fail("Exception expected");
        } catch (final RuntimeException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenCreateQueryElementKeyIfElementBelongsToGroupThatDoesntExistInSchema() {
        // given
        final Schema schema = createSchema();
        final Element element = new Entity.Builder().group("Unknown group").vertex("vertex1").property("Meaning of life", 42).build();
        // when
        final Function<Element, Element> fn = new AggregatorUtil.ToQueryElementKey(schema, new View());
        // then
        try {
            fn.apply(element);
            Assert.fail("Exception expected");
        } catch (final RuntimeException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldGroupElementsWithSameIngestElementKey() {
        // given
        final Schema schema = createSchema();
        // when
        final List<Element> input = Arrays.asList(new Entity.Builder().group(ENTITY_2).vertex("vertex1").property(PROP_1, "control value").property(PROP_3, "unused").build(), new Entity.Builder().group(ENTITY_2).vertex("vertex1").property(PROP_1, "control value").property(PROP_3, "also unused in function").build());
        final Map<Element, List<Element>> results = input.stream().collect(Collectors.groupingBy(new AggregatorUtil.ToIngestElementKey(schema)));
        // then
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(input, results.get(new Entity.Builder().group(ENTITY_2).vertex("vertex1").property(PROP_1, "control value").build()));
    }
}

