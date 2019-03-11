package io.nlopez.smartlocation.location.providers;


import android.content.Context;
import android.location.Location;
import io.nlopez.smartlocation.CustomTestRunner;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.location.LocationProvider;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.utils.Logger;
import io.nlopez.smartlocation.utils.ServiceConnectionListener;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;


/**
 * Unit tests for the {@link MultiFallbackProvider}
 *
 * @author abkaplan07
 */
@RunWith(CustomTestRunner.class)
@Config(manifest = Config.NONE)
public class MultiFallbackProviderTest {
    @Test
    public void testDefaultBuilder() {
        MultiFallbackProvider subject = new MultiFallbackProvider.Builder().build();
        checkExpectedProviders(subject, LocationManagerProvider.class);
    }

    @Test
    public void testGoogleBuilder() {
        MultiFallbackProvider subject = new MultiFallbackProvider.Builder().withGooglePlayServicesProvider().build();
        checkExpectedProviders(subject, LocationGooglePlayServicesProvider.class);
    }

    @Test
    public void testMultiProviderBuilder() {
        MultiFallbackProvider subject = new MultiFallbackProvider.Builder().withGooglePlayServicesProvider().withDefaultProvider().build();
        checkExpectedProviders(subject, LocationGooglePlayServicesProvider.class, LocationManagerProvider.class);
    }

    @Test
    public void testMultiProviderRun() {
        TestServiceProvider testServiceProvider = new TestServiceProvider();
        ServiceConnectionListener mockListener = Mockito.mock(ServiceConnectionListener.class);
        testServiceProvider.setServiceListener(mockListener);
        LocationProvider backupProvider = Mockito.mock(LocationProvider.class);
        MultiFallbackProvider subject = new MultiFallbackProvider.Builder().withServiceProvider(testServiceProvider).withProvider(backupProvider).build();
        // Test initialization passes through to first provider
        subject.init(Mockito.mock(Context.class), Mockito.mock(Logger.class));
        Assert.assertEquals(1, testServiceProvider.getInitCount());
        // Test starting location updates passes through to first provider
        OnLocationUpdatedListener listenerMock = Mockito.mock(OnLocationUpdatedListener.class);
        LocationParams paramsMock = Mockito.mock(LocationParams.class);
        subject.start(listenerMock, paramsMock, false);
        Assert.assertEquals(1, testServiceProvider.getStartCount());
        // Test that falling back initializes and starts the backup provider
        testServiceProvider.simulateFailure();
        // Ensure that our 1st listener from the test service provider was invoked.
        Mockito.verify(mockListener).onConnectionFailed();
        Assert.assertEquals(1, testServiceProvider.getStopCount());
        // Verify that the backup provider is initialized and started.
        Mockito.verify(backupProvider).init(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(Logger.class));
        Mockito.verify(backupProvider).start(listenerMock, paramsMock, false);
        // Test that we're now using the fallback provider to stop.
        subject.stop();
        Mockito.verify(backupProvider).stop();
        Assert.assertEquals(1, testServiceProvider.getStopCount());
        // Test that we're now using the fallback provider to get the last location
        Location mockLocation = Mockito.mock(Location.class);
        Mockito.when(backupProvider.getLastLocation()).thenReturn(mockLocation);
        Assert.assertEquals(mockLocation, subject.getLastLocation());
        Assert.assertEquals(0, testServiceProvider.getLastLocCount());
    }
}

