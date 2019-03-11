package bisq.desktop.util.validation;


import org.junit.Assert;
import org.junit.Test;


public class BranchIdValidatorTest {
    @Test
    public void testValidationForArgentina() {
        BranchIdValidator validator = new BranchIdValidator("AR");
        Assert.assertTrue(validator.validate("0590").isValid);
        Assert.assertFalse(validator.validate("05901").isValid);
    }
}

