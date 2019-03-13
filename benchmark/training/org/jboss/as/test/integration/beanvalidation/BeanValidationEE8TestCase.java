/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.beanvalidation;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.UnwrapByDefault;
import javax.validation.valueextraction.ValueExtractor;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A test validating the Bean Validation 2.0 support.
 *
 * @author <a href="mailto:guillaume@hibernate.org">Guillaume Smet</a>
 */
@RunWith(Arquillian.class)
public class BeanValidationEE8TestCase {
    @Test
    public void testMapKeySupport() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<BeanValidationEE8TestCase.MapKeyBean>> violations = validator.validate(BeanValidationEE8TestCase.MapKeyBean.valid());
        Assert.assertTrue(violations.isEmpty());
        violations = validator.validate(BeanValidationEE8TestCase.MapKeyBean.invalid());
        Assert.assertEquals(1, violations.size());
        ConstraintViolation<BeanValidationEE8TestCase.MapKeyBean> violation = violations.iterator().next();
        Assert.assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    public void testValueExtractor() {
        Validator validator = Validation.byDefaultProvider().configure().addValueExtractor(new BeanValidationEE8TestCase.ContainerValueExtractor()).buildValidatorFactory().getValidator();
        Set<ConstraintViolation<BeanValidationEE8TestCase.ContainerBean>> violations = validator.validate(BeanValidationEE8TestCase.ContainerBean.valid());
        Assert.assertTrue(violations.isEmpty());
        violations = validator.validate(BeanValidationEE8TestCase.ContainerBean.invalid());
        Assert.assertEquals(1, violations.size());
        ConstraintViolation<BeanValidationEE8TestCase.ContainerBean> violation = violations.iterator().next();
        Assert.assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    }

    private static class MapKeyBean {
        private Map<@NotNull
        String, String> mapProperty;

        private static BeanValidationEE8TestCase.MapKeyBean valid() {
            BeanValidationEE8TestCase.MapKeyBean validatedBean = new BeanValidationEE8TestCase.MapKeyBean();
            validatedBean.mapProperty = new HashMap<>();
            validatedBean.mapProperty.put("Paul Auster", "4 3 2 1");
            return validatedBean;
        }

        private static BeanValidationEE8TestCase.MapKeyBean invalid() {
            BeanValidationEE8TestCase.MapKeyBean validatedBean = new BeanValidationEE8TestCase.MapKeyBean();
            validatedBean.mapProperty = new HashMap<>();
            validatedBean.mapProperty.put(null, "4 3 2 1");
            return validatedBean;
        }
    }

    private static class ContainerBean {
        @NotNull
        private BeanValidationEE8TestCase.Container containerProperty;

        private static BeanValidationEE8TestCase.ContainerBean valid() {
            BeanValidationEE8TestCase.ContainerBean validatedBean = new BeanValidationEE8TestCase.ContainerBean();
            validatedBean.containerProperty = new BeanValidationEE8TestCase.Container("value");
            return validatedBean;
        }

        private static BeanValidationEE8TestCase.ContainerBean invalid() {
            BeanValidationEE8TestCase.ContainerBean validatedBean = new BeanValidationEE8TestCase.ContainerBean();
            validatedBean.containerProperty = new BeanValidationEE8TestCase.Container(null);
            return validatedBean;
        }
    }

    private static class Container {
        private String value;

        private Container(String value) {
            this.value = value;
        }

        private String getValue() {
            return value;
        }
    }

    @UnwrapByDefault
    private class ContainerValueExtractor implements ValueExtractor<BeanValidationEE8TestCase.@ExtractedValue(type = String.class)
    Container> {
        @Override
        public void extractValues(BeanValidationEE8TestCase.Container originalValue, ValueReceiver receiver) {
            receiver.value(null, originalValue.getValue());
        }
    }
}

