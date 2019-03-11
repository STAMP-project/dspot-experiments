package bisq.desktop.util.validation;


import org.junit.Assert;
import org.junit.Test;


public class AccountNrValidatorTest {
    @Test
    public void testValidationForArgentina() {
        AccountNrValidator validator = new AccountNrValidator("AR");
        Assert.assertTrue(validator.validate("4009041813520").isValid);
        Assert.assertTrue(validator.validate("035-005198/5").isValid);
    }
}

