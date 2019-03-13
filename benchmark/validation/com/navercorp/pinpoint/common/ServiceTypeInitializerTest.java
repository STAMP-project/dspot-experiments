/**
 * Copyright 2014 NAVER Corp.
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


import AnnotationKey.ARGS0;
import ServiceType.USER;
import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.common.trace.AnnotationKeyFactory;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;
import com.navercorp.pinpoint.common.trace.TraceMetadataLoader;
import com.navercorp.pinpoint.common.trace.TraceMetadataProvider;
import com.navercorp.pinpoint.common.trace.TraceMetadataSetupContext;
import com.navercorp.pinpoint.common.util.StaticFieldLookUp;
import com.navercorp.pinpoint.common.util.logger.CommonLoggerFactory;
import com.navercorp.pinpoint.common.util.logger.StdoutCommonLoggerFactory;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author Jongho Moon <jongho.moon@navercorp.com>
 */
public class ServiceTypeInitializerTest {
    private CommonLoggerFactory loggerFactory = StdoutCommonLoggerFactory.INSTANCE;

    private static final ServiceType[] TEST_TYPES = new ServiceType[]{ ServiceTypeFactory.of(1209, "FOR_UNIT_TEST", "UNDEFINED", TERMINAL, RECORD_STATISTICS, INCLUDE_DESTINATION_ID) };

    private static final AnnotationKey[] TEST_KEYS = new AnnotationKey[]{ AnnotationKeyFactory.of(1209, "Duplicate-API") };

    private static final ServiceType[] DUPLICATED_CODE_WITH_DEFAULT_TYPE = new ServiceType[]{ ServiceTypeFactory.of(USER.getCode(), "FOR_UNIT_TEST", "UNDEFINED", TERMINAL, RECORD_STATISTICS, INCLUDE_DESTINATION_ID) };

    private static final ServiceType[] DUPLICATED_NAME_WITH_DEFAULT_TYPE = new ServiceType[]{ ServiceTypeFactory.of(1209, USER.getName(), "UNDEFINED", TERMINAL, RECORD_STATISTICS, INCLUDE_DESTINATION_ID) };

    private static final AnnotationKey[] DUPLICATED_CODE_WITH_DEFAULT_KEY = new AnnotationKey[]{ AnnotationKeyFactory.of(ARGS0.getCode(), "API") };

    @Test
    public void testWithPlugins() {
        List<TraceMetadataProvider> typeProviders = Arrays.<TraceMetadataProvider>asList(new ServiceTypeInitializerTest.TestProvider(ServiceTypeInitializerTest.TEST_TYPES, ServiceTypeInitializerTest.TEST_KEYS));
        TraceMetadataLoaderService typeLoaderService = new DefaultTraceMetadataLoaderService(typeProviders, loggerFactory);
        AnnotationKeyRegistryService annotationKeyRegistryService = new DefaultAnnotationKeyRegistryService(typeLoaderService, loggerFactory);
        StaticFieldLookUp<AnnotationKey> lookUp = new StaticFieldLookUp<AnnotationKey>(AnnotationKey.class, AnnotationKey.class);
        verifyAnnotationKeys(lookUp.lookup(), annotationKeyRegistryService);
        verifyAnnotationKeys(Arrays.asList(ServiceTypeInitializerTest.TEST_KEYS), annotationKeyRegistryService);
    }

    @Test(expected = RuntimeException.class)
    public void testDuplicated() {
        List<TraceMetadataProvider> providers = Arrays.<TraceMetadataProvider>asList(new ServiceTypeInitializerTest.TestProvider(ServiceTypeInitializerTest.TEST_TYPES, ServiceTypeInitializerTest.TEST_KEYS), new ServiceTypeInitializerTest.TestProvider(new ServiceType[0], ServiceTypeInitializerTest.TEST_KEYS));
        TraceMetadataLoader loader = new TraceMetadataLoader(StdoutCommonLoggerFactory.INSTANCE);
        loader.load(providers);
    }

    @Test(expected = RuntimeException.class)
    public void testDuplicated2() {
        List<TraceMetadataProvider> providers = Arrays.<TraceMetadataProvider>asList(new ServiceTypeInitializerTest.TestProvider(ServiceTypeInitializerTest.TEST_TYPES, ServiceTypeInitializerTest.TEST_KEYS), new ServiceTypeInitializerTest.TestProvider(ServiceTypeInitializerTest.TEST_TYPES, new AnnotationKey[0]));
        TraceMetadataLoader loader = new TraceMetadataLoader(StdoutCommonLoggerFactory.INSTANCE);
        loader.load(providers);
    }

    @Test(expected = RuntimeException.class)
    public void testDuplicated3() {
        List<TraceMetadataProvider> providers = Arrays.<TraceMetadataProvider>asList(new ServiceTypeInitializerTest.TestProvider(ServiceTypeInitializerTest.TEST_TYPES, ServiceTypeInitializerTest.TEST_KEYS), new ServiceTypeInitializerTest.TestProvider(ServiceTypeInitializerTest.TEST_TYPES, new AnnotationKey[0]));
        TraceMetadataLoader loader = new TraceMetadataLoader(StdoutCommonLoggerFactory.INSTANCE);
        loader.load(providers);
    }

    @Test(expected = RuntimeException.class)
    public void testDuplicatedWithDefault() {
        List<TraceMetadataProvider> providers = Arrays.<TraceMetadataProvider>asList(new ServiceTypeInitializerTest.TestProvider(ServiceTypeInitializerTest.DUPLICATED_CODE_WITH_DEFAULT_TYPE, ServiceTypeInitializerTest.TEST_KEYS));
        TraceMetadataLoaderService loaderService = new DefaultTraceMetadataLoaderService(providers, loggerFactory);
        ServiceTypeRegistryService serviceTypeRegistryService = new DefaultServiceTypeRegistryService(loaderService, loggerFactory);
    }

    @Test(expected = RuntimeException.class)
    public void testDuplicatedWithDefault2() {
        List<TraceMetadataProvider> providers = Arrays.<TraceMetadataProvider>asList(new ServiceTypeInitializerTest.TestProvider(ServiceTypeInitializerTest.DUPLICATED_NAME_WITH_DEFAULT_TYPE, ServiceTypeInitializerTest.TEST_KEYS));
        TraceMetadataLoaderService loaderService = new DefaultTraceMetadataLoaderService(providers, loggerFactory);
        ServiceTypeRegistryService serviceTypeRegistryService = new DefaultServiceTypeRegistryService(loaderService, loggerFactory);
    }

    @Test(expected = RuntimeException.class)
    public void testDuplicatedWithDefault3() {
        List<TraceMetadataProvider> providers = Arrays.<TraceMetadataProvider>asList(new ServiceTypeInitializerTest.TestProvider(ServiceTypeInitializerTest.TEST_TYPES, ServiceTypeInitializerTest.DUPLICATED_CODE_WITH_DEFAULT_KEY));
        TraceMetadataLoaderService loaderService = new DefaultTraceMetadataLoaderService(providers, loggerFactory);
        AnnotationKeyRegistryService annotationKeyRegistryService = new DefaultAnnotationKeyRegistryService(loaderService, loggerFactory);
    }

    private static class TestProvider implements TraceMetadataProvider {
        private final ServiceType[] serviceTypes;

        private final AnnotationKey[] annotationKeys;

        public TestProvider(ServiceType[] serviceTypes, AnnotationKey[] annotationKeys) {
            this.serviceTypes = serviceTypes;
            this.annotationKeys = annotationKeys;
        }

        @Override
        public void setup(TraceMetadataSetupContext context) {
            for (ServiceType type : serviceTypes) {
                context.addServiceType(type);
            }
            for (AnnotationKey key : annotationKeys) {
                context.addAnnotationKey(key);
            }
        }
    }
}

