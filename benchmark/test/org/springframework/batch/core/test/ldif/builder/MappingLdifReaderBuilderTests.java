/**
 * Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.springframework.batch.core.test.ldif.builder;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ldif.MappingLdifReader;
import org.springframework.batch.item.ldif.RecordCallbackHandler;
import org.springframework.batch.item.ldif.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapAttributes;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author Glenn Renfro
 */
@RunWith(SpringRunner.class)
public class MappingLdifReaderBuilderTests {
    @Autowired
    private ApplicationContext context;

    private MappingLdifReader<LdapAttributes> mappingLdifReader;

    private String callbackAttributeName;

    @Test
    public void testSkipRecord() throws Exception {
        this.mappingLdifReader = new org.springframework.batch.item.ldif.builder.MappingLdifReaderBuilder<LdapAttributes>().recordsToSkip(1).recordMapper(new MappingLdifReaderBuilderTests.TestMapper()).resource(context.getResource("classpath:/test.ldif")).name("foo").build();
        LdapAttributes ldapAttributes = firstRead();
        Assert.assertEquals("The attribute name for the second record did not match expected result", "cn=Bjorn Jensen, ou=Accounting, dc=airius, dc=com", ldapAttributes.getName().toString());
    }

    @Test
    public void testBasicRead() throws Exception {
        this.mappingLdifReader = new org.springframework.batch.item.ldif.builder.MappingLdifReaderBuilder<LdapAttributes>().recordMapper(new MappingLdifReaderBuilderTests.TestMapper()).resource(context.getResource("classpath:/test.ldif")).name("foo").build();
        LdapAttributes ldapAttributes = firstRead();
        Assert.assertEquals("The attribute name for the first record did not match expected result", "cn=Barbara Jensen, ou=Product Development, dc=airius, dc=com", ldapAttributes.getName().toString());
    }

    @Test
    public void testCurrentItemCount() throws Exception {
        this.mappingLdifReader = new org.springframework.batch.item.ldif.builder.MappingLdifReaderBuilder<LdapAttributes>().currentItemCount(3).recordMapper(new MappingLdifReaderBuilderTests.TestMapper()).resource(context.getResource("classpath:/test.ldif")).name("foo").build();
        LdapAttributes ldapAttributes = firstRead();
        Assert.assertEquals("The attribute name for the third record did not match expected result", "cn=Gern Jensen, ou=Product Testing, dc=airius, dc=com", ldapAttributes.getName().toString());
    }

    @Test
    public void testMaxItemCount() throws Exception {
        this.mappingLdifReader = new org.springframework.batch.item.ldif.builder.MappingLdifReaderBuilder<LdapAttributes>().maxItemCount(1).recordMapper(new MappingLdifReaderBuilderTests.TestMapper()).resource(context.getResource("classpath:/test.ldif")).name("foo").build();
        LdapAttributes ldapAttributes = firstRead();
        Assert.assertEquals("The attribute name for the first record did not match expected result", "cn=Barbara Jensen, ou=Product Development, dc=airius, dc=com", ldapAttributes.getName().toString());
        ldapAttributes = this.mappingLdifReader.read();
        Assert.assertNull("The second read should have returned null", ldapAttributes);
    }

    @Test
    public void testSkipRecordCallback() throws Exception {
        this.mappingLdifReader = new org.springframework.batch.item.ldif.builder.MappingLdifReaderBuilder<LdapAttributes>().recordsToSkip(1).recordMapper(new MappingLdifReaderBuilderTests.TestMapper()).skippedRecordsCallback(new MappingLdifReaderBuilderTests.TestCallBackHandler()).resource(context.getResource("classpath:/test.ldif")).name("foo").build();
        firstRead();
        Assert.assertEquals("The attribute name from the callback handler did not match the  expected result", "cn=Barbara Jensen, ou=Product Development, dc=airius, dc=com", this.callbackAttributeName);
    }

    @Test
    public void testSaveState() throws Exception {
        this.mappingLdifReader = new org.springframework.batch.item.ldif.builder.MappingLdifReaderBuilder<LdapAttributes>().recordMapper(new MappingLdifReaderBuilderTests.TestMapper()).resource(context.getResource("classpath:/test.ldif")).name("foo").build();
        ExecutionContext executionContext = new ExecutionContext();
        firstRead(executionContext);
        this.mappingLdifReader.update(executionContext);
        Assert.assertEquals("foo.read.count did not have the expected result", 1, executionContext.getInt("foo.read.count"));
    }

    @Test
    public void testSaveStateDisabled() throws Exception {
        this.mappingLdifReader = new org.springframework.batch.item.ldif.builder.MappingLdifReaderBuilder<LdapAttributes>().saveState(false).recordMapper(new MappingLdifReaderBuilderTests.TestMapper()).resource(context.getResource("classpath:/test.ldif")).build();
        ExecutionContext executionContext = new ExecutionContext();
        firstRead(executionContext);
        this.mappingLdifReader.update(executionContext);
        Assert.assertEquals("ExecutionContext should have been empty", 0, executionContext.size());
    }

    @Test
    public void testStrict() {
        // Test that strict when enabled will throw an exception.
        try {
            this.mappingLdifReader = new org.springframework.batch.item.ldif.builder.MappingLdifReaderBuilder<LdapAttributes>().recordMapper(new MappingLdifReaderBuilderTests.TestMapper()).resource(context.getResource("classpath:/teadsfst.ldif")).name("foo").build();
            this.mappingLdifReader.open(new ExecutionContext());
            Assert.fail("IllegalStateException should have been thrown, because strict was set to true");
        } catch (ItemStreamException ise) {
            Assert.assertEquals("IllegalStateException message did not match the expected result.", "Failed to initialize the reader", ise.getMessage());
        }
        // Test that strict when disabled will still allow the ldap resource to be opened.
        this.mappingLdifReader = new org.springframework.batch.item.ldif.builder.MappingLdifReaderBuilder<LdapAttributes>().strict(false).name("foo").recordMapper(new MappingLdifReaderBuilderTests.TestMapper()).resource(context.getResource("classpath:/teadsfst.ldif")).build();
        this.mappingLdifReader.open(new ExecutionContext());
    }

    @Test
    public void testNullRecordMapper() {
        try {
            this.mappingLdifReader = new org.springframework.batch.item.ldif.builder.MappingLdifReaderBuilder<LdapAttributes>().resource(context.getResource("classpath:/teadsfst.ldif")).build();
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException ise) {
            Assert.assertEquals("IllegalArgumentException message did not match the expected result.", "RecordMapper is required.", ise.getMessage());
        }
    }

    @Configuration
    public static class LdifConfiguration {}

    public class TestCallBackHandler implements RecordCallbackHandler {
        @Override
        public void handleRecord(LdapAttributes attributes) {
            callbackAttributeName = attributes.getName().toString();
        }
    }

    public class TestMapper implements RecordMapper<LdapAttributes> {
        @Override
        public LdapAttributes mapRecord(LdapAttributes attributes) {
            return attributes;
        }
    }
}

