package io.ribot.app;


import Db.BeaconTable;
import Db.BeaconTable.TABLE_NAME;
import android.database.Cursor;
import io.ribot.app.data.local.DatabaseHelper;
import io.ribot.app.data.model.RegisteredBeacon;
import io.ribot.app.test.common.MockModelFabric;
import io.ribot.app.util.DefaultConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import rx.observers.TestSubscriber;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = DefaultConfig.EMULATE_SDK)
public class DatabaseHelperTest {
    final DatabaseHelper mDatabaseHelper = new DatabaseHelper(new io.ribot.app.data.local.DbOpenHelper(RuntimeEnvironment.application));

    @Test
    public void findRegisteredBeacon() {
        RegisteredBeacon beaconToFind = MockModelFabric.newRegisteredBeacon();
        RegisteredBeacon anotherBeacon = MockModelFabric.newRegisteredBeacon();
        mDatabaseHelper.getBriteDb().insert(TABLE_NAME, BeaconTable.toContentValues(beaconToFind));
        mDatabaseHelper.getBriteDb().insert(TABLE_NAME, BeaconTable.toContentValues(anotherBeacon));
        TestSubscriber<RegisteredBeacon> testSubscriber = new TestSubscriber();
        mDatabaseHelper.findRegisteredBeacon(beaconToFind.uuid, beaconToFind.major, beaconToFind.minor).subscribe(testSubscriber);
        // zone is not saved in DB so we won't expect it to be returned
        beaconToFind.zone = null;
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(beaconToFind);
        testSubscriber.assertCompleted();
    }

    @Test
    public void setRegisteredBeacons() {
        List<RegisteredBeacon> beacons = MockModelFabric.newRegisteredBeaconList(3);
        TestSubscriber<Void> testSubscriber = new TestSubscriber();
        mDatabaseHelper.setRegisteredBeacons(beacons).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertNoValues();
        testSubscriber.assertCompleted();
        checkBeaconsSavedSuccessfully(beacons);
    }

    @Test
    public void setRegisteredBeaconsDeletesPreviousData() {
        List<RegisteredBeacon> existingBeacons = MockModelFabric.newRegisteredBeaconList(10);
        mDatabaseHelper.setRegisteredBeacons(existingBeacons).subscribe();
        List<RegisteredBeacon> newBeacons = MockModelFabric.newRegisteredBeaconList(5);
        TestSubscriber<Void> testSubscriber = new TestSubscriber();
        mDatabaseHelper.setRegisteredBeacons(newBeacons).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertNoValues();
        testSubscriber.assertCompleted();
        checkBeaconsSavedSuccessfully(newBeacons);
    }

    @Test
    public void setRegisteredBeaconsWithEmptyListClearsData() {
        List<RegisteredBeacon> existingBeacons = MockModelFabric.newRegisteredBeaconList(10);
        mDatabaseHelper.setRegisteredBeacons(existingBeacons).subscribe();
        TestSubscriber<Void> testSubscriber = new TestSubscriber();
        mDatabaseHelper.setRegisteredBeacons(new ArrayList<RegisteredBeacon>()).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertNoValues();
        testSubscriber.assertCompleted();
        Cursor cursor = mDatabaseHelper.getBriteDb().query(("SELECT * FROM " + (BeaconTable.TABLE_NAME)));
        Assert.assertEquals(0, cursor.getCount());
    }

    @Test
    public void findRegisteredBeaconsUuids() {
        RegisteredBeacon beacon1 = MockModelFabric.newRegisteredBeacon();
        RegisteredBeacon beacon2 = MockModelFabric.newRegisteredBeacon();
        RegisteredBeacon beacon3 = MockModelFabric.newRegisteredBeacon();
        beacon3.uuid = beacon1.uuid;
        mDatabaseHelper.setRegisteredBeacons(Arrays.asList(beacon1, beacon2, beacon3)).subscribe();
        TestSubscriber<String> testSubscriber = new TestSubscriber();
        mDatabaseHelper.findRegisteredBeaconsUuids().subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        // beacon3 has the same uuid as beacon2 so it shouldn't be returned twice
        testSubscriber.assertReceivedOnNext(Arrays.asList(beacon1.uuid, beacon2.uuid));
        testSubscriber.assertCompleted();
    }
}

