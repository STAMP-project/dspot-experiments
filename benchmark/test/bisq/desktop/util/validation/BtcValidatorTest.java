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


import Coin.SATOSHI;
import bisq.core.util.BSFormatter;
import org.junit.Assert;
import org.junit.Test;


public class BtcValidatorTest {
    @Test
    public void testIsValid() {
        BtcValidator validator = new BtcValidator(new BSFormatter());
        Assert.assertTrue(validator.validate("1").isValid);
        Assert.assertTrue(validator.validate("0,1").isValid);
        Assert.assertTrue(validator.validate("0.1").isValid);
        Assert.assertTrue(validator.validate(",1").isValid);
        Assert.assertTrue(validator.validate(".1").isValid);
        Assert.assertTrue(validator.validate("0.12345678").isValid);
        Assert.assertFalse(validator.validate(SATOSHI.toPlainString()).isValid);// below dust

        Assert.assertFalse(validator.validate(null).isValid);
        Assert.assertFalse(validator.validate("").isValid);
        Assert.assertFalse(validator.validate("0").isValid);
        Assert.assertFalse(validator.validate("0.0").isValid);
        Assert.assertFalse(validator.validate("0,1,1").isValid);
        Assert.assertFalse(validator.validate("0.1.1").isValid);
        Assert.assertFalse(validator.validate("0,000.1").isValid);
        Assert.assertFalse(validator.validate("0.000,1").isValid);
        Assert.assertFalse(validator.validate("0.123456789").isValid);
        Assert.assertFalse(validator.validate("-1").isValid);
        // assertFalse(validator.validate(NetworkParameters.MAX_MONEY.toPlainString()).isValid);
    }
}

