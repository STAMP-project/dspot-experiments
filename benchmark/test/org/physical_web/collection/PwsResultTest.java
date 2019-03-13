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


import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 * PwsResult unit test class.
 */
public class PwsResultTest {
    private static final String URL1 = "http://example.com";

    private static final String URL2 = "http://physical-web.org";

    private static final String TITLE1 = "title1";

    private static final String TITLE2 = "title2";

    private static final String DESCRIPTION1 = "description1";

    private static final String DESCRIPTION2 = "description2";

    private static final String ICON_URL1 = "http://example.com/favicon.ico";

    private static final String ICON_URL2 = "http://physical-web.org/favicon.ico";

    private static final String GROUP_ID1 = "group1";

    private static final String GROUP_ID2 = "group2";

    private static final String KEY1 = "key1";

    private static final String VALUE1 = "value1";

    private PwsResult mPwsResult1 = null;

    private JSONObject jsonObject1 = null;

    @Test
    public void constructorCreatesProperObject() {
        Assert.assertEquals(mPwsResult1.getRequestUrl(), PwsResultTest.URL1);
        Assert.assertEquals(mPwsResult1.getSiteUrl(), PwsResultTest.URL1);
        Assert.assertEquals(mPwsResult1.getTitle(), PwsResultTest.TITLE1);
        Assert.assertEquals(mPwsResult1.getDescription(), PwsResultTest.DESCRIPTION1);
        Assert.assertEquals(mPwsResult1.getIconUrl(), PwsResultTest.ICON_URL1);
        Assert.assertEquals(mPwsResult1.getGroupId(), PwsResultTest.GROUP_ID1);
    }

    @Test
    public void jsonSerializeWorks() {
        JSONAssert.assertEquals(mPwsResult1.jsonSerialize(), jsonObject1, true);
    }

    @Test
    public void jsonDeserializeWorks() throws PhysicalWebCollectionException {
        PwsResult pwsResult = PwsResult.jsonDeserialize(jsonObject1);
        Assert.assertNotNull(pwsResult);
        Assert.assertEquals(pwsResult.getRequestUrl(), PwsResultTest.URL1);
        Assert.assertEquals(pwsResult.getSiteUrl(), PwsResultTest.URL1);
        Assert.assertEquals(pwsResult.getTitle(), PwsResultTest.TITLE1);
        Assert.assertEquals(pwsResult.getDescription(), PwsResultTest.DESCRIPTION1);
        Assert.assertEquals(pwsResult.getIconUrl(), PwsResultTest.ICON_URL1);
        Assert.assertEquals(pwsResult.getGroupId(), PwsResultTest.GROUP_ID1);
    }

    @Test
    public void jsonSerializeAndDeserializePreservesNullValues() throws Exception {
        PwsResult pwsResult = new PwsResult(PwsResultTest.URL1, PwsResultTest.URL1);
        pwsResult = PwsResult.jsonDeserialize(pwsResult.jsonSerialize());
        Assert.assertNull(pwsResult.getTitle());
        Assert.assertNull(pwsResult.getDescription());
        Assert.assertNull(pwsResult.getIconUrl());
        Assert.assertNull(pwsResult.getGroupId());
    }
}

