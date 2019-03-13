package io.cucumber.stepexpression;


import io.cucumber.datatable.DataTable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class StepExpressionFactoryTest {
    private static final TypeResolver UNKNOWN_TYPE = new TypeResolver() {
        @Override
        public Type resolve() {
            return Object.class;
        }
    };

    static class Ingredient {
        String name;

        Integer amount;

        String unit;

        Ingredient() {
        }
    }

    private final TypeRegistry registry = new TypeRegistry(Locale.ENGLISH);

    private final List<List<String>> table = Arrays.asList(Arrays.asList("name", "amount", "unit"), Arrays.asList("chocolate", "2", "tbsp"));

    private final List<List<String>> tableTransposed = Arrays.asList(Arrays.asList("name", "chocolate"), Arrays.asList("amount", "2"), Arrays.asList("unit", "tbsp"));

    @Test
    public void table_expression_with_type_creates_table_from_table() {
        StepExpression expression = new StepExpressionFactory(registry).createExpression("Given some stuff:", DataTable.class);
        List<Argument> match = expression.match("Given some stuff:", table);
        DataTable dataTable = ((DataTable) (match.get(0).getValue()));
        Assert.assertEquals(table, dataTable.cells());
    }

    @Test
    public void table_expression_with_type_creates_single_ingredients_from_table() {
        registry.defineDataTableType(new io.cucumber.datatable.DataTableType(StepExpressionFactoryTest.Ingredient.class, beanMapper(registry)));
        StepExpression expression = new StepExpressionFactory(registry).createExpression("Given some stuff:", StepExpressionFactoryTest.Ingredient.class);
        List<Argument> match = expression.match("Given some stuff:", tableTransposed);
        StepExpressionFactoryTest.Ingredient ingredient = ((StepExpressionFactoryTest.Ingredient) (match.get(0).getValue()));
        Assert.assertEquals(ingredient.name, "chocolate");
    }

    @Test
    public void table_expression_with_list_type_creates_list_of_ingredients_from_table() {
        registry.defineDataTableType(new io.cucumber.datatable.DataTableType(StepExpressionFactoryTest.Ingredient.class, listBeanMapper(registry)));
        StepExpression expression = createExpression("Given some stuff:", getTypeFromStepDefinition());
        List<Argument> match = expression.match("Given some stuff:", table);
        List<StepExpressionFactoryTest.Ingredient> ingredients = ((List<StepExpressionFactoryTest.Ingredient>) (match.get(0).getValue()));
        StepExpressionFactoryTest.Ingredient ingredient = ingredients.get(0);
        Assert.assertEquals(ingredient.name, "chocolate");
    }

    @Test
    public void unknown_target_type_does_no_transform_data_table() {
        StepExpression expression = new StepExpressionFactory(registry).createExpression("Given some stuff:", StepExpressionFactoryTest.UNKNOWN_TYPE);
        List<Argument> match = expression.match("Given some stuff:", table);
        Assert.assertEquals(DataTable.create(table), match.get(0).getValue());
    }

    @Test
    public void unknown_target_type_does_no_transform_doc_string() {
        String docString = "A rather long and boring string of documentation";
        StepExpression expression = new StepExpressionFactory(registry).createExpression("Given some stuff:", StepExpressionFactoryTest.UNKNOWN_TYPE);
        List<Argument> match = expression.match("Given some stuff:", docString);
        Assert.assertEquals(docString, match.get(0).getValue());
    }
}

