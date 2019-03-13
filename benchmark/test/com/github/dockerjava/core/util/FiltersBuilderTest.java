package com.github.dockerjava.core.util;


import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vincent Latombe <vincent@latombe.net>
 */
public class FiltersBuilderTest {
    @Test
    public void newFiltersShouldBeEquals() {
        Assert.assertEquals(new FiltersBuilder(), new FiltersBuilder());
    }

    @Test
    public void newFiltersShouldHaveEqualHashcode() {
        Assert.assertEquals(new FiltersBuilder().hashCode(), new FiltersBuilder().hashCode());
    }

    @Test
    public void filtersWithEqualContentShouldBeEquals() {
        Assert.assertEquals(new FiltersBuilder().withContainers("foo"), new FiltersBuilder().withContainers("foo"));
        Assert.assertEquals(new FiltersBuilder().withLabels("alpha=val"), new FiltersBuilder().withLabels("alpha=val"));
    }

    @Test
    public void filtersWithEqualContentShouldHaveEqualHashcode() {
        Assert.assertEquals(new FiltersBuilder().withContainers("foo").hashCode(), new FiltersBuilder().withContainers("foo").hashCode());
        Assert.assertEquals(new FiltersBuilder().withLabels("alpha=val").hashCode(), new FiltersBuilder().withLabels("alpha=val").hashCode());
    }

    @Test
    public void withLabelsMapShouldBeEqualsToVarargs() {
        Map<String, String> map = Maps.newHashMap();
        map.put("alpha", "val");
        Assert.assertEquals(new FiltersBuilder().withLabels("alpha=val"), new FiltersBuilder().withLabels(map));
        map = Maps.newHashMap();
        map.put("alpha", "val");
        map.put("beta", "val1");
        Assert.assertEquals(new FiltersBuilder().withLabels("alpha=val", "beta=val1"), new FiltersBuilder().withLabels(map));
    }

    @Test
    public void filtersWithDifferentContentShouldntBeEquals() {
        Assert.assertNotEquals(new FiltersBuilder().withContainers("foo"), new FiltersBuilder().withContainers("bar"));
    }

    @Test
    public void filtersWithDifferentContentShouldntHaveEqualHashcode() {
        Assert.assertNotEquals(new FiltersBuilder().withContainers("foo").hashCode(), new FiltersBuilder().withContainers("bar").hashCode());
    }
}

