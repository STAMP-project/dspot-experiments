package com.netflix.conductor.common.events;


import com.netflix.conductor.common.metadata.events.EventHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Assert;
import org.junit.Test;


public class EventHandlerTest {
    @Test
    public void testWorkflowTaskName() {
        EventHandler taskDef = new EventHandler();// name is null

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> result = validator.validate(taskDef);
        Assert.assertEquals(3, result.size());
        List<String> validationErrors = new ArrayList<>();
        result.forEach(( e) -> validationErrors.add(e.getMessage()));
        Assert.assertTrue(validationErrors.contains("Missing event handler name"));
        Assert.assertTrue(validationErrors.contains("Missing event location"));
        Assert.assertTrue(validationErrors.contains("No actions specified. Please specify at-least one action"));
    }
}

