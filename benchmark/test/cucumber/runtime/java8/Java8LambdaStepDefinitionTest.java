package cucumber.runtime.java8;


import StepdefBody.A1;
import StepdefBody.A2;
import cucumber.api.java8.StepdefBody;
import cucumber.runtime.CucumberException;
import gherkin.pickles.PickleCell;
import gherkin.pickles.PickleString;
import gherkin.pickles.PickleTable;
import io.cucumber.datatable.DataTable;
import io.cucumber.stepexpression.Argument;
import io.cucumber.stepexpression.TypeRegistry;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class Java8LambdaStepDefinitionTest {
    private final TypeRegistry typeRegistry = new TypeRegistry(Locale.ENGLISH);

    @Test
    public void should_calculate_parameters_count_from_body_with_one_param() {
        StepdefBody.A1<String> body = ( p1) -> {
        };
        Java8StepDefinition def = Java8StepDefinition.create("I have some step", A1.class, body, typeRegistry);
        Assert.assertEquals(Integer.valueOf(1), def.getParameterCount());
    }

    @Test
    public void should_calculate_parameters_count_from_body_with_two_params() {
        A2<String, String> body = ( p1, p2) -> {
        };
        Java8StepDefinition def = Java8StepDefinition.create("I have some step", A2.class, body, typeRegistry);
        Assert.assertEquals(Integer.valueOf(2), def.getParameterCount());
    }

    @Test
    public void should_apply_identity_transform_to_doc_string_when_target_type_is_object() {
        StepdefBody.A1 body = ( p1) -> {
        };
        Java8StepDefinition def = Java8StepDefinition.create("I have some step", A1.class, body, typeRegistry);
        PickleString pickleString = new PickleString(null, "content", "text");
        List<Argument> arguments = def.matchedArguments(new gherkin.pickles.PickleStep("I have some step", Collections.singletonList(pickleString), Collections.emptyList()));
        Assert.assertEquals("content", arguments.get(0).getValue());
    }

    @Test
    public void should_apply_identity_transform_to_data_table_when_target_type_is_object() {
        StepdefBody.A1 body = ( p1) -> {
        };
        Java8StepDefinition def = Java8StepDefinition.create("I have some step", A1.class, body, typeRegistry);
        PickleTable table = new PickleTable(Collections.singletonList(new gherkin.pickles.PickleRow(Collections.singletonList(new PickleCell(null, "content")))));
        List<Argument> arguments = def.matchedArguments(new gherkin.pickles.PickleStep("I have some step", Collections.singletonList(table), Collections.emptyList()));
        Assert.assertEquals(DataTable.create(Collections.singletonList(Collections.singletonList("content"))), arguments.get(0).getValue());
    }

    @Test
    public void should_fail_for_param_with_non_generic_list() {
        try {
            StepdefBody.A1<List> body = ( p1) -> {
            };
            Java8StepDefinition.create("I have some step", A1.class, body, typeRegistry);
        } catch (CucumberException expected) {
            Assert.assertEquals("Can't use java.util.List in lambda step definition. Declare a DataTable argument instead and convert manually with asList/asLists/asMap/asMaps", expected.getMessage());
        }
    }

    @Test
    public void should_fail_for_param_with_generic_list() {
        try {
            StepdefBody.A1<List<String>> body = ( p1) -> {
            };
            Java8StepDefinition.create("I have some step", A1.class, body, typeRegistry);
        } catch (CucumberException expected) {
            Assert.assertEquals("Can't use java.util.List in lambda step definition. Declare a DataTable argument instead and convert manually with asList/asLists/asMap/asMaps", expected.getMessage());
        }
    }
}

