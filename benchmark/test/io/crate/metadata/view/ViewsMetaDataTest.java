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
package io.crate.metadata.view;


import DeprecationHandler.THROW_UNSUPPORTED_OPERATION;
import JsonXContent.jsonXContent;
import ToXContent.EMPTY_PARAMS;
import io.crate.test.integration.CrateUnitTest;
import java.io.IOException;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.hamcrest.core.IsNull;
import org.junit.Test;


public class ViewsMetaDataTest extends CrateUnitTest {
    @Test
    public void testViewsMetaDataStreaming() throws IOException {
        ViewsMetaData views = ViewsMetaDataTest.createMetaData();
        BytesStreamOutput out = new BytesStreamOutput();
        views.writeTo(out);
        StreamInput in = out.bytes().streamInput();
        ViewsMetaData views2 = new ViewsMetaData(in);
        assertEquals(views, views2);
    }

    @Test
    public void testViewsMetaDataToXContent() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        // reflects the logic used to process custom metadata in the cluster state
        builder.startObject();
        ViewsMetaData views = ViewsMetaDataTest.createMetaData();
        views.toXContent(builder, EMPTY_PARAMS);
        builder.endObject();
        XContentParser parser = jsonXContent.createParser(xContentRegistry(), THROW_UNSUPPORTED_OPERATION, BytesReference.toBytes(BytesReference.bytes(builder)));
        parser.nextToken();// start object

        ViewsMetaData views2 = ViewsMetaData.fromXContent(parser);
        assertEquals(views, views2);
        // a metadata custom must consume the surrounded END_OBJECT token, no token must be left
        assertThat(parser.nextToken(), IsNull.nullValue());
    }
}

