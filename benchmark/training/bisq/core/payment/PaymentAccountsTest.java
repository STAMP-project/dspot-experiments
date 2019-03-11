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


import bisq.core.offer.Offer;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ PaymentAccount.class, AccountAgeWitness.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class PaymentAccountsTest {
    @Test
    public void testGetOldestPaymentAccountForOfferWhenNoValidAccounts() {
        PaymentAccounts accounts = new PaymentAccounts(Collections.emptySet(), Mockito.mock(AccountAgeWitnessService.class));
        PaymentAccount actual = accounts.getOldestPaymentAccountForOffer(Mockito.mock(Offer.class));
        Assert.assertNull(actual);
    }

    @Test
    public void testGetOldestPaymentAccountForOffer() {
        AccountAgeWitnessService service = Mockito.mock(AccountAgeWitnessService.class);
        PaymentAccount oldest = PaymentAccountsTest.createAccountWithAge(service, 3);
        Set<PaymentAccount> accounts = Sets.newHashSet(oldest, PaymentAccountsTest.createAccountWithAge(service, 2), PaymentAccountsTest.createAccountWithAge(service, 1));
        BiFunction<Offer, PaymentAccount, Boolean> dummyValidator = ( offer, account) -> true;
        PaymentAccounts testedEntity = new PaymentAccounts(accounts, service, dummyValidator);
        PaymentAccount actual = testedEntity.getOldestPaymentAccountForOffer(Mockito.mock(Offer.class));
        Assert.assertEquals(oldest, actual);
    }
}

