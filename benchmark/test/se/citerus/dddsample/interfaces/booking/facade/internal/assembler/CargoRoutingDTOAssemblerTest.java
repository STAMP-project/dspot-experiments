package se.citerus.dddsample.interfaces.booking.facade.internal.assembler;


import java.util.Arrays;
import java.util.Date;
import org.junit.Test;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.SampleLocations;
import se.citerus.dddsample.domain.model.voyage.SampleVoyages;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO;


public class CargoRoutingDTOAssemblerTest {
    @Test
    public void testToDTO() {
        final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();
        final Location origin = SampleLocations.STOCKHOLM;
        final Location destination = SampleLocations.MELBOURNE;
        final Cargo cargo = new Cargo(new TrackingId("XYZ"), new se.citerus.dddsample.domain.model.cargo.RouteSpecification(origin, destination, new Date()));
        final Itinerary itinerary = new Itinerary(Arrays.asList(new se.citerus.dddsample.domain.model.cargo.Leg(SampleVoyages.CM001, origin, SampleLocations.SHANGHAI, new Date(), new Date()), new se.citerus.dddsample.domain.model.cargo.Leg(SampleVoyages.CM001, SampleLocations.ROTTERDAM, destination, new Date(), new Date())));
        cargo.assignToRoute(itinerary);
        final CargoRoutingDTO dto = assembler.toDTO(cargo);
        assertThat(dto.getLegs()).hasSize(2);
        LegDTO legDTO = dto.getLegs().get(0);
        assertThat(legDTO.getVoyageNumber()).isEqualTo("CM001");
        assertThat(legDTO.getFrom()).isEqualTo("SESTO");
        assertThat(legDTO.getTo()).isEqualTo("CNSHA");
        legDTO = dto.getLegs().get(1);
        assertThat(legDTO.getVoyageNumber()).isEqualTo("CM001");
        assertThat(legDTO.getFrom()).isEqualTo("NLRTM");
        assertThat(legDTO.getTo()).isEqualTo("AUMEL");
    }

    @Test
    public void testToDTO_NoItinerary() {
        final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();
        final Cargo cargo = new Cargo(new TrackingId("XYZ"), new se.citerus.dddsample.domain.model.cargo.RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, new Date()));
        final CargoRoutingDTO dto = assembler.toDTO(cargo);
        assertThat(dto.getTrackingId()).isEqualTo("XYZ");
        assertThat(dto.getOrigin()).isEqualTo("SESTO");
        assertThat(dto.getFinalDestination()).isEqualTo("AUMEL");
        assertThat(dto.getLegs().isEmpty()).isTrue();
    }
}

