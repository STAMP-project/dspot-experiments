package org.apereo.cas.authentication.principal;


import PrincipalTransformationProperties.CaseConversion.UPPERCASE;
import lombok.val;
import org.apereo.cas.configuration.model.core.authentication.PrincipalTransformationProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link PrincipalNameTransformerUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class PrincipalNameTransformerUtilsTests {
    @Test
    public void verifyAction() {
        val properties = new PrincipalTransformationProperties();
        properties.setPrefix("prefix-");
        properties.setSuffix("-suffix");
        properties.setCaseConversion(UPPERCASE);
        val t = PrincipalNameTransformerUtils.newPrincipalNameTransformer(properties);
        val result = t.transform("userid");
        Assertions.assertEquals("PREFIX-USERID-SUFFIX", result);
    }
}

