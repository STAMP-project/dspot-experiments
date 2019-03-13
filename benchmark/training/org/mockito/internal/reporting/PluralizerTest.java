/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.reporting;


import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class PluralizerTest extends TestBase {
    @Test
    public void pluralizes_number() {
        Assert.assertEquals("0 times", Pluralizer.pluralize(0));
        Assert.assertEquals("1 time", Pluralizer.pluralize(1));
        Assert.assertEquals("2 times", Pluralizer.pluralize(2));
        Assert.assertEquals("20 times", Pluralizer.pluralize(20));
    }

    @Test
    public void pluralizes_interactions() {
        Assert.assertEquals("were exactly 0 interactions", Pluralizer.were_exactly_x_interactions(0));
        Assert.assertEquals("was exactly 1 interaction", Pluralizer.were_exactly_x_interactions(1));
        Assert.assertEquals("were exactly 100 interactions", Pluralizer.were_exactly_x_interactions(100));
    }
}

