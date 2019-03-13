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


import PaymentMethod.MONEY_GRAM;
import PaymentMethod.WESTERN_UNION;
import bisq.core.offer.Offer;
import bisq.core.payment.payload.PaymentMethod;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ SpecificBanksAccount.class, SameBankAccount.class, NationalBankAccount.class, MoneyGramAccount.class, WesternUnionAccount.class, CashDepositAccount.class, PaymentMethod.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class ReceiptValidatorTest {
    private ReceiptValidator validator;

    private PaymentAccount account;

    private Offer offer;

    private ReceiptPredicates predicates;

    @Test
    public void testIsValidWhenCurrencyDoesNotMatch() {
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(false);
        Assert.assertFalse(validator.isValid());
        Mockito.verify(predicates).isMatchingCurrency(offer, account);
    }

    @Test
    public void testIsValidWhenNotCountryBasedAccount() {
        account = Mockito.mock(PaymentAccount.class);
        Assert.assertFalse(((account) instanceof CountryBasedPaymentAccount));
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(true);
        Mockito.when(predicates.isEqualPaymentMethods(offer, account)).thenReturn(true);
        Assert.assertTrue(isValid());
    }

    @Test
    public void testIsValidWhenNotMatchingCodes() {
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingCountryCodes(offer, account)).thenReturn(false);
        Assert.assertFalse(validator.isValid());
        Mockito.verify(predicates).isMatchingCountryCodes(offer, account);
    }

    @Test
    public void testIsValidWhenSepaOffer() {
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingCountryCodes(offer, account)).thenReturn(true);
        Mockito.when(predicates.isEqualPaymentMethods(offer, account)).thenReturn(false);
        Mockito.when(predicates.isMatchingSepaOffer(offer, account)).thenReturn(true);
        Assert.assertTrue(validator.isValid());
        Mockito.verify(predicates).isMatchingSepaOffer(offer, account);
    }

    @Test
    public void testIsValidWhenSepaInstant() {
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingCountryCodes(offer, account)).thenReturn(true);
        Mockito.when(predicates.isEqualPaymentMethods(offer, account)).thenReturn(false);
        Mockito.when(predicates.isMatchingSepaInstant(offer, account)).thenReturn(true);
        Assert.assertTrue(validator.isValid());
        Mockito.verify(predicates).isMatchingSepaOffer(offer, account);
    }

    @Test
    public void testIsValidWhenSpecificBankAccountAndOfferRequireSpecificBank() {
        account = Mockito.mock(SpecificBanksAccount.class);
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(true);
        Mockito.when(predicates.isEqualPaymentMethods(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingCountryCodes(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingSepaOffer(offer, account)).thenReturn(false);
        Mockito.when(predicates.isMatchingSepaInstant(offer, account)).thenReturn(false);
        Mockito.when(predicates.isOfferRequireSameOrSpecificBank(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingBankId(offer, account)).thenReturn(false);
        Assert.assertFalse(isValid());
    }

    @Test
    public void testIsValidWhenSameBankAccountAndOfferRequireSpecificBank() {
        account = Mockito.mock(SameBankAccount.class);
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(true);
        Mockito.when(predicates.isEqualPaymentMethods(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingCountryCodes(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingSepaOffer(offer, account)).thenReturn(false);
        Mockito.when(predicates.isMatchingSepaInstant(offer, account)).thenReturn(false);
        Mockito.when(predicates.isOfferRequireSameOrSpecificBank(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingBankId(offer, account)).thenReturn(false);
        Assert.assertFalse(isValid());
    }

    @Test
    public void testIsValidWhenSpecificBankAccount() {
        account = Mockito.mock(SpecificBanksAccount.class);
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(true);
        Mockito.when(predicates.isEqualPaymentMethods(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingCountryCodes(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingSepaOffer(offer, account)).thenReturn(false);
        Mockito.when(predicates.isMatchingSepaInstant(offer, account)).thenReturn(false);
        Mockito.when(predicates.isOfferRequireSameOrSpecificBank(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingBankId(offer, account)).thenReturn(true);
        Assert.assertTrue(isValid());
    }

    @Test
    public void testIsValidWhenSameBankAccount() {
        account = Mockito.mock(SameBankAccount.class);
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(true);
        Mockito.when(predicates.isEqualPaymentMethods(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingCountryCodes(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingSepaOffer(offer, account)).thenReturn(false);
        Mockito.when(predicates.isMatchingSepaInstant(offer, account)).thenReturn(false);
        Mockito.when(predicates.isOfferRequireSameOrSpecificBank(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingBankId(offer, account)).thenReturn(true);
        Assert.assertTrue(isValid());
    }

    @Test
    public void testIsValidWhenNationalBankAccount() {
        account = Mockito.mock(NationalBankAccount.class);
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(true);
        Mockito.when(predicates.isEqualPaymentMethods(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingCountryCodes(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingSepaOffer(offer, account)).thenReturn(false);
        Mockito.when(predicates.isMatchingSepaOffer(offer, account)).thenReturn(false);
        Mockito.when(predicates.isMatchingSepaInstant(offer, account)).thenReturn(false);
        Mockito.when(predicates.isOfferRequireSameOrSpecificBank(offer, account)).thenReturn(false);
        Assert.assertTrue(isValid());
    }

    @Test
    public void testIsValidWhenWesternUnionAccount() {
        account = Mockito.mock(WesternUnionAccount.class);
        PaymentMethod.WESTERN_UNION = Mockito.mock(PaymentMethod.class);
        Mockito.when(offer.getPaymentMethod()).thenReturn(WESTERN_UNION);
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(true);
        Mockito.when(predicates.isEqualPaymentMethods(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingCountryCodes(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingSepaOffer(offer, account)).thenReturn(false);
        Mockito.when(predicates.isMatchingSepaInstant(offer, account)).thenReturn(false);
        Mockito.when(predicates.isOfferRequireSameOrSpecificBank(offer, account)).thenReturn(false);
        Assert.assertTrue(isValid());
    }

    @Test
    public void testIsValidWhenWesternIrregularAccount() {
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(true);
        Mockito.when(predicates.isEqualPaymentMethods(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingCountryCodes(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingSepaOffer(offer, account)).thenReturn(false);
        Mockito.when(predicates.isMatchingSepaInstant(offer, account)).thenReturn(false);
        Mockito.when(predicates.isOfferRequireSameOrSpecificBank(offer, account)).thenReturn(false);
        Assert.assertTrue(validator.isValid());
    }

    @Test
    public void testIsValidWhenMoneyGramAccount() {
        account = Mockito.mock(MoneyGramAccount.class);
        PaymentMethod.MONEY_GRAM = Mockito.mock(PaymentMethod.class);
        Mockito.when(offer.getPaymentMethod()).thenReturn(MONEY_GRAM);
        Mockito.when(predicates.isMatchingCurrency(offer, account)).thenReturn(true);
        Mockito.when(predicates.isEqualPaymentMethods(offer, account)).thenReturn(true);
        Mockito.when(predicates.isMatchingCountryCodes(offer, account)).thenReturn(false);
        Mockito.when(predicates.isMatchingSepaOffer(offer, account)).thenReturn(false);
        Mockito.when(predicates.isMatchingSepaInstant(offer, account)).thenReturn(false);
        Mockito.when(predicates.isOfferRequireSameOrSpecificBank(offer, account)).thenReturn(false);
        Assert.assertTrue(isValid());
    }
}

