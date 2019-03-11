/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.exportimport;


import DirExportProviderFactory.PROVIDER_ID;
import ExportImportConfig.ACTION_IMPORT;
import ExportImportConfig.DEFAULT_USERS_PER_FILE;
import java.io.File;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.exportimport.dir.DirExportProvider;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class ExportImportTest extends AbstractKeycloakTest {
    @Test
    public void testDirFullExportImport() throws Throwable {
        testingClient.testing().exportImport().setProvider(PROVIDER_ID);
        String targetDirPath = ((testingClient.testing().exportImport().getExportImportTestDirectory()) + (File.separator)) + "dirExport";
        DirExportProvider.recursiveDeleteDir(new File(targetDirPath));
        testingClient.testing().exportImport().setDir(targetDirPath);
        testingClient.testing().exportImport().setUsersPerFile(DEFAULT_USERS_PER_FILE);
        testFullExportImport();
        // There should be 6 files in target directory (3 realm, 3 user)
        Assert.assertEquals(6, new File(targetDirPath).listFiles().length);
    }

    @Test
    public void testDirRealmExportImport() throws Throwable {
        testingClient.testing().exportImport().setProvider(PROVIDER_ID);
        String targetDirPath = ((testingClient.testing().exportImport().getExportImportTestDirectory()) + (File.separator)) + "dirRealmExport";
        DirExportProvider.recursiveDeleteDir(new File(targetDirPath));
        testingClient.testing().exportImport().setDir(targetDirPath);
        testingClient.testing().exportImport().setUsersPerFile(3);
        testRealmExportImport();
        // There should be 3 files in target directory (1 realm, 4 user)
        File[] files = new File(targetDirPath).listFiles();
        Assert.assertEquals(5, files.length);
    }

    @Test
    public void testSingleFileFullExportImport() throws Throwable {
        testingClient.testing().exportImport().setProvider(SingleFileExportProviderFactory.PROVIDER_ID);
        String targetFilePath = ((testingClient.testing().exportImport().getExportImportTestDirectory()) + (File.separator)) + "singleFile-full.json";
        testingClient.testing().exportImport().setFile(targetFilePath);
        testFullExportImport();
    }

    @Test
    public void testSingleFileRealmExportImport() throws Throwable {
        testingClient.testing().exportImport().setProvider(SingleFileExportProviderFactory.PROVIDER_ID);
        String targetFilePath = ((testingClient.testing().exportImport().getExportImportTestDirectory()) + (File.separator)) + "singleFile-realm.json";
        testingClient.testing().exportImport().setFile(targetFilePath);
        testRealmExportImport();
    }

    @Test
    public void testSingleFileRealmWithoutBuiltinsImport() throws Throwable {
        // Remove test realm
        removeRealm("test-realm");
        // Set the realm, which doesn't have builtin clients/roles inside JSON
        testingClient.testing().exportImport().setProvider(SingleFileExportProviderFactory.PROVIDER_ID);
        URL url = ExportImportTest.class.getResource("/model/testrealm.json");
        String targetFilePath = new File(url.getFile()).getAbsolutePath();
        testingClient.testing().exportImport().setFile(targetFilePath);
        testingClient.testing().exportImport().setAction(ACTION_IMPORT);
        testingClient.testing().exportImport().runImport();
        RealmResource testRealmRealm = adminClient.realm("test-realm");
        ExportImportUtil.assertDataImportedInRealm(adminClient, testingClient, testRealmRealm.toRepresentation());
    }

    @Test
    public void testImportFromPartialExport() {
        // import a realm with clients without roles
        importRealmFromFile("/import/partial-import.json");
        org.keycloak.testsuite.Assert.assertTrue("Imported realm hasn't been found!", isRealmPresent("partial-import"));
        addTestRealmToTestRealmReps("partial-import");
        // import a realm with clients without roles
        importRealmFromFile("/import/import-without-roles.json");
        org.keycloak.testsuite.Assert.assertTrue("Imported realm hasn't been found!", isRealmPresent("import-without-roles"));
        addTestRealmToTestRealmReps("import-without-roles");
        // import a realm with roles without clients
        importRealmFromFile("/import/import-without-clients.json");
        org.keycloak.testsuite.Assert.assertTrue("Imported realm hasn't been found!", isRealmPresent("import-without-clients"));
        addTestRealmToTestRealmReps("import-without-clients");
    }
}

