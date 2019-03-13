/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.cf.taste.impl.recommender;


import GenericItemSimilarity.ItemItemSimilarity;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.mahout.cf.taste.impl.TasteTestCase;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.CandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.MostSimilarItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.common.MahoutTestCase;
import org.easymock.EasyMock;
import org.junit.Test;


/**
 * <p>Tests {@link GenericItemBasedRecommender}.</p>
 */
public final class GenericItemBasedRecommenderTest extends TasteTestCase {
    @Test
    public void testRecommender() throws Exception {
        Recommender recommender = GenericItemBasedRecommenderTest.buildRecommender();
        List<RecommendedItem> recommended = recommender.recommend(1, 1);
        assertNotNull(recommended);
        assertEquals(1, recommended.size());
        RecommendedItem firstRecommended = recommended.get(0);
        assertEquals(2, firstRecommended.getItemID());
        assertEquals(0.1F, firstRecommended.getValue(), MahoutTestCase.EPSILON);
        recommender.refresh(null);
        recommended = recommender.recommend(1, 1);
        firstRecommended = recommended.get(0);
        assertEquals(2, firstRecommended.getItemID());
        assertEquals(0.1F, firstRecommended.getValue(), MahoutTestCase.EPSILON);
    }

    @Test
    public void testHowMany() throws Exception {
        DataModel dataModel = TasteTestCase.getDataModel(new long[]{ 1, 2, 3, 4, 5 }, new Double[][]{ new Double[]{ 0.1, 0.2 }, new Double[]{ 0.2, 0.3, 0.3, 0.6 }, new Double[]{ 0.4, 0.4, 0.5, 0.9 }, new Double[]{ 0.1, 0.4, 0.5, 0.8, 0.9, 1.0 }, new Double[]{ 0.2, 0.3, 0.6, 0.7, 0.1, 0.2 } });
        Collection<GenericItemSimilarity.ItemItemSimilarity> similarities = Lists.newArrayList();
        for (int i = 0; i < 6; i++) {
            for (int j = i + 1; j < 6; j++) {
                similarities.add(new GenericItemSimilarity.ItemItemSimilarity(i, j, (1.0 / ((1.0 + i) + j))));
            }
        }
        ItemSimilarity similarity = new GenericItemSimilarity(similarities);
        Recommender recommender = new GenericItemBasedRecommender(dataModel, similarity);
        List<RecommendedItem> fewRecommended = recommender.recommend(1, 2);
        List<RecommendedItem> moreRecommended = recommender.recommend(1, 4);
        for (int i = 0; i < (fewRecommended.size()); i++) {
            assertEquals(fewRecommended.get(i).getItemID(), moreRecommended.get(i).getItemID());
        }
        recommender.refresh(null);
        for (int i = 0; i < (fewRecommended.size()); i++) {
            assertEquals(fewRecommended.get(i).getItemID(), moreRecommended.get(i).getItemID());
        }
    }

    @Test
    public void testRescorer() throws Exception {
        DataModel dataModel = TasteTestCase.getDataModel(new long[]{ 1, 2, 3 }, new Double[][]{ new Double[]{ 0.1, 0.2 }, new Double[]{ 0.2, 0.3, 0.3, 0.6 }, new Double[]{ 0.4, 0.4, 0.5, 0.9 } });
        Collection<GenericItemSimilarity.ItemItemSimilarity> similarities = Lists.newArrayList();
        similarities.add(new GenericItemSimilarity.ItemItemSimilarity(0, 1, 1.0));
        similarities.add(new GenericItemSimilarity.ItemItemSimilarity(0, 2, 0.5));
        similarities.add(new GenericItemSimilarity.ItemItemSimilarity(0, 3, 0.2));
        similarities.add(new GenericItemSimilarity.ItemItemSimilarity(1, 2, 0.7));
        similarities.add(new GenericItemSimilarity.ItemItemSimilarity(1, 3, 0.5));
        similarities.add(new GenericItemSimilarity.ItemItemSimilarity(2, 3, 0.9));
        ItemSimilarity similarity = new GenericItemSimilarity(similarities);
        Recommender recommender = new GenericItemBasedRecommender(dataModel, similarity);
        List<RecommendedItem> originalRecommended = recommender.recommend(1, 2);
        List<RecommendedItem> rescoredRecommended = recommender.recommend(1, 2, new ReversingRescorer<Long>());
        assertNotNull(originalRecommended);
        assertNotNull(rescoredRecommended);
        assertEquals(2, originalRecommended.size());
        assertEquals(2, rescoredRecommended.size());
        assertEquals(originalRecommended.get(0).getItemID(), rescoredRecommended.get(1).getItemID());
        assertEquals(originalRecommended.get(1).getItemID(), rescoredRecommended.get(0).getItemID());
    }

    @Test
    public void testIncludeKnownItems() throws Exception {
        DataModel dataModel = TasteTestCase.getDataModel(new long[]{ 1, 2, 3 }, new Double[][]{ new Double[]{ 0.1, 0.2 }, new Double[]{ 0.2, 0.3, 0.3, 0.6 }, new Double[]{ 0.4, 0.4, 0.5, 0.9 } });
        Collection<GenericItemSimilarity.ItemItemSimilarity> similarities = Lists.newArrayList();
        similarities.add(new GenericItemSimilarity.ItemItemSimilarity(0, 1, 0.8));
        similarities.add(new GenericItemSimilarity.ItemItemSimilarity(0, 2, 0.5));
        similarities.add(new GenericItemSimilarity.ItemItemSimilarity(0, 3, 0.2));
        similarities.add(new GenericItemSimilarity.ItemItemSimilarity(1, 2, 0.7));
        similarities.add(new GenericItemSimilarity.ItemItemSimilarity(1, 3, 0.5));
        similarities.add(new GenericItemSimilarity.ItemItemSimilarity(2, 3, 0.9));
        ItemSimilarity similarity = new GenericItemSimilarity(similarities);
        Recommender recommender = new GenericItemBasedRecommender(dataModel, similarity);
        List<RecommendedItem> originalRecommended = recommender.recommend(1, 4, null, true);
        List<RecommendedItem> rescoredRecommended = recommender.recommend(1, 4, new ReversingRescorer<Long>(), true);
        assertNotNull(originalRecommended);
        assertNotNull(rescoredRecommended);
        assertEquals(4, originalRecommended.size());
        assertEquals(4, rescoredRecommended.size());
        assertEquals(originalRecommended.get(0).getItemID(), rescoredRecommended.get(3).getItemID());
        assertEquals(originalRecommended.get(3).getItemID(), rescoredRecommended.get(0).getItemID());
    }

    @Test
    public void testEstimatePref() throws Exception {
        Recommender recommender = GenericItemBasedRecommenderTest.buildRecommender();
        assertEquals(0.1F, recommender.estimatePreference(1, 2), MahoutTestCase.EPSILON);
    }

    /**
     * Contributed test case that verifies fix for bug
     * <a href="http://sourceforge.net/tracker/index.php?func=detail&amp;aid=1396128&amp;group_id=138771&amp;atid=741665">
     * 1396128</a>.
     */
    @Test
    public void testBestRating() throws Exception {
        Recommender recommender = GenericItemBasedRecommenderTest.buildRecommender();
        List<RecommendedItem> recommended = recommender.recommend(1, 1);
        assertNotNull(recommended);
        assertEquals(1, recommended.size());
        RecommendedItem firstRecommended = recommended.get(0);
        // item one should be recommended because it has a greater rating/score
        assertEquals(2, firstRecommended.getItemID());
        assertEquals(0.1F, firstRecommended.getValue(), MahoutTestCase.EPSILON);
    }

    @Test
    public void testMostSimilar() throws Exception {
        ItemBasedRecommender recommender = GenericItemBasedRecommenderTest.buildRecommender();
        List<RecommendedItem> similar = recommender.mostSimilarItems(0, 2);
        assertNotNull(similar);
        assertEquals(2, similar.size());
        RecommendedItem first = similar.get(0);
        RecommendedItem second = similar.get(1);
        assertEquals(1, first.getItemID());
        assertEquals(1.0F, first.getValue(), MahoutTestCase.EPSILON);
        assertEquals(2, second.getItemID());
        assertEquals(0.5F, second.getValue(), MahoutTestCase.EPSILON);
    }

    @Test
    public void testMostSimilarToMultiple() throws Exception {
        ItemBasedRecommender recommender = GenericItemBasedRecommenderTest.buildRecommender2();
        List<RecommendedItem> similar = recommender.mostSimilarItems(new long[]{ 0, 1 }, 2);
        assertNotNull(similar);
        assertEquals(2, similar.size());
        RecommendedItem first = similar.get(0);
        RecommendedItem second = similar.get(1);
        assertEquals(2, first.getItemID());
        assertEquals(0.85F, first.getValue(), MahoutTestCase.EPSILON);
        assertEquals(3, second.getItemID());
        assertEquals((-0.3F), second.getValue(), MahoutTestCase.EPSILON);
    }

    @Test
    public void testMostSimilarToMultipleExcludeIfNotSimilarToAll() throws Exception {
        ItemBasedRecommender recommender = GenericItemBasedRecommenderTest.buildRecommender2();
        List<RecommendedItem> similar = recommender.mostSimilarItems(new long[]{ 3, 4 }, 2);
        assertNotNull(similar);
        assertEquals(1, similar.size());
        RecommendedItem first = similar.get(0);
        assertEquals(0, first.getItemID());
        assertEquals(0.2F, first.getValue(), MahoutTestCase.EPSILON);
    }

    @Test
    public void testMostSimilarToMultipleDontExcludeIfNotSimilarToAll() throws Exception {
        ItemBasedRecommender recommender = GenericItemBasedRecommenderTest.buildRecommender2();
        List<RecommendedItem> similar = recommender.mostSimilarItems(new long[]{ 1, 2, 4 }, 10, false);
        assertNotNull(similar);
        assertEquals(2, similar.size());
        RecommendedItem first = similar.get(0);
        RecommendedItem second = similar.get(1);
        assertEquals(0, first.getItemID());
        assertEquals(0.93333334F, first.getValue(), MahoutTestCase.EPSILON);
        assertEquals(3, second.getItemID());
        assertEquals((-0.2F), second.getValue(), MahoutTestCase.EPSILON);
    }

    @Test
    public void testRecommendedBecause() throws Exception {
        ItemBasedRecommender recommender = GenericItemBasedRecommenderTest.buildRecommender2();
        List<RecommendedItem> recommendedBecause = recommender.recommendedBecause(1, 4, 3);
        assertNotNull(recommendedBecause);
        assertEquals(3, recommendedBecause.size());
        RecommendedItem first = recommendedBecause.get(0);
        RecommendedItem second = recommendedBecause.get(1);
        RecommendedItem third = recommendedBecause.get(2);
        assertEquals(2, first.getItemID());
        assertEquals(0.99F, first.getValue(), MahoutTestCase.EPSILON);
        assertEquals(3, second.getItemID());
        assertEquals(0.4F, second.getValue(), MahoutTestCase.EPSILON);
        assertEquals(0, third.getItemID());
        assertEquals(0.2F, third.getValue(), MahoutTestCase.EPSILON);
    }

    /**
     * we're making sure that a user's preferences are fetched only once from the {@link DataModel} for one call to
     * {@link GenericItemBasedRecommender#recommend(long, int)}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void preferencesFetchedOnlyOnce() throws Exception {
        DataModel dataModel = EasyMock.createMock(DataModel.class);
        ItemSimilarity itemSimilarity = EasyMock.createMock(ItemSimilarity.class);
        CandidateItemsStrategy candidateItemsStrategy = EasyMock.createMock(CandidateItemsStrategy.class);
        MostSimilarItemsCandidateItemsStrategy mostSimilarItemsCandidateItemsStrategy = EasyMock.createMock(MostSimilarItemsCandidateItemsStrategy.class);
        PreferenceArray preferencesFromUser = new org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray(Arrays.asList(new GenericPreference(1L, 1L, 5.0F), new GenericPreference(1L, 2L, 4.0F)));
        EasyMock.expect(dataModel.getMinPreference()).andReturn(Float.NaN);
        EasyMock.expect(dataModel.getMaxPreference()).andReturn(Float.NaN);
        EasyMock.expect(dataModel.getPreferencesFromUser(1L)).andReturn(preferencesFromUser);
        EasyMock.expect(candidateItemsStrategy.getCandidateItems(1L, preferencesFromUser, dataModel, false)).andReturn(new FastIDSet(new long[]{ 3L, 4L }));
        EasyMock.expect(itemSimilarity.itemSimilarities(3L, preferencesFromUser.getIDs())).andReturn(new double[]{ 0.5, 0.3 });
        EasyMock.expect(itemSimilarity.itemSimilarities(4L, preferencesFromUser.getIDs())).andReturn(new double[]{ 0.4, 0.1 });
        EasyMock.replay(dataModel, itemSimilarity, candidateItemsStrategy, mostSimilarItemsCandidateItemsStrategy);
        Recommender recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity, candidateItemsStrategy, mostSimilarItemsCandidateItemsStrategy);
        recommender.recommend(1L, 3);
        EasyMock.verify(dataModel, itemSimilarity, candidateItemsStrategy, mostSimilarItemsCandidateItemsStrategy);
    }
}

