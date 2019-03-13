/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.parser;


import junit.framework.TestCase;


public class PerfTest extends TestCase {
    public void test_perf() throws Exception {
        for (int i = 0; i < 10; ++i) {
            // perf("SELECT * FROM my_table WHERE TRUNC(SYSDATE) = DATE '2002-10-03';");
            perfOracle("SELECT a.ID, a.GMT_CREATE, a.GMT_MODIFIED, a.COMPANY_NAME, a.BIZ_TYPE , b.SERVICE_TYPE, b.SERVICE_LEVEL, b.BUSINESS_ROLE, a.STATUS, a.RECOMMENDED , a.COUNTRY, a.PROVINCE, a.CITY, a.ADDRESS, a.ZIP , a.LOGO_FILE, a.EMAIL, a.BRIEF_PROFILE, a.DOMAIN_ID, a.IS_PASS_AV , a.KEYWORDS, a.PROVIDE_PRODUCTS, a.PURCHASE_PRODUCTS, a.BRAND_NAME, a.PROMOTION_VALUE , a.OWNER_MEMBER_ID, a.OWNER_SEQ, a.EMPLOYEES_COUNT, a.ANNUAL_REVENUE, a.HOMEPAGE_URL , a.REG_ADDRESS, a.TRADE_REGION, a.TRADE_REGION_USER, a.REG_CAPITAL, a.OWNERSHIP_TYPE , a.ESTABLISHED_YEAR, a.PRINCIPAL, a.ANNUAL_PURCHASE, a.CERTIFICATION, a.CERTIFICATION_2 , a.CONTACT_MANUFACTURING, a.YEARS_OEM, b.STAGE, a.VIDEO_PATH, a.ABOUTUS_IMAGE_PATH , a.ABOUTUS_IMAGE_TITLE, a.CHINESE_NAME, a.IMAGE_VERSION FROM COMPANY a, VACCOUNT b WHERE a.ID = b.ID AND a.id IN (?)");
            perfMySql("SELECT a.ID, a.GMT_CREATE, a.GMT_MODIFIED, a.COMPANY_NAME, a.BIZ_TYPE , b.SERVICE_TYPE, b.SERVICE_LEVEL, b.BUSINESS_ROLE, a.STATUS, a.RECOMMENDED , a.COUNTRY, a.PROVINCE, a.CITY, a.ADDRESS, a.ZIP , a.LOGO_FILE, a.EMAIL, a.BRIEF_PROFILE, a.DOMAIN_ID, a.IS_PASS_AV , a.KEYWORDS, a.PROVIDE_PRODUCTS, a.PURCHASE_PRODUCTS, a.BRAND_NAME, a.PROMOTION_VALUE , a.OWNER_MEMBER_ID, a.OWNER_SEQ, a.EMPLOYEES_COUNT, a.ANNUAL_REVENUE, a.HOMEPAGE_URL , a.REG_ADDRESS, a.TRADE_REGION, a.TRADE_REGION_USER, a.REG_CAPITAL, a.OWNERSHIP_TYPE , a.ESTABLISHED_YEAR, a.PRINCIPAL, a.ANNUAL_PURCHASE, a.CERTIFICATION, a.CERTIFICATION_2 , a.CONTACT_MANUFACTURING, a.YEARS_OEM, b.STAGE, a.VIDEO_PATH, a.ABOUTUS_IMAGE_PATH , a.ABOUTUS_IMAGE_TITLE, a.CHINESE_NAME, a.IMAGE_VERSION FROM COMPANY a, VACCOUNT b WHERE a.ID = b.ID AND a.id IN (?)");
            // perf(loadSql("bvt/parser/oracle-23.txt"));
        }
    }
}

