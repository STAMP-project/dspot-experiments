package bisq.core.offer;


import OpenOffer.State.DEACTIVATED;
import bisq.common.handlers.ErrorMessageHandler;
import bisq.common.handlers.ResultHandler;
import bisq.common.storage.Storage;
import bisq.network.p2p.P2PService;
import bisq.network.p2p.peers.PeerManager;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ P2PService.class, PeerManager.class, OfferBookService.class, Storage.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class OpenOfferManagerTest {
    @Test
    public void testStartEditOfferForActiveOffer() {
        P2PService p2PService = Mockito.mock(P2PService.class);
        OfferBookService offerBookService = Mockito.mock(OfferBookService.class);
        Mockito.when(p2PService.getPeerManager()).thenReturn(Mockito.mock(PeerManager.class));
        final OpenOfferManager manager = new OpenOfferManager(null, null, p2PService, null, null, null, offerBookService, null, null, null, null, null, null, null);
        AtomicBoolean startEditOfferSuccessful = new AtomicBoolean(false);
        Mockito.doAnswer(( invocation) -> {
            handleResult();
            return null;
        }).when(offerBookService).deactivateOffer(ArgumentMatchers.any(OfferPayload.class), ArgumentMatchers.any(ResultHandler.class), ArgumentMatchers.any(ErrorMessageHandler.class));
        final OpenOffer openOffer = new OpenOffer(make(OfferMaker.btcUsdOffer), null);
        ResultHandler resultHandler = () -> {
            startEditOfferSuccessful.set(true);
        };
        manager.editOpenOfferStart(openOffer, resultHandler, null);
        Mockito.verify(offerBookService, Mockito.times(1)).deactivateOffer(ArgumentMatchers.any(OfferPayload.class), ArgumentMatchers.any(ResultHandler.class), ArgumentMatchers.any(ErrorMessageHandler.class));
        TestCase.assertTrue(startEditOfferSuccessful.get());
    }

    @Test
    public void testStartEditOfferForDeactivatedOffer() {
        P2PService p2PService = Mockito.mock(P2PService.class);
        OfferBookService offerBookService = Mockito.mock(OfferBookService.class);
        Storage storage = Mockito.mock(Storage.class);
        Mockito.when(p2PService.getPeerManager()).thenReturn(Mockito.mock(PeerManager.class));
        final OpenOfferManager manager = new OpenOfferManager(null, null, p2PService, null, null, null, offerBookService, null, null, null, null, null, null, null);
        AtomicBoolean startEditOfferSuccessful = new AtomicBoolean(false);
        ResultHandler resultHandler = () -> {
            startEditOfferSuccessful.set(true);
        };
        final OpenOffer openOffer = new OpenOffer(make(OfferMaker.btcUsdOffer), storage);
        openOffer.setState(DEACTIVATED);
        manager.editOpenOfferStart(openOffer, resultHandler, null);
        TestCase.assertTrue(startEditOfferSuccessful.get());
    }

    @Test
    public void testStartEditOfferForOfferThatIsCurrentlyEdited() {
        P2PService p2PService = Mockito.mock(P2PService.class);
        OfferBookService offerBookService = Mockito.mock(OfferBookService.class);
        Storage storage = Mockito.mock(Storage.class);
        Mockito.when(p2PService.getPeerManager()).thenReturn(Mockito.mock(PeerManager.class));
        final OpenOfferManager manager = new OpenOfferManager(null, null, p2PService, null, null, null, offerBookService, null, null, null, null, null, null, null);
        AtomicBoolean startEditOfferSuccessful = new AtomicBoolean(false);
        ResultHandler resultHandler = () -> {
            startEditOfferSuccessful.set(true);
        };
        final OpenOffer openOffer = new OpenOffer(make(OfferMaker.btcUsdOffer), storage);
        openOffer.setState(DEACTIVATED);
        manager.editOpenOfferStart(openOffer, resultHandler, null);
        TestCase.assertTrue(startEditOfferSuccessful.get());
        startEditOfferSuccessful.set(false);
        manager.editOpenOfferStart(openOffer, resultHandler, null);
        TestCase.assertTrue(startEditOfferSuccessful.get());
    }
}

