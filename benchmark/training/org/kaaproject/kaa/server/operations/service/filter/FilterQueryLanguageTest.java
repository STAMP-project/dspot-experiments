/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.server.operations.service.filter;


import ExtendedEndpointProfile.SCHEMA;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.ProfileFilterDto;

import static DefaultFilterEvaluator.CLIENT_PROFILE_VARIABLE_NAME;
import static DefaultFilterEvaluator.EP_KEYHASH_VARIABLE_NAME;
import static DefaultFilterEvaluator.SERVER_PROFILE_VARIABLE_NAME;


public class FilterQueryLanguageTest {
    private static final String PROFILE_SCHEMA = SCHEMA..toString();

    private static final String SERVER_PROFILE_SCHEMA = SCHEMA..toString();

    private static final String EXTENDED_ENDPOINT_PROFILE_SOURCE_PATH = "operations/service/filter/extendedEndpointProfile.json";

    private static final String endpointKeyHash = "QMnPRTdUL+byZ/MTyyRX5MWe02Q=";

    private static final String endpointKeyHash2 = "AMBPRTEUL+byZ/MdTyRX5bWeT6b=";

    private static EndpointProfileDto profile;

    @Test
    public void testEpKeyHashMatch() {
        String filterBody = (((((("{'" + (FilterQueryLanguageTest.endpointKeyHash)) + "','") + (FilterQueryLanguageTest.endpointKeyHash2)) + "'}.contains(") + "#") + (EP_KEYHASH_VARIABLE_NAME)) + ")";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testEpKeyHashMatchFailure() {
        String filterBody = (((("{'" + (FilterQueryLanguageTest.endpointKeyHash2)) + "'}.contains(") + "#") + (EP_KEYHASH_VARIABLE_NAME)) + ")";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.FALSE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithPrimitiveFieldAccess() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "simpleField == 'SIMPLE_FIELD'";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryNoServerProfileBody() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "simpleField == 'SIMPLE_FIELD'";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        String profileBody = FilterQueryLanguageTest.profile.getServerProfileBody();
        FilterQueryLanguageTest.profile.setServerProfileBody(null);
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
        FilterQueryLanguageTest.profile.setServerProfileBody(profileBody);
    }

    @Test
    public void testQueryWithArrayOfPrimitiveFieldAccess() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "arraySimpleField[1] == 'VALUE2'";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
        filterBody = ("#" + (SERVER_PROFILE_VARIABLE_NAME)) + ".arraySimpleField[1] == 'VALUE2'";
        filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        filterDto.setServerProfileSchemaVersion(1);
        filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithArraySizeChecking() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "arraySimpleField.size() == 2";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithArraySizeCheckingDeprecatedProfileFilter() {
        String filterBody = "arraySimpleField.size() == 2";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithRecordPrimitiveFieldAccess() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "recordField.otherSimpleField == 123";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithRecordMapSizeChecking() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "recordField.otherMapSimpleField.size() == 2";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithArrayRecordPrimitiveFieldAccess() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "arrayRecordField[1].otherSimpleField == 789";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithArrayRecordMapFieldAccess() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "arrayRecordField[1].otherMapSimpleField[KEY5] == 5";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithMapPrimitiveFieldAccess() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "mapSimpleField[KEY8] == 8";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithMapRecordPrimitiveFieldAccess() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "mapRecordField[SOME_KEY2].otherSimpleField == 654";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithMapRecordMapPrimitiveFieldAccess() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "mapRecordField[SOME_KEY2].otherMapSimpleField[KEY12] == 12";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithPojoMethodCall() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "mapRecordField[SOME_KEY2].otherMapSimpleField[new java.lang.String('KEY13').toString()] == 13";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithPojoFieldAccess() {
        String filterBody = ("new " + (FilterQueryLanguageTest.TestPojo.class.getName())) + "(123).field == 123";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    @Test
    public void testQueryWithNullChecking() {
        String filterBody = (("#" + (CLIENT_PROFILE_VARIABLE_NAME)) + ".") + "nullableRecordField == null";
        ProfileFilterDto filterDto = new ProfileFilterDto();
        filterDto.setBody(filterBody);
        filterDto.setEndpointProfileSchemaVersion(1);
        FilterEvaluator filter = new DefaultFilterEvaluator();
        filter.init(FilterQueryLanguageTest.profile, FilterQueryLanguageTest.PROFILE_SCHEMA, FilterQueryLanguageTest.SERVER_PROFILE_SCHEMA);
        Assert.assertEquals(Boolean.TRUE, filter.matches(filterDto));
    }

    private static class TestPojo {
        private int field;

        public TestPojo(Integer field) {
            this.field = field;
        }

        public int getField() {
            return field;
        }

        public void setField(int field) {
            this.field = field;
        }
    }
}

