package com.baeldung.convertlisttomap;


import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ConvertListWithDiplicatedIdToMapServiceUnitTest {
    List<Animal> duplicatedIdList;

    private ConvertListToMapService convertListService = new ConvertListToMapService();

    @Test
    public void givenADupIdList_whenConvertBeforeJava8_thenReturnMapWithRewrittenElement() {
        Map<Integer, Animal> map = convertListService.convertListBeforeJava8(duplicatedIdList);
        MatcherAssert.assertThat(map.values(), Matchers.hasSize(4));
        MatcherAssert.assertThat(map.values(), Matchers.hasItem(duplicatedIdList.get(4)));
    }

    @Test
    public void givenADupIdList_whenConvertWithApacheCommons_thenReturnMapWithRewrittenElement() {
        Map<Integer, Animal> map = convertListService.convertListWithApacheCommons(duplicatedIdList);
        MatcherAssert.assertThat(map.values(), Matchers.hasSize(4));
        MatcherAssert.assertThat(map.values(), Matchers.hasItem(duplicatedIdList.get(4)));
    }

    @Test(expected = IllegalStateException.class)
    public void givenADupIdList_whenConvertAfterJava8_thenException() {
        convertListService.convertListAfterJava8(duplicatedIdList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenADupIdList_whenConvertWithGuava_thenException() {
        convertListService.convertListWithGuava(duplicatedIdList);
    }
}

