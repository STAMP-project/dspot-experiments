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
package bisq.desktop.main.market.offerbook;


import bisq.core.provider.price.PriceFeedService;
import bisq.core.util.BSFormatter;
import bisq.desktop.main.offer.offerbook.OfferBook;
import bisq.desktop.main.offer.offerbook.OfferBookListItem;
import bisq.desktop.main.offer.offerbook.OfferBookListItemMaker;
import bisq.desktop.maker.PreferenceMakers;
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


@RunWith(PowerMockRunner.class)
@PrepareForTest({ OfferBook.class, PriceFeedService.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class OfferBookChartViewModelTest {
    @Test
    public void testMaxCharactersForBuyPriceWithNoOffers() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookChartViewModel model = new OfferBookChartViewModel(offerBook, PreferenceMakers.empty, null, null, null, new BSFormatter());
        Assert.assertEquals(0, model.maxPlacesForBuyPrice.intValue());
    }

    @Test
    public void testMaxCharactersForBuyPriceWithOfflinePriceFeedService() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        PriceFeedService priceFeedService = Mockito.mock(PriceFeedService.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        final OfferBookListItem item = make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.useMarketBasedPrice, true)));
        item.getOffer().setPriceFeedService(priceFeedService);
        offerBookListItems.addAll(item);
        Mockito.when(priceFeedService.getMarketPrice(ArgumentMatchers.anyString())).thenReturn(null);
        Mockito.when(priceFeedService.updateCounterProperty()).thenReturn(new SimpleIntegerProperty());
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookChartViewModel model = new OfferBookChartViewModel(offerBook, PreferenceMakers.empty, priceFeedService, null, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(0, model.maxPlacesForBuyPrice.intValue());
    }

    @Test
    public void testMaxCharactersForFiatBuyPrice() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        PriceFeedService service = Mockito.mock(PriceFeedService.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem));
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookChartViewModel model = new OfferBookChartViewModel(offerBook, PreferenceMakers.empty, service, null, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(7, model.maxPlacesForBuyPrice.intValue());
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.price, 94016475L))));
        Assert.assertEquals(9, model.maxPlacesForBuyPrice.intValue());// 9401.6475

        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.price, 101016475L))));
        Assert.assertEquals(10, model.maxPlacesForBuyPrice.intValue());// 10101.6475

    }

    @Test
    public void testMaxCharactersForBuyVolumeWithNoOffers() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookChartViewModel model = new OfferBookChartViewModel(offerBook, PreferenceMakers.empty, null, null, null, new BSFormatter());
        Assert.assertEquals(0, model.maxPlacesForBuyVolume.intValue());
    }

    @Test
    public void testMaxCharactersForFiatBuyVolume() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        PriceFeedService service = Mockito.mock(PriceFeedService.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem));
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookChartViewModel model = new OfferBookChartViewModel(offerBook, PreferenceMakers.empty, service, null, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(4, model.maxPlacesForBuyVolume.intValue());// 0.01

        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.amount, 100000000L))));
        Assert.assertEquals(5, model.maxPlacesForBuyVolume.intValue());// 10.00

        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.amount, 22128600000L))));
        Assert.assertEquals(7, model.maxPlacesForBuyVolume.intValue());// 2212.86

    }

    @Test
    public void testMaxCharactersForSellPriceWithNoOffers() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookChartViewModel model = new OfferBookChartViewModel(offerBook, PreferenceMakers.empty, null, null, null, new BSFormatter());
        Assert.assertEquals(0, model.maxPlacesForSellPrice.intValue());
    }

    @Test
    public void testMaxCharactersForSellPriceWithOfflinePriceFeedService() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        PriceFeedService priceFeedService = Mockito.mock(PriceFeedService.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        final OfferBookListItem item = make(OfferBookListItemMaker.btcSellItem.but(with(OfferBookListItemMaker.useMarketBasedPrice, true)));
        item.getOffer().setPriceFeedService(priceFeedService);
        offerBookListItems.addAll(item);
        Mockito.when(priceFeedService.getMarketPrice(ArgumentMatchers.anyString())).thenReturn(null);
        Mockito.when(priceFeedService.updateCounterProperty()).thenReturn(new SimpleIntegerProperty());
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookChartViewModel model = new OfferBookChartViewModel(offerBook, PreferenceMakers.empty, priceFeedService, null, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(0, model.maxPlacesForSellPrice.intValue());
    }

    @Test
    public void testMaxCharactersForFiatSellPrice() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        PriceFeedService service = Mockito.mock(PriceFeedService.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcSellItem));
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookChartViewModel model = new OfferBookChartViewModel(offerBook, PreferenceMakers.empty, service, null, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(7, model.maxPlacesForSellPrice.intValue());// 10.0000 default price

        offerBookListItems.addAll(make(OfferBookListItemMaker.btcSellItem.but(with(OfferBookListItemMaker.price, 94016475L))));
        Assert.assertEquals(9, model.maxPlacesForSellPrice.intValue());// 9401.6475

        offerBookListItems.addAll(make(OfferBookListItemMaker.btcSellItem.but(with(OfferBookListItemMaker.price, 101016475L))));
        Assert.assertEquals(10, model.maxPlacesForSellPrice.intValue());// 10101.6475

    }

    @Test
    public void testMaxCharactersForSellVolumeWithNoOffers() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookChartViewModel model = new OfferBookChartViewModel(offerBook, PreferenceMakers.empty, null, null, null, new BSFormatter());
        Assert.assertEquals(0, model.maxPlacesForSellVolume.intValue());
    }

    @Test
    public void testMaxCharactersForFiatSellVolume() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        PriceFeedService service = Mockito.mock(PriceFeedService.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcSellItem));
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        final OfferBookChartViewModel model = new OfferBookChartViewModel(offerBook, PreferenceMakers.empty, service, null, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(4, model.maxPlacesForSellVolume.intValue());// 0.01

        offerBookListItems.addAll(make(OfferBookListItemMaker.btcSellItem.but(with(OfferBookListItemMaker.amount, 100000000L))));
        Assert.assertEquals(5, model.maxPlacesForSellVolume.intValue());// 10.00

        offerBookListItems.addAll(make(OfferBookListItemMaker.btcSellItem.but(with(OfferBookListItemMaker.amount, 22128600000L))));
        Assert.assertEquals(7, model.maxPlacesForSellVolume.intValue());// 2212.86

    }
}

