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
package bisq.desktop.main.market.spread;


import bisq.core.provider.price.PriceFeedService;
import bisq.core.util.BSFormatter;
import bisq.desktop.main.offer.offerbook.OfferBook;
import bisq.desktop.main.offer.offerbook.OfferBookListItem;
import bisq.desktop.main.offer.offerbook.OfferBookListItemMaker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ OfferBook.class, PriceFeedService.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class SpreadViewModelTest {
    @Test
    public void testMaxCharactersForAmountWithNoOffers() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        SpreadViewModel model = new SpreadViewModel(offerBook, null, new BSFormatter());
        Assert.assertEquals(0, model.maxPlacesForAmount.intValue());
    }

    @Test
    public void testMaxCharactersForAmount() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem));
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        SpreadViewModel model = new SpreadViewModel(offerBook, null, new BSFormatter());
        model.activate();
        Assert.assertEquals(6, model.maxPlacesForAmount.intValue());// 0.001

        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.amount, 1403000000L))));
        Assert.assertEquals(7, model.maxPlacesForAmount.intValue());// 14.0300

    }

    @Test
    public void testFilterSpreadItemsForUniqueOffers() {
        OfferBook offerBook = Mockito.mock(OfferBook.class);
        PriceFeedService priceFeedService = Mockito.mock(PriceFeedService.class);
        final ObservableList<OfferBookListItem> offerBookListItems = FXCollections.observableArrayList();
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem));
        Mockito.when(offerBook.getOfferBookListItems()).thenReturn(offerBookListItems);
        SpreadViewModel model = new SpreadViewModel(offerBook, priceFeedService, new BSFormatter());
        model.activate();
        Assert.assertEquals(1, model.spreadItems.get(0).numberOfOffers);
        offerBookListItems.addAll(make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.id, "2345"))), make(OfferBookListItemMaker.btcBuyItem.but(with(OfferBookListItemMaker.id, "2345"))), make(OfferBookListItemMaker.btcSellItem.but(with(OfferBookListItemMaker.id, "3456"))), make(OfferBookListItemMaker.btcSellItem.but(with(OfferBookListItemMaker.id, "3456"))));
        Assert.assertEquals(2, model.spreadItems.get(0).numberOfBuyOffers);
        Assert.assertEquals(1, model.spreadItems.get(0).numberOfSellOffers);
        Assert.assertEquals(3, model.spreadItems.get(0).numberOfOffers);
    }
}

