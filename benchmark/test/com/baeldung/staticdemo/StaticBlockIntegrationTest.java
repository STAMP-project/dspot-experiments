package com.baeldung.staticdemo;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class StaticBlockIntegrationTest {
    @Test
    public void whenAddedListElementsThroughStaticBlock_thenEnsureCorrectOrder() {
        List<String> actualList = StaticBlock.getRanks();
        Assert.assertThat(actualList, contains("Lieutenant", "Captain", "Major", "Colonel", "General"));
    }
}

