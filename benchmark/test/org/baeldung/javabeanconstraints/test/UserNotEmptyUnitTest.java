package org.baeldung.javabeanconstraints.test;


import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.baeldung.javabeanconstraints.entities.UserNotEmpty;
import org.junit.Test;


public class UserNotEmptyUnitTest {
    private static Validator validator;

    @Test
    public void whenNotEmptyName_thenNoConstraintViolations() {
        UserNotEmpty user = new UserNotEmpty("John");
        Set<ConstraintViolation<UserNotEmpty>> violations = UserNotEmptyUnitTest.validator.validate(user);
        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void whenEmptyName_thenOneConstraintViolation() {
        UserNotEmpty user = new UserNotEmpty("");
        Set<ConstraintViolation<UserNotEmpty>> violations = UserNotEmptyUnitTest.validator.validate(user);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void whenNullName_thenOneConstraintViolation() {
        UserNotEmpty user = new UserNotEmpty(null);
        Set<ConstraintViolation<UserNotEmpty>> violations = UserNotEmptyUnitTest.validator.validate(user);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void whenToString_thenCorrect() {
        UserNotEmpty user = new UserNotEmpty("John");
        assertThat(user.toString()).isEqualTo("User{name=John}");
    }
}

