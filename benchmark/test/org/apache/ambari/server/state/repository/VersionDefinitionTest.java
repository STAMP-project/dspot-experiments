/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.state.repository;


import RepoTag.GPL;
import RepositoryType.MAINT;
import VersionDefinitionXml.Merger;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.ServiceInfo;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.StackInfo;
import org.apache.ambari.server.state.stack.RepositoryXml;
import org.apache.ambari.server.state.stack.RepositoryXml.Os;
import org.apache.ambari.server.state.stack.RepositoryXml.Repo;
import org.apache.ambari.spi.RepositoryType;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for repository definitions.
 */
public class VersionDefinitionTest extends EasyMockSupport {
    private static File file = new File("src/test/resources/version_definition_test.xml");

    @Test
    public void testLoadingString() throws Exception {
        String xmlString = FileUtils.readFileToString(VersionDefinitionTest.file);
        VersionDefinitionXml xml = VersionDefinitionXml.load(xmlString);
        validateXml(xml);
    }

    @Test
    public void testLoadingUrl() throws Exception {
        VersionDefinitionXml xml = VersionDefinitionXml.load(VersionDefinitionTest.file.toURI().toURL());
        validateXml(xml);
    }

    @Test
    public void testAllServices() throws Exception {
        File f = new File("src/test/resources/version_definition_test_all_services.xml");
        VersionDefinitionXml xml = VersionDefinitionXml.load(f.toURI().toURL());
        StackInfo stack = new StackInfo() {
            @Override
            public ServiceInfo getService(String name) {
                return null;
            }
        };
        // the file does not define available services
        Assert.assertEquals(4, xml.manifestServices.size());
        Assert.assertEquals(3, xml.getAvailableServices(stack).size());
    }

    @Test
    public void testStackManifest() throws Exception {
        File f = new File("src/test/resources/version_definition_test_all_services.xml");
        VersionDefinitionXml xml = VersionDefinitionXml.load(f.toURI().toURL());
        StackInfo stack = new StackInfo() {
            private Map<String, ServiceInfo> m_services = new HashMap<String, ServiceInfo>() {
                {
                    put("HDFS", VersionDefinitionTest.makeService("HDFS"));
                    put("HBASE", VersionDefinitionTest.makeService("HBASE"));
                    put("HIVE", VersionDefinitionTest.makeService("HIVE"));
                    put("YARN", VersionDefinitionTest.makeService("YARN"));
                }
            };

            @Override
            public ServiceInfo getService(String name) {
                return m_services.get(name);
            }

            @Override
            public synchronized Collection<ServiceInfo> getServices() {
                return m_services.values();
            }
        };
        List<ManifestServiceInfo> stackServices = xml.getStackServices(stack);
        // the file does not define available services
        Assert.assertEquals(4, xml.manifestServices.size());
        Assert.assertEquals(3, xml.getAvailableServices(stack).size());
        Assert.assertEquals(4, stackServices.size());
        boolean foundHdfs = false;
        boolean foundYarn = false;
        boolean foundHive = false;
        for (ManifestServiceInfo msi : stackServices) {
            if ("HDFS".equals(msi.m_name)) {
                foundHdfs = true;
                Assert.assertEquals("HDFS Display", msi.m_display);
                Assert.assertEquals("HDFS Comment", msi.m_comment);
                Assert.assertEquals(1, msi.m_versions.size());
                Assert.assertEquals("2.7.1", msi.m_versions.iterator().next());
            } else
                if ("YARN".equals(msi.m_name)) {
                    foundYarn = true;
                    Assert.assertEquals(1, msi.m_versions.size());
                    Assert.assertEquals("1.1.1", msi.m_versions.iterator().next());
                } else
                    if ("HIVE".equals(msi.m_name)) {
                        foundHive = true;
                        Assert.assertEquals(2, msi.m_versions.size());
                        Assert.assertTrue(msi.m_versions.contains("1.1.0"));
                        Assert.assertTrue(msi.m_versions.contains("2.0.0"));
                    }


        }
        Assert.assertTrue(foundHdfs);
        Assert.assertTrue(foundYarn);
        Assert.assertTrue(foundHive);
    }

    @Test
    public void testSerialization() throws Exception {
        File f = new File("src/test/resources/version_definition_test_all_services.xml");
        VersionDefinitionXml xml = VersionDefinitionXml.load(f.toURI().toURL());
        String xmlString = xml.toXml();
        xml = VersionDefinitionXml.load(xmlString);
        Assert.assertNotNull(xml.release.build);
        Assert.assertEquals("1234", xml.release.build);
        f = new File("src/test/resources/version_definition_with_tags.xml");
        xml = VersionDefinitionXml.load(f.toURI().toURL());
        xmlString = xml.toXml();
        xml = VersionDefinitionXml.load(xmlString);
        Assert.assertEquals(2, xml.repositoryInfo.getOses().size());
        List<Repo> repos = null;
        for (Os os : xml.repositoryInfo.getOses()) {
            if (os.getFamily().equals("redhat6")) {
                repos = os.getRepos();
            }
        }
        Assert.assertNotNull(repos);
        Assert.assertEquals(3, repos.size());
        Repo found = null;
        for (Repo repo : repos) {
            if (repo.getRepoName().equals("HDP-GPL")) {
                found = repo;
                break;
            }
        }
        Assert.assertNotNull(found);
        Assert.assertNotNull(found.getTags());
        Assert.assertEquals(1, found.getTags().size());
        Assert.assertEquals(GPL, found.getTags().iterator().next());
    }

    @Test
    public void testMerger() throws Exception {
        File f = new File("src/test/resources/version_definition_test_all_services.xml");
        VersionDefinitionXml xml1 = VersionDefinitionXml.load(f.toURI().toURL());
        VersionDefinitionXml xml2 = VersionDefinitionXml.load(f.toURI().toURL());
        Assert.assertEquals(2, xml1.repositoryInfo.getOses().size());
        Assert.assertEquals(2, xml2.repositoryInfo.getOses().size());
        // make xml1 have only redhat6 (remove redhat7) without a package version
        RepositoryXml.Os target = null;
        for (RepositoryXml.Os os : xml1.repositoryInfo.getOses()) {
            if (os.getFamily().equals("redhat7")) {
                target = os;
            }
        }
        Assert.assertNotNull(target);
        xml1.repositoryInfo.getOses().remove(target);
        // make xml2 have only redhat7 (remove redhat6) with a package version
        target = null;
        for (RepositoryXml.Os os : xml2.repositoryInfo.getOses()) {
            if (os.getFamily().equals("redhat6")) {
                target = os;
            } else {
                Field field = Os.class.getDeclaredField("packageVersion");
                field.setAccessible(true);
                field.set(os, "2_3_4_2");
            }
        }
        Assert.assertNotNull(target);
        xml2.repositoryInfo.getOses().remove(target);
        xml2.release.version = "2.3.4.2";
        xml2.release.build = "2468";
        Assert.assertEquals(1, xml1.repositoryInfo.getOses().size());
        Assert.assertEquals(1, xml2.repositoryInfo.getOses().size());
        VersionDefinitionXml.Merger builder = new VersionDefinitionXml.Merger();
        VersionDefinitionXml xml3 = builder.merge();
        Assert.assertNull(xml3);
        builder.add(xml1.release.version, xml1);
        builder.add("", xml2);
        xml3 = builder.merge();
        Assert.assertNotNull(xml3);
        Assert.assertNull("Merged definition cannot have a build", xml3.release.build);
        Assert.assertEquals(xml3.release.version, "2.3.4.1");
        RepositoryXml.Os redhat6 = null;
        RepositoryXml.Os redhat7 = null;
        Assert.assertEquals(2, xml3.repositoryInfo.getOses().size());
        for (RepositoryXml.Os os : xml3.repositoryInfo.getOses()) {
            if (os.getFamily().equals("redhat6")) {
                redhat6 = os;
            } else
                if (os.getFamily().equals("redhat7")) {
                    redhat7 = os;
                }

        }
        Assert.assertNotNull(redhat6);
        Assert.assertNotNull(redhat7);
        Assert.assertNull(redhat6.getPackageVersion());
        Assert.assertEquals("2_3_4_2", redhat7.getPackageVersion());
        // !!! extra test to make sure it serializes
        xml3.toXml();
    }

    @Test
    public void testLoadingBadNewLine() throws Exception {
        List<?> lines = FileUtils.readLines(VersionDefinitionTest.file);
        // crude
        StringBuilder builder = new StringBuilder();
        for (Object line : lines) {
            String lineString = line.toString().trim();
            if (lineString.startsWith("<baseurl>")) {
                lineString = lineString.replace("<baseurl>", "");
                lineString = lineString.replace("</baseurl>", "");
                builder.append("<baseurl>\n");
                builder.append(lineString).append('\n');
                builder.append("</baseurl>\n");
            } else
                if (lineString.startsWith("<version>")) {
                    lineString = lineString.replace("<version>", "");
                    lineString = lineString.replace("</version>", "");
                    builder.append("<version>\n");
                    builder.append(lineString).append('\n');
                    builder.append("</version>\n");
                } else {
                    builder.append(line.toString().trim()).append('\n');
                }

        }
        VersionDefinitionXml xml = VersionDefinitionXml.load(builder.toString());
        validateXml(xml);
    }

    @Test
    public void testPackageVersion() throws Exception {
        File f = new File("src/test/resources/hbase_version_test.xml");
        VersionDefinitionXml xml = VersionDefinitionXml.load(f.toURI().toURL());
        String xmlString = xml.toXml();
        xml = VersionDefinitionXml.load(xmlString);
        Assert.assertNotNull(xml.release.build);
        Assert.assertEquals("3396", xml.release.build);
        Assert.assertEquals("redhat6", xml.repositoryInfo.getOses().get(0).getFamily());
        Assert.assertEquals("2_3_4_0_3396", xml.repositoryInfo.getOses().get(0).getPackageVersion());
        Assert.assertNotNull(xml.getPackageVersion("redhat6"));
        Assert.assertEquals("2_3_4_0_3396", xml.getPackageVersion("redhat6"));
        Assert.assertNull(xml.getPackageVersion("suse11"));
    }

    @Test
    public void testMaintVersion() throws Exception {
        File f = new File("src/test/resources/version_definition_test_maint.xml");
        VersionDefinitionXml xml = VersionDefinitionXml.load(f.toURI().toURL());
        String xmlString = xml.toXml();
        xml = VersionDefinitionXml.load(xmlString);
        Assert.assertEquals(MAINT, xml.release.repositoryType);
        Assert.assertEquals("2.3.4.1", xml.release.version);
        Assert.assertEquals("1234", xml.release.build);
        Assert.assertEquals("redhat6", xml.repositoryInfo.getOses().get(0).getFamily());
        List<AvailableServiceReference> availableServices = xml.availableServices;
        Assert.assertEquals(3, availableServices.size());
        List<ManifestService> manifestServices = xml.manifestServices;
        Assert.assertEquals(4, manifestServices.size());
        ManifestService hdfs = null;
        ManifestService hive = null;
        for (ManifestService as : manifestServices) {
            if (as.serviceId.equals("HDFS-271")) {
                hdfs = as;
            } else
                if (as.serviceId.equals("HIVE-200")) {
                    hive = as;
                }

        }
        Assert.assertNotNull(hdfs);
        Assert.assertNotNull(hive);
        Assert.assertEquals("2.3.4.0", hdfs.releaseVersion);
        Assert.assertNull(hive.releaseVersion);
        StackInfo stack = new StackInfo() {
            @Override
            public ServiceInfo getService(String name) {
                return VersionDefinitionTest.makeService("HIVE", "HIVE_METASTORE");
            }
        };
        Collection<AvailableService> availables = xml.getAvailableServices(stack);
        Assert.assertEquals(2, availables.size());
        boolean found = false;
        for (AvailableService available : availables) {
            if (available.getName().equals("HIVE")) {
                found = true;
                Assert.assertEquals(2, available.getVersions().size());
                for (AvailableVersion version : available.getVersions()) {
                    if (version.getVersion().equals("1.1.0")) {
                        Assert.assertEquals("1.0.9", version.getReleaseVersion());
                    } else {
                        Assert.assertNull(version.getReleaseVersion());
                    }
                }
            }
        }
        Assert.assertTrue("Found available version for HIVE", found);
    }

    @Test
    public void testAvailableFull() throws Exception {
        Cluster cluster = createNiceMock(Cluster.class);
        RepositoryVersionEntity repositoryVersion = createNiceMock(RepositoryVersionEntity.class);
        expect(repositoryVersion.getVersion()).andReturn("2.3.4.0").atLeastOnce();
        Service serviceHdfs = createNiceMock(Service.class);
        expect(serviceHdfs.getName()).andReturn("HDFS").atLeastOnce();
        expect(serviceHdfs.getDisplayName()).andReturn("HDFS").atLeastOnce();
        expect(serviceHdfs.getDesiredRepositoryVersion()).andReturn(repositoryVersion).atLeastOnce();
        Service serviceHBase = createNiceMock(Service.class);
        expect(serviceHBase.getName()).andReturn("HBASE").atLeastOnce();
        expect(serviceHBase.getDisplayName()).andReturn("HBase").atLeastOnce();
        expect(serviceHBase.getDesiredRepositoryVersion()).andReturn(repositoryVersion).atLeastOnce();
        StackInfo stackInfo = createNiceMock(StackInfo.class);
        expect(stackInfo.getReleaseVersion()).andReturn(new DefaultStackVersion()).atLeastOnce();
        AmbariMetaInfo ami = createNiceMock(AmbariMetaInfo.class);
        expect(ami.getStack(EasyMock.anyObject(StackId.class))).andReturn(stackInfo).atLeastOnce();
        // !!! should never be accessed as it's not in any VDF
        Service serviceAMS = createNiceMock(Service.class);
        expect(cluster.getServices()).andReturn(ImmutableMap.<String, Service>builder().put("HDFS", serviceHdfs).put("HBASE", serviceHBase).put("AMBARI_METRICS", serviceAMS).build()).atLeastOnce();
        replayAll();
        File f = new File("src/test/resources/version_definition_test_all_services.xml");
        VersionDefinitionXml xml = VersionDefinitionXml.load(f.toURI().toURL());
        ClusterVersionSummary summary = xml.getClusterSummary(cluster, ami);
        Assert.assertEquals(2, summary.getAvailableServiceNames().size());
        f = new File("src/test/resources/version_definition_test_maint.xml");
        xml = VersionDefinitionXml.load(f.toURI().toURL());
        summary = xml.getClusterSummary(cluster, ami);
        Assert.assertEquals(0, summary.getAvailableServiceNames().size());
        f = new File("src/test/resources/version_definition_test_maint.xml");
        xml = VersionDefinitionXml.load(f.toURI().toURL());
        xml.release.repositoryType = RepositoryType.STANDARD;
        xml.availableServices = Collections.emptyList();
        summary = xml.getClusterSummary(cluster, ami);
        Assert.assertEquals(2, summary.getAvailableServiceNames().size());
        f = new File("src/test/resources/version_definition_test_maint_partial.xml");
        xml = VersionDefinitionXml.load(f.toURI().toURL());
        summary = xml.getClusterSummary(cluster, ami);
        Assert.assertEquals(1, summary.getAvailableServiceNames().size());
    }

    @Test
    public void testAvailableBuildVersion() throws Exception {
        Cluster cluster = createNiceMock(Cluster.class);
        RepositoryVersionEntity repositoryVersion = createNiceMock(RepositoryVersionEntity.class);
        expect(repositoryVersion.getVersion()).andReturn("2.3.4.1-1").atLeastOnce();
        Service serviceHdfs = createNiceMock(Service.class);
        expect(serviceHdfs.getName()).andReturn("HDFS").atLeastOnce();
        expect(serviceHdfs.getDisplayName()).andReturn("HDFS").atLeastOnce();
        expect(serviceHdfs.getDesiredRepositoryVersion()).andReturn(repositoryVersion).atLeastOnce();
        Service serviceHBase = createNiceMock(Service.class);
        expect(serviceHBase.getName()).andReturn("HBASE").atLeastOnce();
        expect(serviceHBase.getDisplayName()).andReturn("HBase").atLeastOnce();
        expect(serviceHBase.getDesiredRepositoryVersion()).andReturn(repositoryVersion).atLeastOnce();
        StackInfo stackInfo = createNiceMock(StackInfo.class);
        expect(stackInfo.getReleaseVersion()).andReturn(new DefaultStackVersion()).atLeastOnce();
        AmbariMetaInfo ami = createNiceMock(AmbariMetaInfo.class);
        expect(ami.getStack(EasyMock.anyObject(StackId.class))).andReturn(stackInfo).atLeastOnce();
        // !!! should never be accessed as it's not in any VDF
        Service serviceAMS = createNiceMock(Service.class);
        expect(cluster.getServices()).andReturn(ImmutableMap.<String, Service>builder().put("HDFS", serviceHdfs).put("HBASE", serviceHBase).put("AMBARI_METRICS", serviceAMS).build()).atLeastOnce();
        replayAll();
        File f = new File("src/test/resources/version_definition_test_maint_partial.xml");
        VersionDefinitionXml xml = VersionDefinitionXml.load(f.toURI().toURL());
        xml.release.version = "2.3.4.1";
        xml.release.build = "2";
        ClusterVersionSummary summary = xml.getClusterSummary(cluster, ami);
        Assert.assertEquals(1, summary.getAvailableServiceNames().size());
    }

    /**
     * Tests that patch upgrade dependencies can be calculated recursively.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRecursiveDependencyDetection() throws Exception {
        File f = new File("src/test/resources/version_definition_test_all_services.xml");
        VersionDefinitionXml xml = VersionDefinitionXml.load(f.toURI().toURL());
        Map<String, List<String>> dependencies = new HashMap<>();
        dependencies.put("A", Lists.newArrayList("B", "X"));
        dependencies.put("B", Lists.newArrayList("C", "D", "E"));
        dependencies.put("E", Lists.newArrayList("A", "F"));
        dependencies.put("F", Lists.newArrayList("B", "E"));
        // services not installed
        dependencies.put("X", Lists.newArrayList("Y", "Z", "A"));
        dependencies.put("Z", Lists.newArrayList("B"));
        Set<String> installedServices = Sets.newHashSet("A", "B", "C", "D", "E", "F", "G", "H");
        Set<String> servicesInUpgrade = Sets.newHashSet("A");
        Set<String> results = xml.getRecursiveDependencies(Sets.newHashSet("B"), dependencies, servicesInUpgrade, installedServices);
        Assert.assertEquals(5, results.size());
        Assert.assertTrue(results.contains("B"));
        Assert.assertTrue(results.contains("C"));
        Assert.assertTrue(results.contains("D"));
        Assert.assertTrue(results.contains("E"));
        Assert.assertTrue(results.contains("F"));
        servicesInUpgrade = Sets.newHashSet("A", "B", "C", "E", "F");
        results = xml.getRecursiveDependencies(Sets.newHashSet("D"), dependencies, servicesInUpgrade, installedServices);
        Assert.assertEquals(1, results.size());
        Assert.assertTrue(results.contains("D"));
        servicesInUpgrade = Sets.newHashSet("A", "F");
        results = xml.getRecursiveDependencies(Sets.newHashSet("B", "E"), dependencies, servicesInUpgrade, installedServices);
        Assert.assertEquals(4, results.size());
        Assert.assertTrue(results.contains("B"));
        Assert.assertTrue(results.contains("C"));
        Assert.assertTrue(results.contains("D"));
        Assert.assertTrue(results.contains("E"));
    }

    /**
     * Tests that a VDF can be built from the cluster services correctly, taking
     * into account things like whether a service has components which advertise a
     * version. The first part of this will test that services which do not
     * advertise a version are not returned when building a VDF from cluster
     * services. The 2nd part will test to make sure that when combining a VDF
     * with the stack, all services are returned regardless of whether they
     * advertise a version.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBuild() throws Exception {
        ServiceInfo serviceWithVersionAdvertised = createNiceMock(ServiceInfo.class);
        ServiceInfo serviceWithoutVersionAdvertised = createNiceMock(ServiceInfo.class);
        List<ServiceInfo> stackServices = Lists.newArrayList(serviceWithVersionAdvertised, serviceWithoutVersionAdvertised);
        expect(serviceWithVersionAdvertised.isVersionAdvertised()).andReturn(true).atLeastOnce();
        expect(serviceWithVersionAdvertised.getName()).andReturn("BAR").atLeastOnce();
        expect(serviceWithVersionAdvertised.getVersion()).andReturn("1.5.0").atLeastOnce();
        expect(serviceWithoutVersionAdvertised.isVersionAdvertised()).andReturn(false).atLeastOnce();
        expect(serviceWithoutVersionAdvertised.getName()).andReturn("BAZ").atLeastOnce();
        expect(serviceWithoutVersionAdvertised.getVersion()).andReturn("2.0.0").atLeastOnce();
        File f = new File("src/test/resources/version_definition_test_all_services.xml");
        VersionDefinitionXml xml1 = VersionDefinitionXml.load(f.toURI().toURL());
        RepositoryXml repositoryXml = createNiceMock(RepositoryXml.class);
        expect(repositoryXml.getOses()).andReturn(xml1.repositoryInfo.getOses()).atLeastOnce();
        StackInfo stackInfo = createNiceMock(StackInfo.class);
        expect(stackInfo.getName()).andReturn("FOO").anyTimes();
        expect(stackInfo.getVersion()).andReturn("1.0.0").anyTimes();
        expect(stackInfo.getServices()).andReturn(stackServices).atLeastOnce();
        expect(stackInfo.getRepositoryXml()).andReturn(repositoryXml).atLeastOnce();
        replayAll();
        VersionDefinitionXml vdf = VersionDefinitionXml.build(stackInfo);
        Assert.assertEquals(1, vdf.manifestServices.size());
        List<ManifestServiceInfo> manifestServices = vdf.getStackServices(stackInfo);
        Assert.assertEquals(2, manifestServices.size());
        verifyAll();
    }
}

