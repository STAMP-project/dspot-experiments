/**
 * *  Copyright 2016 OrientDB LTD (info(at)orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://www.orientdb.com
 */
package com.orientechnologies.orient.core.db.document;


import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author SDIPro
 */
public class ResourceDerivedTest {
    private ODatabaseDocumentTx db;

    // This tests for a result size of three.  The "Customer_u2" record should not be included.
    @Test
    public void shouldTestFiltering() {
        db.open("tenant1", "password");
        try {
            List<ODocument> result = query("SELECT FROM Customer");
            assertThat(result).hasSize(3);
        } finally {
            db.close();
        }
    }

    // This should return the record in "Customer_t2" but filter out the "Customer_u2" record.
    @Test
    public void shouldTestCustomer_t2() {
        db.open("tenant1", "password");
        try {
            List<ODocument> result = query("SELECT FROM Customer_t2");
            assertThat(result).hasSize(1);
        } finally {
            db.close();
        }
    }

    // This should throw an OSecurityAccessException when trying to read from the "Customer_u2" class.
    @Test(expected = OSecurityAccessException.class)
    public void shouldTestAccess2() {
        db.open("tenant1", "password");
        try {
            query("SELECT FROM Customer_u2");
        } finally {
            db.close();
        }
    }

    // This should throw an OSecurityAccessException when trying to read from the "Customer" class.
    @Test(expected = OSecurityAccessException.class)
    public void shouldTestCustomer() {
        db.open("tenant2", "password");
        try {
            List<ODocument> result = query("SELECT FROM Customer");
        } finally {
            db.close();
        }
    }

    // This tests for a result size of two.  The "Customer_t1" and "Customer_u1" records should not be included.
    @Test
    public void shouldTestCustomer_t2Tenant2() {
        db.open("tenant2", "password");
        try {
            List<ODocument> result = query("SELECT FROM Customer_t2");
            assertThat(result).hasSize(2);
        } finally {
            db.close();
        }
    }
}

