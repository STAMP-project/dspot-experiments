package cucumber.runtime.java;


import org.junit.Assert;
import org.junit.Test;


public class Java8SnippetTest {
    private static final String GIVEN_KEYWORD = "Given";

    @Test
    public void generatesPlainSnippet() {
        String expected = "" + ((("Given(\"I have {int} cukes in my {string} belly\", (Integer int1, String string) -> {\n" + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "});\n");
        System.out.println(expected);
        Assert.assertEquals(expected, snippetFor("I have 4 cukes in my \"big\" belly"));
    }
}

