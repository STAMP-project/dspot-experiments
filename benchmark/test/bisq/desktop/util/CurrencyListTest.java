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
package bisq.desktop.util;


import bisq.core.locale.CryptoCurrency;
import bisq.core.locale.FiatCurrency;
import bisq.core.locale.TradeCurrency;
import bisq.core.user.Preferences;
import com.google.common.collect.Lists;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Preferences.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class CurrencyListTest {
    private static final Locale locale = new Locale("en", "US");

    private static final TradeCurrency USD = new FiatCurrency(Currency.getInstance("USD"), CurrencyListTest.locale);

    private static final TradeCurrency RUR = new FiatCurrency(Currency.getInstance("RUR"), CurrencyListTest.locale);

    private static final TradeCurrency BTC = new CryptoCurrency("BTC", "Bitcoin");

    private static final TradeCurrency ETH = new CryptoCurrency("ETH", "Ether");

    private static final TradeCurrency BSQ = new CryptoCurrency("BSQ", "Bisq Token");

    private Preferences preferences;

    private List<CurrencyListItem> delegate;

    private CurrencyList testedEntity;

    @Test
    public void testUpdateWhenSortNumerically() {
        Mockito.when(preferences.isSortMarketCurrenciesNumerically()).thenReturn(true);
        List<TradeCurrency> currencies = Lists.newArrayList(CurrencyListTest.USD, CurrencyListTest.RUR, CurrencyListTest.USD, CurrencyListTest.ETH, CurrencyListTest.ETH, CurrencyListTest.BTC);
        testedEntity.updateWithCurrencies(currencies, null);
        List<CurrencyListItem> expected = Lists.newArrayList(new CurrencyListItem(CurrencyListTest.USD, 2), new CurrencyListItem(CurrencyListTest.RUR, 1), new CurrencyListItem(CurrencyListTest.ETH, 2), new CurrencyListItem(CurrencyListTest.BTC, 1));
        Assert.assertEquals(expected, delegate);
    }

    @Test
    public void testUpdateWhenNotSortNumerically() {
        Mockito.when(preferences.isSortMarketCurrenciesNumerically()).thenReturn(false);
        List<TradeCurrency> currencies = Lists.newArrayList(CurrencyListTest.USD, CurrencyListTest.RUR, CurrencyListTest.USD, CurrencyListTest.ETH, CurrencyListTest.ETH, CurrencyListTest.BTC);
        testedEntity.updateWithCurrencies(currencies, null);
        List<CurrencyListItem> expected = Lists.newArrayList(new CurrencyListItem(CurrencyListTest.RUR, 1), new CurrencyListItem(CurrencyListTest.USD, 2), new CurrencyListItem(CurrencyListTest.BTC, 1), new CurrencyListItem(CurrencyListTest.ETH, 2));
        Assert.assertEquals(expected, delegate);
    }

    @Test
    public void testUpdateWhenSortNumericallyAndFirstSpecified() {
        Mockito.when(preferences.isSortMarketCurrenciesNumerically()).thenReturn(true);
        List<TradeCurrency> currencies = Lists.newArrayList(CurrencyListTest.USD, CurrencyListTest.RUR, CurrencyListTest.USD, CurrencyListTest.ETH, CurrencyListTest.ETH, CurrencyListTest.BTC);
        CurrencyListItem first = new CurrencyListItem(CurrencyListTest.BSQ, 5);
        testedEntity.updateWithCurrencies(currencies, first);
        List<CurrencyListItem> expected = Lists.newArrayList(first, new CurrencyListItem(CurrencyListTest.USD, 2), new CurrencyListItem(CurrencyListTest.RUR, 1), new CurrencyListItem(CurrencyListTest.ETH, 2), new CurrencyListItem(CurrencyListTest.BTC, 1));
        Assert.assertEquals(expected, delegate);
    }
}

