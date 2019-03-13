package com.github.jknack.handlebars;


import Handlebars.Utils;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;


public class Issue259 {
    @Test
    public void smallBigDecimal() throws IOException {
        Assert.assertFalse(Utils.isEmpty(new BigDecimal("0.01")));
    }

    @Test
    public void zeroBigDecimal() throws IOException {
        Assert.assertTrue(Utils.isEmpty(new BigDecimal("0.0000")));
    }
}

