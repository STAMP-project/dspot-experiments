/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.utils;


import HttpHeaderConstants.CACHE_CONTROL;
import HttpHeaderConstants.ETAG;
import HttpHeaderConstants.IF_MODIFIED_SINCE;
import HttpHeaderConstants.IF_NONE_MATCH;
import HttpHeaderConstants.LAST_MODIFIED;
import NinjaConstant.HTTP_CACHE_CONTROL;
import NinjaConstant.HTTP_CACHE_CONTROL_DEFAULT;
import NinjaConstant.HTTP_USE_ETAG;
import NinjaConstant.HTTP_USE_ETAG_DEFAULT;
import Result.SC_304_NOT_MODIFIED;
import java.util.Optional;
import ninja.Context;
import ninja.Result;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HttpCacheToolkitImplTest {
    @Mock
    NinjaProperties ninjaProperties;

    @Mock
    Result result;

    @Mock
    Context context;

    @Test
    public void testIsModified() {
        HttpCacheToolkit httpCacheToolkit = new HttpCacheToolkitImpl(ninjaProperties);
        // test etag support:
        Mockito.when(context.getHeader(IF_NONE_MATCH)).thenReturn("etag_xyz");
        // same etag => not modified
        Assert.assertFalse(httpCacheToolkit.isModified(Optional.of("etag_xyz"), Optional.of(0L), context));
        // new etag => modified
        Assert.assertTrue(httpCacheToolkit.isModified(Optional.of("etag_xyz_modified"), Optional.of(0L), context));
        // remove etag to test modified timestamp caching:
        Mockito.when(context.getHeader(IF_NONE_MATCH)).thenReturn(null);
        // => no if modified since request => null
        Mockito.when(context.getHeader(IF_MODIFIED_SINCE)).thenReturn(null);
        Assert.assertTrue(httpCacheToolkit.isModified(Optional.of("etag_xyz_modified"), Optional.of(0L), context));
        // => older timestamp => modified
        Mockito.when(context.getHeader(IF_MODIFIED_SINCE)).thenReturn("Thu, 01 Jan 1970 00:00:00 GMT");
        Assert.assertTrue(httpCacheToolkit.isModified(Optional.of("etag_xyz_modified"), Optional.of(1000L), context));
        // => same timestamp => not modified
        Mockito.when(context.getHeader(IF_MODIFIED_SINCE)).thenReturn("Thu, 01 Jan 1970 00:00:00 GMT");
        Assert.assertFalse(httpCacheToolkit.isModified(Optional.of("etag_xyz_modified"), Optional.of(0L), context));
        // => newer timestamp => not modified
        Mockito.when(context.getHeader(IF_MODIFIED_SINCE)).thenReturn("Thu, 01 Jan 1971 00:00:00 GMT");
        Assert.assertFalse(httpCacheToolkit.isModified(Optional.of("etag_xyz_modified"), Optional.of(0L), context));
        // => strange timestamp => modified
        Mockito.when(context.getHeader(IF_MODIFIED_SINCE)).thenReturn("STRANGE_TIMESTAMP");
        Assert.assertTrue(httpCacheToolkit.isModified(Optional.of("etag_xyz_modified"), Optional.of(0L), context));
    }

    @Test
    public void testAddETag() {
        HttpCacheToolkit httpCacheToolkit = new HttpCacheToolkitImpl(ninjaProperties);
        // //////////////////////////////////////////////
        // test Cache-Control header
        // //////////////////////////////////////////////
        // check Cache header:
        // if not in production => no cache:
        Mockito.when(ninjaProperties.isProd()).thenReturn(false);
        httpCacheToolkit.addEtag(context, result, 0L);
        Mockito.verify(result).addHeader(CACHE_CONTROL, "no-cache");
        // in production => make sure cache header is set accordingly:
        Mockito.when(ninjaProperties.isProd()).thenReturn(true);
        // set regular header with request to http cache control constant:
        Mockito.reset(result);
        Mockito.when(ninjaProperties.getWithDefault(HTTP_CACHE_CONTROL, HTTP_CACHE_CONTROL_DEFAULT)).thenReturn("1234");
        httpCacheToolkit.addEtag(context, result, 0L);
        Mockito.verify(result).addHeader(CACHE_CONTROL, "max-age=1234");
        // if cache time = 0 => set to no-cache:
        Mockito.reset(result);
        Mockito.when(ninjaProperties.getWithDefault(HTTP_CACHE_CONTROL, HTTP_CACHE_CONTROL_DEFAULT)).thenReturn("0");
        httpCacheToolkit.addEtag(context, result, 0L);
        Mockito.verify(result).addHeader(CACHE_CONTROL, "no-cache");
        // //////////////////////////////////////////////
        // Test Add etag header
        // //////////////////////////////////////////////
        // do not add etag when not configured:
        Mockito.reset(result);
        Mockito.when(ninjaProperties.getBooleanWithDefault(HTTP_USE_ETAG, HTTP_USE_ETAG_DEFAULT)).thenReturn(false);
        httpCacheToolkit.addEtag(context, result, 0L);
        // not in prod => no-cache
        Mockito.verify(result).addHeader(CACHE_CONTROL, "no-cache");
        // IMPORTANT: etag not added
        Mockito.verify(result, Mockito.never()).addHeader(ETAG, ArgumentMatchers.eq(ArgumentMatchers.anyString()));
        // add etag when configured:
        Mockito.reset(result);
        Mockito.when(ninjaProperties.getBooleanWithDefault(HTTP_USE_ETAG, HTTP_USE_ETAG_DEFAULT)).thenReturn(true);
        httpCacheToolkit.addEtag(context, result, 1234L);
        // not in prod => no-cache
        Mockito.verify(result).addHeader(CACHE_CONTROL, "no-cache");
        // IMPORTANT: etag added
        Mockito.verify(result).addHeader(ETAG, "\"1234\"");
        // //////////////////////////////////////////////
        // Test isModified 304 setting in result
        // //////////////////////////////////////////////
        // test lastmodified is added when etags match:
        Mockito.when(context.getMethod()).thenReturn("GET");
        Mockito.when(context.getHeader(IF_NONE_MATCH)).thenReturn("\"1234\"");
        Mockito.reset(result);
        httpCacheToolkit.addEtag(context, result, 1234L);
        Mockito.verify(result).status(SC_304_NOT_MODIFIED);
        // test lastmodified not added when stuff does not match
        // => but Last-Modified header is added
        Mockito.when(context.getMethod()).thenReturn("GET");
        Mockito.when(context.getHeader(IF_NONE_MATCH)).thenReturn("\"12___34\"");
        Mockito.reset(result);
        httpCacheToolkit.addEtag(context, result, 1234L);
        Mockito.verify(result, Mockito.never()).status(SC_304_NOT_MODIFIED);
        Mockito.verify(result).addHeader(LAST_MODIFIED, DateUtil.formatForHttpHeader(1234L));
    }
}

