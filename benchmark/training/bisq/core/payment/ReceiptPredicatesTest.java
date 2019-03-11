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
package bisq.core.payment;


import PaymentMethod.SAME_BANK;
import PaymentMethod.SEPA;
import PaymentMethod.SEPA_INSTANT;
import bisq.core.locale.CryptoCurrency;
import bisq.core.offer.Offer;
import bisq.core.payment.payload.PaymentMethod;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ NationalBankAccount.class, SepaAccount.class, SepaInstantAccount.class, PaymentMethod.class, SameBankAccount.class, SpecificBanksAccount.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class ReceiptPredicatesTest {
    private final ReceiptPredicates predicates = new ReceiptPredicates();

    @Test
    public void testIsMatchingCurrency() {
        Offer offer = Mockito.mock(Offer.class);
        Mockito.when(offer.getCurrencyCode()).thenReturn("USD");
        PaymentAccount account = Mockito.mock(PaymentAccount.class);
        Mockito.when(account.getTradeCurrencies()).thenReturn(Lists.newArrayList(new CryptoCurrency("BTC", "Bitcoin"), new CryptoCurrency("ETH", "Ether")));
        Assert.assertFalse(predicates.isMatchingCurrency(offer, account));
    }

    @Test
    public void testIsMatchingSepaOffer() {
        Offer offer = Mockito.mock(Offer.class);
        PaymentMethod.SEPA = Mockito.mock(PaymentMethod.class);
        Mockito.when(offer.getPaymentMethod()).thenReturn(SEPA);
        Assert.assertTrue(predicates.isMatchingSepaOffer(offer, Mockito.mock(SepaInstantAccount.class)));
        Assert.assertTrue(predicates.isMatchingSepaOffer(offer, Mockito.mock(SepaAccount.class)));
    }

    @Test
    public void testIsMatchingSepaInstant() {
        Offer offer = Mockito.mock(Offer.class);
        PaymentMethod.SEPA_INSTANT = Mockito.mock(PaymentMethod.class);
        Mockito.when(offer.getPaymentMethod()).thenReturn(SEPA_INSTANT);
        Assert.assertTrue(predicates.isMatchingSepaInstant(offer, Mockito.mock(SepaInstantAccount.class)));
        Assert.assertFalse(predicates.isMatchingSepaInstant(offer, Mockito.mock(SepaAccount.class)));
    }

    @Test
    public void testIsMatchingCountryCodes() {
        CountryBasedPaymentAccount account = Mockito.mock(CountryBasedPaymentAccount.class);
        Mockito.when(account.getCountry()).thenReturn(null);
        Assert.assertFalse(predicates.isMatchingCountryCodes(Mockito.mock(Offer.class), account));
    }

    @Test
    public void testIsSameOrSpecificBank() {
        PaymentMethod.SAME_BANK = Mockito.mock(PaymentMethod.class);
        Offer offer = Mockito.mock(Offer.class);
        Mockito.when(offer.getPaymentMethod()).thenReturn(SAME_BANK);
        Assert.assertTrue(predicates.isOfferRequireSameOrSpecificBank(offer, Mockito.mock(NationalBankAccount.class)));
    }

    @Test
    public void testIsEqualPaymentMethods() {
        PaymentMethod method = PaymentMethod.getDummyPaymentMethod("1");
        Offer offer = Mockito.mock(Offer.class);
        Mockito.when(offer.getPaymentMethod()).thenReturn(method);
        PaymentAccount account = Mockito.mock(PaymentAccount.class);
        Mockito.when(account.getPaymentMethod()).thenReturn(method);
        Assert.assertTrue(predicates.isEqualPaymentMethods(offer, account));
    }
}

