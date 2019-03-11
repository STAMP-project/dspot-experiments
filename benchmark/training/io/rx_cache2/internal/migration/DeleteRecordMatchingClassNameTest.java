package io.rx_cache2.internal.migration;


import io.rx_cache2.internal.Record;
import io.rx_cache2.internal.common.BaseTest;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class DeleteRecordMatchingClassNameTest extends BaseTest {
    private DeleteRecordMatchingClassName deleteRecordMatchingClassNameUT;

    @Test
    public void When_Class_Matches_Delete_Record_1() {
        disk.saveRecord(DeleteRecordMatchingClassNameTest.Mock1.KEY, new Record(new DeleteRecordMatchingClassNameTest.Mock1(), true, 0L), false, null);
        disk.saveRecord(DeleteRecordMatchingClassNameTest.Mock2.KEY, new Record(new DeleteRecordMatchingClassNameTest.Mock2(), true, 0L), false, null);
        MatcherAssert.assertThat(disk.allKeys().size(), Is.is(2));
        deleteRecordMatchingClassNameUT.with(Arrays.<Class>asList(DeleteRecordMatchingClassNameTest.Mock1.class)).react().test().awaitTerminalEvent();
        MatcherAssert.assertThat(disk.allKeys().size(), Is.is(1));
    }

    @Test
    public void When_Class_Matches_Delete_Record_2() {
        disk.saveRecord(DeleteRecordMatchingClassNameTest.Mock1.KEY, new Record(new DeleteRecordMatchingClassNameTest.Mock1(), true, 0L), false, null);
        disk.saveRecord(DeleteRecordMatchingClassNameTest.Mock2.KEY, new Record(new DeleteRecordMatchingClassNameTest.Mock2(), true, 0L), false, null);
        MatcherAssert.assertThat(disk.allKeys().size(), Is.is(2));
        deleteRecordMatchingClassNameUT.with(Arrays.<Class>asList(DeleteRecordMatchingClassNameTest.Mock1.class, DeleteRecordMatchingClassNameTest.Mock2.class)).react().test().awaitTerminalEvent();
        MatcherAssert.assertThat(disk.allKeys().size(), Is.is(0));
    }

    @Test
    public void When_Class_Matches_Delete_Record_1_List() {
        disk.saveRecord(DeleteRecordMatchingClassNameTest.Mock1.KEY, new Record(Arrays.asList(new DeleteRecordMatchingClassNameTest.Mock1()), true, 0L), false, null);
        disk.saveRecord(DeleteRecordMatchingClassNameTest.Mock2.KEY, new Record(Arrays.asList(new DeleteRecordMatchingClassNameTest.Mock2()), true, 0L), false, null);
        MatcherAssert.assertThat(disk.allKeys().size(), Is.is(2));
        deleteRecordMatchingClassNameUT.with(Arrays.<Class>asList(DeleteRecordMatchingClassNameTest.Mock1.class)).react().test().awaitTerminalEvent();
        MatcherAssert.assertThat(disk.allKeys().size(), Is.is(1));
    }

    @Test
    public void When_Class_Matches_Delete_Record_2_List() {
        disk.saveRecord(DeleteRecordMatchingClassNameTest.Mock1.KEY, new Record(Arrays.asList(new DeleteRecordMatchingClassNameTest.Mock1()), true, 0L), false, null);
        disk.saveRecord(DeleteRecordMatchingClassNameTest.Mock2.KEY, new Record(Arrays.asList(new DeleteRecordMatchingClassNameTest.Mock2()), true, 0L), false, null);
        MatcherAssert.assertThat(disk.allKeys().size(), Is.is(2));
        deleteRecordMatchingClassNameUT.with(Arrays.<Class>asList(DeleteRecordMatchingClassNameTest.Mock1.class, DeleteRecordMatchingClassNameTest.Mock2.class)).react().test().awaitTerminalEvent();
        MatcherAssert.assertThat(disk.allKeys().size(), Is.is(0));
    }

    public static class Mock1 {
        private static final String KEY = "Mock1";

        private final String s1;

        public Mock1() {
            s1 = null;
        }

        public Mock1(String s1) {
            this.s1 = s1;
        }
    }

    public static class Mock2 {
        private static final String KEY = "Mock2";

        private final String s1;

        public Mock2() {
            s1 = null;
        }

        public String getS1() {
            return s1;
        }
    }
}

