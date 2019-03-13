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


public class RegexValidatorTest {
    @Test
    public void validate() throws Exception {
        RegexValidator validator = new RegexValidator();
        Assert.assertTrue(validator.validate("").isValid);
        Assert.assertTrue(validator.validate(null).isValid);
        Assert.assertTrue(validator.validate("123456789").isValid);
        validator.setPattern("[a-z]*");
        Assert.assertTrue(validator.validate("abcdefghijklmnopqrstuvwxyz").isValid);
        Assert.assertTrue(validator.validate("").isValid);
        Assert.assertTrue(validator.validate(null).isValid);
        Assert.assertFalse(validator.validate("123").isValid);// invalid

        Assert.assertFalse(validator.validate("ABC").isValid);// invalid

        validator.setPattern("[a-z]+");
        Assert.assertTrue(validator.validate("abcdefghijklmnopqrstuvwxyz").isValid);
        Assert.assertFalse(validator.validate("123").isValid);// invalid

        Assert.assertFalse(validator.validate("ABC").isValid);// invalid

        Assert.assertFalse(validator.validate("").isValid);// invalid

        Assert.assertFalse(validator.validate(null).isValid);// invalid

    }
}

