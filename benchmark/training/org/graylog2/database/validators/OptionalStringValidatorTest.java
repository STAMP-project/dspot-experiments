/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.database.validators;


import ValidationResult.ValidationFailed;
import ValidationResult.ValidationPassed;
import org.junit.Test;


public class OptionalStringValidatorTest {
    private OptionalStringValidator validator = new OptionalStringValidator();

    @Test
    public void validateNull() {
        assertThat(validator.validate(null)).isInstanceOf(ValidationPassed.class);
    }

    @Test
    public void validateEmptyString() {
        assertThat(validator.validate("")).isInstanceOf(ValidationPassed.class);
    }

    @Test
    public void validateString() {
        assertThat(validator.validate("foobar")).isInstanceOf(ValidationPassed.class);
    }

    @Test
    public void validateNonString() {
        assertThat(validator.validate(new Object())).isInstanceOf(ValidationFailed.class);
    }
}

