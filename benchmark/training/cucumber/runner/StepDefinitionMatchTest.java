package cucumber.runner;


import cucumber.runtime.StepDefinition;
import cucumber.runtime.StepDefinitionMatch;
import cucumber.runtime.StubStepDefinition;
import gherkin.pickles.Argument;
import gherkin.pickles.PickleCell;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleStep;
import gherkin.pickles.PickleTable;
import io.cucumber.datatable.DataTableType;
import io.cucumber.stepexpression.TypeRegistry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class StepDefinitionMatchTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final TypeRegistry typeRegistry = new TypeRegistry(Locale.ENGLISH);

    @Test
    public void executes_a_step() throws Throwable {
        PickleStep step = new PickleStep("I have 4 cukes in my belly", Collections.<Argument>emptyList(), Arrays.asList(Mockito.mock(PickleLocation.class)));
        StepDefinition stepDefinition = new StubStepDefinition("I have {int} cukes in my belly", typeRegistry, Integer.class);
        List<io.cucumber.stepexpression.Argument> arguments = stepDefinition.matchedArguments(step);
        StepDefinitionMatch stepDefinitionMatch = new PickleStepDefinitionMatch(arguments, stepDefinition, null, step);
        stepDefinitionMatch.runStep(null);
    }

    @Test
    public void throws_arity_mismatch_exception_when_there_are_fewer_parameters_than_arguments() throws Throwable {
        PickleStep step = new PickleStep("I have 4 cukes in my belly", Collections.<Argument>emptyList(), Arrays.asList(Mockito.mock(PickleLocation.class)));
        StepDefinition stepDefinition = new StubStepDefinition("I have {int} cukes in my belly", typeRegistry);
        List<io.cucumber.stepexpression.Argument> arguments = stepDefinition.matchedArguments(step);
        StepDefinitionMatch stepDefinitionMatch = new PickleStepDefinitionMatch(arguments, stepDefinition, null, step);
        expectedException.expectMessage(("" + ((("Step [I have {int} cukes in my belly] is defined with 0 parameters at \'{stubbed location with details}\'.\n" + "However, the gherkin step has 1 arguments:\n") + " * 4\n") + "Step text: I have 4 cukes in my belly")));
        stepDefinitionMatch.runStep(null);
    }

    @Test
    public void throws_arity_mismatch_exception_when_there_are_fewer_parameters_than_arguments_with_data_table() throws Throwable {
        PickleTable table = new PickleTable(Arrays.asList(new gherkin.pickles.PickleRow(Arrays.asList(new PickleCell(Mockito.mock(PickleLocation.class), "A"), new PickleCell(Mockito.mock(PickleLocation.class), "B"))), new gherkin.pickles.PickleRow(Arrays.asList(new PickleCell(Mockito.mock(PickleLocation.class), "C"), new PickleCell(Mockito.mock(PickleLocation.class), "D")))));
        PickleStep step = new PickleStep("I have 4 cukes in my belly", Arrays.asList(((Argument) (table))), Arrays.asList(Mockito.mock(PickleLocation.class)));
        StepDefinition stepDefinition = new StubStepDefinition("I have {int} cukes in my belly", typeRegistry);
        List<io.cucumber.stepexpression.Argument> arguments = stepDefinition.matchedArguments(step);
        PickleStepDefinitionMatch stepDefinitionMatch = new PickleStepDefinitionMatch(arguments, stepDefinition, null, step);
        expectedException.expectMessage(("" + ((((((("Step [I have {int} cukes in my belly] is defined with 0 parameters at \'{stubbed location with details}\'.\n" + "However, the gherkin step has 2 arguments:\n") + " * 4\n") + " * Table:\n") + "      | A | B |\n") + "      | C | D |\n") + "\n") + "Step text: I have 4 cukes in my belly")));
        stepDefinitionMatch.runStep(null);
    }

    @Test
    public void throws_arity_mismatch_exception_when_there_are_more_parameters_than_arguments() throws Throwable {
        PickleStep step = new PickleStep("I have 4 cukes in my belly", Arrays.asList(((Argument) (Mockito.mock(PickleTable.class)))), Arrays.asList(Mockito.mock(PickleLocation.class)));
        StepDefinition stepDefinition = new StubStepDefinition("I have {int} cukes in my belly", typeRegistry, Integer.TYPE, Short.TYPE, List.class);
        List<io.cucumber.stepexpression.Argument> arguments = stepDefinition.matchedArguments(step);
        PickleStepDefinitionMatch stepDefinitionMatch = new PickleStepDefinitionMatch(arguments, stepDefinition, null, step);
        expectedException.expectMessage(("" + ((((("Step [I have {int} cukes in my belly] is defined with 3 parameters at \'{stubbed location with details}\'.\n" + "However, the gherkin step has 2 arguments:\n") + " * 4\n") + " * Table:\n") + "\n") + "Step text: I have 4 cukes in my belly")));
        stepDefinitionMatch.runStep(null);
    }

    @Test
    public void throws_arity_mismatch_exception_when_there_are_more_parameters_and_no_arguments() throws Throwable {
        PickleStep step = new PickleStep("I have cukes in my belly", Collections.<Argument>emptyList(), Arrays.asList(Mockito.mock(PickleLocation.class)));
        StepDefinition stepDefinition = new StubStepDefinition("I have cukes in my belly", typeRegistry, Integer.TYPE, Short.TYPE, List.class);
        List<io.cucumber.stepexpression.Argument> arguments = stepDefinition.matchedArguments(step);
        StepDefinitionMatch stepDefinitionMatch = new PickleStepDefinitionMatch(arguments, stepDefinition, null, step);
        expectedException.expectMessage(("" + (("Step [I have cukes in my belly] is defined with 3 parameters at \'{stubbed location with details}\'.\n" + "However, the gherkin step has 0 arguments.\n") + "Step text: I have cukes in my belly")));
        stepDefinitionMatch.runStep(null);
    }

    @Test
    public void throws_register_type_in_configuration_exception_when_there_is_no_data_table_type_defined() throws Throwable {
        // Empty table maps to null and doesn't trigger a type check.
        PickleTable table = new PickleTable(Collections.singletonList(new gherkin.pickles.PickleRow(Collections.singletonList(new PickleCell(Mockito.mock(PickleLocation.class), "A")))));
        PickleStep step = new PickleStep("I have a datatable", Arrays.asList(((Argument) (table))), Arrays.asList(Mockito.mock(PickleLocation.class)));
        StepDefinition stepDefinition = new StubStepDefinition("I have a datatable", typeRegistry, StepDefinitionMatchTest.UndefinedDataTableType.class);
        List<io.cucumber.stepexpression.Argument> arguments = stepDefinition.matchedArguments(step);
        StepDefinitionMatch stepDefinitionMatch = new PickleStepDefinitionMatch(arguments, stepDefinition, null, step);
        expectedException.expectMessage(("" + ("Could not convert arguments for step [I have a datatable] defined at \'{stubbed location with details}\'.\n" + "It appears you did not register a data table type. The details are in the stacktrace below.")));
        stepDefinitionMatch.runStep(null);
    }

    @Test
    public void throws_could_not_convert_exception_for_transfomer_and_capture_group_mismatch() throws Throwable {
        typeRegistry.defineParameterType(new io.cucumber.cucumberexpressions.ParameterType<StepDefinitionMatchTest.ItemQuantity>("itemQuantity", "(few|some|lots of) (cukes|gherkins)", StepDefinitionMatchTest.ItemQuantity.class, new io.cucumber.cucumberexpressions.Transformer<StepDefinitionMatchTest.ItemQuantity>() {
            @Override
            public StepDefinitionMatchTest.ItemQuantity transform(String s) throws Throwable {
                return null;
            }
        }));
        PickleStep step = new PickleStep("I have some cukes in my belly", Collections.<Argument>emptyList(), Arrays.asList(Mockito.mock(PickleLocation.class)));
        StepDefinition stepDefinition = new StubStepDefinition("I have {itemQuantity} in my belly", typeRegistry, StepDefinitionMatchTest.ItemQuantity.class);
        List<io.cucumber.stepexpression.Argument> arguments = stepDefinition.matchedArguments(step);
        StepDefinitionMatch stepDefinitionMatch = new PickleStepDefinitionMatch(arguments, stepDefinition, null, step);
        expectedException.expectMessage(("" + ("Could not convert arguments for step [I have {itemQuantity} in my belly] defined at \'{stubbed location with details}\'.\n" + "The details are in the stacktrace below.")));
        stepDefinitionMatch.runStep(null);
    }

    @Test
    public void throws_could_not_convert_exception_for_singleton_table_dimension_mismatch() throws Throwable {
        PickleTable table = new PickleTable(Arrays.asList(new gherkin.pickles.PickleRow(Arrays.asList(new PickleCell(Mockito.mock(PickleLocation.class), "A"), new PickleCell(Mockito.mock(PickleLocation.class), "B"))), new gherkin.pickles.PickleRow(Arrays.asList(new PickleCell(Mockito.mock(PickleLocation.class), "C"), new PickleCell(Mockito.mock(PickleLocation.class), "D")))));
        typeRegistry.defineDataTableType(new DataTableType(StepDefinitionMatchTest.ItemQuantity.class, new io.cucumber.datatable.TableCellTransformer<StepDefinitionMatchTest.ItemQuantity>() {
            @Override
            public StepDefinitionMatchTest.ItemQuantity transform(String s) {
                return new StepDefinitionMatchTest.ItemQuantity(s);
            }
        }));
        PickleStep step = new PickleStep("I have some cukes in my belly", Collections.singletonList(((Argument) (table))), Arrays.asList(Mockito.mock(PickleLocation.class)));
        StepDefinition stepDefinition = new StubStepDefinition("I have some cukes in my belly", typeRegistry, StepDefinitionMatchTest.ItemQuantity.class);
        List<io.cucumber.stepexpression.Argument> arguments = stepDefinition.matchedArguments(step);
        StepDefinitionMatch stepDefinitionMatch = new PickleStepDefinitionMatch(arguments, stepDefinition, null, step);
        expectedException.expectMessage(("" + ("Could not convert arguments for step [I have some cukes in my belly] defined at \'{stubbed location with details}\'.\n" + "The details are in the stacktrace below.")));
        stepDefinitionMatch.runStep(null);
    }

    private static final class ItemQuantity {
        private final String s;

        public ItemQuantity(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private static final class UndefinedDataTableType {}
}

