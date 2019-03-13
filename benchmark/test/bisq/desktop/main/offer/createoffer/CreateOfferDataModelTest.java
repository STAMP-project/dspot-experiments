package bisq.desktop.main.offer.createoffer;


import Coin.ZERO;
import OfferPayload.Direction.BUY;
import bisq.core.btc.TxFeeEstimationService;
import bisq.core.btc.model.AddressEntry;
import bisq.core.btc.wallet.BtcWalletService;
import bisq.core.locale.FiatCurrency;
import bisq.core.offer.OfferUtil;
import bisq.core.payment.ClearXchangeAccount;
import bisq.core.payment.PaymentAccount;
import bisq.core.payment.RevolutAccount;
import bisq.core.provider.fee.FeeService;
import bisq.core.provider.price.PriceFeedService;
import bisq.core.user.Preferences;
import bisq.core.user.User;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ BtcWalletService.class, AddressEntry.class, Preferences.class, User.class, PriceFeedService.class, OfferUtil.class, FeeService.class, TxFeeEstimationService.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class CreateOfferDataModelTest {
    private CreateOfferDataModel model;

    private User user;

    private Preferences preferences;

    @Test
    public void testUseTradeCurrencySetInOfferViewWhenInPaymentAccountAvailable() {
        final HashSet<PaymentAccount> paymentAccounts = new HashSet<>();
        final ClearXchangeAccount zelleAccount = new ClearXchangeAccount();
        zelleAccount.setId("234");
        paymentAccounts.add(zelleAccount);
        final RevolutAccount revolutAccount = new RevolutAccount();
        revolutAccount.setId("123");
        revolutAccount.setSingleTradeCurrency(new FiatCurrency("EUR"));
        revolutAccount.addCurrency(new FiatCurrency("USD"));
        paymentAccounts.add(revolutAccount);
        Mockito.when(user.getPaymentAccounts()).thenReturn(paymentAccounts);
        Mockito.when(preferences.getSelectedPaymentAccountForCreateOffer()).thenReturn(revolutAccount);
        PowerMockito.mockStatic(OfferUtil.class);
        BDDMockito.given(OfferUtil.getMakerFee(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(ZERO);
        model.initWithData(BUY, new FiatCurrency("USD"));
        Assert.assertEquals("USD", model.getTradeCurrencyCode().get());
    }

    @Test
    public void testUseTradeAccountThatMatchesTradeCurrencySetInOffer() {
        final HashSet<PaymentAccount> paymentAccounts = new HashSet<>();
        final ClearXchangeAccount zelleAccount = new ClearXchangeAccount();
        zelleAccount.setId("234");
        paymentAccounts.add(zelleAccount);
        final RevolutAccount revolutAccount = new RevolutAccount();
        revolutAccount.setId("123");
        revolutAccount.setSingleTradeCurrency(new FiatCurrency("EUR"));
        paymentAccounts.add(revolutAccount);
        Mockito.when(user.getPaymentAccounts()).thenReturn(paymentAccounts);
        Mockito.when(user.findFirstPaymentAccountWithCurrency(new FiatCurrency("USD"))).thenReturn(zelleAccount);
        Mockito.when(preferences.getSelectedPaymentAccountForCreateOffer()).thenReturn(revolutAccount);
        PowerMockito.mockStatic(OfferUtil.class);
        BDDMockito.given(OfferUtil.getMakerFee(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(ZERO);
        model.initWithData(BUY, new FiatCurrency("USD"));
        Assert.assertEquals("USD", model.getTradeCurrencyCode().get());
    }
}

