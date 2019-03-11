package cucumber.runtime.java;


import cucumber.runtime.snippets.FunctionNameGenerator;
import cucumber.runtime.snippets.UnderscoreConcatenator;
import gherkin.pickles.PickleCell;
import gherkin.pickles.PickleString;
import gherkin.pickles.PickleTable;
import io.cucumber.cucumberexpressions.ParameterType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class JavaSnippetTest {
    private static final String GIVEN_KEYWORD = "Given";

    private final FunctionNameGenerator functionNameGenerator = new FunctionNameGenerator(new UnderscoreConcatenator());

    @Test
    public void generatesPlainSnippet() {
        String expected = "" + (((("@Given(\"I have {int} cukes in my {string} belly\")\n" + "public void i_have_cukes_in_my_belly(Integer int1, String string) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetFor("I have 4 cukes in my \"big\" belly"));
    }

    @Test
    public void generatesPlainSnippetUsingCustomParameterTypes() {
        ParameterType<JavaSnippetTest.Size> customParameterType = new ParameterType<JavaSnippetTest.Size>("size", "small|medium|large", JavaSnippetTest.Size.class, new io.cucumber.cucumberexpressions.CaptureGroupTransformer<JavaSnippetTest.Size>() {
            @Override
            public JavaSnippetTest.Size transform(String... strings) {
                return null;
            }
        }, true, false);
        String expected = "" + (((("@Given(\"I have {double} cukes in my {size} belly\")\n" + "public void i_have_cukes_in_my_belly(Double double1, Size size) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetFor("I have 4.2 cukes in my large belly", customParameterType));
    }

    @Test
    public void generatesPlainSnippetUsingComplexParameterTypes() {
        ParameterType<List<JavaSnippetTest.Size>> customParameterType = new ParameterType<List<JavaSnippetTest.Size>>("sizes", Collections.singletonList("(small|medium|large)(( and |, )(small|medium|large))*"), getType(), new io.cucumber.cucumberexpressions.CaptureGroupTransformer<List<JavaSnippetTest.Size>>() {
            @Override
            public List<JavaSnippetTest.Size> transform(String... strings) {
                return null;
            }
        }, true, false);
        String expected = "" + (((("@Given(\"I have {sizes} bellies\")\n" + "public void i_have_bellies(java.util.List<cucumber.runtime.java.JavaSnippetTest$Size> sizes) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetFor("I have large and small bellies", customParameterType));
    }

    @Test
    public void generatesCopyPasteReadyStepSnippetForNumberParameters() {
        String expected = "" + (((("@Given(\"before {int} after\")\n" + "public void before_after(Integer int1) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetFor("before 5 after"));
    }

    @Test
    public void generatesCopyPasteReadySnippetWhenStepHasIllegalJavaIdentifierChars() {
        String expected = "" + (((("@Given(\"I have {int} cukes in: my {string} red-belly!\")\n" + "public void i_have_cukes_in_my_red_belly(Integer int1, String string) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetFor("I have 4 cukes in: my \"big\" red-belly!"));
    }

    @Test
    public void generatesCopyPasteReadySnippetWhenStepHasIntegersInsideStringParameter() {
        String expected = "" + (((("@Given(\"the DI system receives a message saying {string}\")\n" + "public void the_DI_system_receives_a_message_saying(String string) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetFor("the DI system receives a message saying \"{ dataIngestion: { feeds: [ feed: { merchantId: 666, feedId: 1, feedFileLocation: feed.csv } ] }\""));
    }

    @Test
    public void generatesSnippetWithDollarSigns() {
        String expected = "" + (((("@Given(\"I have ${int}\")\n" + "public void i_have_$(Integer int1) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetFor("I have $5"));
    }

    @Test
    public void generatesSnippetWithQuestionMarks() {
        String expected = "" + (((("@Given(\"is there an error?:\")\n" + "public void is_there_an_error() {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetFor("is there an error?:"));
    }

    @Test
    public void generatesSnippetWithLotsOfNonIdentifierCharacters() {
        String expected = "" + (((("@Given(\"\\\\([a-z]*)?.+\")\n" + "public void a_z() {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetFor("([a-z]*)?.+"));
    }

    @Test
    public void generatesSnippetWithParentheses() {
        String expected = "" + (((("@Given(\"I have {int} cukes \\\\(maybe more)\")\n" + "public void i_have_cukes_maybe_more(Integer int1) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetFor("I have 5 cukes (maybe more)"));
    }

    @Test
    public void generatesSnippetWithBrackets() {
        String expected = "" + (((("@Given(\"I have {int} cukes [maybe more]\")\n" + "public void i_have_cukes_maybe_more(Integer int1) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetFor("I have 5 cukes [maybe more]"));
    }

    @Test
    public void generatesSnippetWithDocString() {
        String expected = "" + (((("@Given(\"I have:\")\n" + "public void i_have(String docString) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetForDocString("I have:", new PickleString(null, "hello")));
    }

    @Test
    public void generatesSnippetWithMultipleArgumentsNamedDocString() {
        ParameterType<String> customParameterType = new ParameterType<String>("docString", "\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"", String.class, new io.cucumber.cucumberexpressions.CaptureGroupTransformer<String>() {
            @Override
            public String transform(String... strings) {
                return null;
            }
        }, true, false);
        String expected = "" + (((((((((("@Given(\"I have a {docString}:\")\n" + "public void i_have_a(String docString, String docString1) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n") + "\n") + "@Given(\"I have a {string}:\")\n") + "public void i_have_a(String string, String docString) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetForDocString("I have a \"Documentation String\":", new PickleString(null, "hello"), customParameterType));
    }

    @Test
    public void generatesSnippetWithDataTable() {
        String expected = "" + (((((((((("@Given(\"I have:\")\n" + "public void i_have(io.cucumber.datatable.DataTable dataTable) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    // For automatic transformation, change DataTable to one of\n") + "    // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or\n") + "    // Map<K, List<V>>. E,K,V must be a String, Integer, Float,\n") + "    // Double, Byte, Short, Long, BigInteger or BigDecimal.\n") + "    //\n") + "    // For other transformations you can register a DataTableType.\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        PickleTable dataTable = new PickleTable(Arrays.asList(new gherkin.pickles.PickleRow(Arrays.asList(new PickleCell(null, "col1")))));
        Assert.assertEquals(expected, snippetForDataTable("I have:", dataTable));
    }

    @Test
    public void generatesSnippetWithMultipleArgumentsNamedDataTable() {
        ParameterType<String> customParameterType = new ParameterType<String>("dataTable", "\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"", String.class, new io.cucumber.cucumberexpressions.CaptureGroupTransformer<String>() {
            @Override
            public String transform(String... strings) {
                return null;
            }
        }, true, false);
        String expected = "" + (((((((((((((((((((((("@Given(\"I have in table {dataTable}:\")\n" + "public void i_have_in_table(String dataTable, io.cucumber.datatable.DataTable dataTable1) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    // For automatic transformation, change DataTable to one of\n") + "    // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or\n") + "    // Map<K, List<V>>. E,K,V must be a String, Integer, Float,\n") + "    // Double, Byte, Short, Long, BigInteger or BigDecimal.\n") + "    //\n") + "    // For other transformations you can register a DataTableType.\n") + "    throw new cucumber.api.PendingException();\n") + "}\n") + "\n") + "@Given(\"I have in table {string}:\")\n") + "public void i_have_in_table(String string, io.cucumber.datatable.DataTable dataTable) {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    // For automatic transformation, change DataTable to one of\n") + "    // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or\n") + "    // Map<K, List<V>>. E,K,V must be a String, Integer, Float,\n") + "    // Double, Byte, Short, Long, BigInteger or BigDecimal.\n") + "    //\n") + "    // For other transformations you can register a DataTableType.\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        PickleTable dataTable = new PickleTable(Arrays.asList(new gherkin.pickles.PickleRow(Arrays.asList(new PickleCell(null, "col1")))));
        Assert.assertEquals(expected, snippetForDataTable("I have in table \"M6\":", dataTable, customParameterType));
    }

    @Test
    public void generateSnippetWithOutlineParam() {
        String expected = "" + (((("@Given(\"Then it responds <param>\")\n" + "public void then_it_responds_param() {\n") + "    // Write code here that turns the phrase above into concrete actions\n") + "    throw new cucumber.api.PendingException();\n") + "}\n");
        Assert.assertEquals(expected, snippetFor("Then it responds <param>"));
    }

    // Dummy. Makes the test readable
    private static class Size {}
}

