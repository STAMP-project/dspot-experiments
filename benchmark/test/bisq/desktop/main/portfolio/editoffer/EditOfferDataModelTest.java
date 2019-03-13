package bisq.desktop.main.portfolio.editoffer;


import OfferPayload.Direction.BUY;
import bisq.core.btc.model.AddressEntry;
import bisq.core.btc.wallet.BsqWalletService;
import bisq.core.btc.wallet.BtcWalletService;
import bisq.core.payment.AccountAgeWitnessService;
import bisq.core.payment.CryptoCurrencyAccount;
import bisq.core.payment.PaymentAccount;
import bisq.core.provider.fee.FeeService;
import bisq.core.provider.price.PriceFeedService;
import bisq.core.user.Preferences;
import bisq.core.user.User;
import bisq.core.util.BsqFormatter;
import bisq.desktop.maker.OfferMaker;
import bisq.desktop.util.validation.SecurityDepositValidator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ BtcWalletService.class, AddressEntry.class, PriceFeedService.class, User.class, FeeService.class, PaymentAccount.class, BsqWalletService.class, SecurityDepositValidator.class, AccountAgeWitnessService.class, BsqFormatter.class, Preferences.class, BsqWalletService.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class EditOfferDataModelTest {
    private EditOfferDataModel model;

    private User user;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testEditOfferOfRemovedAsset() {
        final CryptoCurrencyAccount bitcoinClashicAccount = new CryptoCurrencyAccount();
        bitcoinClashicAccount.setId("BCHC");
        Mockito.when(user.getPaymentAccount(ArgumentMatchers.anyString())).thenReturn(bitcoinClashicAccount);
        model.applyOpenOffer(new bisq.core.offer.OpenOffer(make(OfferMaker.btcBCHCOffer), null));
        Assert.assertNull(model.getPreselectedPaymentAccount());
    }

    @Test
    public void testInitializeEditOfferWithRemovedAsset() {
        exception.expect(IllegalArgumentException.class);
        model.initWithData(BUY, null);
    }
}

