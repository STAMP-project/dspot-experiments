package com.shekhargulati.ninetynine_problems._03_logic_and_codes;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P46Test {
    @Test
    public void shouldGenerateTruthTable() throws Exception {
        String table = P46.table(( a, b) -> and(a, or(a, b)));
        String result = "A          B          result\n" + ((("true       true       true\n" + "true       false      true\n") + "false      true       false\n") + "false      false      false");
        Assert.assertThat(table, CoreMatchers.is(CoreMatchers.equalTo(result)));
    }
}

