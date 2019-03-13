/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.orm.jpa.persistenceunit;


import PersistenceUnitTransactionType.JTA;
import PersistenceUnitTransactionType.RESOURCE_LOCAL;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup;
import org.springframework.tests.mock.jndi.SimpleNamingContextBuilder;


/**
 * Unit and integration tests for the JPA XML resource parsing support.
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @author Nicholas Williams
 */
public class PersistenceXmlParsingTests {
    @Test
    public void testMetaInfCase() throws Exception {
        PersistenceUnitReader reader = new PersistenceUnitReader(new PathMatchingResourcePatternResolver(), new JndiDataSourceLookup());
        String resource = "/org/springframework/orm/jpa/META-INF/persistence.xml";
        PersistenceUnitInfo[] info = reader.readPersistenceUnitInfos(resource);
        Assert.assertNotNull(info);
        Assert.assertEquals(1, info.length);
        Assert.assertEquals("OrderManagement", info[0].getPersistenceUnitName());
        Assert.assertEquals(2, info[0].getJarFileUrls().size());
        Assert.assertEquals(new ClassPathResource("order.jar").getURL(), info[0].getJarFileUrls().get(0));
        Assert.assertEquals(new ClassPathResource("order-supplemental.jar").getURL(), info[0].getJarFileUrls().get(1));
        Assert.assertFalse("Exclude unlisted should default false in 1.0.", info[0].excludeUnlistedClasses());
    }

    @Test
    public void testExample1() throws Exception {
        PersistenceUnitReader reader = new PersistenceUnitReader(new PathMatchingResourcePatternResolver(), new JndiDataSourceLookup());
        String resource = "/org/springframework/orm/jpa/persistence-example1.xml";
        PersistenceUnitInfo[] info = reader.readPersistenceUnitInfos(resource);
        Assert.assertNotNull(info);
        Assert.assertEquals(1, info.length);
        Assert.assertEquals("OrderManagement", info[0].getPersistenceUnitName());
        Assert.assertFalse("Exclude unlisted should default false in 1.0.", info[0].excludeUnlistedClasses());
    }

    @Test
    public void testExample2() throws Exception {
        PersistenceUnitReader reader = new PersistenceUnitReader(new PathMatchingResourcePatternResolver(), new JndiDataSourceLookup());
        String resource = "/org/springframework/orm/jpa/persistence-example2.xml";
        PersistenceUnitInfo[] info = reader.readPersistenceUnitInfos(resource);
        Assert.assertNotNull(info);
        Assert.assertEquals(1, info.length);
        Assert.assertEquals("OrderManagement2", info[0].getPersistenceUnitName());
        Assert.assertEquals(1, info[0].getMappingFileNames().size());
        Assert.assertEquals("mappings.xml", info[0].getMappingFileNames().get(0));
        Assert.assertEquals(0, info[0].getProperties().keySet().size());
        Assert.assertFalse("Exclude unlisted should default false in 1.0.", info[0].excludeUnlistedClasses());
    }

    @Test
    public void testExample3() throws Exception {
        PersistenceUnitReader reader = new PersistenceUnitReader(new PathMatchingResourcePatternResolver(), new JndiDataSourceLookup());
        String resource = "/org/springframework/orm/jpa/persistence-example3.xml";
        PersistenceUnitInfo[] info = reader.readPersistenceUnitInfos(resource);
        Assert.assertNotNull(info);
        Assert.assertEquals(1, info.length);
        Assert.assertEquals("OrderManagement3", info[0].getPersistenceUnitName());
        Assert.assertEquals(2, info[0].getJarFileUrls().size());
        Assert.assertEquals(new ClassPathResource("order.jar").getURL(), info[0].getJarFileUrls().get(0));
        Assert.assertEquals(new ClassPathResource("order-supplemental.jar").getURL(), info[0].getJarFileUrls().get(1));
        Assert.assertEquals(0, info[0].getProperties().keySet().size());
        Assert.assertNull(info[0].getJtaDataSource());
        Assert.assertNull(info[0].getNonJtaDataSource());
        Assert.assertFalse("Exclude unlisted should default false in 1.0.", info[0].excludeUnlistedClasses());
    }

    @Test
    public void testExample4() throws Exception {
        SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        DataSource ds = new DriverManagerDataSource();
        builder.bind("java:comp/env/jdbc/MyDB", ds);
        PersistenceUnitReader reader = new PersistenceUnitReader(new PathMatchingResourcePatternResolver(), new JndiDataSourceLookup());
        String resource = "/org/springframework/orm/jpa/persistence-example4.xml";
        PersistenceUnitInfo[] info = reader.readPersistenceUnitInfos(resource);
        Assert.assertNotNull(info);
        Assert.assertEquals(1, info.length);
        Assert.assertEquals("OrderManagement4", info[0].getPersistenceUnitName());
        Assert.assertEquals(1, info[0].getMappingFileNames().size());
        Assert.assertEquals("order-mappings.xml", info[0].getMappingFileNames().get(0));
        Assert.assertEquals(3, info[0].getManagedClassNames().size());
        Assert.assertEquals("com.acme.Order", info[0].getManagedClassNames().get(0));
        Assert.assertEquals("com.acme.Customer", info[0].getManagedClassNames().get(1));
        Assert.assertEquals("com.acme.Item", info[0].getManagedClassNames().get(2));
        Assert.assertTrue("Exclude unlisted should be true when no value.", info[0].excludeUnlistedClasses());
        Assert.assertSame(RESOURCE_LOCAL, info[0].getTransactionType());
        Assert.assertEquals(0, info[0].getProperties().keySet().size());
        builder.clear();
    }

    @Test
    public void testExample5() throws Exception {
        PersistenceUnitReader reader = new PersistenceUnitReader(new PathMatchingResourcePatternResolver(), new JndiDataSourceLookup());
        String resource = "/org/springframework/orm/jpa/persistence-example5.xml";
        PersistenceUnitInfo[] info = reader.readPersistenceUnitInfos(resource);
        Assert.assertNotNull(info);
        Assert.assertEquals(1, info.length);
        Assert.assertEquals("OrderManagement5", info[0].getPersistenceUnitName());
        Assert.assertEquals(2, info[0].getMappingFileNames().size());
        Assert.assertEquals("order1.xml", info[0].getMappingFileNames().get(0));
        Assert.assertEquals("order2.xml", info[0].getMappingFileNames().get(1));
        Assert.assertEquals(2, info[0].getJarFileUrls().size());
        Assert.assertEquals(new ClassPathResource("order.jar").getURL(), info[0].getJarFileUrls().get(0));
        Assert.assertEquals(new ClassPathResource("order-supplemental.jar").getURL(), info[0].getJarFileUrls().get(1));
        Assert.assertEquals("com.acme.AcmePersistence", info[0].getPersistenceProviderClassName());
        Assert.assertEquals(0, info[0].getProperties().keySet().size());
        Assert.assertFalse("Exclude unlisted should default false in 1.0.", info[0].excludeUnlistedClasses());
    }

    @Test
    public void testExampleComplex() throws Exception {
        DataSource ds = new DriverManagerDataSource();
        String resource = "/org/springframework/orm/jpa/persistence-complex.xml";
        MapDataSourceLookup dataSourceLookup = new MapDataSourceLookup();
        Map<String, DataSource> dataSources = new HashMap<>();
        dataSources.put("jdbc/MyPartDB", ds);
        dataSources.put("jdbc/MyDB", ds);
        dataSourceLookup.setDataSources(dataSources);
        PersistenceUnitReader reader = new PersistenceUnitReader(new PathMatchingResourcePatternResolver(), dataSourceLookup);
        PersistenceUnitInfo[] info = reader.readPersistenceUnitInfos(resource);
        Assert.assertEquals(2, info.length);
        PersistenceUnitInfo pu1 = info[0];
        Assert.assertEquals("pu1", pu1.getPersistenceUnitName());
        Assert.assertEquals("com.acme.AcmePersistence", pu1.getPersistenceProviderClassName());
        Assert.assertEquals(1, pu1.getMappingFileNames().size());
        Assert.assertEquals("ormap2.xml", pu1.getMappingFileNames().get(0));
        Assert.assertEquals(1, pu1.getJarFileUrls().size());
        Assert.assertEquals(new ClassPathResource("order.jar").getURL(), pu1.getJarFileUrls().get(0));
        Assert.assertFalse(pu1.excludeUnlistedClasses());
        Assert.assertSame(RESOURCE_LOCAL, pu1.getTransactionType());
        Properties props = pu1.getProperties();
        Assert.assertEquals(2, props.keySet().size());
        Assert.assertEquals("on", props.getProperty("com.acme.persistence.sql-logging"));
        Assert.assertEquals("bar", props.getProperty("foo"));
        Assert.assertNull(pu1.getNonJtaDataSource());
        Assert.assertSame(ds, pu1.getJtaDataSource());
        Assert.assertFalse("Exclude unlisted should default false in 1.0.", pu1.excludeUnlistedClasses());
        PersistenceUnitInfo pu2 = info[1];
        Assert.assertSame(JTA, pu2.getTransactionType());
        Assert.assertEquals("com.acme.AcmePersistence", pu2.getPersistenceProviderClassName());
        Assert.assertEquals(1, pu2.getMappingFileNames().size());
        Assert.assertEquals("order2.xml", pu2.getMappingFileNames().get(0));
        // the following assertions fail only during coverage runs
        // assertEquals(1, pu2.getJarFileUrls().size());
        // assertEquals(new ClassPathResource("order-supplemental.jar").getURL(), pu2.getJarFileUrls().get(0));
        Assert.assertTrue(pu2.excludeUnlistedClasses());
        Assert.assertNull(pu2.getJtaDataSource());
        Assert.assertEquals(ds, pu2.getNonJtaDataSource());
        Assert.assertTrue("Exclude unlisted should be true when no value.", pu2.excludeUnlistedClasses());
    }

    @Test
    public void testExample6() throws Exception {
        PersistenceUnitReader reader = new PersistenceUnitReader(new PathMatchingResourcePatternResolver(), new JndiDataSourceLookup());
        String resource = "/org/springframework/orm/jpa/persistence-example6.xml";
        PersistenceUnitInfo[] info = reader.readPersistenceUnitInfos(resource);
        Assert.assertEquals(1, info.length);
        Assert.assertEquals("pu", info[0].getPersistenceUnitName());
        Assert.assertEquals(0, info[0].getProperties().keySet().size());
        Assert.assertFalse("Exclude unlisted should default false in 1.0.", info[0].excludeUnlistedClasses());
    }

    @Test
    public void testPersistenceUnitRootUrl() throws Exception {
        URL url = PersistenceUnitReader.determinePersistenceUnitRootUrl(new ClassPathResource("/org/springframework/orm/jpa/persistence-no-schema.xml"));
        Assert.assertNull(url);
        url = PersistenceUnitReader.determinePersistenceUnitRootUrl(new ClassPathResource("/org/springframework/orm/jpa/META-INF/persistence.xml"));
        Assert.assertTrue("the containing folder should have been returned", url.toString().endsWith("/org/springframework/orm/jpa"));
    }

    @Test
    public void testPersistenceUnitRootUrlWithJar() throws Exception {
        ClassPathResource archive = new ClassPathResource("/org/springframework/orm/jpa/jpa-archive.jar");
        String newRoot = ("jar:" + (archive.getURL().toExternalForm())) + "!/META-INF/persist.xml";
        Resource insideArchive = new UrlResource(newRoot);
        // make sure the location actually exists
        Assert.assertTrue(insideArchive.exists());
        URL url = PersistenceUnitReader.determinePersistenceUnitRootUrl(insideArchive);
        Assert.assertTrue("the archive location should have been returned", archive.getURL().sameFile(url));
    }

    @Test
    public void testJpa1ExcludeUnlisted() throws Exception {
        PersistenceUnitReader reader = new PersistenceUnitReader(new PathMatchingResourcePatternResolver(), new JndiDataSourceLookup());
        String resource = "/org/springframework/orm/jpa/persistence-exclude-1.0.xml";
        PersistenceUnitInfo[] info = reader.readPersistenceUnitInfos(resource);
        Assert.assertNotNull(info);
        Assert.assertEquals("The number of persistence units is incorrect.", 4, info.length);
        PersistenceUnitInfo noExclude = info[0];
        Assert.assertNotNull("noExclude should not be null.", noExclude);
        Assert.assertEquals("noExclude name is not correct.", "NoExcludeElement", noExclude.getPersistenceUnitName());
        Assert.assertFalse("Exclude unlisted should default false in 1.0.", noExclude.excludeUnlistedClasses());
        PersistenceUnitInfo emptyExclude = info[1];
        Assert.assertNotNull("emptyExclude should not be null.", emptyExclude);
        Assert.assertEquals("emptyExclude name is not correct.", "EmptyExcludeElement", emptyExclude.getPersistenceUnitName());
        Assert.assertTrue("emptyExclude should be true.", emptyExclude.excludeUnlistedClasses());
        PersistenceUnitInfo trueExclude = info[2];
        Assert.assertNotNull("trueExclude should not be null.", trueExclude);
        Assert.assertEquals("trueExclude name is not correct.", "TrueExcludeElement", trueExclude.getPersistenceUnitName());
        Assert.assertTrue("trueExclude should be true.", trueExclude.excludeUnlistedClasses());
        PersistenceUnitInfo falseExclude = info[3];
        Assert.assertNotNull("falseExclude should not be null.", falseExclude);
        Assert.assertEquals("falseExclude name is not correct.", "FalseExcludeElement", falseExclude.getPersistenceUnitName());
        Assert.assertFalse("falseExclude should be false.", falseExclude.excludeUnlistedClasses());
    }

    @Test
    public void testJpa2ExcludeUnlisted() throws Exception {
        PersistenceUnitReader reader = new PersistenceUnitReader(new PathMatchingResourcePatternResolver(), new JndiDataSourceLookup());
        String resource = "/org/springframework/orm/jpa/persistence-exclude-2.0.xml";
        PersistenceUnitInfo[] info = reader.readPersistenceUnitInfos(resource);
        Assert.assertNotNull(info);
        Assert.assertEquals("The number of persistence units is incorrect.", 4, info.length);
        PersistenceUnitInfo noExclude = info[0];
        Assert.assertNotNull("noExclude should not be null.", noExclude);
        Assert.assertEquals("noExclude name is not correct.", "NoExcludeElement", noExclude.getPersistenceUnitName());
        Assert.assertFalse("Exclude unlisted still defaults to false in 2.0.", noExclude.excludeUnlistedClasses());
        PersistenceUnitInfo emptyExclude = info[1];
        Assert.assertNotNull("emptyExclude should not be null.", emptyExclude);
        Assert.assertEquals("emptyExclude name is not correct.", "EmptyExcludeElement", emptyExclude.getPersistenceUnitName());
        Assert.assertTrue("emptyExclude should be true.", emptyExclude.excludeUnlistedClasses());
        PersistenceUnitInfo trueExclude = info[2];
        Assert.assertNotNull("trueExclude should not be null.", trueExclude);
        Assert.assertEquals("trueExclude name is not correct.", "TrueExcludeElement", trueExclude.getPersistenceUnitName());
        Assert.assertTrue("trueExclude should be true.", trueExclude.excludeUnlistedClasses());
        PersistenceUnitInfo falseExclude = info[3];
        Assert.assertNotNull("falseExclude should not be null.", falseExclude);
        Assert.assertEquals("falseExclude name is not correct.", "FalseExcludeElement", falseExclude.getPersistenceUnitName());
        Assert.assertFalse("falseExclude should be false.", falseExclude.excludeUnlistedClasses());
    }
}

