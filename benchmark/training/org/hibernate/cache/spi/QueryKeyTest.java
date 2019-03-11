/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.cache.spi;


import AliasToEntityMapResultTransformer.INSTANCE;
import java.io.Serializable;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.CacheableResultTransformer;
import org.junit.Test;


/**
 * Tests relating to {@link QueryKey} instances.
 *
 * @author Steve Ebersole
 */
public class QueryKeyTest extends BaseUnitTestCase {
    private static final String QUERY_STRING = "the query string";

    public static class AClass implements Serializable {
        private String propAccessedByField;

        private String propAccessedByMethod;

        private int propValue;

        public AClass() {
        }

        public AClass(String propAccessedByField) {
            this.propAccessedByField = propAccessedByField;
        }

        public String getPropAccessedByMethod() {
            return propAccessedByMethod;
        }

        public void setPropAccessedByMethod(String propAccessedByMethod) {
            this.propAccessedByMethod = propAccessedByMethod;
        }
    }

    @Test
    public void testSerializedEqualityResultTransformer() throws Exception {
        // settings are lazily initialized when calling transformTuple(),
        // so they have not been initialized for the following test
        // (it *should* be initialized before creating a QueryKey)
        doResultTransformerTest(new AliasToBeanResultTransformer(QueryKeyTest.AClass.class), false);
        // initialize settings for the next test
        AliasToBeanResultTransformer transformer = new AliasToBeanResultTransformer(QueryKeyTest.AClass.class);
        transformer.transformTuple(new Object[]{ "abc", "def" }, new String[]{ "propAccessedByField", "propAccessedByMethod" });
        doResultTransformerTest(transformer, false);
        doResultTransformerTest(INSTANCE, true);
        doResultTransformerTest(DistinctResultTransformer.INSTANCE, true);
        doResultTransformerTest(DistinctRootEntityResultTransformer.INSTANCE, true);
        doResultTransformerTest(PassThroughResultTransformer.INSTANCE, true);
        doResultTransformerTest(RootEntityResultTransformer.INSTANCE, true);
        doResultTransformerTest(ToListResultTransformer.INSTANCE, true);
    }

    @Test
    public void testSerializedEquality() throws Exception {
        doTest(buildBasicKey(null));
        doTest(buildBasicKey(CacheableResultTransformer.create(null, null, new boolean[]{ true })));
        doTest(buildBasicKey(CacheableResultTransformer.create(null, new String[]{ null }, new boolean[]{ true })));
        doTest(buildBasicKey(CacheableResultTransformer.create(null, new String[]{ "a" }, new boolean[]{ true })));
        doTest(buildBasicKey(CacheableResultTransformer.create(null, null, new boolean[]{ false, true })));
        doTest(buildBasicKey(CacheableResultTransformer.create(null, new String[]{ "a" }, new boolean[]{ true, false })));
        doTest(buildBasicKey(CacheableResultTransformer.create(null, new String[]{ "a", null }, new boolean[]{ true, true })));
    }

    @Test
    public void testSerializedEqualityWithTupleSubsetResultTransfprmer() throws Exception {
        doTestWithTupleSubsetResultTransformer(new AliasToBeanResultTransformer(QueryKeyTest.AClass.class), new String[]{ "propAccessedByField", "propAccessedByMethod" });
        doTestWithTupleSubsetResultTransformer(INSTANCE, new String[]{ "a", "b" });
        doTestWithTupleSubsetResultTransformer(DistinctRootEntityResultTransformer.INSTANCE, new String[]{ "a", "b" });
        doTestWithTupleSubsetResultTransformer(PassThroughResultTransformer.INSTANCE, new String[]{ "a", "b" });
        doTestWithTupleSubsetResultTransformer(RootEntityResultTransformer.INSTANCE, new String[]{ "a", "b" });
        // The following are not TupleSubsetResultTransformers:
        // DistinctResultTransformer.INSTANCE
        // ToListResultTransformer.INSTANCE
    }
}

