/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.metadata.pgcatalog;


import ConstraintInfo.Type.PRIMARY_KEY;
import Schemas.DOC_SCHEMA_NAME;
import io.crate.metadata.RelationInfo;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.T3;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Test;


public class OidHashTest extends CrateUnitTest {
    private static final RelationInfo TABLE_INFO = T3.T1_INFO;

    private static final RelationInfo VIEW_INFO = new io.crate.metadata.view.ViewInfo(T3.T1_INFO.ident(), "", Collections.emptyList(), null);

    @Test
    public void testRelationOid() {
        int tableOid = OidHash.relationOid(OidHashTest.TABLE_INFO);
        int viewOid = OidHash.relationOid(OidHashTest.VIEW_INFO);
        assertThat(tableOid, Matchers.not(viewOid));
        assertThat(tableOid, Matchers.is(728874843));
        assertThat(viewOid, Matchers.is(1782608760));
    }

    @Test
    public void testSchemaOid() {
        assertThat(OidHash.schemaOid(DOC_SCHEMA_NAME), Matchers.is((-2048275947)));
    }

    @Test
    public void testConstraintOid() {
        assertThat(OidHash.constraintOid(OidHashTest.TABLE_INFO.ident().fqn(), "id_pk", PRIMARY_KEY.toString()), Matchers.is(279835673));
    }
}

