package de.westnordost.streetcomplete.data.osm.tql;


import Element.Type.NODE;
import Element.Type.RELATION;
import Element.Type.WAY;
import ElementsTypeFilter.NODES;
import ElementsTypeFilter.RELATIONS;
import ElementsTypeFilter.WAYS;
import de.westnordost.osmapi.map.data.Element;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TagFilterExpressionTest {
    // Tests for toOverpassQLString are in FiltersParserTest
    private Element node = createElement(NODE);

    private Element way = createElement(WAY);

    private Element relation = createElement(RELATION);

    @Test
    public void matchesNodes() {
        TagFilterExpression expr = createMatchExpression(NODES);
        Assert.assertTrue(expr.matches(node));
        Assert.assertFalse(expr.matches(way));
        Assert.assertFalse(expr.matches(relation));
    }

    @Test
    public void matchesWays() {
        TagFilterExpression expr = createMatchExpression(WAYS);
        Assert.assertFalse(expr.matches(node));
        Assert.assertTrue(expr.matches(way));
        Assert.assertFalse(expr.matches(relation));
    }

    @Test
    public void matchesRelations() {
        TagFilterExpression expr = createMatchExpression(RELATIONS);
        Assert.assertFalse(expr.matches(node));
        Assert.assertFalse(expr.matches(way));
        Assert.assertTrue(expr.matches(relation));
    }

    @Test
    public void matchesElements() {
        BooleanExpression booleanExpression = Mockito.mock(BooleanExpression.class);
        Mockito.when(booleanExpression.matches(ArgumentMatchers.any())).thenReturn(true);
        TagFilterExpression expr = new TagFilterExpression(Arrays.asList(ElementsTypeFilter.values()), booleanExpression);
        Assert.assertTrue(expr.matches(node));
        Assert.assertTrue(expr.matches(way));
        Assert.assertTrue(expr.matches(relation));
    }
}

