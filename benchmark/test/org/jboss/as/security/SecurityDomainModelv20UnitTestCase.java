/**
 * Copyright 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.security;


import SecurityExtension.SUBSYSTEM_NAME;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.junit.Test;


/**
 * Security subsystem tests for the version 2.0 of the subsystem schema.
 *
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */
public class SecurityDomainModelv20UnitTestCase extends AbstractSubsystemBaseTest {
    public SecurityDomainModelv20UnitTestCase() {
        super(SUBSYSTEM_NAME, new SecurityExtension());
    }

    private static String oldConfig;

    @Test
    @Override
    public void testSchemaOfSubsystemTemplates() throws Exception {
        super.testSchemaOfSubsystemTemplates();
    }
}

