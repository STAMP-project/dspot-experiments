package com.github.jknack.handlebars.i367;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue367 extends AbstractTest {
    @Test
    public void missingHelperOnVariables() throws IOException {
        shouldCompileTo("{{#userss}} <tr> <td>{{fullName}}</td> <td>{{jobTitle}}</td> </tr> {{/userss}}", AbstractTest.$, "nousers");
    }
}

