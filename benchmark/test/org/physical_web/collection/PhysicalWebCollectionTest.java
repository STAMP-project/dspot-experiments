/**
 * Copyright 2015 Google Inc. All rights reserved.
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
package org.physical_web.collection;


import java.util.Comparator;
import java.util.List;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 * PhysicalWebCollection unit test class.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class PhysicalWebCollectionTest {
    private static final String ID1 = "id1";

    private static final String ID2 = "id2";

    private static final String ID3 = "id3";

    private static final String ID4 = "id4";

    private static final String ID5 = "id5";

    private static final String URL1 = "http://example.com";

    private static final String URL2 = "http://physical-web.org";

    private static final String URL3a = "http://example.com/#a";

    private static final String URL3b = "http://example.com/#b";

    private static final String TITLE1 = "title1";

    private static final String TITLE2 = "title2";

    private static final String DESCRIPTION1 = "description1";

    private static final String DESCRIPTION2 = "description2";

    private static final String ICON_URL1 = "http://example.com/favicon.ico";

    private static final String ICON_URL2 = "http://physical-web.org/favicon.ico";

    private static final String GROUP_ID1 = "group1";

    private static final String GROUP_ID2 = "group2";

    private static final byte[] ICON1 = new byte[]{ 16, 0 };

    private PhysicalWebCollection physicalWebCollection1;

    private JSONObject jsonObject1;

    private static Comparator<PwPair> testComparator = new Comparator<PwPair>() {
        @Override
        public int compare(PwPair lhs, PwPair rhs) {
            return lhs.getUrlDevice().getId().compareTo(rhs.getUrlDevice().getId());
        }
    };

    @Test
    public void getUrlDeviceByIdReturnsFoundUrlDevice() {
        UrlDevice urlDevice = physicalWebCollection1.getUrlDeviceById(PhysicalWebCollectionTest.ID1);
        Assert.assertEquals(urlDevice.getId(), PhysicalWebCollectionTest.ID1);
        Assert.assertEquals(urlDevice.getUrl(), PhysicalWebCollectionTest.URL1);
    }

    @Test
    public void getUrlDeviceByIdReturnsNullForMissingUrlDevice() {
        UrlDevice fetchedUrlDevice = physicalWebCollection1.getUrlDeviceById(PhysicalWebCollectionTest.ID2);
        Assert.assertNull(fetchedUrlDevice);
    }

    @Test
    public void getMetadataByRequestUrlReturnsFoundMetadata() {
        PwsResult pwsResult = physicalWebCollection1.getMetadataByBroadcastUrl(PhysicalWebCollectionTest.URL1);
        Assert.assertEquals(pwsResult.getRequestUrl(), PhysicalWebCollectionTest.URL1);
        Assert.assertEquals(pwsResult.getSiteUrl(), PhysicalWebCollectionTest.URL1);
        Assert.assertEquals(pwsResult.getGroupId(), PhysicalWebCollectionTest.GROUP_ID1);
    }

    @Test
    public void getMetadataByRequestUrlReturnsNullForMissingMetadata() {
        PwsResult pwsResult = physicalWebCollection1.getMetadataByBroadcastUrl(PhysicalWebCollectionTest.URL2);
        Assert.assertNull(pwsResult);
    }

    @Test
    public void jsonSerializeWorks() {
        JSONAssert.assertEquals(physicalWebCollection1.jsonSerialize(), jsonObject1, true);
    }

    @Test
    public void jsonDeserializeWorks() throws PhysicalWebCollectionException {
        PhysicalWebCollection physicalWebCollection = PhysicalWebCollection.jsonDeserialize(jsonObject1);
        UrlDevice urlDevice = physicalWebCollection.getUrlDeviceById(PhysicalWebCollectionTest.ID1);
        PwsResult pwsResult = physicalWebCollection.getMetadataByBroadcastUrl(PhysicalWebCollectionTest.URL1);
        Assert.assertNotNull(urlDevice);
        Assert.assertEquals(urlDevice.getId(), PhysicalWebCollectionTest.ID1);
        Assert.assertEquals(urlDevice.getUrl(), PhysicalWebCollectionTest.URL1);
        Assert.assertNotNull(pwsResult);
        Assert.assertEquals(pwsResult.getRequestUrl(), PhysicalWebCollectionTest.URL1);
        Assert.assertEquals(pwsResult.getSiteUrl(), PhysicalWebCollectionTest.URL1);
        Assert.assertEquals(pwsResult.getGroupId(), PhysicalWebCollectionTest.GROUP_ID1);
    }

    @Test
    public void getPwPairsSortedByRankWorks() {
        PhysicalWebCollection physicalWebCollection = new PhysicalWebCollection();
        PhysicalWebCollectionTest.addPair(physicalWebCollection, PhysicalWebCollectionTest.ID1, PhysicalWebCollectionTest.URL1, null);
        PhysicalWebCollectionTest.addPair(physicalWebCollection, PhysicalWebCollectionTest.ID2, PhysicalWebCollectionTest.URL2, null);
        PhysicalWebCollectionTest.addPair(physicalWebCollection, PhysicalWebCollectionTest.ID3, PhysicalWebCollectionTest.URL2, null);// Duplicate URL

        List<PwPair> pwPairs = physicalWebCollection.getPwPairsSortedByRank(PhysicalWebCollectionTest.testComparator);
        Assert.assertEquals(pwPairs.size(), 2);
        Assert.assertEquals(pwPairs.get(0).getUrlDevice().getId(), PhysicalWebCollectionTest.ID1);
        Assert.assertEquals(pwPairs.get(1).getUrlDevice().getId(), PhysicalWebCollectionTest.ID2);
    }

    @Test
    public void getGroupedPwPairsSortedByRankWorks() {
        PhysicalWebCollection physicalWebCollection = new PhysicalWebCollection();
        PhysicalWebCollectionTest.addPair(physicalWebCollection, PhysicalWebCollectionTest.ID1, PhysicalWebCollectionTest.URL1, PhysicalWebCollectionTest.GROUP_ID1);// Group 1

        PhysicalWebCollectionTest.addPair(physicalWebCollection, PhysicalWebCollectionTest.ID2, PhysicalWebCollectionTest.URL2, null);// Ungrouped

        PhysicalWebCollectionTest.addPair(physicalWebCollection, PhysicalWebCollectionTest.ID3, PhysicalWebCollectionTest.URL2, null);// Duplicate URL

        PhysicalWebCollectionTest.addPair(physicalWebCollection, PhysicalWebCollectionTest.ID4, PhysicalWebCollectionTest.URL3a, PhysicalWebCollectionTest.GROUP_ID2);// Group 2

        PhysicalWebCollectionTest.addPair(physicalWebCollection, PhysicalWebCollectionTest.ID5, PhysicalWebCollectionTest.URL3b, PhysicalWebCollectionTest.GROUP_ID2);// Also group 2

        List<PwPair> groupedPairs = physicalWebCollection.getGroupedPwPairsSortedByRank(PhysicalWebCollectionTest.testComparator);
        Assert.assertEquals(groupedPairs.size(), 3);
        Assert.assertEquals(groupedPairs.get(0).getPwsResult().getGroupId(), PhysicalWebCollectionTest.GROUP_ID1);
        Assert.assertEquals(groupedPairs.get(0).getUrlDevice().getId(), PhysicalWebCollectionTest.ID1);
        Assert.assertEquals(groupedPairs.get(1).getPwsResult().getGroupId(), null);
        Assert.assertEquals(groupedPairs.get(1).getUrlDevice().getId(), PhysicalWebCollectionTest.ID2);
        Assert.assertEquals(groupedPairs.get(2).getPwsResult().getGroupId(), PhysicalWebCollectionTest.GROUP_ID2);
        Assert.assertEquals(groupedPairs.get(2).getUrlDevice().getId(), PhysicalWebCollectionTest.ID4);
    }

    @Test
    public void getTopRankedPwPairByGroupIdWorks() {
        PhysicalWebCollection physicalWebCollection = new PhysicalWebCollection();
        PhysicalWebCollectionTest.addPair(physicalWebCollection, PhysicalWebCollectionTest.ID1, PhysicalWebCollectionTest.URL1, PhysicalWebCollectionTest.GROUP_ID1);// Group 1

        PhysicalWebCollectionTest.addPair(physicalWebCollection, PhysicalWebCollectionTest.ID2, PhysicalWebCollectionTest.URL2, PhysicalWebCollectionTest.GROUP_ID1);// Better rank

        PhysicalWebCollectionTest.addPair(physicalWebCollection, PhysicalWebCollectionTest.ID1, PhysicalWebCollectionTest.URL1, PhysicalWebCollectionTest.GROUP_ID2);// Group 2

        Assert.assertNull(physicalWebCollection.getTopRankedPwPairByGroupId("notagroup", PhysicalWebCollectionTest.testComparator));
        PwPair pwPair = physicalWebCollection.getTopRankedPwPairByGroupId(PhysicalWebCollectionTest.GROUP_ID1, PhysicalWebCollectionTest.testComparator);
        Assert.assertNotNull(pwPair);
        Assert.assertEquals(PhysicalWebCollectionTest.ID2, pwPair.getUrlDevice().getId());
    }
}

