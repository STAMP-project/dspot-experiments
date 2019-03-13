package com.baeldung.enummap;


import java.util.AbstractMap;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


public class EnumMapUnitTest {
    public enum DayOfWeek {

        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY;}

    @Test
    public void whenContructedWithEnumType_ThenOnlyAcceptThatAsKey() {
        Map dayMap = new EnumMap<>(EnumMapUnitTest.DayOfWeek.class);
        assertThatCode(() -> dayMap.put(TimeUnit.NANOSECONDS, "NANOSECONDS")).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void whenConstructedWithEnumMap_ThenSameKeyTypeAndInitialMappings() {
        EnumMap<EnumMapUnitTest.DayOfWeek, String> activityMap = new EnumMap<>(EnumMapUnitTest.DayOfWeek.class);
        activityMap.put(EnumMapUnitTest.DayOfWeek.MONDAY, "Soccer");
        activityMap.put(EnumMapUnitTest.DayOfWeek.TUESDAY, "Basketball");
        EnumMap<EnumMapUnitTest.DayOfWeek, String> activityMapCopy = new EnumMap<>(activityMap);
        assertThat(activityMapCopy.size()).isEqualTo(2);
        assertThat(activityMapCopy.get(EnumMapUnitTest.DayOfWeek.MONDAY)).isEqualTo("Soccer");
        assertThat(activityMapCopy.get(EnumMapUnitTest.DayOfWeek.TUESDAY)).isEqualTo("Basketball");
    }

    @Test
    public void givenEmptyMap_whenConstructedWithMap_ThenException() {
        HashMap ordinaryMap = new HashMap();
        assertThatCode(() -> new EnumMap(ordinaryMap)).isInstanceOf(IllegalArgumentException.class).hasMessage("Specified map is empty");
    }

    @Test
    public void givenMapWithEntries_whenConstructedWithMap_ThenSucceed() {
        HashMap<EnumMapUnitTest.DayOfWeek, String> ordinaryMap = new HashMap<>();
        ordinaryMap.put(EnumMapUnitTest.DayOfWeek.MONDAY, "Soccer");
        ordinaryMap.put(EnumMapUnitTest.DayOfWeek.TUESDAY, "Basketball");
        EnumMap<EnumMapUnitTest.DayOfWeek, String> enumMap = new EnumMap<>(ordinaryMap);
        assertThat(enumMap.size()).isEqualTo(2);
        assertThat(enumMap.get(EnumMapUnitTest.DayOfWeek.MONDAY)).isEqualTo("Soccer");
        assertThat(enumMap.get(EnumMapUnitTest.DayOfWeek.TUESDAY)).isEqualTo("Basketball");
    }

    @Test
    public void givenMapWithMultiTypeEntries_whenConstructedWithMap_ThenException() {
        HashMap<Enum, String> ordinaryMap = new HashMap<>();
        ordinaryMap.put(EnumMapUnitTest.DayOfWeek.MONDAY, "Soccer");
        ordinaryMap.put(TimeUnit.MILLISECONDS, "Other enum type");
        assertThatCode(() -> new EnumMap(ordinaryMap)).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void whenPut_thenGet() {
        Map<EnumMapUnitTest.DayOfWeek, String> activityMap = new EnumMap(EnumMapUnitTest.DayOfWeek.class);
        activityMap.put(EnumMapUnitTest.DayOfWeek.WEDNESDAY, "Hiking");
        activityMap.put(EnumMapUnitTest.DayOfWeek.THURSDAY, null);
        assertThat(activityMap.get(EnumMapUnitTest.DayOfWeek.WEDNESDAY)).isEqualTo("Hiking");
        assertThat(activityMap.get(EnumMapUnitTest.DayOfWeek.THURSDAY)).isNull();
    }

    @Test
    public void givenMapping_whenContains_thenTrue() {
        EnumMap<EnumMapUnitTest.DayOfWeek, String> activityMap = new EnumMap(EnumMapUnitTest.DayOfWeek.class);
        assertThat(activityMap.containsKey(EnumMapUnitTest.DayOfWeek.WEDNESDAY)).isFalse();
        assertThat(activityMap.containsValue("Hiking")).isFalse();
        activityMap.put(EnumMapUnitTest.DayOfWeek.WEDNESDAY, "Hiking");
        assertThat(activityMap.containsKey(EnumMapUnitTest.DayOfWeek.WEDNESDAY)).isTrue();
        assertThat(activityMap.containsValue("Hiking")).isTrue();
        assertThat(activityMap.containsKey(EnumMapUnitTest.DayOfWeek.SATURDAY)).isFalse();
        assertThat(activityMap.containsValue(null)).isFalse();
        activityMap.put(EnumMapUnitTest.DayOfWeek.SATURDAY, null);
        assertThat(activityMap.containsKey(EnumMapUnitTest.DayOfWeek.SATURDAY)).isTrue();
        assertThat(activityMap.containsValue(null)).isTrue();
    }

    @Test
    public void whenRemove_thenRemoved() {
        EnumMap<EnumMapUnitTest.DayOfWeek, String> activityMap = new EnumMap(EnumMapUnitTest.DayOfWeek.class);
        activityMap.put(EnumMapUnitTest.DayOfWeek.MONDAY, "Soccer");
        assertThat(activityMap.remove(EnumMapUnitTest.DayOfWeek.MONDAY)).isEqualTo("Soccer");
        assertThat(activityMap.containsKey(EnumMapUnitTest.DayOfWeek.MONDAY)).isFalse();
        activityMap.put(EnumMapUnitTest.DayOfWeek.MONDAY, "Soccer");
        assertThat(activityMap.remove(EnumMapUnitTest.DayOfWeek.MONDAY, "Hiking")).isEqualTo(false);
        assertThat(activityMap.remove(EnumMapUnitTest.DayOfWeek.MONDAY, "Soccer")).isEqualTo(true);
    }

    @Test
    public void whenSubView_thenSubViewOrdered() {
        EnumMap<EnumMapUnitTest.DayOfWeek, String> activityMap = new EnumMap(EnumMapUnitTest.DayOfWeek.class);
        activityMap.put(EnumMapUnitTest.DayOfWeek.THURSDAY, "Karate");
        activityMap.put(EnumMapUnitTest.DayOfWeek.WEDNESDAY, "Hiking");
        activityMap.put(EnumMapUnitTest.DayOfWeek.MONDAY, "Soccer");
        Collection<String> values = activityMap.values();
        assertThat(values).containsExactly("Soccer", "Hiking", "Karate");
        Set<EnumMapUnitTest.DayOfWeek> keys = activityMap.keySet();
        assertThat(keys).containsExactly(EnumMapUnitTest.DayOfWeek.MONDAY, EnumMapUnitTest.DayOfWeek.WEDNESDAY, EnumMapUnitTest.DayOfWeek.THURSDAY);
        assertThat(activityMap.entrySet()).containsExactly(new AbstractMap.SimpleEntry(EnumMapUnitTest.DayOfWeek.MONDAY, "Soccer"), new AbstractMap.SimpleEntry(EnumMapUnitTest.DayOfWeek.WEDNESDAY, "Hiking"), new AbstractMap.SimpleEntry(EnumMapUnitTest.DayOfWeek.THURSDAY, "Karate"));
    }

    @Test
    public void givenSubView_whenChange_thenReflected() {
        EnumMap<EnumMapUnitTest.DayOfWeek, String> activityMap = new EnumMap(EnumMapUnitTest.DayOfWeek.class);
        activityMap.put(EnumMapUnitTest.DayOfWeek.THURSDAY, "Karate");
        activityMap.put(EnumMapUnitTest.DayOfWeek.WEDNESDAY, "Hiking");
        activityMap.put(EnumMapUnitTest.DayOfWeek.MONDAY, "Soccer");
        Collection<String> values = activityMap.values();
        assertThat(values).containsExactly("Soccer", "Hiking", "Karate");
        activityMap.put(EnumMapUnitTest.DayOfWeek.TUESDAY, "Basketball");
        assertThat(values).containsExactly("Soccer", "Basketball", "Hiking", "Karate");
        values.remove("Hiking");
        assertThat(activityMap.containsKey(EnumMapUnitTest.DayOfWeek.WEDNESDAY)).isFalse();
        assertThat(activityMap.size()).isEqualTo(3);
    }
}

