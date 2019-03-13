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


import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.List;
import org.apache.camel.component.salesforce.api.dto.AbstractQueryRecordsBase;
import org.apache.camel.component.salesforce.api.dto.RecentItem;
import org.apache.camel.component.salesforce.dto.generated.Account;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(Standalone.class)
public class RecentIntegrationTest extends AbstractSalesforceTestBase {
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

    private static final Object NOT_USED = null;

    @Test
    public void shouldFetchRecentItems() {
        @SuppressWarnings("unchecked")
        final List<RecentItem> items = template.requestBody("direct:test-recent", RecentIntegrationTest.NOT_USED, List.class);
        RecentIntegrationTest.assertRecentItemsSize(items, 10);
    }

    @Test
    public void shouldFetchRecentItemsLimitingByHeaderParam() {
        @SuppressWarnings("unchecked")
        final List<RecentItem> items = template.requestBody("direct:test-recent-with-header-limit-param", RecentIntegrationTest.NOT_USED, List.class);
        RecentIntegrationTest.assertRecentItemsSize(items, 5);
    }

    @Test
    public void shouldFetchRecentItemsLimitingByParamInBody() {
        @SuppressWarnings("unchecked")
        final List<RecentItem> items = template.requestBody("direct:test-recent-with-body-limit-param", RecentIntegrationTest.NOT_USED, List.class);
        RecentIntegrationTest.assertRecentItemsSize(items, 5);
    }

    @Test
    public void shouldFetchRecentItemsLimitingByUriParam() {
        @SuppressWarnings("unchecked")
        final List<RecentItem> items = template.requestBody("direct:test-recent-with-limit-uri-param", RecentIntegrationTest.NOT_USED, List.class);
        RecentIntegrationTest.assertRecentItemsSize(items, 5);
    }
}

