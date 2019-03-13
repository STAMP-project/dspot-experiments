/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id;


import DialectChecks.SupportsSequences;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.SessionImpl;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * I went back to 3.3 source and grabbed the code/logic as it existed back then and crafted this
 * unit test so that we can make sure the value keep being generated in the expected manner
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({ "deprecation" })
@RequiresDialectFeature(SupportsSequences.class)
public class SequenceHiLoGeneratorNoIncrementTest extends BaseUnitTestCase {
    private static final String TEST_SEQUENCE = "test_sequence";

    private StandardServiceRegistry serviceRegistry;

    private SessionFactoryImplementor sessionFactory;

    private SequenceStyleGenerator generator;

    private SessionImplementor sessionImpl;

    private SequenceValueExtractor sequenceValueExtractor;

    @Test
    public void testHiLoAlgorithm() {
        sessionImpl = ((SessionImpl) (sessionFactory.openSession()));
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // historically the hilo generators skipped the initial block of values;
        // so the first generated id value is maxlo + 1, here be 4
        Assert.assertEquals(1L, generateValue());
        // which should also perform the first read on the sequence which should set it to its "start with" value (1)
        Assert.assertEquals(1L, extractSequenceValue());
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Assert.assertEquals(2L, generateValue());
        Assert.assertEquals(2L, extractSequenceValue());
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Assert.assertEquals(3L, generateValue());
        Assert.assertEquals(3L, extractSequenceValue());
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Assert.assertEquals(4L, generateValue());
        Assert.assertEquals(4L, extractSequenceValue());
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Assert.assertEquals(5L, generateValue());
        Assert.assertEquals(5L, extractSequenceValue());
        close();
    }
}

