package com.pushtorefresh.storio3.contentresolver;


import com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResolver;
import com.pushtorefresh.storio3.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio3.contentresolver.operations.put.PutResolver;
import org.junit.Test;
import org.mockito.Mockito;


public class ContentResolverTypeMappingTest {
    @SuppressWarnings({ "unchecked", "ConstantConditions" })
    @Test(expected = NullPointerException.class)
    public void nullPutResolver() {
        ContentResolverTypeMapping.builder().putResolver(null).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(Mockito.mock(DeleteResolver.class)).build();
    }

    @SuppressWarnings({ "unchecked", "ConstantConditions" })
    @Test(expected = NullPointerException.class)
    public void nullGetResolver() {
        ContentResolverTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(null).deleteResolver(Mockito.mock(DeleteResolver.class)).build();
    }

    @SuppressWarnings({ "unchecked", "ConstantConditions" })
    @Test(expected = NullPointerException.class)
    public void nullDeleteResolver() {
        ContentResolverTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(null).build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void build() {
        class TestItem {}
        final PutResolver<TestItem> putResolver = Mockito.mock(PutResolver.class);
        final GetResolver<TestItem> getResolver = Mockito.mock(GetResolver.class);
        final DeleteResolver<TestItem> deleteResolver = Mockito.mock(DeleteResolver.class);
        final ContentResolverTypeMapping<TestItem> typeMapping = ContentResolverTypeMapping.<TestItem>builder().putResolver(putResolver).getResolver(getResolver).deleteResolver(deleteResolver).build();
        assertThat(typeMapping.putResolver()).isSameAs(putResolver);
        assertThat(typeMapping.getResolver()).isSameAs(getResolver);
        assertThat(typeMapping.deleteResolver()).isSameAs(deleteResolver);
    }
}

