package org.baeldung.javabeanconstraints.test;


import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.baeldung.javabeanconstraints.entities.UserNotBlank;
import org.junit.Test;


public class UserNotBlankUnitTest {
    private static Validator validator;

    @Test
    public void whenNotBlankName_thenNoConstraintViolations() {
        UserNotBlank user = new UserNotBlank("John");
        Set<ConstraintViolation<UserNotBlank>> violations = UserNotBlankUnitTest.validator.validate(user);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void whenBlankName_thenOneConstraintViolation() {
        UserNotBlank user = new UserNotBlank(" ");
        Set<ConstraintViolation<UserNotBlank>> violations = UserNotBlankUnitTest.validator.validate(user);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void whenEmptyName_thenOneConstraintViolation() {
        UserNotBlank user = new UserNotBlank("");
        Set<ConstraintViolation<UserNotBlank>> violations = UserNotBlankUnitTest.validator.validate(user);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void whenNullName_thenOneConstraintViolation() {
        UserNotBlank user = new UserNotBlank(null);
        Set<ConstraintViolation<UserNotBlank>> violations = UserNotBlankUnitTest.validator.validate(user);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void whenToString_thenCorrect() {
        UserNotBlank user = new UserNotBlank("John");
        assertThat(user.toString()).isEqualTo("User{name=John}");
    }
}

