/**
 * *  Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
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
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.orient.graph.sql;


import com.orientechnologies.orient.core.exception.OTransactionException;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.junit.Assert;
import org.junit.Test;


public class RequireTransactionTest {
    protected static OrientGraph graph;

    public static enum ENV {

        DEV,
        RELEASE,
        CI;}

    @Test
    public void requireTxEnable() {
        RequireTransactionTest.graph.setAutoStartTx(false);
        RequireTransactionTest.graph.rollback();
        RequireTransactionTest.graph.setRequireTransaction(true);
        try {
            RequireTransactionTest.graph.addVertex(null);
            Assert.assertFalse(true);
        } catch (OTransactionException e) {
        }
        RequireTransactionTest.graph.begin();
        try {
            RequireTransactionTest.graph.addVertex(null);
            Assert.assertTrue(true);
        } catch (OTransactionException e) {
            Assert.assertFalse(true);
        }
        RequireTransactionTest.graph.commit();
        try {
            RequireTransactionTest.graph.addVertex(null);
            Assert.assertFalse(true);
        } catch (OTransactionException e) {
        }
    }
}

