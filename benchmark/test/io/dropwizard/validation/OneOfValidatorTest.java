package io.dropwizard.validation;


import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import javax.validation.Validator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;


public class OneOfValidatorTest {
    @SuppressWarnings("UnusedDeclaration")
    public static class Example {
        @OneOf({ "one", "two", "three" })
        private String basic = "one";

        @OneOf(value = { "one", "two", "three" }, ignoreCase = true)
        private String caseInsensitive = "one";

        @OneOf(value = { "one", "two", "three" }, ignoreWhitespace = true)
        private String whitespaceInsensitive = "one";

        @Valid
        private List<@OneOf({ "one", "two", "three" })
        String> basicList = Collections.singletonList("one");
    }

    private final Validator validator = BaseValidator.newValidator();

    @Test
    public void allowsExactElements() throws Exception {
        assertThat(ConstraintViolations.format(validator.validate(new OneOfValidatorTest.Example()))).isEmpty();
    }

    @Test
    public void doesNotAllowOtherElements() throws Exception {
        Assumptions.assumeTrue("en".equals(Locale.getDefault().getLanguage()));
        final OneOfValidatorTest.Example example = new OneOfValidatorTest.Example();
        example.basic = "four";
        assertThat(ConstraintViolations.format(validator.validate(example))).containsOnly("basic must be one of [one, two, three]");
    }

    @Test
    public void doesNotAllowBadElementsInList() {
        final OneOfValidatorTest.Example example = new OneOfValidatorTest.Example();
        example.basicList = Collections.singletonList("four");
        assertThat(ConstraintViolations.format(validator.validate(example))).containsOnly("basicList[0].<list element> must be one of [one, two, three]");
    }

    @Test
    public void optionallyIgnoresCase() throws Exception {
        final OneOfValidatorTest.Example example = new OneOfValidatorTest.Example();
        example.caseInsensitive = "ONE";
        assertThat(ConstraintViolations.format(validator.validate(example))).isEmpty();
    }

    @Test
    public void optionallyIgnoresWhitespace() throws Exception {
        final OneOfValidatorTest.Example example = new OneOfValidatorTest.Example();
        example.whitespaceInsensitive = "   one  ";
        assertThat(ConstraintViolations.format(validator.validate(example))).isEmpty();
    }
}

