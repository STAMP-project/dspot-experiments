/**
 * Copyright (C) 2015 Square, Inc.
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
package keywhiz.api.validation;


import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.Test;


public class ValidX500NameValidatorTest {
    private static Validator validator;

    @Test
    public void acceptsValidX500Names() {
        ValidX500NameValidatorTest.X500Name name = new ValidX500NameValidatorTest.X500Name("cn=Sample CN,ou=people,dc=squareup,dc=com");
        Set<ConstraintViolation<ValidX500NameValidatorTest.X500Name>> violations = ValidX500NameValidatorTest.validator.validate(name);
        assertThat(violations).isEmpty();
    }

    @Test
    public void rejectsInvalidX500Names() {
        ValidX500NameValidatorTest.X500Name name = new ValidX500NameValidatorTest.X500Name("not an X500 name");
        Set<ConstraintViolation<ValidX500NameValidatorTest.X500Name>> violations = ValidX500NameValidatorTest.validator.validate(name);
        assertThat(violations).hasSize(1);
    }

    private static class X500Name {
        @ValidX500Name
        private final String name;

        public X500Name(String name) {
            this.name = name;
        }
    }
}

