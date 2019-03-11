package org.hibernate.spatial.integration;


import java.util.HashMap;
import java.util.Map;
import org.geolatte.geom.codec.WktDecodeException;
import org.hibernate.spatial.testing.SpatialFunctionalTestCase;
import org.junit.Test;


// import org.geolatte.geom.C3DM;
// import org.geolatte.geom.Geometry;
// import org.geolatte.geom.GeometryEquality;
// import org.geolatte.geom.GeometryPointEquality;
/**
 * Created by Karel Maesen, Geovise BVBA on 15/02/2018.
 */
public abstract class AbstractTestStoreRetrieve<G, E extends GeomEntityLike<G>> extends SpatialFunctionalTestCase {
    @Test
    public void testAfterStoreRetrievingEqualObject() throws WktDecodeException {
        Map<Integer, E> stored = new HashMap<>();
        // check whether we retrieve exactly what we store
        storeTestObjects(stored);
        retrieveAndCompare(stored);
    }

    @Test
    public void testStoringNullGeometries() {
        storeNullGeometry();
        retrieveNullGeometry();
    }
}

