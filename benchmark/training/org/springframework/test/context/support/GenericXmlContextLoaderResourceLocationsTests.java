/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.test.context.support;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextLoader;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;


/**
 * JUnit 4 based unit test which verifies proper
 * {@link ContextLoader#processLocations(Class, String...) processing} of
 * {@code resource locations} by a {@link GenericXmlContextLoader}
 * configured via {@link ContextConfiguration @ContextConfiguration}.
 * Specifically, this test addresses the issues raised in <a
 * href="http://opensource.atlassian.com/projects/spring/browse/SPR-3949"
 * target="_blank">SPR-3949</a>:
 * <em>ContextConfiguration annotation should accept not only classpath resources</em>.
 *
 * @author Sam Brannen
 * @since 2.5
 */
@RunWith(Parameterized.class)
public class GenericXmlContextLoaderResourceLocationsTests {
    private static final Log logger = LogFactory.getLog(GenericXmlContextLoaderResourceLocationsTests.class);

    protected final Class<?> testClass;

    protected final String[] expectedLocations;

    public GenericXmlContextLoaderResourceLocationsTests(final String testClassName, final String[] expectedLocations) throws Exception {
        this.testClass = ClassUtils.forName((((getClass().getName()) + "$1") + testClassName), getClass().getClassLoader());
        this.expectedLocations = expectedLocations;
    }

    @Test
    public void assertContextConfigurationLocations() throws Exception {
        final ContextConfiguration contextConfig = this.testClass.getAnnotation(ContextConfiguration.class);
        final ContextLoader contextLoader = new GenericXmlContextLoader();
        final String[] configuredLocations = ((String[]) (AnnotationUtils.getValue(contextConfig)));
        final String[] processedLocations = contextLoader.processLocations(this.testClass, configuredLocations);
        if (GenericXmlContextLoaderResourceLocationsTests.logger.isDebugEnabled()) {
            GenericXmlContextLoaderResourceLocationsTests.logger.debug("----------------------------------------------------------------------");
            GenericXmlContextLoaderResourceLocationsTests.logger.debug(("Configured locations: " + (ObjectUtils.nullSafeToString(configuredLocations))));
            GenericXmlContextLoaderResourceLocationsTests.logger.debug(("Expected   locations: " + (ObjectUtils.nullSafeToString(this.expectedLocations))));
            GenericXmlContextLoaderResourceLocationsTests.logger.debug(("Processed  locations: " + (ObjectUtils.nullSafeToString(processedLocations))));
        }
        Assert.assertArrayEquals((("Verifying locations for test [" + (this.testClass)) + "]."), this.expectedLocations, processedLocations);
    }
}

