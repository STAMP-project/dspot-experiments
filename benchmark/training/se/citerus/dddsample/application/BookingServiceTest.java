package se.citerus.dddsample.application;


import java.util.Date;
import org.junit.Test;
import org.mockito.Mockito;
import se.citerus.dddsample.application.impl.BookingServiceImpl;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.SampleLocations;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.RoutingService;


public class BookingServiceTest {
    BookingServiceImpl bookingService;

    CargoRepository cargoRepository;

    LocationRepository locationRepository;

    RoutingService routingService;

    @Test
    public void testRegisterNew() {
        TrackingId expectedTrackingId = new TrackingId("TRK1");
        UnLocode fromUnlocode = new UnLocode("USCHI");
        UnLocode toUnlocode = new UnLocode("SESTO");
        Mockito.when(cargoRepository.nextTrackingId()).thenReturn(expectedTrackingId);
        Mockito.when(locationRepository.find(fromUnlocode)).thenReturn(SampleLocations.CHICAGO);
        Mockito.when(locationRepository.find(toUnlocode)).thenReturn(SampleLocations.STOCKHOLM);
        TrackingId trackingId = bookingService.bookNewCargo(fromUnlocode, toUnlocode, new Date());
        assertThat(trackingId).isEqualTo(expectedTrackingId);
    }
}

