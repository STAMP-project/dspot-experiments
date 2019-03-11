package org.opentripplanner.graph_builder.module.map;


import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.opentripplanner.common.geometry.PackedCoordinateSequence;
import org.opentripplanner.common.geometry.SphericalDistanceLibrary;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.StateEditor;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.core.TraverseModeSet;
import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.vertextype.StreetVertex;
import org.opentripplanner.util.I18NString;
import org.opentripplanner.util.NonLocalizedString;


public class TestStreetMatcher {
    static GeometryFactory gf = new GeometryFactory();

    private Graph graph;

    @Test
    public void testStreetMatcher() {
        LineString geometry = geometry((-122.385689), 47.669484, (-122.387384), 47.66947);
        StreetMatcher matcher = new StreetMatcher(graph);
        List<Edge> match = matcher.match(geometry);
        Assert.assertNotNull(match);
        Assert.assertEquals(1, match.size());
        Assert.assertEquals("56th_24th", getToVertex().getLabel());
        geometry = geometry((-122.385689), 47.669484, (-122.387384), 47.66947, (-122.387588), 47.669325);
        match = matcher.match(geometry);
        Assert.assertNotNull(match);
        Assert.assertEquals(2, match.size());
        geometry = geometry((-122.385689), 47.669484, (-122.387384), 47.66947, (-122.387588), 47.669325, (-122.387255), 47.668675);
        match = matcher.match(geometry);
        Assert.assertNotNull(match);
        Assert.assertEquals(3, match.size());
        geometry = geometry((-122.384756), 47.66926, (-122.384777), 47.667454, (-122.383554), 47.666789, (-122.3825), 47.666);
        match = matcher.match(geometry);
        Assert.assertNotNull(match);
        System.out.println(match);
        Assert.assertEquals(4, match.size());
        Assert.assertEquals("ballard_20th", getToVertex().getLabel());
    }

    private static class SimpleVertex extends StreetVertex {
        private static final long serialVersionUID = 1L;

        public SimpleVertex(Graph g, String label, double lat, double lon) {
            super(g, label, lon, lat, new NonLocalizedString(label));
        }
    }

    /* TODO explain why this exists and is "simple" */
    private static class SimpleEdge extends StreetEdge {
        private static final long serialVersionUID = 1L;

        public SimpleEdge(StreetVertex v1, StreetVertex v2) {
            super(v1, v2, null, ((NonLocalizedString) (null)), 0, null, false);
        }

        @Override
        public State traverse(State s0) {
            double d = getDistance();
            TraverseMode mode = s0.getNonTransitMode();
            int t = ((int) (d / (s0.getOptions().getSpeed(mode))));
            StateEditor s1 = s0.edit(this);
            s1.incrementTimeInSeconds(t);
            s1.incrementWeight(d);
            return s1.makeState();
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public I18NString getRawName() {
            return null;
        }

        @Override
        public String getName(Locale locale) {
            return null;
        }

        @Override
        public LineString getGeometry() {
            return TestStreetMatcher.gf.createLineString(new Coordinate[]{ fromv.getCoordinate(), tov.getCoordinate() });
        }

        @Override
        public double getDistance() {
            return SphericalDistanceLibrary.distance(getFromVertex().getCoordinate(), getToVertex().getCoordinate());
        }

        @Override
        public PackedCoordinateSequence getElevationProfile() {
            return null;
        }

        @Override
        public boolean canTraverse(TraverseModeSet modes) {
            return true;
        }

        @Override
        public StreetTraversalPermission getPermission() {
            return StreetTraversalPermission.ALL;
        }

        @Override
        public boolean isNoThruTraffic() {
            return false;
        }

        public String toString() {
            return ((("SimpleEdge(" + (fromv)) + ", ") + (tov)) + ")";
        }

        @Override
        public int getStreetClass() {
            return StreetEdge.CLASS_STREET;
        }

        @Override
        public boolean isWheelchairAccessible() {
            return true;
        }

        public boolean isElevationFlattened() {
            return false;
        }

        @Override
        public float getCarSpeed() {
            return 11.2F;
        }

        @Override
        public int getInAngle() {
            return 0;
        }

        @Override
        public int getOutAngle() {
            return 0;
        }

        @Override
        public void setCarSpeed(float carSpeed) {
        }
    }
}

