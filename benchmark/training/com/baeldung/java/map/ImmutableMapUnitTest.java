package com.baeldung.java.map;


import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ImmutableMapUnitTest {
    @Test
    public void whenCollectionsUnModifiableMapMethod_thenOriginalCollectionChangesReflectInUnmodifiableMap() {
        Map<String, String> mutableMap = new HashMap<>();
        mutableMap.put("USA", "North America");
        Map<String, String> unmodifiableMap = Collections.unmodifiableMap(mutableMap);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> unmodifiableMap.put("Canada", "North America"));
        mutableMap.remove("USA");
        Assertions.assertFalse(unmodifiableMap.containsKey("USA"));
        mutableMap.put("Mexico", "North America");
        Assertions.assertTrue(unmodifiableMap.containsKey("Mexico"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void whenGuavaImmutableMapFromCopyOfMethod_thenOriginalCollectionChangesDoNotReflectInImmutableMap() {
        Map<String, String> mutableMap = new HashMap<>();
        mutableMap.put("USA", "North America");
        ImmutableMap<String, String> immutableMap = ImmutableMap.copyOf(mutableMap);
        Assertions.assertTrue(immutableMap.containsKey("USA"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> immutableMap.put("Canada", "North America"));
        mutableMap.remove("USA");
        Assertions.assertTrue(immutableMap.containsKey("USA"));
        mutableMap.put("Mexico", "North America");
        Assertions.assertFalse(immutableMap.containsKey("Mexico"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void whenGuavaImmutableMapFromBuilderMethod_thenOriginalCollectionChangesDoNotReflectInImmutableMap() {
        Map<String, String> mutableMap = new HashMap<>();
        mutableMap.put("USA", "North America");
        ImmutableMap<String, String> immutableMap = ImmutableMap.<String, String>builder().putAll(mutableMap).put("Costa Rica", "North America").build();
        Assertions.assertTrue(immutableMap.containsKey("USA"));
        Assertions.assertTrue(immutableMap.containsKey("Costa Rica"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> immutableMap.put("Canada", "North America"));
        mutableMap.remove("USA");
        Assertions.assertTrue(immutableMap.containsKey("USA"));
        mutableMap.put("Mexico", "North America");
        Assertions.assertFalse(immutableMap.containsKey("Mexico"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void whenGuavaImmutableMapFromOfMethod_thenOriginalCollectionChangesDoNotReflectInImmutableMap() {
        ImmutableMap<String, String> immutableMap = ImmutableMap.of("USA", "North America", "Costa Rica", "North America");
        Assertions.assertTrue(immutableMap.containsKey("USA"));
        Assertions.assertTrue(immutableMap.containsKey("Costa Rica"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> immutableMap.put("Canada", "North America"));
    }
}

