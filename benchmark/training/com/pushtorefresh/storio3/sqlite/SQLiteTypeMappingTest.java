package com.pushtorefresh.storio3.sqlite;


import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio3.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResolver;
import org.junit.Test;
import org.mockito.Mockito;


public class SQLiteTypeMappingTest {
    @SuppressWarnings({ "ConstantConditions", "unchecked" })
    @Test(expected = NullPointerException.class)
    public void nullPutResolver() {
        SQLiteTypeMapping.builder().putResolver(null).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(Mockito.mock(DeleteResolver.class)).build();
    }

    @SuppressWarnings({ "ConstantConditions", "unchecked" })
    @Test(expected = NullPointerException.class)
    public void nullMapFromCursor() {
        SQLiteTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(null).deleteResolver(Mockito.mock(DeleteResolver.class)).build();
    }

    @SuppressWarnings({ "ConstantConditions", "unchecked" })
    @Test(expected = NullPointerException.class)
    public void nullMapToDeleteQuery() {
        SQLiteTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(null).build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void build() {
        class TestItem {}
        final PutResolver<TestItem> putResolver = Mockito.mock(PutResolver.class);
        final GetResolver<TestItem> getResolver = Mockito.mock(GetResolver.class);
        final DeleteResolver<TestItem> deleteResolver = Mockito.mock(DeleteResolver.class);
        final SQLiteTypeMapping<TestItem> typeMapping = SQLiteTypeMapping.<TestItem>builder().putResolver(putResolver).getResolver(getResolver).deleteResolver(deleteResolver).build();
        assertThat(typeMapping.putResolver()).isSameAs(putResolver);
        assertThat(typeMapping.getResolver()).isSameAs(getResolver);
        assertThat(typeMapping.deleteResolver()).isSameAs(deleteResolver);
    }
}

