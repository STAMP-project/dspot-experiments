/**
 * Copyright 2015 OrientDB LTD (info--at--orientdb.com)
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
package com.orientechnologies.orient.server.distributed.scenariotest;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.server.distributed.AbstractDistributedWriteTest;
import com.orientechnologies.orient.server.distributed.AbstractServerClusterInsertTest;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;


/**
 * It checks the consistency in the cluster with the following scenario:
 * - 3 server down (quorum=2) with DBs distributed as below:
 * - server1: db A, db B
 * - server2: db B, db C
 * - populating the databases
 * - servers startup
 * - each server deploys its dbs in the cluster of nodes
 * - check consistency on all servers:
 * - all the servers have  db A, db B, db C.
 * - db A, db B and db C are consistent on each server
 *
 * @author Gabriele Ponzi
 * @unknown <gabriele.ponzi--at--gmail.com>
 */
public class MultipleDBAlignmentOnNodesJoiningIT extends AbstractScenarioTest {
    private String dbA = "db-A";

    private String dbB = "db-B";

    private String dbC = "db-C";

    @Test
    public void test() throws Exception {
        AbstractServerClusterInsertTest.writerCount = 1;
        maxRetries = 10;
        init(AbstractScenarioTest.SERVERS);
        prepare(true, true);
        execute();
    }

    class DBStartupWriter implements Callable<Void> {
        private final ODatabaseDocument db;

        public DBStartupWriter(final ODatabaseDocument db) {
            this.db = db;
        }

        @Override
        public Void call() throws Exception {
            db.activateOnCurrentThread();
            for (int i = 0; i < (count); i++) {
                try {
                    if (((i + 1) % 100) == 0)
                        System.out.println((((((((("\nDBStartupWriter \'" + (db.getName())) + "' (") + (db.getURL())) + ") managed ") + (i + 1)) + "/") + (count)) + " records so far"));

                    final ODocument person = createRecord(db, i);
                    updateRecord(db, i);
                    checkRecord(db, i);
                    checkIndex(db, ((String) (person.field("name"))), person.getIdentity());
                    if ((delayWriter) > 0)
                        Thread.sleep(delayWriter);

                } catch (InterruptedException e) {
                    System.out.println(("DBStartupWriter received interrupt (db=" + (db.getURL())));
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.out.println(("DBStartupWriter received exception (db=" + (db.getURL())));
                    e.printStackTrace();
                    break;
                }
            }
            System.out.println((("\nDBStartupWriter \'" + (db.getName())) + "' END"));
            return null;
        }

        private ODocument createRecord(ODatabaseDocument database, int i) {
            final String uniqueId = ((database.getName()) + "-") + i;
            ODocument person = new ODocument("Person").fields("id", UUID.randomUUID().toString(), "name", ("Billy" + uniqueId), "birthday", new Date(), "children", uniqueId);
            database.save(person);
            Assert.assertTrue(person.getIdentity().isPersistent());
            return person;
        }

        private void updateRecord(ODatabaseDocument database, int i) {
            ODocument doc = loadRecord(database, i);
            doc.field("updated", true);
            doc.save();
        }

        private void checkRecord(ODatabaseDocument database, int i) {
            ODocument doc = loadRecord(database, i);
            Assert.assertEquals(doc.field("updated"), Boolean.TRUE);
        }

        private void checkIndex(ODatabaseDocument database, final String key, final ORID rid) {
            final List<OIdentifiable> result = database.command(new OCommandSQL("select from index:Person.name where key = ?")).execute(key);
            Assert.assertNotNull(result);
            Assert.assertEquals(result.size(), 1);
            Assert.assertNotNull(result.get(0).getRecord());
            Assert.assertEquals(field("rid"), rid);
        }

        private ODocument loadRecord(ODatabaseDocument database, int i) {
            final String uniqueId = ((database.getName()) + "-") + i;
            List<ODocument> result = database.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>((("select from Person where name = 'Billy" + uniqueId) + "'")));
            if ((result.size()) == 0)
                Assert.assertTrue((("No record found with name = 'Billy" + uniqueId) + "'!"), false);
            else
                if ((result.size()) > 1)
                    Assert.assertTrue(((((result.size()) + " records found with name = 'Billy") + uniqueId) + "'!"), false);


            return result.get(0);
        }
    }
}

