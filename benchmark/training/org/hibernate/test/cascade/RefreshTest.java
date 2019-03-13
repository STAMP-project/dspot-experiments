/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cascade;


import java.util.Date;
import java.util.Iterator;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Implementation of RefreshTest.
 *
 * @author Steve Ebersole
 */
public class RefreshTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testRefreshCascade() throws Throwable {
        Session session = openSession();
        Transaction txn = session.beginTransaction();
        JobBatch batch = new JobBatch(new Date());
        batch.createJob().setProcessingInstructions("Just do it!");
        batch.createJob().setProcessingInstructions("I know you can do it!");
        // write the stuff to the database; at this stage all job.status values are zero
        session.persist(batch);
        session.flush();
        // behind the session's back, let's modify the statuses
        updateStatuses(((SessionImplementor) (session)));
        // Now lets refresh the persistent batch, and see if the refresh cascaded to the jobs collection elements
        session.refresh(batch);
        Iterator itr = batch.getJobs().iterator();
        while (itr.hasNext()) {
            Job job = ((Job) (itr.next()));
            Assert.assertEquals("Jobs not refreshed!", 1, job.getStatus());
        } 
        txn.rollback();
        session.close();
    }
}

