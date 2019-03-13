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


import bisq.core.monetary.Volume;
import bisq.core.offer.Offer;
import bisq.core.offer.OfferPayload;
import bisq.core.util.BSFormatter;
import bisq.desktop.maker.OfferMaker;
import bisq.desktop.maker.PriceMaker;
import bisq.desktop.maker.VolumeMaker;
import java.util.concurrent.TimeUnit;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.CoinMaker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ Offer.class, OfferPayload.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class BSFormatterTest {
    private BSFormatter formatter;

    @Test
    public void testIsValid() {
        Assert.assertEquals("0 days", formatter.formatAccountAge(TimeUnit.HOURS.toMillis(23)));
        Assert.assertEquals("0 days", formatter.formatAccountAge(0));
        Assert.assertEquals("0 days", formatter.formatAccountAge((-1)));
        Assert.assertEquals("1 day", formatter.formatAccountAge(TimeUnit.DAYS.toMillis(1)));
        Assert.assertEquals("2 days", formatter.formatAccountAge(TimeUnit.DAYS.toMillis(2)));
        Assert.assertEquals("30 days", formatter.formatAccountAge(TimeUnit.DAYS.toMillis(30)));
        Assert.assertEquals("60 days", formatter.formatAccountAge(TimeUnit.DAYS.toMillis(60)));
    }

    @Test
    public void testFormatDurationAsWords() {
        long oneDay = TimeUnit.DAYS.toMillis(1);
        long oneHour = TimeUnit.HOURS.toMillis(1);
        long oneMinute = TimeUnit.MINUTES.toMillis(1);
        long oneSecond = TimeUnit.SECONDS.toMillis(1);
        Assert.assertEquals("1 hour, 0 minutes", formatter.formatDurationAsWords(oneHour));
        Assert.assertEquals("1 day, 0 hours, 0 minutes", formatter.formatDurationAsWords(oneDay));
        Assert.assertEquals("2 days, 0 hours, 1 minute", formatter.formatDurationAsWords(((oneDay * 2) + oneMinute)));
        Assert.assertEquals("2 days, 0 hours, 2 minutes", formatter.formatDurationAsWords(((oneDay * 2) + (oneMinute * 2))));
        Assert.assertEquals("1 hour, 0 minutes, 0 seconds", formatter.formatDurationAsWords(oneHour, true, true));
        Assert.assertEquals("1 hour, 0 minutes, 1 second", formatter.formatDurationAsWords((oneHour + oneSecond), true, true));
        Assert.assertEquals("1 hour, 0 minutes, 2 seconds", formatter.formatDurationAsWords((oneHour + (oneSecond * 2)), true, true));
        Assert.assertEquals("2 days, 21 hours, 28 minutes", formatter.formatDurationAsWords((((oneDay * 2) + (oneHour * 21)) + (oneMinute * 28))));
        Assert.assertEquals("", formatter.formatDurationAsWords(0));
        Assert.assertTrue(formatter.formatDurationAsWords(0).isEmpty());
    }

    @Test
    public void testFormatPrice() {
        Assert.assertEquals("100.0000", formatter.formatPrice(make(PriceMaker.usdPrice)));
        Assert.assertEquals("7098.4700", formatter.formatPrice(make(PriceMaker.usdPrice.but(with(PriceMaker.priceString, "7098.4700")))));
    }

    @Test
    public void testFormatCoin() {
        Assert.assertEquals("1.00", formatter.formatCoin(CoinMaker.oneBitcoin));
        Assert.assertEquals("1.0000", formatter.formatCoin(CoinMaker.oneBitcoin, 4));
        Assert.assertEquals("1.00", formatter.formatCoin(CoinMaker.oneBitcoin, 5));
        Assert.assertEquals("0.000001", formatter.formatCoin(make(a(CoinMaker.Coin).but(with(CoinMaker.satoshis, 100L)))));
        Assert.assertEquals("0.00000001", formatter.formatCoin(make(a(CoinMaker.Coin).but(with(CoinMaker.satoshis, 1L)))));
    }

    @Test
    public void testFormatVolume() {
        Assert.assertEquals("1.00", formatter.formatVolume(make(OfferMaker.btcUsdOffer), true, 4));
        Assert.assertEquals("100.00", formatter.formatVolume(make(VolumeMaker.usdVolume)));
        Assert.assertEquals("1774.62", formatter.formatVolume(make(VolumeMaker.usdVolume.but(with(VolumeMaker.volumeString, "1774.62")))));
    }

    @Test
    public void testFormatSameVolume() {
        Offer offer = Mockito.mock(Offer.class);
        Volume btc = Volume.parse("0.10", "BTC");
        Mockito.when(offer.getMinVolume()).thenReturn(btc);
        Mockito.when(offer.getVolume()).thenReturn(btc);
        Assert.assertEquals("0.10000000", formatter.formatVolume(offer.getVolume()));
    }

    @Test
    public void testFormatDifferentVolume() {
        Offer offer = Mockito.mock(Offer.class);
        Volume btcMin = Volume.parse("0.10", "BTC");
        Volume btcMax = Volume.parse("0.25", "BTC");
        Mockito.when(offer.isRange()).thenReturn(true);
        Mockito.when(offer.getMinVolume()).thenReturn(btcMin);
        Mockito.when(offer.getVolume()).thenReturn(btcMax);
        Assert.assertEquals("0.10000000 - 0.25000000", formatter.formatVolume(offer, false, 0));
    }

    @Test
    public void testFormatNullVolume() {
        Offer offer = Mockito.mock(Offer.class);
        Mockito.when(offer.getMinVolume()).thenReturn(null);
        Mockito.when(offer.getVolume()).thenReturn(null);
        Assert.assertEquals("", formatter.formatVolume(offer.getVolume()));
    }

    @Test
    public void testFormatSameAmount() {
        Offer offer = Mockito.mock(Offer.class);
        Mockito.when(offer.getMinAmount()).thenReturn(Coin.valueOf(10000000));
        Mockito.when(offer.getAmount()).thenReturn(Coin.valueOf(10000000));
        Assert.assertEquals("0.10", formatter.formatAmount(offer));
    }

    @Test
    public void testFormatDifferentAmount() {
        OfferPayload offerPayload = Mockito.mock(OfferPayload.class);
        Offer offer = new Offer(offerPayload);
        Mockito.when(offerPayload.getMinAmount()).thenReturn(10000000L);
        Mockito.when(offerPayload.getAmount()).thenReturn(20000000L);
        Assert.assertEquals("0.10 - 0.20", formatter.formatAmount(offer));
    }

    @Test
    public void testFormatAmountWithAlignmenWithDecimals() {
        OfferPayload offerPayload = Mockito.mock(OfferPayload.class);
        Offer offer = new Offer(offerPayload);
        Mockito.when(offerPayload.getMinAmount()).thenReturn(10000000L);
        Mockito.when(offerPayload.getAmount()).thenReturn(20000000L);
        Assert.assertEquals("0.1000 - 0.2000", formatter.formatAmount(offer, 4, true, 15));
    }

    @Test
    public void testFormatAmountWithAlignmenWithDecimalsNoRange() {
        OfferPayload offerPayload = Mockito.mock(OfferPayload.class);
        Offer offer = new Offer(offerPayload);
        Mockito.when(offerPayload.getMinAmount()).thenReturn(10000000L);
        Mockito.when(offerPayload.getAmount()).thenReturn(10000000L);
        Assert.assertEquals("0.1000", formatter.formatAmount(offer, 4, true, 15));
    }

    @Test
    public void testFormatNullAmount() {
        Offer offer = Mockito.mock(Offer.class);
        Mockito.when(offer.getMinAmount()).thenReturn(null);
        Mockito.when(offer.getAmount()).thenReturn(null);
        Assert.assertEquals("", formatter.formatAmount(offer));
    }
}

