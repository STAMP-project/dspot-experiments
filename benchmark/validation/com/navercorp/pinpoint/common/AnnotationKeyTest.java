/**
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.common;


import AnnotationKey.API;
import AnnotationKey.ARGS0;
import AnnotationKey.ARGS5;
import AnnotationKey.ARGSN;
import AnnotationKey.CACHE_ARGS0;
import com.navercorp.pinpoint.common.service.AnnotationKeyRegistryService;
import com.navercorp.pinpoint.common.service.TraceMetadataLoaderService;
import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.common.trace.TraceMetadataProvider;
import com.navercorp.pinpoint.common.util.AnnotationKeyUtils;
import com.navercorp.pinpoint.common.util.logger.CommonLoggerFactory;
import com.navercorp.pinpoint.common.util.logger.StdoutCommonLoggerFactory;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author emeroad
 */
public class AnnotationKeyTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void getCode() {
        CommonLoggerFactory loggerFactory = StdoutCommonLoggerFactory.INSTANCE;
        TraceMetadataLoaderService typeLoaderService = new com.navercorp.pinpoint.common.service.DefaultTraceMetadataLoaderService(Collections.<TraceMetadataProvider>emptyList(), loggerFactory);
        AnnotationKeyRegistryService annotationKeyRegistryService = new com.navercorp.pinpoint.common.service.DefaultAnnotationKeyRegistryService(typeLoaderService, loggerFactory);
        AnnotationKey annotationKey = annotationKeyRegistryService.findAnnotationKey(API.getCode());
        Assert.assertEquals(annotationKey, API);
    }

    @Test
    public void isArgsKey() {
        Assert.assertTrue(AnnotationKeyUtils.isArgsKey(ARGS0.getCode()));
        Assert.assertTrue(AnnotationKeyUtils.isArgsKey(ARGSN.getCode()));
        Assert.assertTrue(AnnotationKeyUtils.isArgsKey(ARGS5.getCode()));
        Assert.assertFalse(AnnotationKeyUtils.isArgsKey(((ARGS0.getCode()) + 1)));
        Assert.assertFalse(AnnotationKeyUtils.isArgsKey(((ARGSN.getCode()) - 1)));
        Assert.assertFalse(AnnotationKeyUtils.isArgsKey(Integer.MAX_VALUE));
        Assert.assertFalse(AnnotationKeyUtils.isArgsKey(Integer.MIN_VALUE));
    }

    @Test
    public void isCachedArgsToArgs() {
        int i = AnnotationKeyUtils.cachedArgsToArgs(CACHE_ARGS0.getCode());
        Assert.assertEquals(i, ARGS0.getCode());
    }

    @Test
    public void testValueOf() {
        CommonLoggerFactory loggerFactory = StdoutCommonLoggerFactory.INSTANCE;
        TraceMetadataLoaderService typeLoaderService = new com.navercorp.pinpoint.common.service.DefaultTraceMetadataLoaderService(Collections.<TraceMetadataProvider>emptyList(), loggerFactory);
        AnnotationKeyRegistryService annotationKeyRegistryService = new com.navercorp.pinpoint.common.service.DefaultAnnotationKeyRegistryService(typeLoaderService, loggerFactory);
        annotationKeyRegistryService.findAnnotationKeyByName(ARGS0.getName());
        AnnotationKey valueof = annotationKeyRegistryService.findAnnotationKeyByName(ARGS0.getName());
        Assert.assertSame(ARGS0, valueof);
    }
}

