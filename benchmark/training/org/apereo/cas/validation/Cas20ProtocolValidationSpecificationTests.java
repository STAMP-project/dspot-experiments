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
public class Cas20ProtocolValidationSpecificationTests {
    @Test
    public void verifyRenewGettersAndSettersFalse() {
        val s = new Cas20ProtocolValidationSpecification();
        s.setRenew(false);
        Assertions.assertFalse(s.isRenew());
    }

    @Test
    public void verifyRenewGettersAndSettersTrue() {
        val s = new Cas20ProtocolValidationSpecification();
        s.setRenew(true);
        Assertions.assertTrue(s.isRenew());
    }

    @Test
    public void verifyRenewAsTrueAsConstructor() {
        Assertions.assertTrue(new Cas20ProtocolValidationSpecification(true).isRenew());
    }

    @Test
    public void verifyRenewAsFalseAsConstructor() {
        Assertions.assertFalse(new Cas20ProtocolValidationSpecification(false).isRenew());
    }

    @Test
    public void verifySatisfiesSpecOfTrue() {
        Assertions.assertTrue(new Cas20ProtocolValidationSpecification(true).isSatisfiedBy(CoreValidationTestUtils.getAssertion(true), new MockHttpServletRequest()));
    }

    @Test
    public void verifyNotSatisfiesSpecOfTrue() {
        Assertions.assertFalse(new Cas20ProtocolValidationSpecification(true).isSatisfiedBy(CoreValidationTestUtils.getAssertion(false), new MockHttpServletRequest()));
    }

    @Test
    public void verifySatisfiesSpecOfFalse() {
        Assertions.assertTrue(new Cas20ProtocolValidationSpecification(false).isSatisfiedBy(CoreValidationTestUtils.getAssertion(true), new MockHttpServletRequest()));
    }

    @Test
    public void verifySatisfiesSpecOfFalse2() {
        Assertions.assertTrue(new Cas20ProtocolValidationSpecification(false).isSatisfiedBy(CoreValidationTestUtils.getAssertion(false), new MockHttpServletRequest()));
    }
}

