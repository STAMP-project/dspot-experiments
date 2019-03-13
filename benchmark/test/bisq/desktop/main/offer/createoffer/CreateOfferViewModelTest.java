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
package bisq.desktop.main.offer.createoffer;


import bisq.core.btc.TxFeeEstimationService;
import bisq.core.btc.model.AddressEntry;
import bisq.core.btc.wallet.BsqWalletService;
import bisq.core.btc.wallet.BtcWalletService;
import bisq.core.payment.AccountAgeWitnessService;
import bisq.core.payment.PaymentAccount;
import bisq.core.provider.fee.FeeService;
import bisq.core.provider.price.PriceFeedService;
import bisq.core.user.Preferences;
import bisq.core.user.User;
import bisq.core.util.BsqFormatter;
import bisq.desktop.util.validation.SecurityDepositValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ BtcWalletService.class, AddressEntry.class, PriceFeedService.class, User.class, FeeService.class, CreateOfferDataModel.class, PaymentAccount.class, BsqWalletService.class, SecurityDepositValidator.class, AccountAgeWitnessService.class, BsqFormatter.class, Preferences.class, BsqWalletService.class, TxFeeEstimationService.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class CreateOfferViewModelTest {
    private CreateOfferViewModel model;

    @Test
    public void testSyncMinAmountWithAmountUntilChanged() {
        Assert.assertNull(model.amount.get());
        Assert.assertNull(model.minAmount.get());
        model.amount.set("0.0");
        Assert.assertEquals("0.0", model.amount.get());
        Assert.assertNull(model.minAmount.get());
        model.amount.set("0.03");
        Assert.assertEquals("0.03", model.amount.get());
        Assert.assertEquals("0.03", model.minAmount.get());
        model.amount.set("0.0312");
        Assert.assertEquals("0.0312", model.amount.get());
        Assert.assertEquals("0.0312", model.minAmount.get());
        model.minAmount.set("0.01");
        model.onFocusOutMinAmountTextField(true, false);
        Assert.assertEquals("0.01", model.minAmount.get());
        model.amount.set("0.0301");
        Assert.assertEquals("0.0301", model.amount.get());
        Assert.assertEquals("0.01", model.minAmount.get());
    }

    @Test
    public void testSyncMinAmountWithAmountWhenZeroCoinIsSet() {
        model.amount.set("0.03");
        Assert.assertEquals("0.03", model.amount.get());
        Assert.assertEquals("0.03", model.minAmount.get());
        model.minAmount.set("0.00");
        model.onFocusOutMinAmountTextField(true, false);
        model.amount.set("0.04");
        Assert.assertEquals("0.04", model.amount.get());
        Assert.assertEquals("0.04", model.minAmount.get());
    }

    @Test
    public void testSyncMinAmountWithAmountWhenSameValueIsSet() {
        model.amount.set("0.03");
        Assert.assertEquals("0.03", model.amount.get());
        Assert.assertEquals("0.03", model.minAmount.get());
        model.minAmount.set("0.03");
        model.onFocusOutMinAmountTextField(true, false);
        model.amount.set("0.04");
        Assert.assertEquals("0.04", model.amount.get());
        Assert.assertEquals("0.04", model.minAmount.get());
    }

    @Test
    public void testSyncMinAmountWithAmountWhenHigherMinAmountValueIsSet() {
        model.amount.set("0.03");
        Assert.assertEquals("0.03", model.amount.get());
        Assert.assertEquals("0.03", model.minAmount.get());
        model.minAmount.set("0.05");
        model.onFocusOutMinAmountTextField(true, false);
        Assert.assertEquals("0.05", model.amount.get());
        Assert.assertEquals("0.05", model.minAmount.get());
    }

    @Test
    public void testSyncPriceMarginWithVolumeAndFixedPrice() {
        model.amount.set("0.01");
        model.onFocusOutPriceAsPercentageTextField(true, false);// leave focus without changing

        Assert.assertEquals("0.00", model.marketPriceMargin.get());
        Assert.assertEquals("0.00000078", model.volume.get());
        Assert.assertEquals("12684.04500000", model.price.get());
    }
}

