package bisq.core.dao.governance.ballot;


import bisq.common.storage.Storage;
import bisq.core.dao.governance.ballot.BallotListService.BallotListChangeListener;
import bisq.core.dao.governance.period.PeriodService;
import bisq.core.dao.governance.proposal.ProposalService;
import bisq.core.dao.governance.proposal.ProposalValidator;
import bisq.core.dao.governance.proposal.storage.appendonly.ProposalPayload;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ PeriodService.class, ProposalPayload.class })
public class BallotListServiceTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testAddListenersWhenNewPayloadAdded() {
        // given
        ObservableList<ProposalPayload> payloads = FXCollections.observableArrayList();
        ProposalService proposalService = Mockito.mock(ProposalService.class);
        Mockito.when(proposalService.getProposalPayloads()).thenReturn(payloads);
        BallotListService service = new BallotListService(proposalService, Mockito.mock(PeriodService.class), Mockito.mock(ProposalValidator.class), Mockito.mock(Storage.class));
        BallotListChangeListener listener = Mockito.mock(BallotListChangeListener.class);
        service.addListener(listener);
        service.addListeners();
        // when
        payloads.add(Mockito.mock(ProposalPayload.class, Mockito.RETURNS_DEEP_STUBS));
        // then
        Mockito.verify(listener).onListChanged(ArgumentMatchers.any());
    }
}

