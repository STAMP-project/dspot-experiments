package org.hibernate.spatial.dialect.hana;


import java.sql.SQLException;
import org.hibernate.spatial.HSMessageLogger;
import org.hibernate.spatial.integration.TestSpatialFunctions;
import org.hibernate.spatial.testing.SpatialFunctionalTestCase;
import org.hibernate.spatial.testing.dialects.hana.HANAExpectationsFactory;
import org.hibernate.testing.RequiresDialect;
import org.jboss.logging.Logger;
import org.junit.Test;


@RequiresDialect(value = HANASpatialDialect.class, comment = "This test tests the HANA spatial functions not covered by Hibernate Spatial", jiraKey = "HHH-12426")
public class TestHANASpatialFunctions extends TestSpatialFunctions {
    private static final HSMessageLogger LOG = Logger.getMessageLogger(HSMessageLogger.class, TestHANASpatialFunctions.class.getName());

    protected HANAExpectationsFactory hanaExpectationsFactory;

    @Test
    public void test_alphashape_on_jts() throws SQLException {
        alphashape(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_alphashape_on_geolatte() throws SQLException {
        alphashape(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_area_on_jts() throws SQLException {
        area(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_area_on_geolatte() throws SQLException {
        area(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_asewkb_on_jts() throws SQLException {
        asewkb(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_asewkb_on_geolatte() throws SQLException {
        asewkb(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_asewkt_on_jts() throws SQLException {
        asewkt(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_asewkt_on_geolatte() throws SQLException {
        asewkt(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_asgeojson_on_jts() throws SQLException {
        asgeojson(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_asgeojson_on_geolatte() throws SQLException {
        asgeojson(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_assvg_on_jts() throws SQLException {
        assvg(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_assvg_on_geolatte() throws SQLException {
        assvg(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_assvgaggr_on_jts() throws SQLException {
        assvgaggr(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_assvgaggr_on_geolatte() throws SQLException {
        assvgaggr(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_aswkb_on_jts() throws SQLException {
        aswkb(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_aswkb_on_geolatte() throws SQLException {
        aswkb(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_aswkt_on_jts() throws SQLException {
        aswkt(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_aswkt_on_geolatte() throws SQLException {
        aswkt(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_convexhullaggr_on_jts() throws SQLException {
        convexhullaggr(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_convexhullaggr_on_geolatte() throws SQLException {
        convexhullaggr(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_centroid_on_jts() throws SQLException {
        centroid(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_centroid_on_geolatte() throws SQLException {
        centroid(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_coorddim_on_jts() throws SQLException {
        coorddim(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_coorddim_on_geolatte() throws SQLException {
        coorddim(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_coveredby_on_jts() throws SQLException {
        coveredby(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_coveredby_on_geolatte() throws SQLException {
        coveredby(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_covers_on_jts() throws SQLException {
        covers(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_covers_on_geolatte() throws SQLException {
        covers(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_endpoint_on_jts() throws SQLException {
        endpoint(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_endpoint_on_geolatte() throws SQLException {
        endpoint(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_envelopeaggr_on_jts() throws SQLException {
        envelopeaggr(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_envelopeaggr_on_geolatte() throws SQLException {
        envelopeaggr(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_exteriorring_on_jts() throws SQLException {
        exteriorring(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_exteriorring_on_geolatte() throws SQLException {
        exteriorring(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_geomfromewkb_on_jts() throws SQLException {
        geomfromewkb(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_geomfromewkb_on_geolatte() throws SQLException {
        geomfromewkb(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_geomfromewkt_on_jts() throws SQLException {
        geomfromewkt(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_geomfromewkt_on_geolatte() throws SQLException {
        geomfromewkt(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_geomfromtext_on_jts() throws SQLException {
        geomfromtext(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_geomfromtext_on_geolatte() throws SQLException {
        geomfromtext(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_geomfromwkb_on_jts() throws SQLException {
        geomfromwkb(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_geomfromwkb_on_geolatte() throws SQLException {
        geomfromwkb(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_geomfromwkt_on_jts() throws SQLException {
        geomfromwkt(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_geomfromwkt_on_geolatte() throws SQLException {
        geomfromwkt(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_geometryn_on_jts() throws SQLException {
        geometryn(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_geometryn_on_geolatte() throws SQLException {
        geometryn(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_interiorringn_on_jts() throws SQLException {
        interiorringn(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_interiorringn_on_geolatte() throws SQLException {
        interiorringn(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_intersectionaggr_on_jts() throws SQLException {
        intersectionaggr(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_intersectionaggr_on_geolatte() throws SQLException {
        intersectionaggr(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_intersectsrect_on_jts() throws SQLException {
        intersectsrect(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_intersectsrect_on_geolatte() throws SQLException {
        intersectsrect(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_is3d_on_jts() throws SQLException {
        is3d(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_is3d_on_geolatte() throws SQLException {
        is3d(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_isclosed_on_jts() throws SQLException {
        isclosed(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_isclosed_on_geolatte() throws SQLException {
        isclosed(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_ismeasured_on_jts() throws SQLException {
        ismeasured(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_ismeasured_on_geolatte() throws SQLException {
        ismeasured(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_isring_on_jts() throws SQLException {
        isring(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_isring_on_geolatte() throws SQLException {
        isring(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_isvalid_on_jts() throws SQLException {
        isvalid(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_isvalid_on_geolatte() throws SQLException {
        isvalid(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_length_on_jts() throws SQLException {
        length(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_length_on_geolatte() throws SQLException {
        length(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_m_on_jts() throws SQLException {
        m(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_m_on_geolatte() throws SQLException {
        m(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_mmax_on_jts() throws SQLException {
        mmax(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_mmax_on_geolatte() throws SQLException {
        mmax(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_mmin_on_jts() throws SQLException {
        mmin(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_mmin_on_geolatte() throws SQLException {
        mmin(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_numgeometries_on_jts() throws SQLException {
        numgeometries(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_numgeometries_on_geolatte() throws SQLException {
        numgeometries(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_numinteriorring_on_jts() throws SQLException {
        numinteriorring(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_numnuminteriorring_on_geolatte() throws SQLException {
        numinteriorring(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_numinteriorrings_on_jts() throws SQLException {
        numinteriorrings(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_numnuminteriorrings_on_geolatte() throws SQLException {
        numinteriorrings(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_numpoints_on_jts() throws SQLException {
        numpoints(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_numpoints_on_geolatte() throws SQLException {
        numpoints(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_orderingequals_on_jts() throws SQLException {
        orderingequals(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_orderingequals_on_geolatte() throws SQLException {
        orderingequals(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_perimeter_on_jts() throws SQLException {
        perimeter(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_perimeter_on_geolatte() throws SQLException {
        perimeter(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_pointonsurface_on_jts() throws SQLException {
        pointonsurface(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_pointonsurface_on_geolatte() throws SQLException {
        pointonsurface(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_pointn_on_jts() throws SQLException {
        pointn(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_pointn_on_geolatte() throws SQLException {
        pointn(SpatialFunctionalTestCase.GEOLATTE);
    }

    // ST_GEOMETRY columns are not supported
    @Test(expected = SQLException.class)
    public void test_snaptogrid_on_jts() throws SQLException {
        snaptogrid(SpatialFunctionalTestCase.JTS);
    }

    // ST_GEOMETRY columns are not supported
    @Test(expected = SQLException.class)
    public void test_snaptogrid_on_geolatte() throws SQLException {
        snaptogrid(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_startpoint_on_jts() throws SQLException {
        startpoint(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_startpoint_on_geolatte() throws SQLException {
        startpoint(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_unionaggr_on_jts() throws SQLException {
        unionaggr(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_unionaggr_on_geolatte() throws SQLException {
        unionaggr(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_x_on_jts() throws SQLException {
        x(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_x_on_geolatte() throws SQLException {
        x(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_xmax_on_jts() throws SQLException {
        xmax(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_xmax_on_geolatte() throws SQLException {
        xmax(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_xmin_on_jts() throws SQLException {
        xmin(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_xmin_on_geolatte() throws SQLException {
        xmin(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_y_on_jts() throws SQLException {
        y(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_y_on_geolatte() throws SQLException {
        y(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_ymax_on_jts() throws SQLException {
        ymax(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_ymax_on_geolatte() throws SQLException {
        ymax(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_ymin_on_jts() throws SQLException {
        ymin(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_ymin_on_geolatte() throws SQLException {
        ymin(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_z_on_jts() throws SQLException {
        z(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_z_on_geolatte() throws SQLException {
        z(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_zmax_on_jts() throws SQLException {
        zmax(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_zmax_on_geolatte() throws SQLException {
        zmax(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_zmin_on_jts() throws SQLException {
        zmin(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_zmin_on_geolatte() throws SQLException {
        zmin(SpatialFunctionalTestCase.GEOLATTE);
    }

    @Test
    public void test_nestedfunction_on_jts() throws SQLException {
        nestedfunction(SpatialFunctionalTestCase.JTS);
    }

    @Test
    public void test_nestedfunction_on_geolatte() throws SQLException {
        nestedfunction(SpatialFunctionalTestCase.GEOLATTE);
    }
}

