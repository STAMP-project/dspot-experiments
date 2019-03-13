package cucumber.runtime.java8;


import StepdefBody.A1;
import StepdefBody.A2;
import io.cucumber.stepexpression.TypeRegistry;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class Java8AnonInnerClassStepDefinitionTest {
    private final TypeRegistry typeRegistry = new TypeRegistry(Locale.ENGLISH);

    @Test
    public void should_calculate_parameters_count_from_body_with_one_param() {
        Java8StepDefinition java8StepDefinition = Java8StepDefinition.create("I have some step", A1.class, oneParamStep(), typeRegistry);
        Assert.assertEquals(Integer.valueOf(1), java8StepDefinition.getParameterCount());
    }

    @Test
    public void should_calculate_parameters_count_from_body_with_two_params() {
        Java8StepDefinition java8StepDefinition = Java8StepDefinition.create("I have some step", A2.class, twoParamStep(), typeRegistry);
        Assert.assertEquals(Integer.valueOf(2), java8StepDefinition.getParameterCount());
    }
}

