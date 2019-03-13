/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.ldapinput;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Tests LDAP Input Step
 *
 * @author nhudak
 */
public class LDAPInputTest {
    private static StepMockHelper<LDAPInputMeta, LDAPInputData> stepMockHelper;

    @Test
    public void testRowProcessing() throws Exception {
        // Setup step
        LDAPInput ldapInput = new LDAPInput(LDAPInputTest.stepMockHelper.stepMeta, LDAPInputTest.stepMockHelper.stepDataInterface, 0, LDAPInputTest.stepMockHelper.transMeta, LDAPInputTest.stepMockHelper.trans);
        LDAPInputData data = new LDAPInputData();
        LDAPInputMeta meta = mockMeta();
        // Mock fields
        LDAPInputField[] fields = new LDAPInputField[]{ new LDAPInputField("dn"), new LDAPInputField("cn"), new LDAPInputField("role") };
        int sortedField = 1;
        fields[sortedField].setSortedKey(true);
        Mockito.when(meta.getInputFields()).thenReturn(fields);
        // Mock LDAP Connection
        Mockito.when(meta.getProtocol()).thenReturn(LdapMockProtocol.getName());
        Mockito.when(meta.getHost()).thenReturn("host.mock");
        Mockito.when(meta.getDerefAliases()).thenReturn("never");
        Mockito.when(meta.getReferrals()).thenReturn("ignore");
        LdapMockProtocol.setup();
        try {
            // Run Initialization
            Assert.assertTrue("Input Initialization Failed", ldapInput.init(meta, data));
            // Verify
            Assert.assertEquals("Field not marked as sorted", 1, data.connection.getSortingAttributes().size());
            Assert.assertEquals("Field not marked as sorted", data.attrReturned[sortedField], data.connection.getSortingAttributes().get(0));
            Assert.assertNotNull(data.attrReturned[sortedField]);
        } finally {
            LdapMockProtocol.cleanup();
        }
    }
}

