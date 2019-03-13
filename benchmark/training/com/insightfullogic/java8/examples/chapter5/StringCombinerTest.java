package com.insightfullogic.java8.examples.chapter5;


import org.junit.Assert;
import org.junit.Test;


public class StringCombinerTest {
    private StringCombiner combiner;

    @Test
    public void add() throws Exception {
        Assert.assertEquals("[A, B, C, D]", combiner.toString());
    }

    @Test
    public void mergeWithOther() throws Exception {
        StringCombiner other = new StringCombiner(", ", "[", "]");
        other.add("E").add("F").add("G");
        this.combiner.merge(other);
        Assert.assertEquals("[A, B, C, D, E, F, G]", this.combiner.toString());
    }

    @Test
    public void mergeWithEmpty() {
        this.combiner.merge(new StringCombiner(", ", "[", "]"));
        Assert.assertEquals("[A, B, C, D]", this.combiner.toString());
    }

    @Test
    public void mergeSelf() throws Exception {
        Assert.assertEquals("[A, B, C, D]", this.combiner.merge(this.combiner).toString());
    }

    @Test
    public void twiceCallToString() throws Exception {
        Assert.assertEquals("[A, B, C, D]", this.combiner.toString());
        Assert.assertEquals("[A, B, C, D]", this.combiner.toString());
    }
}

