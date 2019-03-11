/**
 * Copyright 2019 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.common.logging;


import MediaType.ANY_TEXT_TYPE;
import MediaType.JSON;
import MediaType.PLAIN_TEXT_UTF_8;
import MediaType.SOAP_XML_UTF_8;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.RequestContext;
import java.nio.charset.Charset;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ContentPreviewerFactoryTest {
    @Rule
    public MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    RequestContext ctx;

    private static final HttpHeaders textHeader = ContentPreviewerFactoryTest.headers(PLAIN_TEXT_UTF_8);

    private static final HttpHeaders jsonHeader = ContentPreviewerFactoryTest.headers(JSON);

    private static final HttpHeaders pXmlHeader = ContentPreviewerFactoryTest.headers(SOAP_XML_UTF_8);

    @Test
    public void testCreating() {
        assertThat(ContentPreviewerFactory.of(ContentPreviewerFactory.ofText(10))).isInstanceOf(TextualContentPreviewerFactory.class);
        assertThat(ContentPreviewerFactory.of(ContentPreviewerFactory.ofText(10), ContentPreviewerFactory.of(() -> ContentPreviewer.ofText(10), JSON))).isInstanceOf(CompositeContentPreviewerFactory.class);
        assertThat(ContentPreviewerFactory.of(ContentPreviewerFactory.of(() -> ContentPreviewer.ofText(10), JSON), ContentPreviewerFactory.of(() -> ContentPreviewer.ofText(1), "text/*"))).isInstanceOf(MappedContentPreviewerFactory.class);
    }

    @Test
    public void testOfText() {
        assertThat(ContentPreviewerFactory.ofText(10).get(ctx, ContentPreviewerFactoryTest.textHeader)).isInstanceOf(StringContentPreviewer.class);
        assertThat(ContentPreviewerFactory.ofText(10).get(ctx, ContentPreviewerFactoryTest.jsonHeader)).isInstanceOf(StringContentPreviewer.class);
        assertThat(ContentPreviewerFactory.ofText(10).get(ctx, ContentPreviewerFactoryTest.pXmlHeader)).isInstanceOf(StringContentPreviewer.class);
        // returns disabled when length == 0
        assertThat(ContentPreviewerFactory.ofText(0)).isSameAs(ContentPreviewerFactory.disabled());
        assertThat(ContentPreviewerFactory.ofText(0, Charset.defaultCharset(), "text/plain")).isSameAs(ContentPreviewerFactory.disabled());
    }

    @Test
    public void testComposite() {
        ContentPreviewerFactory factory = // shouldn't get those.
        ContentPreviewerFactory.of(ContentPreviewerFactory.ofText(20, Charset.defaultCharset(), "text/test"), ContentPreviewerFactory.ofText(10), ContentPreviewerFactory.ofText(30, Charset.defaultCharset(), "text/aaa"));
        assertThat(factory.get(ctx, ContentPreviewerFactoryTest.textHeader)).isInstanceOf(StringContentPreviewer.class);
        assertThat(length()).isEqualTo(10);
        assertThat(length()).isEqualTo(20);
        assertThat(length()).isEqualTo(10);
        // returns disabled if all components are null.
        assertThat(ContentPreviewerFactory.of(ContentPreviewerFactory.disabled(), ContentPreviewerFactory.disabled())).isEqualTo(ContentPreviewerFactory.disabled());
        // returns the left one if others are disabled.
        final ContentPreviewerFactory f = ContentPreviewerFactory.ofText(10);
        assertThat(ContentPreviewerFactory.of(ContentPreviewerFactory.disabled(), f)).isSameAs(f);
        assertThat(((CompositeContentPreviewerFactory) (ContentPreviewerFactory.of(factory, ContentPreviewerFactory.ofText(10)))).factoryList).hasSize(4);
    }

    @Test
    public void testMapped() {
        ContentPreviewerFactory factory = // shouldn't get those.
        ContentPreviewerFactory.of(ContentPreviewerFactory.ofText(10, Charset.defaultCharset(), JSON), ContentPreviewerFactory.ofText(20, Charset.defaultCharset(), ANY_TEXT_TYPE), ContentPreviewerFactory.ofText(30, Charset.defaultCharset(), JSON), ContentPreviewerFactory.ofText(40, Charset.defaultCharset(), JSON), ContentPreviewerFactory.ofText(50, Charset.defaultCharset(), JSON), ContentPreviewerFactory.ofText(60, Charset.defaultCharset(), JSON));
        assertThat(length()).isEqualTo(20);
        assertThat(length()).isEqualTo(10);
    }
}

