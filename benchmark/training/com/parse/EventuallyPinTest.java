package com.parse;


import EventuallyPin.PIN_NAME;
import ParseQuery.State;
import android.database.sqlite.SQLiteException;
import bolts.Task;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = TestHelper.ROBOLECTRIC_SDK_VERSION)
public class EventuallyPinTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFailingFindAllPinned() throws Exception {
        OfflineStore offlineStore = Mockito.mock(OfflineStore.class);
        Parse.setLocalDatastore(offlineStore);
        Mockito.when(offlineStore.findFromPinAsync(ArgumentMatchers.eq(PIN_NAME), ArgumentMatchers.any(State.class), ArgumentMatchers.any(ParseUser.class))).thenReturn(Task.forError(new SQLiteException()));
        ParsePlugins plugins = Mockito.mock(ParsePlugins.class);
        ParsePlugins.set(plugins);
        Mockito.when(plugins.restClient()).thenReturn(ParseHttpClient.createClient(null));
        thrown.expect(SQLiteException.class);
        ParseTaskUtils.wait(EventuallyPin.findAllPinned());
    }
}

