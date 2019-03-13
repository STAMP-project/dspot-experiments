/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.hibernate;


import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.hibernate.stat.Statistics;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test that Hibernate statistics is working on native Hibernate and second level cache
 *
 * @author Madhumita Sadhukhan
 */
@RunWith(Arquillian.class)
public class Hibernate2LCacheStatsTestCase {
    private static final String FACTORY_CLASS = "<property name=\"hibernate.cache.region.factory_class\">org.infinispan.hibernate.cache.v51.InfinispanRegionFactory</property>";

    private static final String MODULE_DEPENDENCIES = "Dependencies: org.hibernate.envers export,org.hibernate\n";

    private static final String ARCHIVE_NAME = "hibernateSecondLevelStats_test";

    public static final String hibernate_cfg = (((("<?xml version='1.0' encoding='utf-8'?>" + (((((("<!DOCTYPE hibernate-configuration PUBLIC " + "\"//Hibernate/Hibernate Configuration DTD 3.0//EN\" ") + "\"http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd\">") + "<hibernate-configuration><session-factory>") + "<property name=\"show_sql\">false</property>") + "<property name=\"hibernate.cache.use_second_level_cache\">true</property>") + "<property name=\"hibernate.show_sql\">false</property>")) + (Hibernate2LCacheStatsTestCase.FACTORY_CLASS)) + "<property name=\"hibernate.cache.infinispan.shared\">false</property>") + "<mapping resource=\"testmapping.hbm.xml\"/>") + "</session-factory></hibernate-configuration>";

    public static final String testmapping = "<?xml version=\"1.0\"?>" + ((((((((((((((((((((((("<!DOCTYPE hibernate-mapping PUBLIC " + "\"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" ") + "\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">") + "<hibernate-mapping package=\"org.jboss.as.test.integration.hibernate\">") + "<class name=\"org.jboss.as.test.integration.hibernate.Planet\" table=\"PLANET\">") + "<cache usage=\"transactional\"/>") + "<id name=\"planetId\" column=\"planetId\">") + "<generator class=\"native\"/>") + "</id>") + "<property name=\"planetName\" column=\"planet_name\"/>") + "<property name=\"galaxy\" column=\"galaxy_name\"/>") + "<property name=\"star\" column=\"star_name\"/>") + "<set name=\"satellites\">") + "<cache usage=\"read-only\"/>") + "<key column=\"id\"/>") + "<one-to-many class=\"org.jboss.as.test.integration.hibernate.Satellite\"/>") + "</set>") + "</class>") + "<class name=\"org.jboss.as.test.integration.hibernate.Satellite\" table=\"SATELLITE\">") + "<id name=\"id\">") + "<generator class=\"native\"/>") + "</id>") + "<property name=\"name\" column=\"satellite_name\"/>") + "</class></hibernate-mapping>");

    @ArquillianResource
    private static InitialContext iniCtx;

    @Test
    public void testHibernateStatistics() throws Exception {
        SFSBHibernate2LcacheStats sfsb = Hibernate2LCacheStatsTestCase.lookup("SFSBHibernate2LcacheStats", SFSBHibernate2LcacheStats.class);
        // setup Configuration and SessionFactory
        sfsb.setupConfig();
        try {
            Set<Satellite> satellites1 = new HashSet<Satellite>();
            Satellite sat = new Satellite();
            sat.setId(new Integer(1));
            sat.setName("MOON");
            satellites1.add(sat);
            Planet s1 = sfsb.prepareData("EARTH", "MILKY WAY", "SUN", satellites1, new Integer(1));
            Planet s2 = sfsb.getPlanet(s1.getPlanetId());
            DataSource ds = rawLookup("java:jboss/datasources/ExampleDS", DataSource.class);
            Connection conn = ds.getConnection();
            int updated = conn.prepareStatement("update PLANET set galaxy_name='ANDROMEDA' where planetId=1").executeUpdate();
            Assert.assertTrue(("was able to update added Planet.  update count=" + updated), (updated > 0));
            conn.close();
            // read updated (dirty) data from second level cache
            s2 = sfsb.getPlanet(s2.getPlanetId());
            Assert.assertTrue("was able to read updated Planet entity", (s2 != null));
            Assert.assertEquals(((("Galaxy for Planet " + (s2.getPlanetName())) + " was read from second level cache = ") + (s2.getGalaxy())), "MILKY WAY", s2.getGalaxy());
            Assert.assertEquals(s2.getSatellites().size(), 1);
            Statistics stats = sfsb.getStatistics();
            Assert.assertEquals(stats.getCollectionLoadCount(), 1);
            Assert.assertEquals(stats.getEntityLoadCount(), 2);
            Assert.assertEquals(stats.getSecondLevelCacheHitCount(), 1);
            // Collection in secondlevel cache before eviction
            Assert.assertTrue(sfsb.isSatellitesPresentInCache(1));
            Statistics statsAfterEviction = sfsb.getStatisticsAfterEviction();
            // Collection in secondlevel cache after eviction
            Assert.assertFalse(sfsb.isSatellitesPresentInCache(1));
        } finally {
            sfsb.cleanup();
        }
    }
}

