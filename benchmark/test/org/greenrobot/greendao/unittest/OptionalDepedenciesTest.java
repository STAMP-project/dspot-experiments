package org.greenrobot.greendao.unittest;


import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.identityscope.IdentityScope;
import org.greenrobot.greendao.query.CountQuery;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.LazyList;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;
import org.greenrobot.greendao.rx.RxDao;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * We should not expose any optional library classes in signatures of greenDAO's primary classes and interfaces.
 * Reflection utils like Mockito should not fail with NoClassDefFoundError or the likes.
 */
public class OptionalDepedenciesTest {
    @Test(expected = ClassNotFoundException.class)
    public void testOptionalDependenciesAbsentRx() throws Exception {
        Class.forName("rx.Observable");
    }

    @Test
    public void testMockitoMocks() {
        Mockito.mock(DaoMaster.class).newSession();
        getDatabase();
        Mockito.mock(Database.class).getRawDatabase();
        Mockito.mock(DatabaseStatement.class).execute();
        Mockito.mock(IdentityScope.class).clear();
        Mockito.mock(AbstractDao.class).queryBuilder();
        queryBuilder();
        Mockito.mock(MinimalEntity.class).getId();
        Mockito.mock(Query.class).forCurrentThread();
        Mockito.mock(QueryBuilder.class).build();
        Mockito.mock(CountQuery.class).forCurrentThread();
        Mockito.mock(DeleteQuery.class).forCurrentThread();
        Mockito.mock(Join.class).getTablePrefix();
        Mockito.mock(LazyList.class).getLoadedCount();
        Mockito.mock(WhereCondition.class).appendValuesTo(null);
        Mockito.mock(Property.class).isNull();
        Mockito.mock(DaoException.class).getMessage();
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testMockitoMocksFailForRx() {
        Mockito.mock(RxDao.class);
    }
}

