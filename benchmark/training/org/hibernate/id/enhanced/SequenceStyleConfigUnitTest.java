/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id.enhanced;


import AvailableSettings.DIALECT;
import Environment.PREFERRED_POOLED_OPTIMIZER;
import Environment.PREFER_POOLED_VALUES_LO;
import SequenceStyleGenerator.DEF_SEQUENCE_NAME;
import SequenceStyleGenerator.FORCE_TBL_PARAM;
import SequenceStyleGenerator.INCREMENT_PARAM;
import SequenceStyleGenerator.OPT_PARAM;
import StandardBasicTypes.LONG;
import StandardOptimizerDescriptor.HILO;
import StandardOptimizerDescriptor.NONE;
import StandardOptimizerDescriptor.POOLED;
import StandardOptimizerDescriptor.POOLED_LOTL;
import java.util.Properties;
import org.hibernate.MappingException;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.Dialect;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that SequenceStyleGenerator configures itself as expected in various scenarios
 *
 * @author Steve Ebersole
 */
public class SequenceStyleConfigUnitTest extends BaseUnitTestCase {
    /**
     * Test all params defaulted with a dialect supporting sequences
     */
    @Test
    public void testDefaultedSequenceBackedConfiguration() {
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySetting(DIALECT, SequenceStyleConfigUnitTest.SequenceDialect.class.getName()).build();
        try {
            Properties props = buildGeneratorPropertiesBase(serviceRegistry);
            SequenceStyleGenerator generator = new SequenceStyleGenerator();
            generator.configure(LONG, props, serviceRegistry);
            generator.registerExportables(new Database(new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry)));
            ExtraAssertions.assertClassAssignability(SequenceStructure.class, generator.getDatabaseStructure().getClass());
            ExtraAssertions.assertClassAssignability(NoopOptimizer.class, generator.getOptimizer().getClass());
            Assert.assertEquals(DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName());
        } finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    /**
     * Test all params defaulted with a dialect which does not support sequences
     */
    @Test
    public void testDefaultedTableBackedConfiguration() {
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySetting(DIALECT, SequenceStyleConfigUnitTest.TableDialect.class.getName()).build();
        try {
            Properties props = buildGeneratorPropertiesBase(serviceRegistry);
            SequenceStyleGenerator generator = new SequenceStyleGenerator();
            generator.configure(LONG, props, serviceRegistry);
            generator.registerExportables(new Database(new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry)));
            ExtraAssertions.assertClassAssignability(TableStructure.class, generator.getDatabaseStructure().getClass());
            ExtraAssertions.assertClassAssignability(NoopOptimizer.class, generator.getOptimizer().getClass());
            Assert.assertEquals(DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName());
        } finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    /**
     * Test default optimizer selection for sequence backed generators
     * based on the configured increment size; both in the case of the
     * dialect supporting pooled sequences (pooled) and not (hilo)
     */
    @Test
    public void testDefaultOptimizerBasedOnIncrementBackedBySequence() {
        // for dialects which do not support pooled sequences, we default to pooled+table
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySetting(DIALECT, SequenceStyleConfigUnitTest.SequenceDialect.class.getName()).build();
        try {
            Properties props = buildGeneratorPropertiesBase(serviceRegistry);
            props.setProperty(INCREMENT_PARAM, "10");
            SequenceStyleGenerator generator = new SequenceStyleGenerator();
            generator.configure(LONG, props, serviceRegistry);
            generator.registerExportables(new Database(new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry)));
            ExtraAssertions.assertClassAssignability(TableStructure.class, generator.getDatabaseStructure().getClass());
            ExtraAssertions.assertClassAssignability(PooledOptimizer.class, generator.getOptimizer().getClass());
            Assert.assertEquals(DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName());
        } finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
        // for dialects which do support pooled sequences, we default to pooled+sequence
        serviceRegistry = new StandardServiceRegistryBuilder().applySetting(DIALECT, SequenceStyleConfigUnitTest.PooledSequenceDialect.class.getName()).build();
        try {
            Properties props = buildGeneratorPropertiesBase(serviceRegistry);
            props.setProperty(INCREMENT_PARAM, "10");
            SequenceStyleGenerator generator = new SequenceStyleGenerator();
            generator.configure(LONG, props, serviceRegistry);
            generator.registerExportables(new Database(new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry)));
            ExtraAssertions.assertClassAssignability(SequenceStructure.class, generator.getDatabaseStructure().getClass());
            ExtraAssertions.assertClassAssignability(PooledOptimizer.class, generator.getOptimizer().getClass());
            Assert.assertEquals(DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName());
        } finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    /**
     * Test default optimizer selection for table backed generators
     * based on the configured increment size.  Here we always prefer
     * pooled.
     */
    @Test
    public void testDefaultOptimizerBasedOnIncrementBackedByTable() {
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySetting(DIALECT, SequenceStyleConfigUnitTest.TableDialect.class.getName()).build();
        try {
            Properties props = buildGeneratorPropertiesBase(serviceRegistry);
            props.setProperty(INCREMENT_PARAM, "10");
            SequenceStyleGenerator generator = new SequenceStyleGenerator();
            generator.configure(LONG, props, serviceRegistry);
            generator.registerExportables(new Database(new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry)));
            ExtraAssertions.assertClassAssignability(TableStructure.class, generator.getDatabaseStructure().getClass());
            ExtraAssertions.assertClassAssignability(PooledOptimizer.class, generator.getOptimizer().getClass());
            Assert.assertEquals(DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName());
        } finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    /**
     * Test forcing of table as backing structure with dialect supporting sequences
     */
    @Test
    public void testForceTableUse() {
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySetting(DIALECT, SequenceStyleConfigUnitTest.SequenceDialect.class.getName()).build();
        try {
            Properties props = buildGeneratorPropertiesBase(serviceRegistry);
            props.setProperty(FORCE_TBL_PARAM, "true");
            SequenceStyleGenerator generator = new SequenceStyleGenerator();
            generator.configure(LONG, props, serviceRegistry);
            generator.registerExportables(new Database(new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry)));
            ExtraAssertions.assertClassAssignability(TableStructure.class, generator.getDatabaseStructure().getClass());
            ExtraAssertions.assertClassAssignability(NoopOptimizer.class, generator.getOptimizer().getClass());
            Assert.assertEquals(DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName());
        } finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    /**
     * Test explicitly specifying both optimizer and increment
     */
    @Test
    public void testExplicitOptimizerWithExplicitIncrementSize() {
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySetting(DIALECT, SequenceStyleConfigUnitTest.SequenceDialect.class.getName()).build();
        // optimizer=none w/ increment > 1 => should honor optimizer
        try {
            Properties props = buildGeneratorPropertiesBase(serviceRegistry);
            props.setProperty(OPT_PARAM, NONE.getExternalName());
            props.setProperty(INCREMENT_PARAM, "20");
            SequenceStyleGenerator generator = new SequenceStyleGenerator();
            generator.configure(LONG, props, serviceRegistry);
            generator.registerExportables(new Database(new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry)));
            ExtraAssertions.assertClassAssignability(SequenceStructure.class, generator.getDatabaseStructure().getClass());
            ExtraAssertions.assertClassAssignability(NoopOptimizer.class, generator.getOptimizer().getClass());
            Assert.assertEquals(1, generator.getOptimizer().getIncrementSize());
            Assert.assertEquals(1, generator.getDatabaseStructure().getIncrementSize());
            // optimizer=hilo w/ increment > 1 => hilo
            props = buildGeneratorPropertiesBase(serviceRegistry);
            props.setProperty(OPT_PARAM, HILO.getExternalName());
            props.setProperty(INCREMENT_PARAM, "20");
            generator = new SequenceStyleGenerator();
            generator.configure(LONG, props, serviceRegistry);
            generator.registerExportables(new Database(new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry)));
            ExtraAssertions.assertClassAssignability(SequenceStructure.class, generator.getDatabaseStructure().getClass());
            ExtraAssertions.assertClassAssignability(HiLoOptimizer.class, generator.getOptimizer().getClass());
            Assert.assertEquals(20, generator.getOptimizer().getIncrementSize());
            Assert.assertEquals(20, generator.getDatabaseStructure().getIncrementSize());
            // optimizer=pooled w/ increment > 1 => hilo
            props = buildGeneratorPropertiesBase(serviceRegistry);
            props.setProperty(OPT_PARAM, POOLED.getExternalName());
            props.setProperty(INCREMENT_PARAM, "20");
            generator = new SequenceStyleGenerator();
            generator.configure(LONG, props, serviceRegistry);
            generator.registerExportables(new Database(new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry)));
            // because the dialect reports to not support pooled seqyences, the expectation is that we will
            // use a table for the backing structure...
            ExtraAssertions.assertClassAssignability(TableStructure.class, generator.getDatabaseStructure().getClass());
            ExtraAssertions.assertClassAssignability(PooledOptimizer.class, generator.getOptimizer().getClass());
            Assert.assertEquals(20, generator.getOptimizer().getIncrementSize());
            Assert.assertEquals(20, generator.getDatabaseStructure().getIncrementSize());
        } finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    @Test
    public void testPreferredPooledOptimizerSetting() {
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySetting(DIALECT, SequenceStyleConfigUnitTest.PooledSequenceDialect.class.getName()).build();
        try {
            Properties props = buildGeneratorPropertiesBase(serviceRegistry);
            props.setProperty(INCREMENT_PARAM, "20");
            SequenceStyleGenerator generator = new SequenceStyleGenerator();
            generator.configure(LONG, props, serviceRegistry);
            generator.registerExportables(new Database(new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry)));
            ExtraAssertions.assertClassAssignability(SequenceStructure.class, generator.getDatabaseStructure().getClass());
            ExtraAssertions.assertClassAssignability(PooledOptimizer.class, generator.getOptimizer().getClass());
            props.setProperty(PREFER_POOLED_VALUES_LO, "true");
            generator = new SequenceStyleGenerator();
            generator.configure(LONG, props, serviceRegistry);
            generator.registerExportables(new Database(new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry)));
            ExtraAssertions.assertClassAssignability(SequenceStructure.class, generator.getDatabaseStructure().getClass());
            ExtraAssertions.assertClassAssignability(PooledLoOptimizer.class, generator.getOptimizer().getClass());
            props.setProperty(PREFERRED_POOLED_OPTIMIZER, POOLED_LOTL.getExternalName());
            generator = new SequenceStyleGenerator();
            generator.configure(LONG, props, serviceRegistry);
            generator.registerExportables(new Database(new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(serviceRegistry)));
            ExtraAssertions.assertClassAssignability(SequenceStructure.class, generator.getDatabaseStructure().getClass());
            ExtraAssertions.assertClassAssignability(PooledLoThreadLocalOptimizer.class, generator.getOptimizer().getClass());
        } finally {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    public static class TableDialect extends Dialect {
        public boolean supportsSequences() {
            return false;
        }
    }

    public static class SequenceDialect extends Dialect {
        public boolean supportsSequences() {
            return true;
        }

        public boolean supportsPooledSequences() {
            return false;
        }

        public String getSequenceNextValString(String sequenceName) throws MappingException {
            return "";
        }
    }

    public static class PooledSequenceDialect extends SequenceStyleConfigUnitTest.SequenceDialect {
        public boolean supportsPooledSequences() {
            return true;
        }
    }
}

