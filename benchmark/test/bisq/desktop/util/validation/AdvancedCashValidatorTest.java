package bisq.desktop.util.validation;


import org.junit.Assert;
import org.junit.Test;


public class AdvancedCashValidatorTest {
    @Test
    public void validate() {
        AdvancedCashValidator validator = new AdvancedCashValidator(new EmailValidator(), new RegexValidator());
        Assert.assertTrue(validator.validate("U123456789012").isValid);
        Assert.assertTrue(validator.validate("test@user.com").isValid);
        Assert.assertFalse(validator.validate("").isValid);
        Assert.assertFalse(validator.validate(null).isValid);
        Assert.assertFalse(validator.validate("123456789012").isValid);
    }
}

