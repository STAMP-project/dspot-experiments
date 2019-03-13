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
package org.apache.camel.component.salesforce;


import Method.GET;
import com.googlecode.junittoolbox.ParallelParameterized;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.camel.component.salesforce.api.dto.AbstractQueryRecordsBase;
import org.apache.camel.component.salesforce.api.dto.composite.SObjectComposite;
import org.apache.camel.component.salesforce.api.dto.composite.SObjectCompositeResponse;
import org.apache.camel.component.salesforce.api.utils.Version;
import org.apache.camel.component.salesforce.dto.generated.Account;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ParallelParameterized.class)
public class CompositeApiIntegrationTest extends AbstractSalesforceTestBase {
    public static class Accounts extends AbstractQueryRecordsBase {
        @XStreamImplicit
        private List<Account> records;

        public List<Account> getRecords() {
            return records;
        }

        public void setRecords(final List<Account> records) {
            this.records = records;
        }
    }

    private static final Set<String> VERSIONS = new HashSet<>(Arrays.asList("38.0", "41.0"));

    private String accountId;

    private final String compositeUri;

    private final String version;

    public CompositeApiIntegrationTest(final String format, final String version) {
        this.version = version;
        compositeUri = "salesforce:composite?format=" + format;
    }

    @Test
    public void shouldSubmitBatchUsingCompositeApi() {
        final SObjectComposite composite = new SObjectComposite(version, true);
        final Account updates = new Account();
        setName("NewName");
        composite.addUpdate("Account", accountId, updates, "UpdateExistingAccountReferenceId");
        final Account newAccount = new Account();
        setName("Account created from Composite batch API");
        composite.addCreate(newAccount, "CreateAccountReferenceId");
        composite.addGet("Account", accountId, "GetAccountReferenceId", "Name", "BillingPostalCode");
        composite.addDelete("Account", accountId, "DeleteAccountReferenceId");
        testComposite(composite);
    }

    @Test
    public void shouldSupportGenericCompositeRequests() {
        final SObjectComposite composite = new SObjectComposite(version, true);
        composite.addGeneric(GET, ("/sobjects/Account/" + (accountId)), "GetExistingAccountReferenceId");
        testComposite(composite);
    }

    @Test
    public void shouldSupportObjectCreation() {
        final SObjectComposite compoiste = new SObjectComposite(version, true);
        final Account newAccount = new Account();
        setName("Account created from Composite batch API");
        compoiste.addCreate(newAccount, "CreateAccountReferenceId");
        final SObjectCompositeResponse response = testComposite(compoiste);
        CompositeApiIntegrationTest.assertResponseContains(response, "id");
    }

    @Test
    public void shouldSupportObjectDeletion() {
        final SObjectComposite composite = new SObjectComposite(version, true);
        composite.addDelete("Account", accountId, "DeleteAccountReferenceId");
        testComposite(composite);
    }

    @Test
    public void shouldSupportObjectRetrieval() {
        final SObjectComposite composite = new SObjectComposite(version, true);
        composite.addGet("Account", accountId, "GetExistingAccountReferenceId", "Name");
        final SObjectCompositeResponse response = testComposite(composite);
        CompositeApiIntegrationTest.assertResponseContains(response, "Name");
    }

    @Test
    public void shouldSupportObjectUpdates() {
        final SObjectComposite composite = new SObjectComposite(version, true);
        final Account updates = new Account();
        setName("NewName");
        updates.setAccountNumber("AC12345");
        composite.addUpdate("Account", accountId, updates, "UpdateAccountReferenceId");
        testComposite(composite);
    }

    @Test
    public void shouldSupportQuery() {
        final SObjectComposite composite = new SObjectComposite(version, true);
        composite.addQuery("SELECT Id, Name FROM Account", "SelectQueryReferenceId");
        final SObjectCompositeResponse response = testComposite(composite);
        CompositeApiIntegrationTest.assertResponseContains(response, "totalSize");
    }

    @Test
    public void shouldSupportQueryAll() {
        final SObjectComposite composite = new SObjectComposite(version, true);
        composite.addQueryAll("SELECT Id, Name FROM Account", "SelectQueryReferenceId");
        final SObjectCompositeResponse response = testComposite(composite);
        CompositeApiIntegrationTest.assertResponseContains(response, "totalSize");
    }

    @Test
    public void shouldSupportRelatedObjectRetrieval() {
        if ((Version.create(version).compareTo(Version.create("36.0"))) < 0) {
            return;
        }
        final SObjectComposite composite = new SObjectComposite("36.0", true);
        composite.addGetRelated("Account", accountId, "CreatedBy", "GetRelatedAccountReferenceId");
        final SObjectCompositeResponse response = testComposite(composite);
        CompositeApiIntegrationTest.assertResponseContains(response, "Username");
    }
}

