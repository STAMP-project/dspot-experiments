/**
 * Copyright 2016 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.keycloak.test.config.migration;


import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Test;


/**
 * Compare outputs from jboss-cli read-resource operations.  This compare the total
 * configuration of all subsystems to make sure that the version in master
 * matches the migrated version.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class ConfigMigrationTest {
    private static final File TARGET_DIR = new File("./target");

    private final Logger log = Logger.getLogger(ConfigMigrationTest.class);

    private final Deque<String> nav = new LinkedList<>();

    @Test
    public void testStandalone() throws IOException {
        compareConfigs("master-standalone.txt", "migrated-standalone.txt");
    }

    @Test
    public void testStandaloneHA() throws IOException {
        compareConfigs("master-standalone-ha.txt", "migrated-standalone-ha.txt");
    }

    @Test
    public void testDomain() throws IOException {
        compareConfigs("master-domain-standalone.txt", "migrated-domain-standalone.txt");
        compareConfigs("master-domain-clustered.txt", "migrated-domain-clustered.txt");
        compareConfigs("master-domain-core-service.txt", "migrated-domain-core-service.txt");
        compareConfigs("master-domain-extension.txt", "migrated-domain-extension.txt");
        // compareConfigs("master-domain-interface.txt", "migrated-domain-interface.txt");
    }

    private static final Comparator<ModelNode> nodeStringComparator = ( n1, n2) -> {
        // ascending order
        return n1.toString().compareTo(n2.toString());
    };
}

