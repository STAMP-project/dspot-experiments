package com.baeldung.convertlisttomap;


import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ConvertListToMapServiceUnitTest {
    List<Animal> list;

    private ConvertListToMapService convertListService;

    @Test
    public void givenAList_whenConvertBeforeJava8_thenReturnMapWithTheSameElements() {
        Map<Integer, Animal> map = convertListService.convertListBeforeJava8(list);
        MatcherAssert.assertThat(map.values(), Matchers.containsInAnyOrder(list.toArray()));
    }

    @Test
    public void givenAList_whenConvertAfterJava8_thenReturnMapWithTheSameElements() {
        Map<Integer, Animal> map = convertListService.convertListAfterJava8(list);
        MatcherAssert.assertThat(map.values(), Matchers.containsInAnyOrder(list.toArray()));
    }

    @Test
    public void givenAList_whenConvertWithGuava_thenReturnMapWithTheSameElements() {
        Map<Integer, Animal> map = convertListService.convertListWithGuava(list);
        MatcherAssert.assertThat(map.values(), Matchers.containsInAnyOrder(list.toArray()));
    }

    @Test
    public void givenAList_whenConvertWithApacheCommons_thenReturnMapWithTheSameElements() {
        Map<Integer, Animal> map = convertListService.convertListWithApacheCommons(list);
        MatcherAssert.assertThat(map.values(), Matchers.containsInAnyOrder(list.toArray()));
    }
}

