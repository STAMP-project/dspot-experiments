/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.metadata;


import DeprecationHandler.THROW_UNSUPPORTED_OPERATION;
import JsonXContent.jsonXContent;
import MetaData.FORMAT;
import UserDefinedFunctionsMetaDataTest.DUMMY_UDF_META_DATA;
import UsersMetaData.TYPE;
import XContentParser.Token.END_OBJECT;
import io.crate.metadata.view.ViewsMetaDataTest;
import java.io.IOException;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.xcontent.XContentParser;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CustomMetaDataTest {
    @Test
    public void testAllMetaDataXContentRoundtrip() throws IOException {
        MetaData metaData = MetaData.builder().putCustom(TYPE, new UsersMetaData(UserDefinitions.DUMMY_USERS)).putCustom(UserDefinedFunctionsMetaData.TYPE, DUMMY_UDF_META_DATA).putCustom(UsersPrivilegesMetaData.TYPE, UsersPrivilegesMetaDataTest.createMetaData()).putCustom(ViewsMetaData.TYPE, ViewsMetaDataTest.createMetaData()).generateClusterUuidIfNeeded().version(1L).build();
        String xContent = xContentFromMetaData(metaData);
        XContentParser parser = jsonXContent.createParser(getNamedXContentRegistry(), THROW_UNSUPPORTED_OPERATION, xContent);
        MetaData restoredMetaData = FORMAT.fromXContent(parser);
        boolean isEqual = MetaData.isGlobalStateEquals(restoredMetaData, metaData);
        if (!isEqual) {
            Assert.assertEquals("meta-data must be equal", xContent, xContentFromMetaData(restoredMetaData));
        }
        Assert.assertTrue(isEqual);
        Assert.assertThat(parser.currentToken(), Matchers.is(END_OBJECT));
    }
}

