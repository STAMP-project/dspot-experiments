package se.citerus.dddsample.application;


import HandlingEvent.Type.LOAD;
import java.util.Date;
import org.junit.Test;
import org.mockito.Mockito;
import se.citerus.dddsample.application.impl.HandlingEventServiceImpl;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.SampleLocations;
import se.citerus.dddsample.domain.model.voyage.SampleVoyages;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;


public class HandlingEventServiceTest {
    private HandlingEventServiceImpl service;

    private ApplicationEvents applicationEvents;

    private CargoRepository cargoRepository;

    private VoyageRepository voyageRepository;

    private HandlingEventRepository handlingEventRepository;

    private LocationRepository locationRepository;

    private final Cargo cargo = new Cargo(new TrackingId("ABC"), new se.citerus.dddsample.domain.model.cargo.RouteSpecification(SampleLocations.HAMBURG, SampleLocations.TOKYO, new Date()));

    @Test
    public void testRegisterEvent() throws Exception {
        Mockito.when(cargoRepository.find(cargo.trackingId())).thenReturn(cargo);
        Mockito.when(voyageRepository.find(SampleVoyages.CM001.voyageNumber())).thenReturn(SampleVoyages.CM001);
        Mockito.when(locationRepository.find(SampleLocations.STOCKHOLM.unLocode())).thenReturn(SampleLocations.STOCKHOLM);
        service.registerHandlingEvent(new Date(), cargo.trackingId(), SampleVoyages.CM001.voyageNumber(), SampleLocations.STOCKHOLM.unLocode(), LOAD);
    }
}

