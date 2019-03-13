package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class P27Test {
    @Test
    public void shouldGroupIntoThreeGroupsOfSize_2_3_and_4() throws Exception {
        List<String> input = Stream.of("aldo", "beat", "carla", "david", "evi", "flip", "gary", "hugo", "ida").collect(Collectors.toList());
        List<List<List<String>>> groups = P27.group3(input);
        Assert.assertThat(groups, hasSize(1260));
    }

    @Test
    public void shouldGroupIntoThreeGroupsOfSize_2_2_and_5() throws Exception {
        List<String> input = Stream.of("aldo", "beat", "carla", "david", "evi", "flip", "gary", "hugo", "ida").collect(Collectors.toList());
        List<List<List<String>>> groups = P27.group(input, Stream.of(2, 2, 5).collect(Collectors.toList()));
        Assert.assertThat(groups, hasSize(756));
    }

    @Test
    public void shouldGroupIntoThreeGroupsOfSize_1_and_2_part2() throws Exception {
        List<String> input = Stream.of("a", "b", "c").collect(Collectors.toList());
        List<List<List<String>>> groups = P27.group(input, Stream.of(1, 2).collect(Collectors.toList()));
        Assert.assertThat(groups, hasSize(3));
    }

    @Test
    public void shouldGroupIntoThreeGroupsOfSize_2_3_and_4_part2() throws Exception {
        List<String> input = Stream.of("aldo", "beat", "carla", "david", "evi", "flip", "gary", "hugo", "ida").collect(Collectors.toList());
        List<List<List<String>>> groups = P27.group(input, Stream.of(2, 3, 4).collect(Collectors.toList()));
        Assert.assertThat(groups, hasSize(1260));
    }
}

