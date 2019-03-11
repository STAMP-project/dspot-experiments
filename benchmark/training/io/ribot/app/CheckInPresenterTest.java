package io.ribot.app;


import io.ribot.app.data.DataManager;
import io.ribot.app.data.model.CheckIn;
import io.ribot.app.data.model.CheckInRequest;
import io.ribot.app.data.model.Venue;
import io.ribot.app.test.common.MockModelFabric;
import io.ribot.app.ui.checkin.CheckInMvpView;
import io.ribot.app.ui.checkin.CheckInPresenter;
import io.ribot.app.util.RxSchedulersOverrideRule;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;


@RunWith(MockitoJUnitRunner.class)
public class CheckInPresenterTest {
    @Mock
    CheckInMvpView mMockMvpView;

    @Mock
    DataManager mMockDataManager;

    private CheckInPresenter mPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Test
    public void loadVenuesSuccessful() {
        List<Venue> venues = MockModelFabric.newVenueList(10);
        Mockito.doReturn(Observable.just(venues)).when(mMockDataManager).getVenues();
        mPresenter.loadVenues();
        Mockito.verify(mMockMvpView).showVenuesProgress(true);
        Mockito.verify(mMockMvpView).showVenues(venues, null);
        Mockito.verify(mMockMvpView).showVenuesProgress(false);
    }

    @Test
    public void loadVenuesSuccessfulWhenLatestCheckInToday() {
        List<Venue> venues = MockModelFabric.newVenueList(10);
        CheckIn checkIn = MockModelFabric.newCheckInWithVenue();
        Mockito.doReturn(Observable.just(checkIn)).when(mMockDataManager).getTodayLatestCheckIn();
        Mockito.doReturn(Observable.just(venues)).when(mMockDataManager).getVenues();
        mPresenter.loadVenues();
        Mockito.verify(mMockMvpView).showVenuesProgress(true);
        Mockito.verify(mMockMvpView).showVenues(venues, checkIn.venue.id);
        Mockito.verify(mMockMvpView).showVenuesProgress(false);
    }

    @Test
    public void loadVenuesFail() {
        List<Venue> venues = MockModelFabric.newVenueList(10);
        Mockito.doReturn(Observable.error(new RuntimeException())).when(mMockDataManager).getVenues();
        mPresenter.loadVenues();
        Mockito.verify(mMockMvpView).showVenuesProgress(true);
        Mockito.verify(mMockMvpView, Mockito.never()).showVenues(venues, null);
        Mockito.verify(mMockMvpView).showVenuesProgress(false);
    }

    @Test
    public void checkInAtVenueSuccessful() {
        Venue venue = MockModelFabric.newVenue();
        CheckIn checkIn = MockModelFabric.newCheckInWithVenue();
        checkIn.venue = venue;
        CheckInRequest request = CheckInRequest.fromVenue(venue.id);
        Mockito.doReturn(Observable.just(checkIn)).when(mMockDataManager).checkIn(request);
        mPresenter.checkInAtVenue(venue);
        Mockito.verify(mMockMvpView).showCheckInAtVenueProgress(true, venue.id);
        Mockito.verify(mMockMvpView).showCheckInAtVenueSuccessful(venue);
        Mockito.verify(mMockMvpView).showCheckInAtVenueProgress(false, venue.id);
    }

    @Test
    public void checkInAtVenueFail() {
        Venue venue = MockModelFabric.newVenue();
        CheckInRequest request = CheckInRequest.fromVenue(venue.id);
        Mockito.doReturn(Observable.error(new RuntimeException())).when(mMockDataManager).checkIn(request);
        mPresenter.checkInAtVenue(venue);
        Mockito.verify(mMockMvpView).showCheckInAtVenueProgress(true, venue.id);
        Mockito.verify(mMockMvpView).showCheckInFailed();
        Mockito.verify(mMockMvpView).showCheckInAtVenueProgress(false, venue.id);
    }

    @Test
    public void checkInSuccessful() {
        String locationName = MockModelFabric.randomString();
        CheckIn checkIn = MockModelFabric.newCheckInWithLabel();
        checkIn.label = locationName;
        CheckInRequest request = CheckInRequest.fromLabel(locationName);
        Mockito.doReturn(Observable.just(checkIn)).when(mMockDataManager).checkIn(request);
        mPresenter.checkIn(locationName);
        Mockito.verify(mMockMvpView).showCheckInButton(false);
        Mockito.verify(mMockMvpView).showCheckInProgress(true);
        Mockito.verify(mMockMvpView).showCheckInSuccessful(locationName);
        Mockito.verify(mMockMvpView).showCheckInProgress(false);
    }

    @Test
    public void checkInFail() {
        String locationName = MockModelFabric.randomString();
        CheckInRequest request = CheckInRequest.fromLabel(locationName);
        Mockito.doReturn(Observable.error(new RuntimeException())).when(mMockDataManager).checkIn(request);
        mPresenter.checkIn(locationName);
        Mockito.verify(mMockMvpView).showCheckInButton(false);
        Mockito.verify(mMockMvpView).showCheckInProgress(true);
        Mockito.verify(mMockMvpView).showCheckInFailed();
        Mockito.verify(mMockMvpView).showCheckInProgress(false);
        Mockito.verify(mMockMvpView).showCheckInButton(true);
    }

    @Test
    public void loadTodayLatestCheckInWithLabel() {
        CheckIn checkIn = MockModelFabric.newCheckInWithLabel();
        Mockito.doReturn(Observable.just(checkIn)).when(mMockDataManager).getTodayLatestCheckIn();
        mPresenter.loadTodayLatestCheckInWithLabel();
        Mockito.verify(mMockMvpView).showTodayLatestCheckInWithLabel(checkIn.label);
    }

    @Test
    public void loadTodayLatestCheckInWithLabelWhenNoneSaved() {
        mPresenter.loadTodayLatestCheckInWithLabel();
        Mockito.verify(mMockMvpView, Mockito.never()).showTodayLatestCheckInWithLabel(ArgumentMatchers.anyString());
    }
}

