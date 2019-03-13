/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.common.hbase;


import com.navercorp.pinpoint.common.hbase.namespace.NamespaceValidator;
import org.apache.hadoop.hbase.TableName;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author HyunGil Jeong
 */
@RunWith(MockitoJUnitRunner.class)
public class HbaseTableNameProviderTest {
    private static final String TABLE_QUALIFIER = "testTable";

    @Mock
    private NamespaceValidator namespaceValidator;

    @Test(expected = IllegalArgumentException.class)
    public void nullNamespaceShouldThrowException() {
        // Given
        final String nullNamespace = null;
        // When
        new HbaseTableNameProvider(nullNamespace, namespaceValidator);
        Assert.fail("Expected IllegalArgumentException to be thrown");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyNamespaceShouldThrowException() {
        // Given
        final String emptyNamespace = "";
        // When
        new HbaseTableNameProvider(emptyNamespace, namespaceValidator);
        Assert.fail("Expected IllegalArgumentException to be thrown");
    }

    @Test
    public void validNamespace() {
        // Given
        final String validNamespace = "namespace";
        Mockito.when(namespaceValidator.validate(validNamespace)).thenReturn(true);
        // When
        final TableNameProvider tableNameProvider = new HbaseTableNameProvider(validNamespace, namespaceValidator);
        final TableName tableName = tableNameProvider.getTableName(HbaseTableNameProviderTest.TABLE_QUALIFIER);
        // Then
        Mockito.verify(namespaceValidator, Mockito.only()).validate(validNamespace);
        Assert.assertEquals(validNamespace, tableName.getNamespaceAsString());
        Assert.assertEquals(HbaseTableNameProviderTest.TABLE_QUALIFIER, tableName.getQualifierAsString());
    }

    @Test
    public void invalidNamespace() {
        // Given
        final String invalidNamespace = "invalidNamespace";
        Mockito.when(namespaceValidator.validate(invalidNamespace)).thenReturn(false);
        // When
        try {
            new HbaseTableNameProvider(invalidNamespace, namespaceValidator);
            Assert.fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            Mockito.verify(namespaceValidator, Mockito.only()).validate(invalidNamespace);
        }
    }
}

