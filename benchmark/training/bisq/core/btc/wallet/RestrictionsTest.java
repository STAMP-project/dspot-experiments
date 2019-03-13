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
package bisq.core.btc.wallet;


import org.bitcoinj.core.Coin;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("ConstantConditions")
public class RestrictionsTest {
    @Test
    public void testIsMinSpendableAmount() {
        Coin amount = null;
        Coin txFee = Coin.valueOf(20000);
        amount = Coin.ZERO;
        Assert.assertFalse(Restrictions.isAboveDust(amount.subtract(txFee)));
        amount = txFee;
        Assert.assertFalse(Restrictions.isAboveDust(amount.subtract(txFee)));
        amount = Restrictions.getMinNonDustOutput();
        Assert.assertFalse(Restrictions.isAboveDust(amount.subtract(txFee)));
        amount = txFee.add(Restrictions.getMinNonDustOutput());
        Assert.assertTrue(Restrictions.isAboveDust(amount.subtract(txFee)));
        amount = txFee.add(Restrictions.getMinNonDustOutput()).add(Coin.valueOf(1));
        Assert.assertTrue(Restrictions.isAboveDust(amount.subtract(txFee)));
    }
}

