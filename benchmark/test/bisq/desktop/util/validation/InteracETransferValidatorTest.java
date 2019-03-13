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


public class InteracETransferValidatorTest {
    @Test
    public void validate() throws Exception {
        InteracETransferValidator validator = new InteracETransferValidator(new EmailValidator(), new InteracETransferQuestionValidator(new LengthValidator(), new RegexValidator()), new InteracETransferAnswerValidator(new LengthValidator(), new RegexValidator()));
        Assert.assertTrue(validator.validate("name@domain.tld").isValid);
        Assert.assertTrue(validator.validate("n1.n2@c.dd").isValid);
        Assert.assertTrue(validator.validate("+1 236 123-4567").isValid);
        Assert.assertTrue(validator.validate("15061234567").isValid);
        Assert.assertTrue(validator.validate("1 289 784 2134").isValid);
        Assert.assertTrue(validator.validate("+1-514-654-7412").isValid);
        Assert.assertFalse(validator.validate("abc@.de").isValid);// Domain name missing

        Assert.assertFalse(validator.validate("abc@d.e").isValid);// TLD too short

        Assert.assertFalse(validator.validate("2361234567").isValid);// Prefix for North America missing (often required for local calls as well)

        Assert.assertFalse(validator.validate("+150612345678").isValid);// Too long

        Assert.assertFalse(validator.validate("1289784213").isValid);// Too short

        Assert.assertFalse(validator.validate("+1 555 123-4567").isValid);// Non-Canadian area code

        Assert.assertFalse(validator.validate("+1 236 1234-567").isValid);// Wrong grouping

    }
}

