/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache.lucene.internal;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.geode.cache.lucene.LuceneSerializer;
import org.apache.geode.cache.lucene.internal.filesystem.FileSystemStats;
import org.apache.geode.cache.lucene.internal.repository.IndexRepository;
import org.apache.geode.cache.lucene.internal.repository.IndexRepositoryImpl;
import org.apache.geode.internal.cache.BucketNotFoundException;
import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.DistributedRegion;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.PartitionRegionConfig;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.PartitionedRegion.RetryTimeKeeper;
import org.apache.geode.internal.cache.PartitionedRegionDataStore;
import org.apache.geode.internal.cache.PartitionedRegionHelper;
import org.apache.geode.internal.cache.execute.InternalRegionFunctionContext;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@Category({ LuceneTest.class })
public class PartitionedRepositoryManagerJUnitTest {
    protected PartitionedRegion userRegion;

    protected PartitionedRegion fileAndChunkRegion;

    protected LuceneSerializer serializer;

    protected PartitionedRegionDataStore userDataStore;

    protected PartitionedRegionDataStore fileDataStore;

    protected PartitionedRegionHelper prHelper;

    protected PartitionRegionConfig prConfig;

    protected DistributedRegion prRoot;

    protected Map<Integer, BucketRegion> fileAndChunkBuckets = new HashMap<Integer, BucketRegion>();

    protected Map<Integer, BucketRegion> dataBuckets = new HashMap<Integer, BucketRegion>();

    protected LuceneIndexStats indexStats;

    protected FileSystemStats fileSystemStats;

    protected LuceneIndexImpl indexForPR;

    protected PartitionedRepositoryManager repoManager;

    protected GemFireCacheImpl cache;

    private final Map<Integer, Boolean> isIndexAvailableMap = new HashMap<>();

    @Test
    public void getByKey() throws BucketNotFoundException {
        setUpMockBucket(0);
        setUpMockBucket(1);
        IndexRepositoryImpl repo0 = ((IndexRepositoryImpl) (repoManager.getRepository(userRegion, 0, null)));
        IndexRepositoryImpl repo1 = ((IndexRepositoryImpl) (repoManager.getRepository(userRegion, 1, null)));
        IndexRepositoryImpl repo113 = ((IndexRepositoryImpl) (repoManager.getRepository(userRegion, 113, null)));
        Assert.assertNotNull(repo0);
        Assert.assertNotNull(repo1);
        Assert.assertNotNull(repo113);
        Assert.assertEquals(repo0, repo113);
        Assert.assertNotEquals(repo0, repo1);
        checkRepository(repo0, 0);
        checkRepository(repo1, 1);
    }

    /**
     * Test what happens when a bucket is destroyed.
     */
    @Test
    public void destroyBucketShouldCreateNewIndexRepository() throws IOException, BucketNotFoundException {
        setUpMockBucket(0);
        IndexRepositoryImpl repo0 = ((IndexRepositoryImpl) (repoManager.getRepository(userRegion, 0, null)));
        Assert.assertNotNull(repo0);
        checkRepository(repo0, 0);
        BucketRegion fileBucket0 = fileAndChunkBuckets.get(0);
        BucketRegion dataBucket0 = dataBuckets.get(0);
        // Simulate rebalancing of a bucket by marking the old bucket is destroyed
        // and creating a new bucket
        Mockito.when(dataBucket0.isDestroyed()).thenReturn(true);
        setUpMockBucket(0);
        IndexRepositoryImpl newRepo0 = ((IndexRepositoryImpl) (repoManager.getRepository(userRegion, 0, null)));
        Assert.assertNotEquals(repo0, newRepo0);
        checkRepository(newRepo0, 0);
        Assert.assertTrue(repo0.isClosed());
        Assert.assertFalse(repo0.getWriter().isOpen());
    }

    /**
     * Test that we get the expected exception when a user bucket is missing
     */
    @Test(expected = BucketNotFoundException.class)
    public void getMissingBucketByKey() throws BucketNotFoundException {
        repoManager.getRepository(userRegion, 0, null);
    }

    @Test
    public void createMissingBucket() throws BucketNotFoundException {
        setUpMockBucket(0);
        Mockito.when(fileDataStore.getLocalBucketById(ArgumentMatchers.eq(0))).thenReturn(null);
        Mockito.when(fileAndChunkRegion.getOrCreateNodeForBucketWrite(ArgumentMatchers.eq(0), ((RetryTimeKeeper) (ArgumentMatchers.any())))).then(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Mockito.when(fileDataStore.getLocalBucketById(ArgumentMatchers.eq(0))).thenReturn(fileAndChunkBuckets.get(0));
                return null;
            }
        });
        Assert.assertNotNull(repoManager.getRepository(userRegion, 0, null));
    }

    @Test
    public void getByRegion() throws BucketNotFoundException {
        setUpMockBucket(0);
        setUpMockBucket(1);
        Mockito.when(indexForPR.isIndexAvailable(0)).thenReturn(true);
        Mockito.when(indexForPR.isIndexAvailable(1)).thenReturn(true);
        Set<Integer> buckets = new LinkedHashSet<Integer>(Arrays.asList(0, 1));
        InternalRegionFunctionContext ctx = Mockito.mock(InternalRegionFunctionContext.class);
        Mockito.when(ctx.getLocalBucketSet(ArgumentMatchers.any())).thenReturn(buckets);
        Collection<IndexRepository> repos = repoManager.getRepositories(ctx);
        Assert.assertEquals(2, repos.size());
        Iterator<IndexRepository> itr = repos.iterator();
        IndexRepositoryImpl repo0 = ((IndexRepositoryImpl) (itr.next()));
        IndexRepositoryImpl repo1 = ((IndexRepositoryImpl) (itr.next()));
        Assert.assertNotNull(repo0);
        Assert.assertNotNull(repo1);
        Assert.assertNotEquals(repo0, repo1);
        checkRepository(repo0, 0);
        checkRepository(repo1, 1);
    }

    /**
     * Test that we get the expected exception when a user bucket is missing
     */
    @Test(expected = BucketNotFoundException.class)
    public void getMissingBucketByRegion() throws BucketNotFoundException {
        setUpMockBucket(0);
        Mockito.when(indexForPR.isIndexAvailable(0)).thenReturn(true);
        Set<Integer> buckets = new LinkedHashSet<Integer>(Arrays.asList(0, 1));
        InternalRegionFunctionContext ctx = Mockito.mock(InternalRegionFunctionContext.class);
        Mockito.when(ctx.getLocalBucketSet(ArgumentMatchers.any())).thenReturn(buckets);
        repoManager.getRepositories(ctx);
    }

    /**
     * Test that we get the expected exception when a user bucket is not indexed yet
     */
    @Test(expected = LuceneIndexCreationInProgressException.class)
    public void luceneIndexCreationInProgressExceptionExpectedIfIndexIsNotYetIndexed() throws BucketNotFoundException {
        setUpMockBucket(0);
        Set<Integer> buckets = new LinkedHashSet<Integer>(Arrays.asList(0, 1));
        InternalRegionFunctionContext ctx = Mockito.mock(InternalRegionFunctionContext.class);
        Mockito.when(ctx.getLocalBucketSet(ArgumentMatchers.any())).thenReturn(buckets);
        repoManager.getRepositories(ctx);
    }

    @Test
    public void queryOnlyWhenIndexIsAvailable() throws Exception {
        setUpMockBucket(0);
        setUpMockBucket(1);
        Mockito.when(indexForPR.isIndexAvailable(0)).thenReturn(true);
        Mockito.when(indexForPR.isIndexAvailable(1)).thenReturn(true);
        Set<Integer> buckets = new LinkedHashSet<>(Arrays.asList(0, 1));
        InternalRegionFunctionContext ctx = Mockito.mock(InternalRegionFunctionContext.class);
        Mockito.when(ctx.getLocalBucketSet(ArgumentMatchers.any())).thenReturn(buckets);
        await().until(() -> {
            final Collection<IndexRepository> repositories = new HashSet<>();
            try {
                repositories.addAll(repoManager.getRepositories(ctx));
            } catch (BucketNotFoundException | LuceneIndexCreationInProgressException e) {
            }
            return (repositories.size()) == 2;
        });
        Iterator<IndexRepository> itr = repoManager.getRepositories(ctx).iterator();
        IndexRepositoryImpl repo0 = ((IndexRepositoryImpl) (itr.next()));
        IndexRepositoryImpl repo1 = ((IndexRepositoryImpl) (itr.next()));
        Assert.assertNotNull(repo0);
        Assert.assertNotNull(repo1);
        Assert.assertNotEquals(repo0, repo1);
        checkRepository(repo0, 0, 1);
        checkRepository(repo1, 0, 1);
    }
}

