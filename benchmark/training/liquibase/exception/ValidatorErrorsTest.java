package liquibase.exception;


import org.junit.Assert;
import org.junit.Test;


public class ValidatorErrorsTest {
    @Test
    public void hasErrors() {
        ValidationErrors errors = new ValidationErrors();
        Assert.assertFalse(errors.hasErrors());
        errors.addError("test message");
        Assert.assertTrue(errors.hasErrors());
    }

    @Test
    public void checkRequiredField_nullValue() {
        ValidationErrors errors = new ValidationErrors();
        Assert.assertFalse(errors.hasErrors());
        errors.checkRequiredField("testField", null);
        Assert.assertTrue(errors.hasErrors());
        Assert.assertTrue(errors.getErrorMessages().contains("testField is required"));
    }
}

