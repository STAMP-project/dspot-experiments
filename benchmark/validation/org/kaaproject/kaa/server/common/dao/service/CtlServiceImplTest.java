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
package org.kaaproject.kaa.server.common.dao.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.HasId;
import org.kaaproject.kaa.common.dto.TenantDto;
import org.kaaproject.kaa.common.dto.ctl.CTLSchemaDto;
import org.kaaproject.kaa.common.dto.ctl.CtlSchemaMetaInfoDto;
import org.kaaproject.kaa.server.common.dao.AbstractTest;
import org.kaaproject.kaa.server.common.dao.exception.DatabaseProcessingException;


@Ignore("This test should be extended and initialized with proper context in each NoSQL submodule")
public class CtlServiceImplTest extends AbstractTest {
    private static final String TEST_CTL_SCHEMA_ALPHA = "dao/ctl/alpha.json";

    private static final String TEST_CTL_SCHEMA_ALPHA_FLAT = "dao/ctl/alphaFlat.json";

    private static final String TEST_CTL_SCHEMA_BETA = "dao/ctl/beta.json";

    private static final String TEST_CTL_SCHEMA_GAMMA = "dao/ctl/gamma.json";

    private static final String TEST_CTL_SCHEMA_FLAT = "dao/ctl/flatCtlExample.json";

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private TenantDto tenant;

    private ApplicationDto appDto;

    private ApplicationDto appDto2;

    private CTLSchemaDto firstSchema;

    private CTLSchemaDto secondSchema;

    private CTLSchemaDto thirdSchema;

    private CTLSchemaDto fourthSchema;

    private CTLSchemaDto mainSchema;

    private CTLSchemaDto defaultSystemSchema;

    private CTLSchemaDto systemSchema;

    private CTLSchemaDto tenantSchema;

    private CTLSchemaDto tenantSchema2;

    private CTLSchemaDto appSchema;

    private CTLSchemaDto app2Schema;

    private CTLSchemaDto appSchema2;

    private CTLSchemaDto appSchema3;

    private CTLSchemaDto alpha;

    private CTLSchemaDto beta;

    private CTLSchemaDto gamma;

    @Test
    public void testSaveFlatCtlSchema() throws IOException {
        CTLSchemaDto testCtl = new CTLSchemaDto();
        CtlSchemaMetaInfoDto ctlMetaInfo = new CtlSchemaMetaInfoDto("org.kaaproject.kaa.NoDependenciesCtlExample", tenant.getId());
        testCtl.setMetaInfo(ctlMetaInfo);
        testCtl.setVersion(1);
        testCtl.setBody(readSchemaFileAsString(CtlServiceImplTest.TEST_CTL_SCHEMA_FLAT));
        testCtl = ctlService.saveCtlSchema(testCtl);
        CTLSchemaDto foundCtl = ctlService.findCtlSchemaById(testCtl.getId());
        String defaultRecord = foundCtl.getDefaultRecord();
        Assert.assertNotNull(foundCtl);
        Assert.assertTrue(defaultRecord.contains("\"bytesField\":\"\""));
        Assert.assertTrue(defaultRecord.contains("\"booleanField\":false"));
        Assert.assertTrue(defaultRecord.contains("\"intField\":0"));
        Assert.assertTrue(defaultRecord.contains("\"longField\":0"));
        Assert.assertTrue(defaultRecord.contains("\"floatField\":0.0"));
        Assert.assertTrue(defaultRecord.contains("\"doubleField\":0.0"));
        Assert.assertTrue(defaultRecord.contains("\"stringField\":\"\""));
        Assert.assertTrue(defaultRecord.contains("\"enumField\":\"SPADES\""));
        Assert.assertTrue(defaultRecord.contains("\"arrayField\":[]"));
        Assert.assertTrue(defaultRecord.contains("\"recordsArrayField\":[]"));
        Assert.assertTrue(defaultRecord.contains("\"recordField\":{\"bytesFieldInRecord\":\"\",\"booleanFieldInRecord\":false,\"intFieldInRecord\":0,\"floatFieldInRecord\":0.0}"));
    }

    @Test
    public void testRemoveCTLSchemaByFqnAndVerAndTenantIdAndApplicationId() {
        String schemaId = tenantSchema.getId();
        ctlService.removeCtlSchemaByFqnAndVerAndTenantIdAndApplicationId(tenantSchema.getMetaInfo().getFqn(), tenantSchema.getVersion(), tenantSchema.getMetaInfo().getTenantId(), tenantSchema.getMetaInfo().getApplicationId());
        Assert.assertNull(ctlService.findCtlSchemaById(schemaId));
    }

    @Test
    public void testRemoveCTLSchemaByFqnAndVerAndWithoutTenantId() {
        String schemaId = systemSchema.getId();
        ctlService.removeCtlSchemaByFqnAndVerAndTenantIdAndApplicationId(systemSchema.getMetaInfo().getFqn(), systemSchema.getVersion(), systemSchema.getMetaInfo().getTenantId(), systemSchema.getMetaInfo().getApplicationId());
        Assert.assertNull(ctlService.findCtlSchemaById(schemaId));
    }

    @Test
    public void testFindCTLSchemaByFqnAndVerAndTenantIdAndApplicationId() {
        CtlSchemaMetaInfoDto metaInfo = firstSchema.getMetaInfo();
        CTLSchemaDto found = ctlService.findCtlSchemaByFqnAndVerAndTenantIdAndApplicationId(metaInfo.getFqn(), firstSchema.getVersion(), metaInfo.getTenantId(), metaInfo.getApplicationId());
        Assert.assertEquals(firstSchema, found);
    }

    @Test
    public void testFindCTLSchemaById() {
        CTLSchemaDto found = ctlService.findCtlSchemaById(firstSchema.getId());
        Assert.assertEquals(firstSchema, found);
    }

    @Test
    public void testFindSystemCTLSchemas() {
        List<CTLSchemaDto> appSchemas = ctlService.findSystemCtlSchemas();
        Assert.assertEquals(getIdsDto(Arrays.asList(defaultSystemSchema, systemSchema)), getIdsDto(appSchemas));
    }

    @Test
    public void testFindSystemCTLSchemasMetaInfo() {
        List<CtlSchemaMetaInfoDto> appSchemas = ctlService.findSystemCtlSchemasMetaInfo();
        Comparator<HasId> comparator = new Comparator<HasId>() {
            @Override
            public int compare(HasId o1, HasId o2) {
                return o1.getId().compareTo(o2.getId());
            }
        };
        Collections.sort(appSchemas, comparator);
        List<CtlSchemaMetaInfoDto> expectedSchemas = Arrays.asList(defaultSystemSchema.getMetaInfo(), systemSchema.getMetaInfo());
        Collections.sort(expectedSchemas, comparator);
        Assert.assertEquals(expectedSchemas, appSchemas);
    }

    @Test
    public void testFindLatestCTLSchemaByFqn() {
        CTLSchemaDto latestTenantScope = ctlService.findLatestCtlSchemaByFqnAndTenantIdAndApplicationId(((AbstractTest.DEFAULT_FQN) + 7), tenant.getId(), null);
        Assert.assertEquals(Integer.valueOf(78), latestTenantScope.getVersion());
        CTLSchemaDto latestAppScope = ctlService.findLatestCtlSchemaByFqnAndTenantIdAndApplicationId(((AbstractTest.DEFAULT_FQN) + 8), tenant.getId(), appDto.getId());
        Assert.assertEquals(Integer.valueOf(81), latestAppScope.getVersion());
    }

    @Test
    public void testScopeUpdate() {
        CtlSchemaMetaInfoDto metaInfo = appSchema3.getMetaInfo();
        metaInfo.setApplicationId(null);
        ctlService.updateCtlSchemaMetaInfoScope(metaInfo);
        CTLSchemaDto found = ctlService.findCtlSchemaByFqnAndVerAndTenantIdAndApplicationId(metaInfo.getFqn(), appSchema3.getVersion(), metaInfo.getTenantId(), null);
        Assert.assertEquals(appSchema3, found);
    }

    @Test(expected = DatabaseProcessingException.class)
    public void testScopeUpdateForbidden() {
        CtlSchemaMetaInfoDto metaInfo = appSchema.getMetaInfo();
        metaInfo.setApplicationId(null);
        ctlService.updateCtlSchemaMetaInfoScope(metaInfo);
    }

    @Test
    public void testFindSiblingsFqns() {
        List<CtlSchemaMetaInfoDto> siblingSchemas = ctlService.findSiblingsByFqnTenantIdAndApplicationId(appSchema.getMetaInfo().getFqn(), appSchema.getMetaInfo().getTenantId(), appSchema.getMetaInfo().getApplicationId());
        Assert.assertNotNull(siblingSchemas);
        Assert.assertEquals(1, siblingSchemas.size());
        Assert.assertEquals(app2Schema.getMetaInfo(), siblingSchemas.get(0));
    }

    @Test
    public void testFindCTLSchemaDependentsByFqnVersionTenantId() {
        List<CTLSchemaDto> appSchemas = ctlService.findCtlSchemaDependents(firstSchema.getMetaInfo().getFqn(), firstSchema.getVersion(), tenant.getId(), null);
        Assert.assertEquals(Arrays.asList(mainSchema), appSchemas);
        appSchemas = ctlService.findCtlSchemaDependents(secondSchema.getMetaInfo().getFqn(), secondSchema.getVersion(), tenant.getId(), null);
        Assert.assertEquals(Arrays.asList(mainSchema), appSchemas);
        appSchemas = ctlService.findCtlSchemaDependents(thirdSchema.getMetaInfo().getFqn(), thirdSchema.getVersion(), tenant.getId(), null);
        Assert.assertEquals(Arrays.asList(mainSchema), appSchemas);
        appSchemas = ctlService.findCtlSchemaDependents(fourthSchema.getMetaInfo().getFqn(), fourthSchema.getVersion(), tenant.getId(), null);
        Assert.assertEquals(Arrays.asList(mainSchema), appSchemas);
        appSchemas = ctlService.findCtlSchemaDependents(mainSchema.getMetaInfo().getFqn(), mainSchema.getVersion(), tenant.getId(), null);
        Assert.assertTrue(appSchemas.isEmpty());
    }

    @Test
    public void multiThreadCTLSchemaSaveTest() throws InterruptedException, ExecutionException {
        List<Future<CTLSchemaDto>> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(executorService.submit(new Callable<CTLSchemaDto>() {
                @Override
                public CTLSchemaDto call() {
                    CTLSchemaDto sch = null;
                    try {
                        sch = ctlService.saveCtlSchema(generateCTLSchemaDto(generateTenantDto().getId()));
                    } catch (Throwable t) {
                        throw t;
                    }
                    return sch;
                }
            }));
        }
        Iterator<Future<CTLSchemaDto>> iterator = list.iterator();
        List<CTLSchemaDto> schemas = new ArrayList<>();
        while (iterator.hasNext()) {
            Future<CTLSchemaDto> f = iterator.next();
            while (!(f.isDone())) {
            } 
            schemas.add(f.get());
            iterator.remove();
        } 
        Assert.assertEquals(100, schemas.size());
        for (CTLSchemaDto schema : schemas) {
            CTLSchemaDto savedSchema = ctlService.findCtlSchemaByFqnAndVerAndTenantIdAndApplicationId(AbstractTest.DEFAULT_FQN, 100, schema.getMetaInfo().getTenantId(), null);
            Assert.assertNotNull(savedSchema);
            Assert.assertEquals(schema, savedSchema);
        }
    }

    @Test
    public void testShallowExport() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode expected = mapper.readTree(readSchemaFileAsString(CtlServiceImplTest.TEST_CTL_SCHEMA_ALPHA));
        JsonNode actual = mapper.readTree(ctlService.shallowExport(alpha).getFileData());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testFlatExport() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode expected = mapper.readTree(readSchemaFileAsString(CtlServiceImplTest.TEST_CTL_SCHEMA_ALPHA_FLAT));
        JsonNode actual = mapper.readTree(ctlService.flatExport(alpha).getFileData());
        Assert.assertEquals(expected, actual);
    }
}

