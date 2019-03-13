package cucumber.runtime.java;


import cucumber.api.Transpose;
import io.cucumber.datatable.DataTable;
import io.cucumber.stepexpression.TypeRegistry;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class JavaStepDefinitionTransposeTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final TypeRegistry typeRegistry = new TypeRegistry(Locale.ENGLISH);

    public static class StepDefs {
        List<List<Double>> listOfListOfDoubles;

        public Map<Double, Double> mapOfDoubleToDouble;

        public DataTable dataTable;

        private Map<Double, List<Double>> mapOfDoubleToListOfDouble;

        public void listOfListOfDoubles(List<List<Double>> listOfListOfDoubles) {
            this.listOfListOfDoubles = listOfListOfDoubles;
        }

        public void listOfListOfDoublesTransposed(@Transpose
        List<List<Double>> listOfListOfDoubles) {
            this.listOfListOfDoubles = listOfListOfDoubles;
        }

        public void plainDataTable(DataTable dataTable) {
            this.dataTable = dataTable;
        }

        public void transposedDataTable(@Transpose
        DataTable dataTable) {
            this.dataTable = dataTable;
        }

        public void mapOfDoubleToDouble(Map<Double, Double> mapOfDoubleToDouble) {
            this.mapOfDoubleToDouble = mapOfDoubleToDouble;
        }

        public void transposedMapOfDoubleToListOfDouble(@Transpose
        Map<Double, List<Double>> mapOfDoubleToListOfDouble) {
            this.mapOfDoubleToListOfDouble = mapOfDoubleToListOfDouble;
        }
    }

    @Test
    public void transforms_to_map_of_double_to_double() throws Throwable {
        Method m = JavaStepDefinitionTransposeTest.StepDefs.class.getMethod("mapOfDoubleToDouble", Map.class);
        JavaStepDefinitionTransposeTest.StepDefs stepDefs = runStepDef(m, new gherkin.pickles.PickleTable(listOfDoublesWithoutHeader()));
        Assert.assertEquals(Double.valueOf(999.0), stepDefs.mapOfDoubleToDouble.get(1000.0));
        Assert.assertEquals(Double.valueOf((-0.5)), stepDefs.mapOfDoubleToDouble.get(0.5));
        Assert.assertEquals(Double.valueOf(99.5), stepDefs.mapOfDoubleToDouble.get(100.5));
    }

    @Test
    public void transforms_transposed_to_map_of_double_to_double() throws Throwable {
        Method m = JavaStepDefinitionTransposeTest.StepDefs.class.getMethod("transposedMapOfDoubleToListOfDouble", Map.class);
        JavaStepDefinitionTransposeTest.StepDefs stepDefs = runStepDef(m, new gherkin.pickles.PickleTable(listOfDoublesWithoutHeader()));
        Assert.assertEquals(Arrays.asList(0.5, 1000.0), stepDefs.mapOfDoubleToListOfDouble.get(100.5));
    }

    @Test
    public void transforms_to_list_of_single_values() throws Throwable {
        Method m = JavaStepDefinitionTransposeTest.StepDefs.class.getMethod("listOfListOfDoubles", List.class);
        JavaStepDefinitionTransposeTest.StepDefs stepDefs = runStepDef(m, new gherkin.pickles.PickleTable(listOfDoublesWithoutHeader()));
        Assert.assertEquals("[[100.5, 99.5], [0.5, -0.5], [1000.0, 999.0]]", stepDefs.listOfListOfDoubles.toString());
    }

    @Test
    public void transforms_to_list_of_single_values_transposed() throws Throwable {
        Method m = JavaStepDefinitionTransposeTest.StepDefs.class.getMethod("listOfListOfDoublesTransposed", List.class);
        JavaStepDefinitionTransposeTest.StepDefs stepDefs = runStepDef(m, new gherkin.pickles.PickleTable(transposedListOfDoublesWithoutHeader()));
        Assert.assertEquals("[[100.5, 99.5], [0.5, -0.5], [1000.0, 999.0]]", stepDefs.listOfListOfDoubles.toString());
    }

    @Test
    public void passes_plain_data_table() throws Throwable {
        Method m = JavaStepDefinitionTransposeTest.StepDefs.class.getMethod("plainDataTable", DataTable.class);
        JavaStepDefinitionTransposeTest.StepDefs stepDefs = runStepDef(m, new gherkin.pickles.PickleTable(listOfDatesWithHeader()));
        Assert.assertEquals("Birth Date", stepDefs.dataTable.cell(0, 0));
        Assert.assertEquals("1957-05-10", stepDefs.dataTable.cell(1, 0));
    }

    @Test
    public void passes_transposed_data_table() throws Throwable {
        Method m = JavaStepDefinitionTransposeTest.StepDefs.class.getMethod("transposedDataTable", DataTable.class);
        JavaStepDefinitionTransposeTest.StepDefs stepDefs = runStepDef(m, new gherkin.pickles.PickleTable(listOfDatesWithHeader()));
        Assert.assertEquals("Birth Date", stepDefs.dataTable.cell(0, 0));
        Assert.assertEquals("1957-05-10", stepDefs.dataTable.cell(0, 1));
    }
}

