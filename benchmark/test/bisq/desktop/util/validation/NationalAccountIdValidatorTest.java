package bisq.desktop.util.validation;


import org.junit.Assert;
import org.junit.Test;


public class NationalAccountIdValidatorTest {
    @Test
    public void testValidationForArgentina() {
        NationalAccountIdValidator validator = new NationalAccountIdValidator("AR");
        Assert.assertTrue(validator.validate("2850590940090418135201").isValid);
        final String wrongNationalAccountId = "285059094009041813520";
        Assert.assertFalse(validator.validate(wrongNationalAccountId).isValid);
        Assert.assertEquals("CBU number must consist of 22 numbers.", validator.validate(wrongNationalAccountId).errorMessage);
    }
}

