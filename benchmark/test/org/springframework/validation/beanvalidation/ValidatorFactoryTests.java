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


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Payload;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class ValidatorFactoryTests {
    @Test
    public void testSimpleValidation() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ValidatorFactoryTests.ValidPerson person = new ValidatorFactoryTests.ValidPerson();
        Set<ConstraintViolation<ValidatorFactoryTests.ValidPerson>> result = validator.validate(person);
        Assert.assertEquals(2, result.size());
        for (ConstraintViolation<ValidatorFactoryTests.ValidPerson> cv : result) {
            String path = cv.getPropertyPath().toString();
            if (("name".equals(path)) || ("address.street".equals(path))) {
                Assert.assertTrue(((cv.getConstraintDescriptor().getAnnotation()) instanceof NotNull));
            } else {
                Assert.fail((("Invalid constraint violation with path '" + path) + "'"));
            }
        }
        Validator nativeValidator = validator.unwrap(Validator.class);
        Assert.assertTrue(nativeValidator.getClass().getName().startsWith("org.hibernate"));
        Assert.assertTrue(((validator.unwrap(ValidatorFactory.class)) instanceof HibernateValidatorFactory));
        Assert.assertTrue(((validator.unwrap(HibernateValidatorFactory.class)) instanceof HibernateValidatorFactory));
        validator.destroy();
    }

    @Test
    public void testSimpleValidationWithCustomProvider() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.afterPropertiesSet();
        ValidatorFactoryTests.ValidPerson person = new ValidatorFactoryTests.ValidPerson();
        Set<ConstraintViolation<ValidatorFactoryTests.ValidPerson>> result = validator.validate(person);
        Assert.assertEquals(2, result.size());
        for (ConstraintViolation<ValidatorFactoryTests.ValidPerson> cv : result) {
            String path = cv.getPropertyPath().toString();
            if (("name".equals(path)) || ("address.street".equals(path))) {
                Assert.assertTrue(((cv.getConstraintDescriptor().getAnnotation()) instanceof NotNull));
            } else {
                Assert.fail((("Invalid constraint violation with path '" + path) + "'"));
            }
        }
        Validator nativeValidator = validator.unwrap(Validator.class);
        Assert.assertTrue(nativeValidator.getClass().getName().startsWith("org.hibernate"));
        Assert.assertTrue(((validator.unwrap(ValidatorFactory.class)) instanceof HibernateValidatorFactory));
        Assert.assertTrue(((validator.unwrap(HibernateValidatorFactory.class)) instanceof HibernateValidatorFactory));
        validator.destroy();
    }

    @Test
    public void testSimpleValidationWithClassLevel() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ValidatorFactoryTests.ValidPerson person = new ValidatorFactoryTests.ValidPerson();
        person.setName("Juergen");
        person.getAddress().setStreet("Juergen's Street");
        Set<ConstraintViolation<ValidatorFactoryTests.ValidPerson>> result = validator.validate(person);
        Assert.assertEquals(1, result.size());
        Iterator<ConstraintViolation<ValidatorFactoryTests.ValidPerson>> iterator = result.iterator();
        ConstraintViolation<?> cv = iterator.next();
        Assert.assertEquals("", cv.getPropertyPath().toString());
        Assert.assertTrue(((cv.getConstraintDescriptor().getAnnotation()) instanceof ValidatorFactoryTests.NameAddressValid));
    }

    @Test
    public void testSpringValidationFieldType() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ValidatorFactoryTests.ValidPerson person = new ValidatorFactoryTests.ValidPerson();
        person.setName("Phil");
        person.getAddress().setStreet("Phil's Street");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(person, "person");
        validator.validate(person, errors);
        Assert.assertEquals(1, errors.getErrorCount());
        Assert.assertThat("Field/Value type mismatch", errors.getFieldError("address").getRejectedValue(), instanceOf(ValidatorFactoryTests.ValidAddress.class));
    }

    @Test
    public void testSpringValidation() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ValidatorFactoryTests.ValidPerson person = new ValidatorFactoryTests.ValidPerson();
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(person, "person");
        validator.validate(person, result);
        Assert.assertEquals(2, result.getErrorCount());
        FieldError fieldError = result.getFieldError("name");
        Assert.assertEquals("name", fieldError.getField());
        List<String> errorCodes = Arrays.asList(fieldError.getCodes());
        Assert.assertEquals(4, errorCodes.size());
        Assert.assertTrue(errorCodes.contains("NotNull.person.name"));
        Assert.assertTrue(errorCodes.contains("NotNull.name"));
        Assert.assertTrue(errorCodes.contains("NotNull.java.lang.String"));
        Assert.assertTrue(errorCodes.contains("NotNull"));
        fieldError = result.getFieldError("address.street");
        Assert.assertEquals("address.street", fieldError.getField());
        errorCodes = Arrays.asList(fieldError.getCodes());
        Assert.assertEquals(5, errorCodes.size());
        Assert.assertTrue(errorCodes.contains("NotNull.person.address.street"));
        Assert.assertTrue(errorCodes.contains("NotNull.address.street"));
        Assert.assertTrue(errorCodes.contains("NotNull.street"));
        Assert.assertTrue(errorCodes.contains("NotNull.java.lang.String"));
        Assert.assertTrue(errorCodes.contains("NotNull"));
    }

    @Test
    public void testSpringValidationWithClassLevel() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ValidatorFactoryTests.ValidPerson person = new ValidatorFactoryTests.ValidPerson();
        person.setName("Juergen");
        person.getAddress().setStreet("Juergen's Street");
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(person, "person");
        validator.validate(person, result);
        Assert.assertEquals(1, result.getErrorCount());
        ObjectError globalError = result.getGlobalError();
        List<String> errorCodes = Arrays.asList(globalError.getCodes());
        Assert.assertEquals(2, errorCodes.size());
        Assert.assertTrue(errorCodes.contains("NameAddressValid.person"));
        Assert.assertTrue(errorCodes.contains("NameAddressValid"));
    }

    @Test
    public void testSpringValidationWithAutowiredValidator() {
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(LocalValidatorFactoryBean.class);
        LocalValidatorFactoryBean validator = ctx.getBean(LocalValidatorFactoryBean.class);
        ValidatorFactoryTests.ValidPerson person = new ValidatorFactoryTests.ValidPerson();
        person.expectsAutowiredValidator = true;
        person.setName("Juergen");
        person.getAddress().setStreet("Juergen's Street");
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(person, "person");
        validator.validate(person, result);
        Assert.assertEquals(1, result.getErrorCount());
        ObjectError globalError = result.getGlobalError();
        List<String> errorCodes = Arrays.asList(globalError.getCodes());
        Assert.assertEquals(2, errorCodes.size());
        Assert.assertTrue(errorCodes.contains("NameAddressValid.person"));
        Assert.assertTrue(errorCodes.contains("NameAddressValid"));
        ctx.close();
    }

    @Test
    public void testSpringValidationWithErrorInListElement() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ValidatorFactoryTests.ValidPerson person = new ValidatorFactoryTests.ValidPerson();
        person.getAddressList().add(new ValidatorFactoryTests.ValidAddress());
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(person, "person");
        validator.validate(person, result);
        Assert.assertEquals(3, result.getErrorCount());
        FieldError fieldError = result.getFieldError("name");
        Assert.assertEquals("name", fieldError.getField());
        fieldError = result.getFieldError("address.street");
        Assert.assertEquals("address.street", fieldError.getField());
        fieldError = result.getFieldError("addressList[0].street");
        Assert.assertEquals("addressList[0].street", fieldError.getField());
    }

    @Test
    public void testSpringValidationWithErrorInSetElement() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ValidatorFactoryTests.ValidPerson person = new ValidatorFactoryTests.ValidPerson();
        person.getAddressSet().add(new ValidatorFactoryTests.ValidAddress());
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(person, "person");
        validator.validate(person, result);
        Assert.assertEquals(3, result.getErrorCount());
        FieldError fieldError = result.getFieldError("name");
        Assert.assertEquals("name", fieldError.getField());
        fieldError = result.getFieldError("address.street");
        Assert.assertEquals("address.street", fieldError.getField());
        fieldError = result.getFieldError("addressSet[].street");
        Assert.assertEquals("addressSet[].street", fieldError.getField());
    }

    @Test
    public void testInnerBeanValidation() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ValidatorFactoryTests.MainBean mainBean = new ValidatorFactoryTests.MainBean();
        Errors errors = new BeanPropertyBindingResult(mainBean, "mainBean");
        validator.validate(mainBean, errors);
        Object rejected = errors.getFieldValue("inner.value");
        Assert.assertNull(rejected);
    }

    @Test
    public void testValidationWithOptionalField() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ValidatorFactoryTests.MainBeanWithOptional mainBean = new ValidatorFactoryTests.MainBeanWithOptional();
        Errors errors = new BeanPropertyBindingResult(mainBean, "mainBean");
        validator.validate(mainBean, errors);
        Object rejected = errors.getFieldValue("inner.value");
        Assert.assertNull(rejected);
    }

    @Test
    public void testListValidation() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ValidatorFactoryTests.ListContainer listContainer = new ValidatorFactoryTests.ListContainer();
        listContainer.addString("A");
        listContainer.addString("X");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(listContainer, "listContainer");
        errors.initConversion(new DefaultConversionService());
        validator.validate(listContainer, errors);
        FieldError fieldError = errors.getFieldError("list[1]");
        Assert.assertEquals("X", errors.getFieldValue("list[1]"));
    }

    @ValidatorFactoryTests.NameAddressValid
    public static class ValidPerson {
        @NotNull
        private String name;

        @Valid
        private ValidatorFactoryTests.ValidAddress address = new ValidatorFactoryTests.ValidAddress();

        @Valid
        private List<ValidatorFactoryTests.ValidAddress> addressList = new LinkedList<>();

        @Valid
        private Set<ValidatorFactoryTests.ValidAddress> addressSet = new LinkedHashSet<>();

        public boolean expectsAutowiredValidator = false;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ValidatorFactoryTests.ValidAddress getAddress() {
            return address;
        }

        public void setAddress(ValidatorFactoryTests.ValidAddress address) {
            this.address = address;
        }

        public List<ValidatorFactoryTests.ValidAddress> getAddressList() {
            return addressList;
        }

        public void setAddressList(List<ValidatorFactoryTests.ValidAddress> addressList) {
            this.addressList = addressList;
        }

        public Set<ValidatorFactoryTests.ValidAddress> getAddressSet() {
            return addressSet;
        }

        public void setAddressSet(Set<ValidatorFactoryTests.ValidAddress> addressSet) {
            this.addressSet = addressSet;
        }
    }

    public static class ValidAddress {
        @NotNull
        private String street;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = ValidatorFactoryTests.NameAddressValidator.class)
    public @interface NameAddressValid {
        String message() default "Street must not contain name";

        Class<?>[] groups() default {  };

        Class<?>[] payload() default {  };
    }

    public static class NameAddressValidator implements ConstraintValidator<ValidatorFactoryTests.NameAddressValid, ValidatorFactoryTests.ValidPerson> {
        @Autowired
        private Environment environment;

        @Override
        public void initialize(ValidatorFactoryTests.NameAddressValid constraintAnnotation) {
        }

        @Override
        public boolean isValid(ValidatorFactoryTests.ValidPerson value, ConstraintValidatorContext context) {
            if (value.expectsAutowiredValidator) {
                Assert.assertNotNull(this.environment);
            }
            boolean valid = ((value.name) == null) || (!(value.address.street.contains(value.name)));
            if ((!valid) && ("Phil".equals(value.name))) {
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()).addPropertyNode("address").addConstraintViolation().disableDefaultConstraintViolation();
            }
            return valid;
        }
    }

    public static class MainBean {
        @ValidatorFactoryTests.InnerValid
        private ValidatorFactoryTests.InnerBean inner = new ValidatorFactoryTests.InnerBean();

        public ValidatorFactoryTests.InnerBean getInner() {
            return inner;
        }
    }

    public static class MainBeanWithOptional {
        @ValidatorFactoryTests.InnerValid
        private ValidatorFactoryTests.InnerBean inner = new ValidatorFactoryTests.InnerBean();

        public Optional<ValidatorFactoryTests.InnerBean> getInner() {
            return Optional.ofNullable(inner);
        }
    }

    public static class InnerBean {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @Constraint(validatedBy = ValidatorFactoryTests.InnerValidator.class)
    public @interface InnerValid {
        String message() default "NOT VALID";

        Class<?>[] groups() default {  };

        Class<? extends Payload>[] payload() default {  };
    }

    public static class InnerValidator implements ConstraintValidator<ValidatorFactoryTests.InnerValid, ValidatorFactoryTests.InnerBean> {
        @Override
        public void initialize(ValidatorFactoryTests.InnerValid constraintAnnotation) {
        }

        @Override
        public boolean isValid(ValidatorFactoryTests.InnerBean bean, ConstraintValidatorContext context) {
            context.disableDefaultConstraintViolation();
            if ((bean.getValue()) == null) {
                context.buildConstraintViolationWithTemplate("NULL").addPropertyNode("value").addConstraintViolation();
                return false;
            }
            return true;
        }
    }

    public static class ListContainer {
        @ValidatorFactoryTests.NotXList
        private List<String> list = new LinkedList<>();

        public void addString(String value) {
            list.add(value);
        }

        public List<String> getList() {
            return list;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @Constraint(validatedBy = ValidatorFactoryTests.NotXListValidator.class)
    public @interface NotXList {
        String message() default "Should not be X";

        Class<?>[] groups() default {  };

        Class<? extends Payload>[] payload() default {  };
    }

    public static class NotXListValidator implements ConstraintValidator<ValidatorFactoryTests.NotXList, List<String>> {
        @Override
        public void initialize(ValidatorFactoryTests.NotXList constraintAnnotation) {
        }

        @Override
        public boolean isValid(List<String> list, ConstraintValidatorContext context) {
            context.disableDefaultConstraintViolation();
            boolean valid = true;
            for (int i = 0; i < (list.size()); i++) {
                if ("X".equals(list.get(i))) {
                    context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()).addBeanNode().inIterable().atIndex(i).addConstraintViolation();
                    valid = false;
                }
            }
            return valid;
        }
    }
}

