package org.drools.modelcompiler.builder.generator;


import org.junit.Assert;
import org.junit.Test;


public class ConsequenceTest {
    @Test
    public void containsWordTest() throws Exception {
        Assert.assertFalse(Consequence.containsWord("$cheesery", "results.add($cheeseryResult);\n"));
        Assert.assertTrue(Consequence.containsWord("$cheeseryResult", "results.add($cheeseryResult);\n"));
        Assert.assertFalse(Consequence.containsWord("cheesery", "results.add($cheesery);\n"));
    }
}

