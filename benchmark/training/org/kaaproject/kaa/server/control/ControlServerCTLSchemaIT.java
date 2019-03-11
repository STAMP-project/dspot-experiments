/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.server.control;


import CTLSchemaExportMethod.FLAT;
import CTLSchemaScopeDto.TENANT;
import HttpStatus.CONFLICT;
import HttpStatus.NOT_FOUND;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.avro.ui.shared.FqnVersion;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.ctl.CTLSchemaDto;
import org.kaaproject.kaa.common.dto.ctl.CtlSchemaMetaInfoDto;
import org.kaaproject.kaa.common.dto.file.FileData;
import org.springframework.web.client.HttpClientErrorException;


/**
 *
 *
 * @author Bohdan Khablenko
 * @since v0.8.0
 */
public class ControlServerCTLSchemaIT extends AbstractTestControlServer {
    /**
     * Saves a CTL schema to the database.
     */
    @Test
    public void saveCTLSchemaTest() throws Exception {
        this.loginKaaAdmin();
        CTLSchemaDto beta = client.saveCTLSchemaWithAppToken(AbstractTestControlServer.getResourceAsString(AbstractTestControlServer.TEST_CTL_SCHEMA_BETA), null, null);
        Assert.assertNotNull(beta.getId());
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        CTLSchemaDto alpha = client.saveCTLSchemaWithAppToken(AbstractTestControlServer.getResourceAsString(AbstractTestControlServer.TEST_CTL_SCHEMA_ALPHA), tenantDeveloperDto.getTenantId(), null);
        Assert.assertNotNull(alpha.getId());
    }

    /**
     * Saves a CTL schema with incorrect version to the database.
     */
    @Test(expected = HttpClientErrorException.class)
    public void saveCTLSchemaWithWrongVerionTest() throws Exception {
        this.loginKaaAdmin();
        CTLSchemaDto gamma = client.saveCTLSchemaWithAppToken(AbstractTestControlServer.getResourceAsString(AbstractTestControlServer.TEST_CTL_SCHEMA_GAMMA), null, null);
    }

    /**
     * Saves a CTL schema with application token with incorrect version to the database.
     */
    @Test(expected = HttpClientErrorException.class)
    public void saveCTLSchemaWithAppTokenAndWrongVerionTest() throws Exception {
        this.loginKaaAdmin();
        CTLSchemaDto gamma = client.saveCTLSchemaWithAppToken(AbstractTestControlServer.getResourceAsString(AbstractTestControlServer.TEST_CTL_SCHEMA_GAMMA), null, null);
    }

    /**
     * Tries to save a CTL schema with a dependency that is missing from the
     * database. (This action is prohibited).
     */
    @Test
    public void saveCTLSchemaWithMissingDependenciesTest() throws Exception {
        // Declare a dependency
        String dependencyFqn = ((AbstractTestControlServer.CTL_DEFAULT_NAMESPACE) + ".") + (this.ctlRandomFieldType());
        Integer dependencyVersion = 1;
        final Set<FqnVersion> dependencies = new HashSet<>();
        dependencies.add(new FqnVersion(dependencyFqn, dependencyVersion));
        // Map a CTL schema field name to its type
        final Map<String, String> fields = new HashMap<>();
        fields.put(this.ctlRandomFieldName(), dependencyFqn);
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        this.checkBadRequest(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                createCTLSchema(ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, dependencies, fields);
            }
        });
    }

    /**
     * Removes a CTL schema from the database by its fully qualified name and
     * version number.
     */
    @Test
    public void deleteCTLSchemaByFqnAndVersionTest() throws Exception {
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        final CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, null, null);
        client.deleteCTLSchemaByFqnVersionTenantIdAndApplicationToken(saved.getMetaInfo().getFqn(), saved.getVersion(), tenantDeveloperDto.getTenantId(), null);
        this.checkNotFound(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.getCTLSchemaByFqnVersionTenantIdAndApplicationToken(saved.getMetaInfo().getFqn(), saved.getVersion(), tenantDeveloperDto.getTenantId(), null);
            }
        });
    }

    /**
     * Removes a CTL schema from the database by its fully qualified name and
     * version number.
     */
    @Test
    public void deleteCTLSchemaByAppTokenByFqnAndVersionTest() throws Exception {
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        final CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, null, null);
        client.deleteCTLSchemaByFqnVersionTenantIdAndApplicationToken(saved.getMetaInfo().getFqn(), saved.getVersion(), tenantDeveloperDto.getTenantId(), null);
        this.checkNotFound(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.getCTLSchemaByFqnVersionTenantIdAndApplicationToken(saved.getMetaInfo().getFqn(), saved.getVersion(), tenantDeveloperDto.getTenantId(), null);
            }
        });
    }

    /**
     * Tries to remove a CTL schema that is referenced by something else. (This
     * action is prohibited).
     */
    @Test
    public void deleteCTLSchemaWithDependents() throws Exception {
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        final CTLSchemaDto dependency = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, null, null);
        Set<FqnVersion> dependencies = new HashSet<>();
        dependencies.add(new FqnVersion(dependency.getMetaInfo().getFqn(), dependency.getVersion()));
        Map<String, String> fields = new HashMap<>();
        fields.put(this.ctlRandomFieldName(), dependency.getMetaInfo().getFqn());
        this.createCTLSchema(this.ctlRandomFieldName(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, dependencies, fields);
        this.checkBadRequest(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.deleteCTLSchemaByFqnVersionTenantIdAndApplicationToken(dependency.getMetaInfo().getFqn(), dependency.getVersion(), tenantDeveloperDto.getTenantId(), null);
            }
        });
    }

    /**
     * Tries to remove a CTL schema that is referenced by something else. (This
     * action is prohibited).
     */
    @Test
    public void deleteCTLSchemaByAppTokenWithDependents() throws Exception {
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        final CTLSchemaDto dependency = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, null, null);
        Set<FqnVersion> dependencies = new HashSet<>();
        dependencies.add(new FqnVersion(dependency.getMetaInfo().getFqn(), dependency.getVersion()));
        Map<String, String> fields = new HashMap<>();
        fields.put(this.ctlRandomFieldName(), dependency.getMetaInfo().getFqn());
        this.createCTLSchema(this.ctlRandomFieldName(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, dependencies, fields);
        this.checkBadRequest(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.deleteCTLSchemaByFqnVersionTenantIdAndApplicationToken(dependency.getMetaInfo().getFqn(), dependency.getVersion(), tenantDeveloperDto.getTenantId(), null);
            }
        });
    }

    /**
     * Tries to remove a system CTL schema by its fully qualified name and
     * version number as a regular user. (This action is prohibited).
     */
    @Test
    public void deleteSystemCTLSchemaByFqnAndVersionTest() throws Exception {
        this.loginKaaAdmin();
        final CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, null, null, null, null);
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        this.checkForbidden(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.deleteCTLSchemaByFqnVersionTenantIdAndApplicationToken(saved.getMetaInfo().getFqn(), saved.getVersion(), null, null);
            }
        });
    }

    /**
     * Tries to remove a system CTL schema by its fully qualified name and
     * version number as a regular user. (This action is prohibited).
     */
    @Test
    public void deleteSystemCTLSchemaByAppTokenByFqnAndVersionTest() throws Exception {
        this.loginKaaAdmin();
        final CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, null, null, null, null);
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        this.checkForbidden(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.deleteCTLSchemaByFqnVersionTenantIdAndApplicationToken(saved.getMetaInfo().getFqn(), saved.getVersion(), null, null);
            }
        });
    }

    /**
     * Retrieves a CTL schema by its fully qualified name and version number.
     */
    @Test
    public void getCTLSchemaByFqnAndVersionTest() throws Exception {
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, null, null);
        CTLSchemaDto loaded = client.getCTLSchemaByFqnVersionTenantIdAndApplicationToken(saved.getMetaInfo().getFqn(), saved.getVersion(), tenantDeveloperDto.getTenantId(), null);
        Assert.assertNotNull(loaded);
        Assert.assertEquals(saved, loaded);
    }

    /**
     * Retrieves a CTL schema by its fully qualified name and version number.
     */
    @Test
    public void getCTLSchemaWithAppTokenByFqnAndVersionTest() throws Exception {
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, null, null);
        CTLSchemaDto loaded = client.getCTLSchemaByFqnVersionTenantIdAndApplicationToken(saved.getMetaInfo().getFqn(), saved.getVersion(), tenantDeveloperDto.getTenantId(), null);
        Assert.assertNotNull(loaded);
        Assert.assertEquals(saved, loaded);
    }

    /**
     * Retrieves a system CTL schema by its fully qualified name and version
     * number as a regular user.
     */
    @Test
    public void getSystemCTLSchemaByFqnAndVersionTest() throws Exception {
        this.loginKaaAdmin();
        CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, null, null, null, null);
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        CTLSchemaDto loaded = client.getCTLSchemaByFqnVersionTenantIdAndApplicationToken(saved.getMetaInfo().getFqn(), saved.getVersion(), null, null);
        Assert.assertNotNull(loaded);
        Assert.assertEquals(saved, loaded);
    }

    /**
     * Retrieves a system CTL schema by its fully qualified name and version
     * number as a regular user.
     */
    @Test
    public void getSystemCTLSchemaWithAppTokenByFqnAndVersionTest() throws Exception {
        this.loginKaaAdmin();
        CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, null, null, null, null);
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        CTLSchemaDto loaded = client.getCTLSchemaByFqnVersionTenantIdAndApplicationToken(saved.getMetaInfo().getFqn(), saved.getVersion(), null, null);
        Assert.assertNotNull(loaded);
        Assert.assertEquals(saved, loaded);
    }

    /**
     * Retrieves a CTL schema by its id.
     */
    @Test
    public void getCTLSchemaByIdTest() throws Exception {
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, null, null);
        CTLSchemaDto loaded = client.getCTLSchemaById(saved.getId());
        Assert.assertNotNull(loaded);
        Assert.assertEquals(saved, loaded);
    }

    @Test
    public void getCTLFlatSchemaByIdTest() throws Exception {
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, null, null);
        CTLSchemaDto loaded = client.getCTLSchemaById(saved.getId());
        String savedFlatSchema = ctlService.flatExportAsString(saved);
        String loadedFlatSchema = client.getFlatSchemaByCtlSchemaId(loaded.getId());
        Assert.assertNotNull(loaded);
        Assert.assertEquals(savedFlatSchema, loadedFlatSchema);
    }

    /**
     * Retrieves a CTL schema by its id.
     */
    @Test
    public void downloadCtlSchemaTest() throws Exception {
        ApplicationDto application = createApplication(tenantAdminDto);
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        String name = this.ctlRandomFieldType();
        CTLSchemaDto saved = this.createCTLSchema(name, AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), application.getApplicationToken(), null, null);
        FileData fd = client.downloadCtlSchemaByAppToken(client.getCTLSchemaById(saved.getId()), FLAT, application.getApplicationToken());
        Assert.assertNotNull(fd);
        Schema loaded = new Parser().parse(new String(fd.getFileData()));
        Assert.assertEquals(name, loaded.getName());
    }

    /**
     * Check existence of CTL schema with same fqn and another scope
     */
    @Test
    public void checkCTLFqnExistsTest() throws Exception {
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, null, null);
        String fqn = saved.getMetaInfo().getFqn();
        boolean result = client.checkFqnExistsWithAppToken(fqn, tenantDeveloperDto.getTenantId(), null);
        Assert.assertFalse(result);
        result = client.checkFqnExistsWithAppToken(fqn, null, null);
        Assert.assertFalse(result);
        ApplicationDto application1 = createApplication(tenantAdminDto);
        ApplicationDto application2 = createApplication(tenantAdminDto);
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        CTLSchemaDto schema1 = this.createCTLSchema("TestAppFqn1", AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), application1.getApplicationToken(), null, null);
        this.createCTLSchema("TestAppFqn1", AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), application2.getApplicationToken(), null, null);
        fqn = schema1.getMetaInfo().getFqn();
        result = client.checkFqnExistsWithAppToken(fqn, tenantDeveloperDto.getTenantId(), application1.getApplicationToken());
        Assert.assertTrue(result);
        result = client.checkFqnExistsWithAppToken(fqn, tenantDeveloperDto.getTenantId(), application2.getApplicationToken());
        Assert.assertTrue(result);
        result = client.checkFqnExistsWithAppToken(fqn, tenantDeveloperDto.getTenantId(), null);
        Assert.assertFalse(result);
    }

    @Test
    public void updateCTLSchemaScopeTest() throws Exception {
        ApplicationDto application = createApplication(tenantAdminDto);
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), application.getApplicationToken(), null, null);
        CtlSchemaMetaInfoDto metaInfo = saved.getMetaInfo();
        CtlSchemaMetaInfoDto updatedMetaInfo = client.promoteScopeToTenant(metaInfo.getApplicationId(), metaInfo.getFqn());
        Assert.assertNull(updatedMetaInfo.getApplicationId());
        Assert.assertNotNull(updatedMetaInfo.getTenantId());
        Assert.assertEquals(tenantDeveloperDto.getTenantId(), updatedMetaInfo.getTenantId());
        Assert.assertEquals(CTLSchemaScopeDto.TENANT, updatedMetaInfo.getScope());
    }

    @Test
    public void promoteScopeToTenantNotFoundTest() throws Exception {
        ApplicationDto application = createApplication(tenantAdminDto);
        this.loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), null, null, null);
        final CtlSchemaMetaInfoDto metaInfo2 = saved.getMetaInfo();
        Assert.assertNull(metaInfo2.getApplicationId());
        metaInfo2.setApplicationId(application.getId());
        this.checkRestErrorStatusCode(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.promoteScopeToTenant(metaInfo2.getApplicationId(), metaInfo2.getFqn());
            }
        }, NOT_FOUND);
    }

    @Test
    public void promoteScopeToTenantForbiddenTest() throws Exception {
        ApplicationDto application = createApplication(tenantAdminDto);
        this.loginTenantAdmin(AbstractTestControlServer.tenantAdminUser);
        CTLSchemaDto saved = this.createCTLSchema(this.ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantAdminDto.getTenantId(), null, null, null);
        final CtlSchemaMetaInfoDto metaInfo3 = saved.getMetaInfo();
        Assert.assertNull(metaInfo3.getApplicationId());
        metaInfo3.setApplicationId(application.getId());
        this.checkForbidden(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.promoteScopeToTenant(metaInfo3.getApplicationId(), metaInfo3.getFqn());
            }
        });
    }

    @Test
    public void promoteScopeToTenantWithDependenciesInAppScopeTest() throws Exception {
        ApplicationDto application = createApplication(tenantAdminDto);
        loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        CTLSchemaDto dep = createCTLSchema(ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), application.getApplicationToken(), null, null);
        String fqn = dep.getMetaInfo().getFqn();
        int version = dep.getVersion();
        Map<String, String> fields = ImmutableMap.of("test", fqn);
        Set<FqnVersion> deps = ImmutableSet.of(new FqnVersion(fqn, version));
        CTLSchemaDto schema = createCTLSchema(ctlRandomFieldType(), AbstractTestControlServer.CTL_DEFAULT_NAMESPACE, 1, tenantDeveloperDto.getTenantId(), application.getApplicationToken(), deps, fields);
        final CtlSchemaMetaInfoDto metaInfo = schema.getMetaInfo();
        checkRestErrorStatusCode(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.promoteScopeToTenant(metaInfo.getApplicationId(), metaInfo.getFqn());
            }
        }, CONFLICT);
    }
}

