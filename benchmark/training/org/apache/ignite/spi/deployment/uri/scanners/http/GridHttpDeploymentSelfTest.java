/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.spi.deployment.uri.scanners.http;


import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.spi.deployment.uri.GridUriDeploymentAbstractSelfTest;
import org.apache.ignite.spi.deployment.uri.UriDeploymentSpi;
import org.apache.ignite.testframework.config.GridTestProperties;
import org.apache.ignite.testframework.junits.spi.GridSpiTest;
import org.eclipse.jetty.server.Server;
import org.junit.Test;


/**
 * Test http scanner.
 */
@GridSpiTest(spi = UriDeploymentSpi.class, group = "Deployment SPI")
public class GridHttpDeploymentSelfTest extends GridUriDeploymentAbstractSelfTest {
    /**
     * Frequency
     */
    private static final int FREQ = 5000;

    /**
     *
     */
    public static final String LIBS_GAR = "libs-file.gar";

    /**
     *
     */
    public static final String CLASSES_GAR = "classes-file.gar";

    /**
     *
     */
    public static final String ALL_GAR = "file.gar";

    /**
     * Gar-file which contains libs.
     */
    public static final String LIBS_GAR_FILE_PATH = U.resolveIgnitePath(GridTestProperties.getProperty("ant.urideployment.gar.libs-file")).getPath();

    /**
     * Gar-file which contains classes (cannot be used without libs).
     */
    public static final String CLASSES_GAR_FILE_PATH = U.resolveIgnitePath(GridTestProperties.getProperty("ant.urideployment.gar.classes-file")).getPath();

    /**
     * Gar-file which caontains both libs and classes.
     */
    public static final String ALL_GAR_FILE_PATH = U.resolveIgnitePath(GridTestProperties.getProperty("ant.urideployment.gar.file")).getPath();

    /**
     * Jetty.
     */
    private static Server srv;

    /**
     * Resource base.
     */
    private static String rsrcBase;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeployUndeploy2Files() throws Exception {
        String taskName = "org.apache.ignite.spi.deployment.uri.tasks.GridUriDeploymentTestTask3";
        checkNoTask(taskName);
        try {
            copyToResourceBase(GridHttpDeploymentSelfTest.LIBS_GAR_FILE_PATH, GridHttpDeploymentSelfTest.LIBS_GAR);
            copyToResourceBase(GridHttpDeploymentSelfTest.CLASSES_GAR_FILE_PATH, GridHttpDeploymentSelfTest.CLASSES_GAR);
            waitForTask(taskName, true, ((GridHttpDeploymentSelfTest.FREQ) + 3000));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deleteFromResourceBase(GridHttpDeploymentSelfTest.LIBS_GAR);
            deleteFromResourceBase(GridHttpDeploymentSelfTest.CLASSES_GAR);
            waitForTask(taskName, false, ((GridHttpDeploymentSelfTest.FREQ) + 3000));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSameContantFiles() throws Exception {
        String taskName = "org.apache.ignite.spi.deployment.uri.tasks.GridUriDeploymentTestTask3";
        checkNoTask(taskName);
        try {
            copyToResourceBase(GridHttpDeploymentSelfTest.ALL_GAR_FILE_PATH, GridHttpDeploymentSelfTest.ALL_GAR);
            waitForTask(taskName, true, ((GridHttpDeploymentSelfTest.FREQ) + 3000));
            copyToResourceBase(GridHttpDeploymentSelfTest.ALL_GAR_FILE_PATH, "file-copy.gar");
            waitForTask(taskName, true, ((GridHttpDeploymentSelfTest.FREQ) + 3000));
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            deleteFromResourceBase(GridHttpDeploymentSelfTest.ALL_GAR);
            deleteFromResourceBase("file-copy.gar");
            waitForTask(taskName, false, ((GridHttpDeploymentSelfTest.FREQ) + 3000));
        }
    }
}

