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
package bisq.desktop.main.offer.offerbook;


import bisq.core.offer.OpenOfferManager;
import bisq.core.provider.price.MarketPrice;
import bisq.core.provider.price.PriceFeedService;
import bisq.core.util.BSFormatter;
import bisq.desktop.maker.PreferenceMakers;
import com.natpryce.makeiteasy.Maker;
import java.time.Instant;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ OfferBook.class, OpenOfferManager.class, PriceFeedService.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class OfferBookViewModelTest {
    private static final Logger log = LoggerFactory.getLogger(OfferBookViewModelTest.class);

    @Test
    public void testMaxCharactersForAmountWithNoOffes() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookViewModel model = new OfferBookViewModel(null, null, offerBook, PreferenceMakers.empty, null, null, null, null, null, null, new BSFormatter());
        Assert.assertEquals(0, model.maxPlacesForAmount.intValue());
    }

    @Test
    public void testMaxCharactersForAmount() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        OpenOfferManager openOfferManager = Mockito.mock(OpenOfferManager.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem));
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookViewModel model = new OfferBookViewModel(null, openOfferManager, offerBook, PreferenceMakers.empty, null, null, null, null, null, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(6, model.maxPlacesForAmount.intValue());
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.amount, 2000000000L))));
        Assert.assertEquals(7, model.maxPlacesForAmount.intValue());
    }

    @Test
    public void testMaxCharactersForAmountRange() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        OpenOfferManager openOfferManager = Mockito.mock(OpenOfferManager.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcItemWithRange));
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookViewModel model = new OfferBookViewModel(null, openOfferManager, offerBook, PreferenceMakers.empty, null, null, null, null, null, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(15, model.maxPlacesForAmount.intValue());
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcItemWithRange.but(with(OfferBookListItemMaker.amount, 2000000000L))));
        Assert.assertEquals(16, model.maxPlacesForAmount.intValue());
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcItemWithRange.but(with(OfferBookListItemMaker.minAmount, 30000000000L), with(OfferBookListItemMaker.amount, 30000000000L))));
        Assert.assertEquals(19, model.maxPlacesForAmount.intValue());
    }

    @Test
    public void testMaxCharactersForVolumeWithNoOffes() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookViewModel model = new OfferBookViewModel(null, null, offerBook, PreferenceMakers.empty, null, null, null, null, null, null, new BSFormatter());
        Assert.assertEquals(0, model.maxPlacesForVolume.intValue());
    }

    @Test
    public void testMaxCharactersForVolume() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        OpenOfferManager openOfferManager = Mockito.mock(OpenOfferManager.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem));
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookViewModel model = new OfferBookViewModel(null, openOfferManager, offerBook, PreferenceMakers.empty, null, null, null, null, null, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(8, model.maxPlacesForVolume.intValue());
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.amount, 2000000000L))));
        Assert.assertEquals(10, model.maxPlacesForVolume.intValue());
    }

    @Test
    public void testMaxCharactersForVolumeRange() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        OpenOfferManager openOfferManager = Mockito.mock(OpenOfferManager.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcItemWithRange));
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookViewModel model = new OfferBookViewModel(null, openOfferManager, offerBook, PreferenceMakers.empty, null, null, null, null, null, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(15, model.maxPlacesForVolume.intValue());
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcItemWithRange.but(with(OfferBookListItemMaker.amount, 2000000000L))));
        Assert.assertEquals(17, model.maxPlacesForVolume.intValue());
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcItemWithRange.but(with(OfferBookListItemMaker.minAmount, 30000000000L), with(OfferBookListItemMaker.amount, 30000000000L))));
        Assert.assertEquals(25, model.maxPlacesForVolume.intValue());
    }

    @Test
    public void testMaxCharactersForPriceWithNoOffers() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookViewModel model = new OfferBookViewModel(null, null, offerBook, PreferenceMakers.empty, null, null, null, null, null, null, new BSFormatter());
        Assert.assertEquals(0, model.maxPlacesForPrice.intValue());
    }

    @Test
    public void testMaxCharactersForPrice() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        OpenOfferManager openOfferManager = Mockito.mock(OpenOfferManager.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem));
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookViewModel model = new OfferBookViewModel(null, openOfferManager, offerBook, PreferenceMakers.empty, null, null, null, null, null, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(7, model.maxPlacesForPrice.intValue());
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.price, 149558240L))));// 14955.8240

        Assert.assertEquals(10, model.maxPlacesForPrice.intValue());
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.price, 14955824L))));// 1495.58240

        Assert.assertEquals(10, model.maxPlacesForPrice.intValue());
    }

    @Test
    public void testMaxCharactersForPriceDistanceWithNoOffers() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookViewModel model = new OfferBookViewModel(null, null, offerBook, PreferenceMakers.empty, null, null, null, null, null, null, new BSFormatter());
        Assert.assertEquals(0, model.maxPlacesForMarketPriceMargin.intValue());
    }

    @Test
    public void testMaxCharactersForPriceDistance() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        OpenOfferManager openOfferManager = Mockito.mock(OpenOfferManager.class);
        PriceFeedService priceFeedService = Mockito.mock(PriceFeedService.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        final Maker<OfferBookListItem> item = OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.useMarketBasedPrice, true));
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        Mockito.when(priceFeedService.getMarketPrice(ArgumentMatchers.anyString())).thenReturn(null);
        Mockito.when(priceFeedService.updateCounterProperty()).thenReturn(new SimpleIntegerProperty());
        final OfferBookListItem item1 = make(item);
        item1.getOffer().setPriceFeedService(priceFeedService);
        final OfferBookListItem item2 = make(item.but(with(OfferBookListItemMaker.marketPriceMargin, 0.0197)));
        item2.getOffer().setPriceFeedService(priceFeedService);
        final OfferBookListItem item3 = make(item.but(with(OfferBookListItemMaker.marketPriceMargin, 0.1)));
        item3.getOffer().setPriceFeedService(priceFeedService);
        final OfferBookListItem item4 = make(item.but(with(OfferBookListItemMaker.marketPriceMargin, (-0.1))));
        item4.getOffer().setPriceFeedService(priceFeedService);
        offerBookListItems.addAll(item1, item2);
        final OfferBookViewModel model = new OfferBookViewModel(null, openOfferManager, offerBook, PreferenceMakers.empty, null, priceFeedService, null, null, null, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(8, model.maxPlacesForMarketPriceMargin.intValue());// " (1.97%)"

        offerBookListItems.addAll(item3);
        Assert.assertEquals(9, model.maxPlacesForMarketPriceMargin.intValue());// " (10.00%)"

        offerBookListItems.addAll(item4);
        Assert.assertEquals(10, model.maxPlacesForMarketPriceMargin.intValue());// " (-10.00%)"

    }

    @Test
    public void testGetPrice() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        OpenOfferManager openOfferManager = Mockito.mock(OpenOfferManager.class);
        PriceFeedService priceFeedService = Mockito.mock(PriceFeedService.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        Mockito.when(priceFeedService.getMarketPrice(ArgumentMatchers.anyString())).thenReturn(new MarketPrice("USD", 12684.045, Instant.now().getEpochSecond(), true));
        final OfferBookViewModel model = new OfferBookViewModel(null, openOfferManager, offerBook, PreferenceMakers.empty, null, null, null, null, null, null, new BSFormatter());
        final OfferBookListItem item = make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.useMarketBasedPrice, true), with(OfferBookListItemMaker.marketPriceMargin, (-0.12))));
        final OfferBookListItem lowItem = make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.useMarketBasedPrice, true), with(OfferBookListItemMaker.marketPriceMargin, 0.01)));
        final OfferBookListItem fixedItem = make(OfferBookListItemMaker.btcBuyItem);
        item.getOffer().setPriceFeedService(priceFeedService);
        lowItem.getOffer().setPriceFeedService(priceFeedService);
        offerBookListItems.addAll(lowItem, fixedItem);
        model.activate();
        Assert.assertEquals("12557.2046 (1.00%)", model.getPrice(lowItem));
        Assert.assertEquals("10.0000", model.getPrice(fixedItem));
        offerBookListItems.addAll(item);
        Assert.assertEquals("14206.1304 (-12.00%)", model.getPrice(item));
        Assert.assertEquals("12557.2046 (1.00%)", model.getPrice(lowItem));
    }
}

