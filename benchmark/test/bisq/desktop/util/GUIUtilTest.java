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


import bisq.common.util.Utilities;
import bisq.core.locale.Res;
import bisq.core.locale.TradeCurrency;
import bisq.core.user.DontShowAgainLookup;
import bisq.core.user.Preferences;
import bisq.desktop.maker.TradeCurrencyMakers;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javafx.util.StringConverter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class, Preferences.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class GUIUtilTest {
    @Test
    public void testTradeCurrencyConverter() {
        Map<String, Integer> offerCounts = new HashMap<String, Integer>() {
            {
                put("BTC", 11);
                put("EUR", 10);
            }
        };
        StringConverter<TradeCurrency> tradeCurrencyConverter = GUIUtil.getTradeCurrencyConverter(Res.get("shared.oneOffer"), Res.get("shared.multipleOffers"), offerCounts);
        Assert.assertEquals("? Bitcoin (BTC) - 11 offers", tradeCurrencyConverter.toString(TradeCurrencyMakers.bitcoin));
        Assert.assertEquals("? Euro (EUR) - 10 offers", tradeCurrencyConverter.toString(TradeCurrencyMakers.euro));
    }

    @Test
    public void testOpenURLWithCampaignParameters() throws Exception {
        Preferences preferences = Mockito.mock(Preferences.class);
        DontShowAgainLookup.setPreferences(preferences);
        GUIUtil.setPreferences(preferences);
        Mockito.when(preferences.showAgain("warnOpenURLWhenTorEnabled")).thenReturn(false);
        Mockito.when(preferences.getUserLanguage()).thenReturn("en");
        PowerMockito.mockStatic(Utilities.class);
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        PowerMockito.doNothing().when(Utilities.class, "openURI", captor.capture());
        GUIUtil.openWebPage("https://bisq.network");
        Assert.assertEquals("https://bisq.network?utm_source=desktop-client&utm_medium=in-app-link&utm_campaign=language_en", captor.getValue().toString());
        GUIUtil.openWebPage("https://docs.bisq.network/trading-rules.html#f2f-trading");
        Assert.assertEquals("https://docs.bisq.network/trading-rules.html?utm_source=desktop-client&utm_medium=in-app-link&utm_campaign=language_en#f2f-trading", captor.getValue().toString());
    }

    @Test
    public void testOpenURLWithoutCampaignParameters() throws Exception {
        Preferences preferences = Mockito.mock(Preferences.class);
        DontShowAgainLookup.setPreferences(preferences);
        GUIUtil.setPreferences(preferences);
        Mockito.when(preferences.showAgain("warnOpenURLWhenTorEnabled")).thenReturn(false);
        PowerMockito.mockStatic(Utilities.class);
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        PowerMockito.doNothing().when(Utilities.class, "openURI", captor.capture());
        GUIUtil.openWebPage("https://www.github.com");
        Assert.assertEquals("https://www.github.com", captor.getValue().toString());
    }
}

