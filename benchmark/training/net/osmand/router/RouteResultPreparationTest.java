package net.osmand.router;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.osmand.PlatformUtil;
import net.osmand.data.LatLon;
import net.osmand.util.Algorithms;
import org.apache.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static RouteResultPreparation.SHIFT_ID;


/**
 * Created by yurkiss on 04.03.16.
 */
@RunWith(Parameterized.class)
public class RouteResultPreparationTest {
    private static RoutePlannerFrontEnd fe;

    private static RoutingContext ctx;

    private String testName;

    private LatLon startPoint;

    private LatLon endPoint;

    private Map<Long, String> expectedResults;

    private Log log = PlatformUtil.getLog(RouteResultPreparationTest.class);

    public RouteResultPreparationTest(String testName, LatLon startPoint, LatLon endPoint, Map<Long, String> expectedResults) {
        this.testName = testName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.expectedResults = expectedResults;
    }

    @Test
    public void testLanes() throws Exception {
        List<RouteSegmentResult> routeSegments = RouteResultPreparationTest.fe.searchRoute(RouteResultPreparationTest.ctx, startPoint, endPoint, null);
        Set<Long> reachedSegments = new TreeSet<Long>();
        Assert.assertNotNull(routeSegments);
        int prevSegment = -1;
        for (int i = 0; i <= (routeSegments.size()); i++) {
            if ((i == (routeSegments.size())) || ((routeSegments.get(i).getTurnType()) != null)) {
                if (prevSegment >= 0) {
                    String lanes = getLanesString(routeSegments.get(prevSegment));
                    String turn = routeSegments.get(prevSegment).getTurnType().toXmlString();
                    String turnLanes = (turn + ":") + lanes;
                    String name = routeSegments.get(prevSegment).getDescription();
                    long segmentId = (routeSegments.get(prevSegment).getObject().getId()) >> (SHIFT_ID);
                    String expectedResult = expectedResults.get(segmentId);
                    if (expectedResult != null) {
                        if (((!(Algorithms.objectEquals(expectedResult, turnLanes))) && (!(Algorithms.objectEquals(expectedResult, lanes)))) && (!(Algorithms.objectEquals(expectedResult, turn)))) {
                            Assert.assertEquals(("Segment " + segmentId), expectedResult, turnLanes);
                        }
                    }
                    System.out.println(((("segmentId: " + segmentId) + " description: ") + name));
                }
                prevSegment = i;
            }
            if (i < (routeSegments.size())) {
                reachedSegments.add(((routeSegments.get(i).getObject().getId()) >> (SHIFT_ID)));
            }
        }
        Set<Long> expectedSegments = expectedResults.keySet();
        for (Long expSegId : expectedSegments) {
            Assert.assertTrue(((("Expected segment " + expSegId) + " weren't reached in route segments ") + (reachedSegments.toString())), reachedSegments.contains(expSegId));
        }
    }
}

