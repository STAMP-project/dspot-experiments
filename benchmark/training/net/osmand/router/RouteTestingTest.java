package net.osmand.router;


import RoutePlannerFrontEnd.RouteCalculationMode.NORMAL;
import RoutingConfiguration.Builder;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.osmand.binary.BinaryMapIndexReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static RouteResultPreparation.SHIFT_ID;
import static RoutingConfiguration.DEFAULT_MEMORY_LIMIT;


@RunWith(Parameterized.class)
public class RouteTestingTest {
    private TestEntry te;

    public RouteTestingTest(String name, TestEntry te) {
        this.te = te;
    }

    @Test
    public void testRouting() throws Exception {
        String fl = "src/test/resources/Routing_test.obf";
        RandomAccessFile raf = new RandomAccessFile(fl, "r");
        RoutePlannerFrontEnd fe = new RoutePlannerFrontEnd();
        BinaryMapIndexReader[] binaryMapIndexReaders = new BinaryMapIndexReader[]{ new BinaryMapIndexReader(raf, new File(fl)) };
        RoutingConfiguration.Builder builder = RoutingConfiguration.getDefault();
        Map<String, String> params = te.getParams();
        RoutingConfiguration config = builder.build((params.containsKey("vehicle") ? params.get("vehicle") : "car"), ((DEFAULT_MEMORY_LIMIT) * 3), params);
        RoutingContext ctx = fe.buildRoutingContext(config, null, binaryMapIndexReaders, NORMAL);
        ctx.leftSideNavigation = false;
        List<RouteSegmentResult> routeSegments = fe.searchRoute(ctx, te.getStartPoint(), te.getEndPoint(), te.getTransitPoint());
        Set<Long> reachedSegments = new TreeSet<Long>();
        Assert.assertNotNull(routeSegments);
        int prevSegment = -1;
        for (int i = 0; i <= (routeSegments.size()); i++) {
            if ((i == (routeSegments.size())) || ((routeSegments.get(i).getTurnType()) != null)) {
                if (prevSegment >= 0) {
                    String name = routeSegments.get(prevSegment).getDescription();
                    long segmentId = (routeSegments.get(prevSegment).getObject().getId()) >> (SHIFT_ID);
                    System.out.println(((("segmentId: " + segmentId) + " description: ") + name));
                }
                prevSegment = i;
            }
            if (i < (routeSegments.size())) {
                reachedSegments.add(((routeSegments.get(i).getObject().getId()) >> (SHIFT_ID)));
            }
        }
        Map<Long, String> expectedResults = te.getExpectedResults();
        Iterator<Map.Entry<Long, String>> it = expectedResults.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, String> es = it.next();
            if (es.getValue().equals("false")) {
                Assert.assertTrue(((("Expected segment " + (es.getKey())) + " was wrongly reached in route segments ") + (reachedSegments.toString())), (!(reachedSegments.contains(es.getKey()))));
            } else {
                Assert.assertTrue(((("Expected segment " + (es.getKey())) + " weren't reached in route segments ") + (reachedSegments.toString())), reachedSegments.contains(es.getKey()));
            }
        } 
    }
}

