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


public class LimitedOptionalStringValidatorTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeMaxLength() {
        new LimitedOptionalStringValidator((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroMaxLength() {
        new LimitedOptionalStringValidator(0);
    }

    @Test
    public void testValidateNullValue() {
        assertThat(new LimitedOptionalStringValidator(1).validate(null)).isInstanceOf(ValidationPassed.class);
    }

    @Test
    public void testValidateEmptyValue() {
        assertThat(new LimitedOptionalStringValidator(1).validate("")).isInstanceOf(ValidationPassed.class);
    }

    @Test
    public void testValidateLongString() {
        assertThat(new LimitedOptionalStringValidator(1).validate("12")).isInstanceOf(ValidationFailed.class);
    }

    @Test
    public void testValidateNoString() {
        assertThat(new LimitedOptionalStringValidator(1).validate(123)).isInstanceOf(ValidationFailed.class);
    }

    @Test
    public void testValidateMaxLengthString() {
        assertThat(new LimitedOptionalStringValidator(5).validate("12345")).isInstanceOf(ValidationPassed.class);
    }
}

