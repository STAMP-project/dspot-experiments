package com.github.jknack.handlebars.issues;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class Hbs531 extends v4Test {
    @Test
    public void dynamicPartialLookupWithADefaultParameterDoesn_tWork() throws IOException {
        shouldCompileTo("{{#each this}} {{> (lookup profession 'name') this.person}}<br /> {{/each}}", v4Test.$("hash", Arrays.asList(v4Test.$("firstName", "John", "lastName", "Foe", "profession", "developer"), v4Test.$("firstName", "James", "lastName", "Doe", "profession", "analyst")), "partials", v4Test.$("analyst", "Analyst - {{firstName}} {{lastName}}", "developer", "Developer - {{firstName}} {{lastName}}")), " Developer - John Foe<br />  Analyst - James Doe<br /> ");
        Assert.assertEquals("{{#each this}} {{>(lookup profession 'name') this.person}}<br /> {{/each}}", compile("{{#each this}} {{> (lookup profession 'name') this.person}}<br /> {{/each}}").text());
    }
}

