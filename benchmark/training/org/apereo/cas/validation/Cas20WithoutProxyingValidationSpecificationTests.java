package org.apereo.cas.validation;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class Cas20WithoutProxyingValidationSpecificationTests {
    private Cas20WithoutProxyingValidationSpecification validationSpecification;

    @Test
    public void verifySatisfiesSpecOfTrue() {
        Assertions.assertTrue(this.validationSpecification.isSatisfiedBy(CoreValidationTestUtils.getAssertion(true), new MockHttpServletRequest()));
    }

    @Test
    public void verifyNotSatisfiesSpecOfTrue() {
        this.validationSpecification.setRenew(true);
        Assertions.assertFalse(this.validationSpecification.isSatisfiedBy(CoreValidationTestUtils.getAssertion(false), new MockHttpServletRequest()));
    }

    @Test
    public void verifySatisfiesSpecOfFalse() {
        Assertions.assertTrue(this.validationSpecification.isSatisfiedBy(CoreValidationTestUtils.getAssertion(false), new MockHttpServletRequest()));
    }

    @Test
    public void verifyDoesNotSatisfiesSpecOfFalse() {
        Assertions.assertFalse(this.validationSpecification.isSatisfiedBy(CoreValidationTestUtils.getAssertion(false, new String[]{ "test2" }), new MockHttpServletRequest()));
    }

    @Test
    public void verifySettingRenew() {
        val validation = new Cas20WithoutProxyingValidationSpecification(true);
        Assertions.assertTrue(validation.isRenew());
    }
}

