package com.baeldung;


import com.baeldung.decorator.ChristmasTree;
import com.baeldung.decorator.ChristmasTreeImpl;
import com.baeldung.decorator.Garland;
import org.junit.Assert;
import org.junit.Test;


public class DecoratorPatternIntegrationTest {
    @Test
    public void givenDecoratorPattern_WhenDecoratorsInjectedAtRuntime_thenConfigSuccess() {
        ChristmasTree tree1 = new Garland(new ChristmasTreeImpl());
        Assert.assertEquals(tree1.decorate(), "Christmas tree with Garland");
        ChristmasTree tree2 = new com.baeldung.decorator.BubbleLights(new Garland(new Garland(new ChristmasTreeImpl())));
        Assert.assertEquals(tree2.decorate(), "Christmas tree with Garland with Garland with Bubble Lights");
    }
}

