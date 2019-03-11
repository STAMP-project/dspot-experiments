package bisq.core.locale;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public class BankUtilTest {
    @Test
    public void testBankFieldsForArgentina() {
        final String argentina = "AR";
        TestCase.assertTrue(BankUtil.isHolderIdRequired(argentina));
        Assert.assertEquals("CUIL/CUIT", BankUtil.getHolderIdLabel(argentina));
        Assert.assertEquals("CUIT", BankUtil.getHolderIdLabelShort(argentina));
        TestCase.assertTrue(BankUtil.isNationalAccountIdRequired(argentina));
        Assert.assertEquals("CBU number", BankUtil.getNationalAccountIdLabel(argentina));
        TestCase.assertTrue(BankUtil.isBankNameRequired(argentina));
        TestCase.assertTrue(BankUtil.isBranchIdRequired(argentina));
        TestCase.assertTrue(BankUtil.isAccountNrRequired(argentina));
        Assert.assertEquals("N?mero de cuenta", BankUtil.getAccountNrLabel(argentina));
        TestCase.assertTrue(BankUtil.useValidation(argentina));
        Assert.assertFalse(BankUtil.isBankIdRequired(argentina));
        Assert.assertFalse(BankUtil.isStateRequired(argentina));
        Assert.assertFalse(BankUtil.isAccountTypeRequired(argentina));
    }
}

