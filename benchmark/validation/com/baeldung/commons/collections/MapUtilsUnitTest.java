package com.baeldung.commons.collections;


import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.TransformerUtils;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MapUtilsUnitTest {
    private String[][] color2DArray = new String[][]{ new String[]{ "RED", "#FF0000" }, new String[]{ "GREEN", "#00FF00" }, new String[]{ "BLUE", "#0000FF" } };

    private String[] color1DArray = new String[]{ "RED", "#FF0000", "GREEN", "#00FF00", "BLUE", "#0000FF" };

    private Map<String, String> colorMap;

    @Test
    public void whenCreateMapFrom2DArray_theMapIsCreated() {
        this.colorMap = MapUtils.putAll(new HashMap<String, String>(), this.color2DArray);
        Assert.assertThat(this.colorMap, Matchers.is(aMapWithSize(this.color2DArray.length)));
        Assert.assertThat(this.colorMap, hasEntry("RED", "#FF0000"));
        Assert.assertThat(this.colorMap, hasEntry("GREEN", "#00FF00"));
        Assert.assertThat(this.colorMap, hasEntry("BLUE", "#0000FF"));
    }

    @Test
    public void whenCreateMapFrom1DArray_theMapIsCreated() {
        this.colorMap = MapUtils.putAll(new HashMap<String, String>(), this.color1DArray);
        Assert.assertThat(this.colorMap, Matchers.is(aMapWithSize(((this.color1DArray.length) / 2))));
        Assert.assertThat(this.colorMap, hasEntry("RED", "#FF0000"));
        Assert.assertThat(this.colorMap, hasEntry("GREEN", "#00FF00"));
        Assert.assertThat(this.colorMap, hasEntry("BLUE", "#0000FF"));
    }

    @Test
    public void whenVerbosePrintMap_thenMustPrintFormattedMap() {
        MapUtils.verbosePrint(System.out, "Optional Label", this.colorMap);
    }

    @Test
    public void whenGetKeyNotPresent_thenMustReturnDefaultValue() {
        String defaultColorStr = "COLOR_NOT_FOUND";
        String color = MapUtils.getString(this.colorMap, "BLACK", defaultColorStr);
        Assert.assertEquals(color, defaultColorStr);
    }

    @Test
    public void whenGetOnNullMap_thenMustReturnDefaultValue() {
        String defaultColorStr = "COLOR_NOT_FOUND";
        String color = MapUtils.getString(null, "RED", defaultColorStr);
        Assert.assertEquals(color, defaultColorStr);
    }

    @Test
    public void whenInvertMap_thenMustReturnInvertedMap() {
        Map<String, String> invColorMap = MapUtils.invertMap(this.colorMap);
        int size = invColorMap.size();
        Assertions.assertThat(invColorMap).hasSameSizeAs(colorMap).containsKeys(this.colorMap.values().toArray(new String[size])).containsValues(this.colorMap.keySet().toArray(new String[size]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCreateFixedSizedMapAndAdd_thenMustThrowException() {
        Map<String, String> rgbMap = MapUtils.fixedSizeMap(MapUtils.putAll(new HashMap<String, String>(), this.color1DArray));
        rgbMap.put("ORANGE", "#FFA500");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenAddDuplicateToUniqueValuesPredicateMap_thenMustThrowException() {
        Map<String, String> uniqValuesMap = MapUtils.predicatedMap(this.colorMap, null, PredicateUtils.uniquePredicate());
        uniqValuesMap.put("NEW_RED", "#FF0000");
    }

    @Test
    public void whenCreateLazyMap_theMapIsCreated() {
        Map<Integer, String> intStrMap = MapUtils.lazyMap(new HashMap<Integer, String>(), TransformerUtils.stringValueTransformer());
        Assert.assertThat(intStrMap, Matchers.is(anEmptyMap()));
        intStrMap.get(1);
        intStrMap.get(2);
        intStrMap.get(3);
        Assert.assertThat(intStrMap, Matchers.is(aMapWithSize(3)));
    }
}

