package org.apereo.cas.authentication.handler;


import lombok.val;
import org.apereo.cas.util.transforms.ConvertCasePrincipalNameTransformer;
import org.apereo.cas.util.transforms.PrefixSuffixPrincipalNameTransformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for the switch-case transformer.
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public class ConvertCasePrincipalNameTransformerTests {
    @Test
    public void verifyUpperCaseTransformerWithTrimAndDelegate() {
        val suffixTrans = new PrefixSuffixPrincipalNameTransformer();
        suffixTrans.setPrefix("a");
        suffixTrans.setSuffix("z");
        val transformer = new ConvertCasePrincipalNameTransformer();
        transformer.setToUpperCase(true);
        val result = transformer.transform(suffixTrans.transform("   uid  "));
        Assertions.assertEquals("A   UID  Z", result);
    }

    @Test
    public void verifyUpperCaseTransformerWithTrim() {
        val transformer = new ConvertCasePrincipalNameTransformer();
        transformer.setToUpperCase(true);
        val result = transformer.transform("   uid  ");
        Assertions.assertEquals("UID", result);
    }

    @Test
    public void verifyLowerCaseTransformerWithTrim() {
        val transformer = new ConvertCasePrincipalNameTransformer();
        val result = transformer.transform("   UID  ");
        Assertions.assertEquals("uid", result);
    }
}

