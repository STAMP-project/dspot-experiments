package org.drools.compiler.kie.builder.impl;


import KieModuleRepo.MAX_SIZE_GA_CACHE;
import KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.appformer.maven.support.DependencyFilter;
import org.appformer.maven.support.PomModel;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl.KieModuleRepo;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.common.ResourceProvider;
import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.ReleaseIdComparator.ComparableVersion;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResourceChangeSet;
import org.mockito.Mockito;


/**
 * This test contains
 * - normal tests that test for concurrency issues and memory leaks (that the KieModuleRepo
 * functions as a LRU cache, and evicts old {@link KieModule} instances )
 */
public class KieModuleRepoTest {
    private KieModuleRepo kieModuleRepo;

    private int maxSizeGaCacheOrig;

    private int maxSizeGaVersionsCacheOrig;

    private Field maxSizeGaCacheField;

    private Field maxSizeGaVersionsCacheField;

    /**
     * TESTS ----------------------------------------------------------------------------------------------------------------------
     */
    // simultaneous requests to deploy two new deployments (different versions)
    // for an empty/new GA artifactMap
    @Test(timeout = 5000)
    public void testDeployTwoArtifactVersionsSameTime() throws Exception {
        final String groupId = "org";
        final String artifactId = "one";
        final String firstVersion = "1.0";
        final String secondVersion = "1.0-NEW-FEATURE";
        final CyclicBarrier storeOperationBarrier = new CyclicBarrier(2);
        final CyclicBarrier threadsFinishedBarrier = new CyclicBarrier(3);
        final Thread firstThread = new Thread(getStoreArtifactRunnable(kieModuleRepo, groupId, artifactId, firstVersion, storeOperationBarrier, threadsFinishedBarrier));
        final Thread secondThread = new Thread(getStoreArtifactRunnable(kieModuleRepo, groupId, artifactId, secondVersion, storeOperationBarrier, threadsFinishedBarrier));
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            firstThread.setName("normal");
            executor.submit(firstThread);
            secondThread.setName("newFeature");
            executor.submit(secondThread);
            KieModuleRepoTest.waitFor(threadsFinishedBarrier);
        } finally {
            executor.shutdownNow();
        }
        final String ga = (groupId + ":") + artifactId;
        final Map<ComparableVersion, KieModule> artifactMap = kieModuleRepo.kieModules.get(ga);
        final ComparableVersion normalVersion = new ComparableVersion(firstVersion);
        final KieModule normalKieModule = artifactMap.get(normalVersion);
        final ComparableVersion newFeatureVersion = new ComparableVersion(secondVersion);
        final KieModule newFeatureKieModule = artifactMap.get(newFeatureVersion);
        Assert.assertNotNull("Race condition occurred: normal KieModule disappeared from KieModuleRepo!", normalKieModule);
        Assert.assertNotNull("Race condition occurred: new feature KieModule disappeared from KieModuleRepo!", newFeatureKieModule);
    }

    // remove request followed by a store request on a high load system
    // * remove does not completely finish before store starts
    @Test(timeout = 5000)
    public void removeStoreArtifactMapTest() throws Exception {
        // actual test
        final ReleaseIdImpl releaseId = new ReleaseIdImpl("org", "redeploy", "2.0");
        final InternalKieModule originalKieModule = Mockito.mock(InternalKieModule.class);
        Mockito.when(originalKieModule.getReleaseId()).thenReturn(releaseId);
        Mockito.when(originalKieModule.getCreationTimestamp()).thenReturn(0L);
        final InternalKieModule redeployKieModule = Mockito.mock(InternalKieModule.class);
        Mockito.when(redeployKieModule.getReleaseId()).thenReturn(releaseId);
        Mockito.when(redeployKieModule.getCreationTimestamp()).thenReturn(1L);
        // 1. initial deploy ("long ago")
        kieModuleRepo.store(originalKieModule);
        final CyclicBarrier storeRemoveOperationBarrier = new CyclicBarrier(2);
        final CyclicBarrier operationsSerializationBarrier = new CyclicBarrier(2);
        final CyclicBarrier threadsFinishedBarrier = new CyclicBarrier(3);
        final Runnable removeRunnable = () -> {
            KieModuleRepoTest.waitFor(storeRemoveOperationBarrier);
            kieModuleRepo.remove(releaseId);
            KieModuleRepoTest.waitFor(operationsSerializationBarrier);
            KieModuleRepoTest.waitFor(threadsFinishedBarrier);
        };
        final Runnable redeployRunnable = () -> {
            KieModuleRepoTest.waitFor(storeRemoveOperationBarrier);
            KieModuleRepoTest.waitFor(operationsSerializationBarrier);
            kieModuleRepo.store(redeployKieModule);
            KieModuleRepoTest.waitFor(threadsFinishedBarrier);
        };
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            // 2. remove and redploy
            executor.submit(removeRunnable);
            executor.submit(redeployRunnable);
            KieModuleRepoTest.waitFor(threadsFinishedBarrier);
        } finally {
            executor.shutdownNow();
        }
        final String ga = ((releaseId.getGroupId()) + ":") + (releaseId.getArtifactId());
        final Map<ComparableVersion, KieModule> artifactMap = kieModuleRepo.kieModules.get(ga);
        Assert.assertNotNull((("Artifact Map for GA '" + ga) + "' not in KieModuleRepo!"), artifactMap);
        // never gets this far, but this is a good check
        final KieModule redeployedKieModule = artifactMap.get(new ComparableVersion(releaseId.getVersion()));
        Assert.assertNotNull("Redeployed module has disappeared from KieModuleRepo!", redeployedKieModule);
        Assert.assertEquals("Original module retrieved instead of redeployed module!", 1L, redeployKieModule.getCreationTimestamp());
    }

    private static class InternalKieModuleStub implements InternalKieModule {
        @Override
        public void cacheKnowledgeBuilderForKieBase(String kieBaseName, KnowledgeBuilder kbuilder) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.cacheKnowledgeBuilderForKieBase -> TODO");
        }

        @Override
        public KnowledgeBuilder getKnowledgeBuilderForKieBase(String kieBaseName) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getKnowledgeBuilderForKieBase -> TODO");
        }

        @Override
        public Collection<KiePackage> getKnowledgePackagesForKieBase(String kieBaseName) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getKnowledgePackagesForKieBase -> TODO");
        }

        @Override
        public void cacheResultsForKieBase(String kieBaseName, Results results) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.cacheResultsForKieBase -> TODO");
        }

        @Override
        public Map<String, Results> getKnowledgeResultsCache() {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getKnowledgeResultsCache -> TODO");
        }

        @Override
        public KieModuleModel getKieModuleModel() {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getKieModuleModel -> TODO");
        }

        @Override
        public byte[] getBytes() {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getBytes -> TODO");
        }

        @Override
        public boolean hasResource(String fileName) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.hasResource -> TODO");
        }

        @Override
        public Resource getResource(String fileName) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getResource -> TODO");
        }

        @Override
        public ResourceConfiguration getResourceConfiguration(String fileName) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getResourceConfiguration -> TODO");
        }

        @Override
        public Map<ReleaseId, InternalKieModule> getKieDependencies() {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getKieDependencies -> TODO");
        }

        @Override
        public void addKieDependency(InternalKieModule dependency) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.addKieDependency -> TODO");
        }

        @Override
        public Collection<ReleaseId> getJarDependencies(DependencyFilter filter) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getJarDependencies -> TODO");
        }

        @Override
        public Collection<ReleaseId> getUnresolvedDependencies() {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getUnresolvedDependencies -> TODO");
        }

        @Override
        public void setUnresolvedDependencies(Collection<ReleaseId> unresolvedDependencies) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.setUnresolvedDependencies -> TODO");
        }

        @Override
        public boolean isAvailable(String pResourceName) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.isAvailable -> TODO");
        }

        @Override
        public byte[] getBytes(String pResourceName) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getBytes -> TODO");
        }

        @Override
        public Collection<String> getFileNames() {
            return Collections.emptyList();
        }

        @Override
        public File getFile() {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getFile -> TODO");
        }

        @Override
        public ResourceProvider createResourceProvider() {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.createResourceProvider -> TODO");
        }

        @Override
        public Map<String, byte[]> getClassesMap() {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getClassesMap -> TODO");
        }

        @Override
        public boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.addResourceToCompiler -> TODO");
        }

        @Override
        public boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName, ResourceChangeSet rcs) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.addResourceToCompiler -> TODO");
        }

        @Override
        public long getCreationTimestamp() {
            return 0L;
        }

        @Override
        public InputStream getPomAsStream() {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getPomAsStream -> TODO");
        }

        @Override
        public PomModel getPomModel() {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getPomModel -> TODO");
        }

        @Override
        public KnowledgeBuilderConfiguration getBuilderConfiguration(KieBaseModel kBaseModel, ClassLoader classLoader) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.getBuilderConfiguration -> TODO");
        }

        @Override
        public InternalKnowledgeBase createKieBase(KieBaseModelImpl kBaseModel, KieProject kieProject, ResultsImpl messages, KieBaseConfiguration conf) {
            throw new UnsupportedOperationException("org.drools.compiler.kie.builder.impl.KieModuleRepoTest.InternalKieModuleStub.createKieBase -> TODO");
        }

        @Override
        public ClassLoader getModuleClassLoader() {
            return null;
        }

        @Override
        public ReleaseId getReleaseId() {
            return new ReleaseIdImpl("org", "deployTwiceAfterUpdateDependency", "1.0");
        }
    }

    // 2. simultaneous deploy requests
    // - multitenant UI
    // - duplicate REST requests
    @Test(timeout = 5000)
    public void newerVersionDeployOverwritesTest() throws Exception {
        // setup
        final ReleaseIdImpl releaseId = new ReleaseIdImpl("org", "deployTwiceAfterUpdateDependency", "1.0");
        final InternalKieModule originalOldKieModule = new KieModuleRepoTest.InternalKieModuleStub();
        final ReleaseId dependentReleaseid = new ReleaseIdImpl("org", "deployTwiceAfterUpdate", "1.0");
        final KieContainerImpl kieContainer = KieModuleRepoTest.createMockKieContainer(dependentReleaseid, kieModuleRepo);
        // 1. deploy
        kieModuleRepo.store(originalOldKieModule);
        // 2. another project is dependent on this project
        kieContainer.updateDependencyToVersion(releaseId, releaseId);
        final InternalKieModule newKieModule = Mockito.mock(InternalKieModule.class);
        Mockito.when(newKieModule.getReleaseId()).thenReturn(releaseId);
        Mockito.when(newKieModule.getCreationTimestamp()).thenReturn(10L);
        final CyclicBarrier storeOperationBarrier = new CyclicBarrier(2);
        final CyclicBarrier storeSerializationBarrier = new CyclicBarrier(2);
        final CyclicBarrier threadsFinishedBarrier = new CyclicBarrier(3);
        final Runnable deployRunnable = () -> {
            KieModuleRepoTest.waitFor(storeOperationBarrier);
            // Second thread waits with store until the first one finishes with it.
            if (Thread.currentThread().getName().equals("two")) {
                KieModuleRepoTest.waitFor(storeSerializationBarrier);
            }
            kieModuleRepo.store(newKieModule);
            if (Thread.currentThread().getName().equals("one")) {
                KieModuleRepoTest.waitFor(storeSerializationBarrier);
            }
            KieModuleRepoTest.waitFor(threadsFinishedBarrier);
        };
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            // 2. remove and redploy
            final Thread deployThread = new Thread(deployRunnable);
            deployThread.setName("one");
            executor.submit(deployThread);
            final Thread secondDeployThread = new Thread(deployRunnable);
            secondDeployThread.setName("two");
            executor.submit(secondDeployThread);
            KieModuleRepoTest.waitFor(threadsFinishedBarrier);
        } finally {
            executor.shutdownNow();
        }
        // never gets this far, but this is a good check
        final KieModule oldKieModule = kieModuleRepo.oldKieModules.get(releaseId);
        final long oldKieModuleTimeStamp = getCreationTimestamp();
        final long originalKieModuleTimestamp = originalOldKieModule.getCreationTimestamp();
        Assert.assertEquals("The old kie module in the repo is not the originally deployed module!", originalKieModuleTimestamp, oldKieModuleTimeStamp);
    }

    @Test
    public void storingNewProjectsCausesOldProjectEvictionFromKieModuleRepoTest() throws Exception {
        // setup
        KieModuleRepoTest.setFinalField(maxSizeGaCacheField, null, 3);
        KieModuleRepoTest.setFinalField(maxSizeGaVersionsCacheField, null, 2);// to test oldKieModules caching

        final ReleaseIdImpl[] releaseIds = new ReleaseIdImpl[7];
        for (int i = 0; i < (releaseIds.length); ++i) {
            final String artifactId = Character.toString(((char) ('A' + i)));
            releaseIds[i] = new ReleaseIdImpl("org", artifactId, "1.0");
        }
        // store
        for (int i = 0; i < (releaseIds.length); ++i) {
            final InternalKieModule kieModule = Mockito.mock(InternalKieModule.class);
            Mockito.when(kieModule.getReleaseId()).thenReturn(releaseIds[i]);
            Mockito.when(kieModule.getCreationTimestamp()).thenReturn(10L);
            kieModuleRepo.store(kieModule);
            kieModuleRepo.store(kieModule);// store module 2 times to trigger storage to oldKieModules

        }
        final int numKieModules = KieModuleRepoTest.countKieModules(kieModuleRepo.kieModules);
        Assert.assertEquals((("KieModuleRepo cache should not grow past " + (KieModuleRepo.MAX_SIZE_GA_CACHE)) + ": "), MAX_SIZE_GA_CACHE, numKieModules);
        final int oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        final int max = (KieModuleRepo.MAX_SIZE_GA_CACHE) * (KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE);
        Assert.assertTrue(((("KieModuleRepot old KieModules map is not limited in it's growth: " + oldKieModulesSize) + " > ") + max), (oldKieModulesSize <= max));
    }

    @Test
    public void storingNewProjectVersionsCausesOldVersionEvictionFromKieModuleRepoTest() throws Exception {
        // setup
        KieModuleRepoTest.setFinalField(maxSizeGaCacheField, null, 2);// to test oldKieModules caching

        KieModuleRepoTest.setFinalField(maxSizeGaVersionsCacheField, null, 3);
        final ReleaseIdImpl[] releaseIds = new ReleaseIdImpl[7];
        for (int i = 0; i < (releaseIds.length); ++i) {
            releaseIds[i] = new ReleaseIdImpl("org", "test", ("1." + i));
        }
        // store
        for (int i = 0; i < (releaseIds.length); ++i) {
            final InternalKieModule kieModule = Mockito.mock(InternalKieModule.class);
            Mockito.when(kieModule.getReleaseId()).thenReturn(releaseIds[i]);
            Mockito.when(kieModule.getCreationTimestamp()).thenReturn(10L);
            kieModuleRepo.store(kieModule);
            kieModuleRepo.store(kieModule);// in order to trigger storage to oldKieModules

        }
        int numKieModules = KieModuleRepoTest.countKieModules(kieModuleRepo.kieModules);
        Assert.assertEquals((("KieModuleRepo cache should not grow past " + (KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE)) + ": "), MAX_SIZE_GA_VERSIONS_CACHE, numKieModules);
        int oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        final int maxOldKieModules = (KieModuleRepo.MAX_SIZE_GA_CACHE) * (KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE);
        Assert.assertTrue(((("KieModuleRepo old KieModules map is not limited in it's growth: " + oldKieModulesSize) + " > ") + maxOldKieModules), (oldKieModulesSize <= maxOldKieModules));
        // store
        for (int o = 0; o < 2; ++o) {
            // loop 2 times in order to trigger storage to oldKieModules
            for (int i = 0; i < (releaseIds.length); ++i) {
                final InternalKieModule kieModule = Mockito.mock(InternalKieModule.class);
                Mockito.when(kieModule.getReleaseId()).thenReturn(releaseIds[i]);
                Mockito.when(kieModule.getCreationTimestamp()).thenReturn(10L);
                kieModuleRepo.store(kieModule);
            }
        }
        numKieModules = KieModuleRepoTest.countKieModules(kieModuleRepo.kieModules);
        Assert.assertEquals((("KieModuleRepo cache should not grow past " + (KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE)) + ": "), MAX_SIZE_GA_VERSIONS_CACHE, numKieModules);
        oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        Assert.assertTrue(((("KieModuleRepo old KieModules map is not limited in it's growth: " + oldKieModulesSize) + " > ") + maxOldKieModules), (oldKieModulesSize <= maxOldKieModules));
    }

    @Test
    public void testOldKieModulesLRUCache() throws Exception {
        // setup
        KieModuleRepoTest.setFinalField(maxSizeGaCacheField, null, 2);
        KieModuleRepoTest.setFinalField(maxSizeGaVersionsCacheField, null, 4);
        final ReleaseIdImpl[] releaseIds = new ReleaseIdImpl[9];
        for (int i = 0; i < (releaseIds.length); ++i) {
            final String artifactId = Character.toString(((char) ('A' + (i / 2))));
            releaseIds[i] = new ReleaseIdImpl("org", artifactId, ("1." + i));
        }
        // store
        for (int i = 0; i < (releaseIds.length); ++i) {
            final InternalKieModule kieModule = Mockito.mock(InternalKieModule.class);
            Mockito.when(kieModule.getReleaseId()).thenReturn(releaseIds[i]);
            Mockito.when(kieModule.getCreationTimestamp()).thenReturn(10L);
            kieModuleRepo.store(kieModule);
            kieModuleRepo.store(kieModule);// in order to trigger storage to oldKieModules

        }
        int maxSameGAModules = 0;
        int maxGAs = 0;
        for (final Map<ComparableVersion, KieModule> artifactMap : kieModuleRepo.kieModules.values()) {
            maxGAs++;
            if ((artifactMap.size()) > maxSameGAModules) {
                maxSameGAModules = artifactMap.size();
            }
        }
        Assert.assertTrue(((((("The maximum of artifacts per GA should not grow past " + (KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE)) + ": ") + (KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE)) + " < ") + maxSameGAModules), ((KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE) >= maxSameGAModules));
        Assert.assertTrue(((((("The number of GAs not grow past " + (KieModuleRepo.MAX_SIZE_GA_CACHE)) + ": ") + (KieModuleRepo.MAX_SIZE_GA_CACHE)) + " > ") + maxGAs), ((KieModuleRepo.MAX_SIZE_GA_CACHE) >= maxGAs));
        final int oldKieModulesSize = kieModuleRepo.oldKieModules.size();
        final int maxOldKieModules = (KieModuleRepo.MAX_SIZE_GA_CACHE) * (KieModuleRepo.MAX_SIZE_GA_VERSIONS_CACHE);
        Assert.assertTrue(((("KieModuleRepo old KieModules map is not limited in it's growth: " + oldKieModulesSize) + " > ") + maxOldKieModules), (oldKieModulesSize <= maxOldKieModules));
    }
}

