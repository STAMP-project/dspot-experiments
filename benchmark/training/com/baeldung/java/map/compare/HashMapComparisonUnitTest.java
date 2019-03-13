package com.baeldung.java.map.compare;


import com.google.common.base.Equivalence;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class HashMapComparisonUnitTest {
    Map<String, String> asiaCapital1;

    Map<String, String> asiaCapital2;

    Map<String, String> asiaCapital3;

    Map<String, String[]> asiaCity1;

    Map<String, String[]> asiaCity2;

    Map<String, String[]> asiaCity3;

    @Test
    public void whenCompareTwoHashMapsUsingEquals_thenSuccess() {
        Assert.assertTrue(asiaCapital1.equals(asiaCapital2));
        Assert.assertFalse(asiaCapital1.equals(asiaCapital3));
    }

    @Test
    public void whenCompareTwoHashMapsWithArrayValuesUsingEquals_thenFail() {
        Assert.assertFalse(asiaCity1.equals(asiaCity2));
    }

    @Test
    public void whenCompareTwoHashMapsUsingStreamAPI_thenSuccess() {
        Assert.assertTrue(areEqual(asiaCapital1, asiaCapital2));
        Assert.assertFalse(areEqual(asiaCapital1, asiaCapital3));
    }

    @Test
    public void whenCompareTwoHashMapsWithArrayValuesUsingStreamAPI_thenSuccess() {
        Assert.assertTrue(areEqualWithArrayValue(asiaCity1, asiaCity2));
        Assert.assertFalse(areEqualWithArrayValue(asiaCity1, asiaCity3));
    }

    @Test
    public void whenCompareTwoHashMapKeys_thenSuccess() {
        Assert.assertTrue(asiaCapital1.keySet().equals(asiaCapital2.keySet()));
        Assert.assertFalse(asiaCapital1.keySet().equals(asiaCapital3.keySet()));
    }

    @Test
    public void whenCompareTwoHashMapKeyValuesUsingStreamAPI_thenSuccess() {
        Map<String, String> asiaCapital3 = new HashMap<String, String>();
        asiaCapital3.put("Japan", "Tokyo");
        asiaCapital3.put("South Korea", "Seoul");
        asiaCapital3.put("China", "Beijing");
        Map<String, String> asiaCapital4 = new HashMap<String, String>();
        asiaCapital4.put("South Korea", "Seoul");
        asiaCapital4.put("Japan", "Osaka");
        asiaCapital4.put("China", "Beijing");
        Map<String, Boolean> result = areEqualKeyValues(asiaCapital3, asiaCapital4);
        Assert.assertEquals(3, result.size());
        MatcherAssert.assertThat(result, hasEntry("Japan", false));
        MatcherAssert.assertThat(result, hasEntry("South Korea", true));
        MatcherAssert.assertThat(result, hasEntry("China", true));
    }

    @Test
    public void givenDifferentMaps_whenGetDiffUsingGuava_thenSuccess() {
        Map<String, String> asia1 = new HashMap<String, String>();
        asia1.put("Japan", "Tokyo");
        asia1.put("South Korea", "Seoul");
        asia1.put("India", "New Delhi");
        Map<String, String> asia2 = new HashMap<String, String>();
        asia2.put("Japan", "Tokyo");
        asia2.put("China", "Beijing");
        asia2.put("India", "Delhi");
        MapDifference<String, String> diff = Maps.difference(asia1, asia2);
        Map<String, MapDifference.ValueDifference<String>> entriesDiffering = diff.entriesDiffering();
        Assert.assertFalse(diff.areEqual());
        Assert.assertEquals(1, entriesDiffering.size());
        MatcherAssert.assertThat(entriesDiffering, hasKey("India"));
        Assert.assertEquals("New Delhi", entriesDiffering.get("India").leftValue());
        Assert.assertEquals("Delhi", entriesDiffering.get("India").rightValue());
    }

    @Test
    public void givenDifferentMaps_whenGetEntriesOnOneSideUsingGuava_thenSuccess() {
        Map<String, String> asia1 = new HashMap<String, String>();
        asia1.put("Japan", "Tokyo");
        asia1.put("South Korea", "Seoul");
        asia1.put("India", "New Delhi");
        Map<String, String> asia2 = new HashMap<String, String>();
        asia2.put("Japan", "Tokyo");
        asia2.put("China", "Beijing");
        asia2.put("India", "Delhi");
        MapDifference<String, String> diff = Maps.difference(asia1, asia2);
        Map<String, String> entriesOnlyOnRight = diff.entriesOnlyOnRight();
        Map<String, String> entriesOnlyOnLeft = diff.entriesOnlyOnLeft();
        Assert.assertEquals(1, entriesOnlyOnRight.size());
        MatcherAssert.assertThat(entriesOnlyOnRight, hasEntry("China", "Beijing"));
        Assert.assertEquals(1, entriesOnlyOnLeft.size());
        MatcherAssert.assertThat(entriesOnlyOnLeft, hasEntry("South Korea", "Seoul"));
    }

    @Test
    public void givenDifferentMaps_whenGetCommonEntriesUsingGuava_thenSuccess() {
        Map<String, String> asia1 = new HashMap<String, String>();
        asia1.put("Japan", "Tokyo");
        asia1.put("South Korea", "Seoul");
        asia1.put("India", "New Delhi");
        Map<String, String> asia2 = new HashMap<String, String>();
        asia2.put("Japan", "Tokyo");
        asia2.put("China", "Beijing");
        asia2.put("India", "Delhi");
        MapDifference<String, String> diff = Maps.difference(asia1, asia2);
        Map<String, String> entriesInCommon = diff.entriesInCommon();
        Assert.assertEquals(1, entriesInCommon.size());
        MatcherAssert.assertThat(entriesInCommon, hasEntry("Japan", "Tokyo"));
    }

    @Test
    public void givenSimilarMapsWithArrayValue_whenCompareUsingGuava_thenFail() {
        MapDifference<String, String[]> diff = Maps.difference(asiaCity1, asiaCity2);
        Assert.assertFalse(diff.areEqual());
    }

    @Test
    public void givenSimilarMapsWithArrayValue_whenCompareUsingGuavaEquivalence_thenSuccess() {
        Equivalence<String[]> eq = new Equivalence<String[]>() {
            @Override
            protected boolean doEquivalent(String[] a, String[] b) {
                return Arrays.equals(a, b);
            }

            @Override
            protected int doHash(String[] value) {
                return value.hashCode();
            }
        };
        MapDifference<String, String[]> diff = Maps.difference(asiaCity1, asiaCity2, eq);
        Assert.assertTrue(diff.areEqual());
        diff = Maps.difference(asiaCity1, asiaCity3, eq);
        Assert.assertFalse(diff.areEqual());
    }
}

