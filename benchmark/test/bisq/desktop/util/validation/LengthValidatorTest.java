/**
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */
package bisq.desktop.util.validation;


import org.junit.Assert;
import org.junit.Test;


public class LengthValidatorTest {
    @Test
    public void validate() throws Exception {
        LengthValidator validator = new LengthValidator();
        Assert.assertTrue(validator.validate("").isValid);
        Assert.assertTrue(validator.validate(null).isValid);
        Assert.assertTrue(validator.validate("123456789").isValid);
        validator.setMinLength(2);
        validator.setMaxLength(5);
        Assert.assertTrue(validator.validate("12").isValid);
        Assert.assertTrue(validator.validate("12345").isValid);
        Assert.assertFalse(validator.validate("1").isValid);// too short

        Assert.assertFalse(validator.validate("").isValid);// too short

        Assert.assertFalse(validator.validate(null).isValid);// too short

        Assert.assertFalse(validator.validate("123456789").isValid);// too long

        LengthValidator validator2 = new LengthValidator(2, 5);
        Assert.assertTrue(validator2.validate("12").isValid);
        Assert.assertTrue(validator2.validate("12345").isValid);
        Assert.assertFalse(validator2.validate("1").isValid);// too short

        Assert.assertFalse(validator2.validate("").isValid);// too short

        Assert.assertFalse(validator2.validate(null).isValid);// too short

        Assert.assertFalse(validator2.validate("123456789").isValid);// too long

    }
}

