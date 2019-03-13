package se.citerus.dddsample.domain.model.handling;


import Type.CLAIM;
import Type.LOAD;
import Voyage.NONE;
import java.util.Date;
import org.junit.Test;
import org.mockito.Mockito;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.SampleLocations;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.SampleVoyages;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;


public class HandlingEventFactoryTest {
    private HandlingEventFactory factory;

    private CargoRepository cargoRepository;

    private VoyageRepository voyageRepository;

    private LocationRepository locationRepository;

    private TrackingId trackingId;

    private Cargo cargo;

    @Test
    public void testCreateHandlingEventWithCarrierMovement() throws Exception {
        Mockito.when(cargoRepository.find(trackingId)).thenReturn(cargo);
        VoyageNumber voyageNumber = SampleVoyages.CM001.voyageNumber();
        UnLocode unLocode = SampleLocations.STOCKHOLM.unLocode();
        HandlingEvent handlingEvent = factory.createHandlingEvent(new Date(), new Date(100), trackingId, voyageNumber, unLocode, LOAD);
        assertThat(handlingEvent).isNotNull();
        assertThat(handlingEvent.location()).isEqualTo(SampleLocations.STOCKHOLM);
        assertThat(handlingEvent.voyage()).isEqualTo(SampleVoyages.CM001);
        assertThat(handlingEvent.cargo()).isEqualTo(cargo);
        assertThat(handlingEvent.completionTime()).isEqualTo(new Date(100));
        assertThat(handlingEvent.registrationTime().before(new Date(((System.currentTimeMillis()) + 1)))).isTrue();
    }

    @Test
    public void testCreateHandlingEventWithoutCarrierMovement() throws Exception {
        Mockito.when(cargoRepository.find(trackingId)).thenReturn(cargo);
        UnLocode unLocode = SampleLocations.STOCKHOLM.unLocode();
        HandlingEvent handlingEvent = factory.createHandlingEvent(new Date(), new Date(100), trackingId, null, unLocode, CLAIM);
        assertThat(handlingEvent).isNotNull();
        assertThat(handlingEvent.location()).isEqualTo(SampleLocations.STOCKHOLM);
        assertThat(handlingEvent.voyage()).isEqualTo(NONE);
        assertThat(handlingEvent.cargo()).isEqualTo(cargo);
        assertThat(handlingEvent.completionTime()).isEqualTo(new Date(100));
        assertThat(handlingEvent.registrationTime().before(new Date(((System.currentTimeMillis()) + 1)))).isTrue();
    }

    @Test
    public void testCreateHandlingEventUnknownLocation() throws Exception {
        Mockito.when(cargoRepository.find(trackingId)).thenReturn(cargo);
        UnLocode invalid = new UnLocode("NOEXT");
        try {
            factory.createHandlingEvent(new Date(), new Date(100), trackingId, SampleVoyages.CM001.voyageNumber(), invalid, LOAD);
            fail("Expected UnknownLocationException");
        } catch (UnknownLocationException expected) {
        }
    }

    @Test
    public void testCreateHandlingEventUnknownCarrierMovement() throws Exception {
        Mockito.when(cargoRepository.find(trackingId)).thenReturn(cargo);
        try {
            VoyageNumber invalid = new VoyageNumber("XXX");
            factory.createHandlingEvent(new Date(), new Date(100), trackingId, invalid, SampleLocations.STOCKHOLM.unLocode(), LOAD);
            fail("Expected UnknownVoyageException");
        } catch (UnknownVoyageException expected) {
        }
    }

    @Test
    public void testCreateHandlingEventUnknownTrackingId() throws Exception {
        Mockito.when(cargoRepository.find(trackingId)).thenReturn(null);
        try {
            factory.createHandlingEvent(new Date(), new Date(100), trackingId, SampleVoyages.CM001.voyageNumber(), SampleLocations.STOCKHOLM.unLocode(), LOAD);
            fail("Expected UnknownCargoException");
        } catch (UnknownCargoException expected) {
        }
    }
}

