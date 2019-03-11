/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.examples.vehiclerouting.domain.location.segmented;


import org.junit.Assert;
import org.junit.Test;


public class RoadSegmentLocationTest {
    @Test
    public void getDistance() {
        long id = 0;
        RoadSegmentLocation a = new RoadSegmentLocation((id++), 0.0, 0.0);
        RoadSegmentLocation b = new RoadSegmentLocation((id++), 0.0, 4.0);
        RoadSegmentLocation c = new RoadSegmentLocation((id++), 2.0, 0.0);
        RoadSegmentLocation d = new RoadSegmentLocation((id++), 100.0, 2.0);
        HubSegmentLocation x = new HubSegmentLocation((id++), 1.0, 0.0);
        HubSegmentLocation y = new HubSegmentLocation((id++), 1.0, 3.0);
        HubSegmentLocation z = new HubSegmentLocation((id++), 99.0, 3.0);
        a.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(a, b, c));
        a.setHubTravelDistanceMap(createHubTravelDistanceMap(a, x, y));
        b.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(b, a));
        b.setHubTravelDistanceMap(createHubTravelDistanceMap(b, x, y));
        c.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(c, a));
        c.setHubTravelDistanceMap(createHubTravelDistanceMap(c, x, y));
        d.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(d));
        d.setHubTravelDistanceMap(createHubTravelDistanceMap(d, z));
        x.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(x, a, b, c));
        x.setHubTravelDistanceMap(createHubTravelDistanceMap(x, y, z));
        y.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(y, a, b, c));
        y.setHubTravelDistanceMap(createHubTravelDistanceMap(y, x, z));
        z.setNearbyTravelDistanceMap(createNearbyTravelDistanceMap(z, d));
        z.setHubTravelDistanceMap(createHubTravelDistanceMap(z, x, y));
        Assert.assertEquals(sumOfArcs(a, b), a.getDistanceTo(b));
        Assert.assertEquals(sumOfArcs(a, c), a.getDistanceTo(c));
        Assert.assertEquals(sumOfArcs(a, x, z, d), a.getDistanceTo(d));
        Assert.assertEquals(sumOfArcs(b, a), b.getDistanceTo(a));
        Assert.assertEquals(sumOfArcs(b, y, c), b.getDistanceTo(c));
        Assert.assertEquals(sumOfArcs(b, y, z, d), b.getDistanceTo(d));
        Assert.assertEquals(sumOfArcs(c, a), c.getDistanceTo(a));
        Assert.assertEquals(sumOfArcs(c, y, b), c.getDistanceTo(b));
        Assert.assertEquals(sumOfArcs(c, x, z, d), c.getDistanceTo(d));
        Assert.assertEquals(sumOfArcs(d, z, x, a), d.getDistanceTo(a));
        Assert.assertEquals(sumOfArcs(d, z, y, b), d.getDistanceTo(b));
        Assert.assertEquals(sumOfArcs(d, z, x, c), d.getDistanceTo(c));
    }
}

