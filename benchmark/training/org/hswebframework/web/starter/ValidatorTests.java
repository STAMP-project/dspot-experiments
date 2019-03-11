package org.hswebframework.web.starter;


import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.junit.Assert;
import org.junit.Test;


public class ValidatorTests {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    @Test
    public void validate() {
        ValidatorTests.TestBean bean = new ValidatorTests.TestBean();
        Set<ConstraintViolation<ValidatorTests.TestBean>> violations = factory.getValidator().validate(bean);
        Assert.assertTrue(((violations.size()) == 2));
        for (ConstraintViolation<ValidatorTests.TestBean> violation : violations) {
            System.out.println(((violation.getPropertyPath()) + (violation.getMessage())));
        }
    }

    public static class TestBean {
        @Range(max = 99)
        private int range = 100;

        @NotNull
        private String notNull = "";

        @NotBlank
        public String getString() {
            return "";
        }
    }
}

