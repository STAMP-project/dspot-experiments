package dev.morphia.issue646;


import dev.morphia.TestBase;
import org.junit.Assert;
import org.junit.Test;


public class SubClassConvertersTest extends TestBase {
    @Test
    public final void convertersFoundFromSubClass() {
        getMorphia().map(SubClass.class);
        Assert.assertTrue("Converters annotated in super class are not present.", getMorphia().getMapper().getConverters().isRegistered(SquareConverter.class));
        Assert.assertTrue("Converters annotated in sub class are not present.", getMorphia().getMapper().getConverters().isRegistered(TriangleConverter.class));
    }
}

