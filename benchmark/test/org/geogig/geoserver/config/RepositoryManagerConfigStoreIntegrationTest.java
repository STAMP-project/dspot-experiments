/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.config;


import com.google.common.collect.ImmutableSet;
import java.net.URI;
import org.geoserver.platform.resource.ResourceStore;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Integration test suite that simulates a clustered setup where two {@link RepositoryManager}/
 * {@link ConfigStore} combos work against the same {@link ResourceStore}, making sure events issued
 * by the {@code ResourceStore} are properly handled both by the node that triggered it and the one
 * that didn't.
 */
public class RepositoryManagerConfigStoreIntegrationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ResourceStore dataDir;

    private ConfigStore _store1;

    /**
     * Do not access directly but through {@link #store1()}/ {@link #store2()}. They're lazily
     * created to let test cases decide when they get initialized
     */
    private ConfigStore _store2;

    private RepositoryManager _repoManager1;

    /**
     * Do not access directly but through {@link #repoManager1()}/ {@link #repoManager2()}. They're
     * lazily created to let test cases decide when they get initialized. Calling these methods
     * imply the corresponding {@link #store1()} or {@link #store2()}
     */
    private RepositoryManager _repoManager2;

    private RepositoryInfo repo1;

    private RepositoryInfo repo2;

    private RepositoryInfo repo3;

    private RepositoryInfo repo4;

    /**
     * repos added to {@link #store1()} are immediately available on {@link #store2()} when it
     * "joins the cluster"
     */
    @Test
    public void testJoinCluster() {
        ConfigStore store1 = store1();
        store1.save(repo1.clone());
        store1.save(repo2.clone());
        store1.save(repo3.clone());
        store1.save(repo4.clone());
        ImmutableSet<RepositoryInfo> expected = ImmutableSet.of(repo1, repo2, repo3, repo4);
        Assert.assertEquals(expected, new java.util.HashSet(store1.getRepositories()));
        // "join cluster" since it's lazily created
        ConfigStore store2 = store2();
        Assert.assertEquals(expected, new java.util.HashSet(store2.getRepositories()));
    }

    /**
     * both {@link #store1()} and {@link #store2()} are in the cluster, repos added to #1 get
     * reflected in #2
     */
    @Test
    public void verifyResourceCreateEvents() {
        ConfigStore store1 = store1();
        ConfigStore store2 = store2();
        store1.save(repo1.clone());
        store1.save(repo2.clone());
        store1.save(repo3.clone());
        store1.save(repo4.clone());
        Assert.assertEquals(4, store1.getRepositories().size());
        ImmutableSet<RepositoryInfo> expected = ImmutableSet.of(repo1, repo2, repo3, repo4);
        Assert.assertEquals(expected, new java.util.HashSet(store1.getRepositories()));
        Assert.assertEquals(4, store2.getRepositories().size());
        Assert.assertEquals(expected, new java.util.HashSet(store2.getRepositories()));
    }

    /**
     * both {@link #store1()} and {@link #store2()} are in the cluster, repos changed on #1 get
     * reflected in #2
     */
    @Test
    public void verifyResourceEditEvents() {
        ConfigStore store1 = store1();
        ConfigStore store2 = store2();
        store1.save(repo1.clone());
        store1.save(repo2.clone());
        // preflight asserts
        ImmutableSet<RepositoryInfo> expected = ImmutableSet.of(repo1, repo2);
        Assert.assertEquals(expected, new java.util.HashSet(store1.getRepositories()));
        Assert.assertEquals(expected, new java.util.HashSet(store2.getRepositories()));
        URI uri1 = URI.create(((repo1.getLocation().toString()) + "-changed"));
        // make a change to #1
        repo1.setLocation(uri1);
        Assert.assertNotEquals(repo1, store1.get(repo1.getId()));
        Assert.assertNotEquals(repo1, store2.get(repo1.getId()));
        RepositoryManager repoManager2 = repoManager2();
        store1.save(repo1);
        Assert.assertEquals(repo1, store1.get(repo1.getId()));
        Assert.assertEquals(repo1, store2.get(repo1.getId()));
        Mockito.verify(repoManager2, Mockito.times(1)).invalidate(ArgumentMatchers.eq(repo1.getId()));
    }

    /**
     * both {@link #store1()} and {@link #store2()} are in the cluster, repos removed on #2 get
     * reflected in #1
     */
    @Test
    public void verifyResourceRemovedEvents() {
        ConfigStore store1 = store1();
        ConfigStore store2 = store2();
        store1.save(repo1.clone());
        store1.save(repo2.clone());
        store2.save(repo3.clone());
        store2.save(repo4.clone());
        // preflight asserts
        ImmutableSet<RepositoryInfo> expected = ImmutableSet.of(repo1, repo2, repo3, repo4);
        Assert.assertEquals(expected, new java.util.HashSet(store1.getRepositories()));
        Assert.assertEquals(expected, new java.util.HashSet(store2.getRepositories()));
        RepositoryManager repoManager1 = repoManager1();
        RepositoryManager repoManager2 = repoManager2();
        repoManager1.delete(repo1.getId());
        Mockito.verify(repoManager2, Mockito.times(1)).invalidate(ArgumentMatchers.eq(repo1.getId()));
        repoManager2.delete(repo2.getId());
        Mockito.verify(repoManager1, Mockito.times(1)).invalidate(ArgumentMatchers.eq(repo2.getId()));
    }
}

