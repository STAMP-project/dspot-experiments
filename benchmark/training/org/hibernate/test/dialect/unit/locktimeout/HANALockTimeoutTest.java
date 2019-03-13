/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.unit.locktimeout;


import LockMode.PESSIMISTIC_READ;
import LockMode.PESSIMISTIC_WRITE;
import LockOptions.NO_WAIT;
import LockOptions.SKIP_LOCKED;
import org.hibernate.LockMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HANARowStoreDialect;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Expected lock clauses according to the official HANA FOR UPDATE clause documentation:
 *
 * https://help.sap.com/saphelp_hanaplatform/helpdata/en/20/fcf24075191014a89e9dc7b8408b26/content.htm#loio20fcf24075191014a89e9dc7b8408b26__sql_select_1sql_select_for_update
 *
 * @author Vlad Mihalcea
 */
public class HANALockTimeoutTest extends BaseUnitTestCase {
    private final Dialect dialect = new HANARowStoreDialect();

    @Test
    public void testLockTimeoutNoAliasNoTimeout() {
        Assert.assertEquals(" for update", dialect.getForUpdateString(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_READ)));
        Assert.assertEquals(" for update", dialect.getForUpdateString(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE)));
    }

    @Test
    public void testLockTimeoutNoAliasNoWait() {
        Assert.assertEquals(" for update nowait", dialect.getForUpdateString(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_READ).setTimeOut(NO_WAIT)));
        Assert.assertEquals(" for update nowait", dialect.getForUpdateString(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE).setTimeOut(NO_WAIT)));
    }

    @Test
    public void testLockTimeoutNoAliasSkipLocked() {
        Assert.assertEquals(" for update", dialect.getForUpdateString(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_READ).setTimeOut(SKIP_LOCKED)));
        Assert.assertEquals(" for update", dialect.getForUpdateString(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE).setTimeOut(SKIP_LOCKED)));
    }

    @Test
    public void testLockTimeoutAliasNoTimeout() {
        String alias = "a";
        Assert.assertEquals(" for update of a", dialect.getForUpdateString(alias, new org.hibernate.LockOptions(LockMode.PESSIMISTIC_READ).setAliasSpecificLockMode(alias, PESSIMISTIC_READ)));
        Assert.assertEquals(" for update of a", dialect.getForUpdateString(alias, new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE).setAliasSpecificLockMode(alias, PESSIMISTIC_WRITE)));
    }

    @Test
    public void testLockTimeoutAliasNoWait() {
        String alias = "a";
        Assert.assertEquals(" for update of a nowait", dialect.getForUpdateString(alias, new org.hibernate.LockOptions(LockMode.PESSIMISTIC_READ).setAliasSpecificLockMode(alias, PESSIMISTIC_READ).setTimeOut(NO_WAIT)));
        Assert.assertEquals(" for update of a nowait", dialect.getForUpdateString(alias, new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE).setAliasSpecificLockMode(alias, PESSIMISTIC_WRITE).setTimeOut(NO_WAIT)));
    }

    @Test
    public void testLockTimeoutAliasSkipLocked() {
        String alias = "a";
        Assert.assertEquals(" for update of a", dialect.getForUpdateString(alias, new org.hibernate.LockOptions(LockMode.PESSIMISTIC_READ).setAliasSpecificLockMode(alias, PESSIMISTIC_READ).setTimeOut(SKIP_LOCKED)));
        Assert.assertEquals(" for update of a", dialect.getForUpdateString(alias, new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE).setAliasSpecificLockMode(alias, PESSIMISTIC_WRITE).setTimeOut(SKIP_LOCKED)));
    }
}

