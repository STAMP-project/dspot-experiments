package io.ribot.app;


import BusEvent.BeaconsSyncCompleted;
import RibotService.SignInRequest;
import RibotService.SignInResponse;
import android.accounts.Account;
import android.text.format.DateUtils;
import io.ribot.app.data.BeaconNotRegisteredException;
import io.ribot.app.data.DataManager;
import io.ribot.app.data.local.DatabaseHelper;
import io.ribot.app.data.local.PreferencesHelper;
import io.ribot.app.data.model.CheckIn;
import io.ribot.app.data.model.CheckInRequest;
import io.ribot.app.data.model.Encounter;
import io.ribot.app.data.model.RegisteredBeacon;
import io.ribot.app.data.model.Ribot;
import io.ribot.app.data.model.Venue;
import io.ribot.app.data.remote.GoogleAuthHelper;
import io.ribot.app.data.remote.RibotService;
import io.ribot.app.test.common.MockModelFabric;
import io.ribot.app.util.EventPosterHelper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;
import rx.observers.TestSubscriber;


/**
 * This test class performs local unit tests without dependencies on the Android framework
 */
@RunWith(MockitoJUnitRunner.class)
public class DataManagerTest {
    @Mock
    RibotService mMockRibotsService;

    @Mock
    DatabaseHelper mMockDatabaseHelper;

    @Mock
    PreferencesHelper mMockPreferencesHelper;

    @Mock
    GoogleAuthHelper mMockGoogleAuthHelper;

    @Mock
    EventPosterHelper mMockEventPosterHelper;

    DataManager mDataManager;

    @Test
    public void signInSuccessful() {
        // Stub GoogleAuthHelper and RibotService mocks
        RibotService.SignInResponse signInResponse = new RibotService.SignInResponse();
        signInResponse.ribot = MockModelFabric.newRibot();
        signInResponse.accessToken = MockModelFabric.randomString();
        Account account = new Account("ivan@ribot.co.uk", "google.com");
        String googleAccessCode = MockModelFabric.randomString();
        Mockito.doReturn(Observable.just(googleAccessCode)).when(mMockGoogleAuthHelper).retrieveAuthTokenAsObservable(account);
        Mockito.doReturn(Observable.just(signInResponse)).when(mMockRibotsService).signIn(ArgumentMatchers.any(SignInRequest.class));
        // Test the sign in Observable
        TestSubscriber<Ribot> testSubscriber = new TestSubscriber();
        mDataManager.signIn(account).subscribe(testSubscriber);
        testSubscriber.assertValue(signInResponse.ribot);
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        Mockito.verify(mMockPreferencesHelper).putAccessToken(signInResponse.accessToken);
        Mockito.verify(mMockPreferencesHelper).putSignedInRibot(signInResponse.ribot);
    }

    @Test
    public void signOutCompletes() {
        Mockito.doReturn(Observable.empty()).when(mMockDatabaseHelper).clearTables();
        TestSubscriber<Void> testSubscriber = new TestSubscriber();
        mDataManager.signOut().subscribe(testSubscriber);
        testSubscriber.assertCompleted();
    }

    @Test
    public void signOutClearsData() {
        Mockito.doReturn(Observable.empty()).when(mMockDatabaseHelper).clearTables();
        mDataManager.signOut().subscribe(new TestSubscriber<Void>());
        Mockito.verify(mMockDatabaseHelper).clearTables();
        Mockito.verify(mMockPreferencesHelper).clear();
    }

    @Test
    public void getRibots() {
        List<Ribot> ribots = MockModelFabric.newRibotList(17);
        Mockito.doReturn(Observable.just(ribots)).when(mMockRibotsService).getRibots(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        TestSubscriber<List<Ribot>> testSubscriber = new TestSubscriber();
        mDataManager.getRibots().subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertValue(ribots);
    }

    @Test
    public void getVenuesWhenEmptyCache() {
        List<Venue> venuesApi = MockModelFabric.newVenueList(10);
        stubRibotServiceGetVenues(Observable.just(venuesApi));
        stubPreferencesHelperGetVenues(Observable.<List<Venue>>empty());
        TestSubscriber<List<Venue>> testSubscriber = new TestSubscriber();
        mDataManager.getVenues().subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(venuesApi));
        // Check that the API result is cached
        Mockito.verify(mMockPreferencesHelper).putVenues(venuesApi);
    }

    @Test
    public void getVenuesWhenDataCachedSameAsApi() {
        List<Venue> venues = MockModelFabric.newVenueList(10);
        stubRibotServiceGetVenues(Observable.just(venues));
        stubPreferencesHelperGetVenues(Observable.just(venues));
        TestSubscriber<List<Venue>> testSubscriber = new TestSubscriber();
        mDataManager.getVenues().subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(venues));
    }

    @Test
    public void getVenuesWhenDataCachedDifferentToApi() {
        List<Venue> venuesApi = MockModelFabric.newVenueList(10);
        List<Venue> venuesCache = MockModelFabric.newVenueList(4);
        stubRibotServiceGetVenues(Observable.just(venuesApi));
        stubPreferencesHelperGetVenues(Observable.just(venuesCache));
        TestSubscriber<List<Venue>> testSubscriber = new TestSubscriber();
        mDataManager.getVenues().subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(2);
        testSubscriber.assertReceivedOnNext(Arrays.asList(venuesCache, venuesApi));
        // Check that the new API result is cached
        Mockito.verify(mMockPreferencesHelper).putVenues(venuesApi);
    }

    @Test
    public void getVenuesWhenDataCachedAndApiFails() {
        List<Venue> venuesCache = MockModelFabric.newVenueList(4);
        stubRibotServiceGetVenues(Observable.<List<Venue>>error(new RuntimeException()));
        stubPreferencesHelperGetVenues(Observable.just(venuesCache));
        TestSubscriber<List<Venue>> testSubscriber = new TestSubscriber();
        mDataManager.getVenues().subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertReceivedOnNext(Collections.singletonList(venuesCache));
    }

    @Test
    public void checkInSuccessful() {
        CheckIn checkIn = MockModelFabric.newCheckInWithLabel();
        CheckInRequest request = CheckInRequest.fromLabel(MockModelFabric.randomString());
        Mockito.doReturn(Observable.just(checkIn)).when(mMockRibotsService).checkIn(ArgumentMatchers.anyString(), ArgumentMatchers.eq(request));
        TestSubscriber<CheckIn> testSubscriber = new TestSubscriber();
        mDataManager.checkIn(request).subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(checkIn));
        // Check that is saved in preferences
        Mockito.verify(mMockPreferencesHelper).putLatestCheckIn(checkIn);
    }

    @Test
    public void checkInFail() {
        CheckInRequest request = CheckInRequest.fromLabel(MockModelFabric.randomString());
        Mockito.doReturn(Observable.error(new RuntimeException())).when(mMockRibotsService).checkIn(ArgumentMatchers.anyString(), ArgumentMatchers.eq(request));
        TestSubscriber<CheckIn> testSubscriber = new TestSubscriber();
        mDataManager.checkIn(request).subscribe(testSubscriber);
        testSubscriber.assertError(RuntimeException.class);
        testSubscriber.assertNoValues();
        Mockito.verify(mMockPreferencesHelper, Mockito.never()).putLatestCheckIn(ArgumentMatchers.any(CheckIn.class));
    }

    @Test
    public void getTodayLatestCheckIn() {
        CheckIn checkIn = MockModelFabric.newCheckInWithVenue();
        Mockito.doReturn(Observable.just(checkIn)).when(mMockPreferencesHelper).getLatestCheckInAsObservable();
        TestSubscriber<CheckIn> testSubscriber = new TestSubscriber();
        mDataManager.getTodayLatestCheckIn().subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(checkIn));
    }

    @Test
    public void getTodayLatestCheckInWhenLatestWasBeforeToday() {
        CheckIn checkIn = MockModelFabric.newCheckInWithVenue();
        checkIn.checkedInDate.setTime(((System.currentTimeMillis()) - (DateUtils.DAY_IN_MILLIS)));
        Mockito.doReturn(Observable.just(checkIn)).when(mMockPreferencesHelper).getLatestCheckInAsObservable();
        TestSubscriber<CheckIn> testSubscriber = new TestSubscriber();
        mDataManager.getTodayLatestCheckIn().subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertNoValues();
    }

    @Test
    public void performBeaconEncounter() {
        String beaconId = MockModelFabric.randomString();
        Encounter encounter = MockModelFabric.newEncounter();
        Mockito.doReturn(Observable.just(encounter)).when(mMockRibotsService).performBeaconEncounter(ArgumentMatchers.anyString(), ArgumentMatchers.eq(beaconId));
        TestSubscriber<Encounter> testSubscriber = new TestSubscriber();
        mDataManager.performBeaconEncounter(beaconId).subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertValue(encounter);
        Mockito.verify(mMockPreferencesHelper).putLatestEncounter(encounter);
    }

    @Test
    public void performBeaconEncounterFails() {
        String beaconId = MockModelFabric.randomString();
        Mockito.doReturn(Observable.error(new RuntimeException())).when(mMockRibotsService).performBeaconEncounter(ArgumentMatchers.anyString(), ArgumentMatchers.eq(beaconId));
        TestSubscriber<Encounter> testSubscriber = new TestSubscriber();
        mDataManager.performBeaconEncounter(beaconId).subscribe(testSubscriber);
        testSubscriber.assertError(RuntimeException.class);
        testSubscriber.assertNoValues();
        Mockito.verify(mMockPreferencesHelper, Mockito.never()).putLatestEncounter(ArgumentMatchers.any(Encounter.class));
    }

    @Test
    public void performBeaconEncounterWithUuidMajorAndMinor() {
        RegisteredBeacon registeredBeacon = MockModelFabric.newRegisteredBeacon();
        Mockito.doReturn(Observable.just(registeredBeacon)).when(mMockDatabaseHelper).findRegisteredBeacon(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Encounter encounter = MockModelFabric.newEncounter();
        Mockito.doReturn(Observable.just(encounter)).when(mMockRibotsService).performBeaconEncounter(ArgumentMatchers.anyString(), ArgumentMatchers.eq(registeredBeacon.id));
        TestSubscriber<Encounter> testSubscriber = new TestSubscriber();
        mDataManager.performBeaconEncounter(registeredBeacon.uuid, registeredBeacon.major, registeredBeacon.minor).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(encounter);
        testSubscriber.assertCompleted();
    }

    @Test
    public void performBeaconEncounterFailWithBeaconNotRegistered() {
        // This beacon is not returned in the local database so the encounter should fail
        RegisteredBeacon registeredBeacon = MockModelFabric.newRegisteredBeacon();
        Mockito.doReturn(Observable.empty()).when(mMockDatabaseHelper).findRegisteredBeacon(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        TestSubscriber<Encounter> testSubscriber = new TestSubscriber();
        mDataManager.performBeaconEncounter(registeredBeacon.uuid, registeredBeacon.major, registeredBeacon.minor).subscribe(testSubscriber);
        testSubscriber.assertError(BeaconNotRegisteredException.class);
    }

    @Test
    public void syncRegisteredBeacons() {
        List<RegisteredBeacon> registeredBeacons = MockModelFabric.newRegisteredBeaconList(3);
        Mockito.doReturn(Observable.just(registeredBeacons)).when(mMockRibotsService).getRegisteredBeacons(ArgumentMatchers.anyString());
        Mockito.doReturn(Observable.empty()).when(mMockDatabaseHelper).setRegisteredBeacons(ArgumentMatchers.anyListOf(RegisteredBeacon.class));
        TestSubscriber<Void> testSubscriber = new TestSubscriber();
        mDataManager.syncRegisteredBeacons().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        Mockito.verify(mMockDatabaseHelper).setRegisteredBeacons(registeredBeacons);
        Mockito.verify(mMockEventPosterHelper).postEventSafely(ArgumentMatchers.any(BeaconsSyncCompleted.class));
    }

    @Test
    public void checkOutCompletesAndEmitsCheckIn() {
        Encounter encounter = MockModelFabric.newEncounter();
        CheckIn checkIn = encounter.checkIn;
        stubPreferencesHelperLatestEncounter(encounter);
        checkIn.isCheckedOut = true;
        stubRibotServiceUpdateCheckIn(checkIn);
        TestSubscriber<CheckIn> testSubscriber = new TestSubscriber();
        mDataManager.checkOut(checkIn.id).subscribe(testSubscriber);
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(checkIn);
    }

    @Test
    public void checkOutSuccessfulUpdatesLatestCheckIn() {
        Encounter encounter = MockModelFabric.newEncounter();
        CheckIn checkIn = encounter.checkIn;
        checkIn.isCheckedOut = false;
        stubPreferencesHelperLatestEncounter(encounter);
        checkIn.isCheckedOut = true;// api would return isCheckedOut true if successful

        stubRibotServiceUpdateCheckIn(checkIn);
        mDataManager.checkOut(checkIn.id).subscribe();
        Mockito.verify(mMockPreferencesHelper).putLatestCheckIn(checkIn);
    }

    @Test
    public void checkOutSuccessfulClearsLatestEncounter() {
        Encounter encounter = MockModelFabric.newEncounter();
        CheckIn checkIn = encounter.checkIn;
        checkIn.isCheckedOut = false;
        stubPreferencesHelperLatestEncounter(encounter);
        checkIn.isCheckedOut = true;// api would return isCheckedOut true if successful

        stubRibotServiceUpdateCheckIn(checkIn);
        mDataManager.checkOut(checkIn.id).subscribe();
        Mockito.verify(mMockPreferencesHelper).clearLatestEncounter();
    }
}

