package com.orm.helper;


import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.TestRecord;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author jonatan.salas
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class SugarTransactionHelperTest {
    private List<TestRecord> recordList = new ArrayList<>();

    private TestRecord record1 = new TestRecord();

    private TestRecord record2 = new TestRecord();

    private TestRecord record3 = new TestRecord();

    @Test(expected = IllegalAccessException.class)
    public void testPrivateConstructor() throws Exception {
        SugarTransactionHelper helper = SugarTransactionHelper.class.getDeclaredConstructor().newInstance();
        Assert.assertNull(helper);
    }

    @Test
    public void testDoInTransaction() {
        SugarTransactionHelper.doInTransaction(new SugarTransactionHelper.Callback() {
            @Override
            public void manipulateInTransaction() {
                for (TestRecord record : recordList) {
                    save(record);
                }
            }
        });
        final List<TestRecord> results = listAll(TestRecord.class);
        Assert.assertEquals(true, inList(results, record1));
        Assert.assertEquals(true, inList(results, record2));
        Assert.assertEquals(true, inList(results, record3));
    }
}

