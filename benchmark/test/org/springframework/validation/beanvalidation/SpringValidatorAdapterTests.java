/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.validation.beanvalidation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Payload;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;


/**
 *
 *
 * @author Kazuki Shimizu
 * @author Juergen Hoeller
 */
public class SpringValidatorAdapterTests {
    private final Validator nativeValidator = Validation.buildDefaultValidatorFactory().getValidator();

    private final SpringValidatorAdapter validatorAdapter = new SpringValidatorAdapter(nativeValidator);

    private final StaticMessageSource messageSource = new StaticMessageSource();

    @Test
    public void testUnwrap() {
        Validator nativeValidator = validatorAdapter.unwrap(Validator.class);
        Assert.assertSame(this.nativeValidator, nativeValidator);
    }

    // SPR-13406
    @Test
    public void testNoStringArgumentValue() {
        SpringValidatorAdapterTests.TestBean testBean = new SpringValidatorAdapterTests.TestBean();
        testBean.setPassword("pass");
        testBean.setConfirmPassword("pass");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(testBean, "testBean");
        validatorAdapter.validate(testBean, errors);
        Assert.assertThat(errors.getFieldErrorCount("password"), Is.is(1));
        Assert.assertThat(errors.getFieldValue("password"), Is.is("pass"));
        FieldError error = errors.getFieldError("password");
        Assert.assertNotNull(error);
        Assert.assertThat(messageSource.getMessage(error, Locale.ENGLISH), Is.is("Size of Password is must be between 8 and 128"));
        Assert.assertTrue(error.contains(ConstraintViolation.class));
        Assert.assertThat(error.unwrap(ConstraintViolation.class).getPropertyPath().toString(), Is.is("password"));
    }

    // SPR-13406
    @Test
    public void testApplyMessageSourceResolvableToStringArgumentValueWithResolvedLogicalFieldName() {
        SpringValidatorAdapterTests.TestBean testBean = new SpringValidatorAdapterTests.TestBean();
        testBean.setPassword("password");
        testBean.setConfirmPassword("PASSWORD");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(testBean, "testBean");
        validatorAdapter.validate(testBean, errors);
        Assert.assertThat(errors.getFieldErrorCount("password"), Is.is(1));
        Assert.assertThat(errors.getFieldValue("password"), Is.is("password"));
        FieldError error = errors.getFieldError("password");
        Assert.assertNotNull(error);
        Assert.assertThat(messageSource.getMessage(error, Locale.ENGLISH), Is.is("Password must be same value as Password(Confirm)"));
        Assert.assertTrue(error.contains(ConstraintViolation.class));
        Assert.assertThat(error.unwrap(ConstraintViolation.class).getPropertyPath().toString(), Is.is("password"));
    }

    // SPR-13406
    @Test
    public void testApplyMessageSourceResolvableToStringArgumentValueWithUnresolvedLogicalFieldName() {
        SpringValidatorAdapterTests.TestBean testBean = new SpringValidatorAdapterTests.TestBean();
        testBean.setEmail("test@example.com");
        testBean.setConfirmEmail("TEST@EXAMPLE.IO");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(testBean, "testBean");
        validatorAdapter.validate(testBean, errors);
        Assert.assertThat(errors.getFieldErrorCount("email"), Is.is(1));
        Assert.assertThat(errors.getFieldValue("email"), Is.is("test@example.com"));
        Assert.assertThat(errors.getFieldErrorCount("confirmEmail"), Is.is(1));
        FieldError error1 = errors.getFieldError("email");
        FieldError error2 = errors.getFieldError("confirmEmail");
        Assert.assertNotNull(error1);
        Assert.assertNotNull(error2);
        Assert.assertThat(messageSource.getMessage(error1, Locale.ENGLISH), Is.is("email must be same value as confirmEmail"));
        Assert.assertThat(messageSource.getMessage(error2, Locale.ENGLISH), Is.is("Email required"));
        Assert.assertTrue(error1.contains(ConstraintViolation.class));
        Assert.assertThat(error1.unwrap(ConstraintViolation.class).getPropertyPath().toString(), Is.is("email"));
        Assert.assertTrue(error2.contains(ConstraintViolation.class));
        Assert.assertThat(error2.unwrap(ConstraintViolation.class).getPropertyPath().toString(), Is.is("confirmEmail"));
    }

    // SPR-15123
    @Test
    public void testApplyMessageSourceResolvableToStringArgumentValueWithAlwaysUseMessageFormat() {
        messageSource.setAlwaysUseMessageFormat(true);
        SpringValidatorAdapterTests.TestBean testBean = new SpringValidatorAdapterTests.TestBean();
        testBean.setEmail("test@example.com");
        testBean.setConfirmEmail("TEST@EXAMPLE.IO");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(testBean, "testBean");
        validatorAdapter.validate(testBean, errors);
        Assert.assertThat(errors.getFieldErrorCount("email"), Is.is(1));
        Assert.assertThat(errors.getFieldValue("email"), Is.is("test@example.com"));
        Assert.assertThat(errors.getFieldErrorCount("confirmEmail"), Is.is(1));
        FieldError error1 = errors.getFieldError("email");
        FieldError error2 = errors.getFieldError("confirmEmail");
        Assert.assertNotNull(error1);
        Assert.assertNotNull(error2);
        Assert.assertThat(messageSource.getMessage(error1, Locale.ENGLISH), Is.is("email must be same value as confirmEmail"));
        Assert.assertThat(messageSource.getMessage(error2, Locale.ENGLISH), Is.is("Email required"));
        Assert.assertTrue(error1.contains(ConstraintViolation.class));
        Assert.assertThat(error1.unwrap(ConstraintViolation.class).getPropertyPath().toString(), Is.is("email"));
        Assert.assertTrue(error2.contains(ConstraintViolation.class));
        Assert.assertThat(error2.unwrap(ConstraintViolation.class).getPropertyPath().toString(), Is.is("confirmEmail"));
    }

    // SPR-16177
    @Test
    public void testWithList() {
        SpringValidatorAdapterTests.Parent parent = new SpringValidatorAdapterTests.Parent();
        parent.setName("Parent whit list");
        parent.getChildList().addAll(createChildren(parent));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(parent, "parent");
        validatorAdapter.validate(parent, errors);
        Assert.assertTrue(((errors.getErrorCount()) > 0));
    }

    // SPR-16177
    @Test
    public void testWithSet() {
        SpringValidatorAdapterTests.Parent parent = new SpringValidatorAdapterTests.Parent();
        parent.setName("Parent with set");
        parent.getChildSet().addAll(createChildren(parent));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(parent, "parent");
        validatorAdapter.validate(parent, errors);
        Assert.assertTrue(((errors.getErrorCount()) > 0));
    }

    @SpringValidatorAdapterTests.Same(field = "password", comparingField = "confirmPassword")
    @SpringValidatorAdapterTests.Same(field = "email", comparingField = "confirmEmail")
    static class TestBean {
        @Size(min = 8, max = 128)
        private String password;

        private String confirmPassword;

        private String email;

        @Pattern(regexp = "[\\p{L} -]*", message = "Email required")
        private String confirmEmail;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getConfirmEmail() {
            return confirmEmail;
        }

        public void setConfirmEmail(String confirmEmail) {
            this.confirmEmail = confirmEmail;
        }
    }

    @Documented
    @Constraint(validatedBy = { SpringValidatorAdapterTests.SameValidator.class })
    @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(SpringValidatorAdapterTests.SameGroup.class)
    @interface Same {
        String message() default "{org.springframework.validation.beanvalidation.Same.message}";

        Class<?>[] groups() default {  };

        Class<? extends Payload>[] payload() default {  };

        String field();

        String comparingField();

        @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            SpringValidatorAdapterTests.Same[] value();
        }
    }

    @Documented
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
    @interface SameGroup {
        SpringValidatorAdapterTests.Same[] value();
    }

    public static class SameValidator implements ConstraintValidator<SpringValidatorAdapterTests.Same, Object> {
        private String field;

        private String comparingField;

        private String message;

        public void initialize(SpringValidatorAdapterTests.Same constraintAnnotation) {
            field = constraintAnnotation.field();
            comparingField = constraintAnnotation.comparingField();
            message = constraintAnnotation.message();
        }

        public boolean isValid(Object value, ConstraintValidatorContext context) {
            BeanWrapper beanWrapper = new BeanWrapperImpl(value);
            Object fieldValue = beanWrapper.getPropertyValue(field);
            Object comparingFieldValue = beanWrapper.getPropertyValue(comparingField);
            boolean matched = ObjectUtils.nullSafeEquals(fieldValue, comparingFieldValue);
            if (matched) {
                return true;
            } else {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addPropertyNode(field).addConstraintViolation();
                return false;
            }
        }
    }

    public static class Parent {
        private Integer id;

        @NotNull
        private String name;

        @Valid
        private Set<SpringValidatorAdapterTests.Child> childSet = new LinkedHashSet<>();

        @Valid
        private java.util.List<SpringValidatorAdapterTests.Child> childList = new LinkedList<>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<SpringValidatorAdapterTests.Child> getChildSet() {
            return childSet;
        }

        public void setChildSet(Set<SpringValidatorAdapterTests.Child> childSet) {
            this.childSet = childSet;
        }

        public java.util.List<SpringValidatorAdapterTests.Child> getChildList() {
            return childList;
        }

        public void setChildList(java.util.List<SpringValidatorAdapterTests.Child> childList) {
            this.childList = childList;
        }
    }

    @SpringValidatorAdapterTests.AnythingValid
    public static class Child {
        private Integer id;

        @NotNull
        private String name;

        @NotNull
        private Integer age;

        @NotNull
        private SpringValidatorAdapterTests.Parent parent;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public SpringValidatorAdapterTests.Parent getParent() {
            return parent;
        }

        public void setParent(SpringValidatorAdapterTests.Parent parent) {
            this.parent = parent;
        }
    }

    @Constraint(validatedBy = SpringValidatorAdapterTests.AnythingValidator.class)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnythingValid {
        String message() default "{AnythingValid.message}";

        Class<?>[] groups() default {  };

        Class<? extends Payload>[] payload() default {  };
    }

    public static class AnythingValidator implements ConstraintValidator<SpringValidatorAdapterTests.AnythingValid, Object> {
        private static final String ID = "id";

        @Override
        public void initialize(SpringValidatorAdapterTests.AnythingValid constraintAnnotation) {
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            java.util.List<Field> fieldsErros = new ArrayList<>();
            Arrays.asList(value.getClass().getDeclaredFields()).forEach(( f) -> {
                f.setAccessible(true);
                try {
                    if ((!(f.getName().equals(SpringValidatorAdapterTests.AnythingValidator.ID))) && ((f.get(value)) == null)) {
                        fieldsErros.add(f);
                        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()).addPropertyNode(f.getName()).addConstraintViolation();
                    }
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException(ex);
                }
            });
            return fieldsErros.isEmpty();
        }
    }
}

