package se.citerus.dddsample.infrastructure.routing;


import SampleVoyages.CM002;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;


public class ExternalRoutingServiceTest {
    private ExternalRoutingService externalRoutingService;

    private VoyageRepository voyageRepository;

    // TODO this test belongs in com.pathfinder
    @Test
    public void testCalculatePossibleRoutes() {
        TrackingId trackingId = new TrackingId("ABC");
        RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, HELSINKI, new Date());
        Cargo cargo = new Cargo(trackingId, routeSpecification);
        Mockito.when(voyageRepository.find(ArgumentMatchers.isA(VoyageNumber.class))).thenReturn(CM002);
        List<Itinerary> candidates = externalRoutingService.fetchRoutesForSpecification(routeSpecification);
        assertThat(candidates).isNotNull();
        for (Itinerary itinerary : candidates) {
            List<Leg> legs = itinerary.legs();
            assertThat(legs).isNotNull();
            assertThat(legs.isEmpty()).isFalse();
            // Cargo origin and start of first leg should match
            assertThat(legs.get(0).loadLocation()).isEqualTo(cargo.origin());
            // Cargo final destination and last leg stop should match
            Location lastLegStop = legs.get(((legs.size()) - 1)).unloadLocation();
            assertThat(lastLegStop).isEqualTo(cargo.routeSpecification().destination());
            for (int i = 0; i < ((legs.size()) - 1); i++) {
                // Assert that all legs are connected
                assertThat(legs.get((i + 1)).loadLocation()).isEqualTo(legs.get(i).unloadLocation());
            }
        }
    }
}

