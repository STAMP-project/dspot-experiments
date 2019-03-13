package org.apache.ibatis.executor;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.Configuration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ResultExtractorTest {
    private ResultExtractor resultExtractor;

    @Mock
    private Configuration configuration;

    @Mock
    private ObjectFactory objectFactory;

    @Test
    public void shouldExtractNullForNullTargetType() {
        final Object result = resultExtractor.extractObjectFromList(null, null);
        Assert.assertThat(result, CoreMatchers.nullValue());
    }

    @Test
    public void shouldExtractList() {
        final List list = Arrays.asList(1, 2, 3);
        final Object result = resultExtractor.extractObjectFromList(list, List.class);
        Assert.assertThat(result, CoreMatchers.instanceOf(List.class));
        final List resultList = ((List) (result));
        Assert.assertThat(resultList, CoreMatchers.equalTo(list));
    }

    @Test
    public void shouldExtractArray() {
        final List list = Arrays.asList(1, 2, 3);
        final Object result = resultExtractor.extractObjectFromList(list, Integer[].class);
        Assert.assertThat(result, CoreMatchers.instanceOf(Integer[].class));
        final Integer[] resultArray = ((Integer[]) (result));
        Assert.assertThat(resultArray, CoreMatchers.equalTo(new Integer[]{ 1, 2, 3 }));
    }

    @Test
    public void shouldExtractSet() {
        final List list = Arrays.asList(1, 2, 3);
        final Class<Set> targetType = Set.class;
        final Set set = new HashSet();
        final MetaObject metaObject = Mockito.mock(MetaObject.class);
        Mockito.when(objectFactory.isCollection(targetType)).thenReturn(true);
        Mockito.when(objectFactory.create(targetType)).thenReturn(set);
        Mockito.when(configuration.newMetaObject(set)).thenReturn(metaObject);
        final Set result = ((Set) (resultExtractor.extractObjectFromList(list, targetType)));
        Assert.assertThat(result, CoreMatchers.sameInstance(set));
        Mockito.verify(metaObject).addAll(list);
    }

    @Test
    public void shouldExtractSingleObject() {
        final List list = Collections.singletonList("single object");
        Assert.assertThat(((String) (resultExtractor.extractObjectFromList(list, String.class))), CoreMatchers.equalTo("single object"));
        Assert.assertThat(((String) (resultExtractor.extractObjectFromList(list, null))), CoreMatchers.equalTo("single object"));
        Assert.assertThat(((String) (resultExtractor.extractObjectFromList(list, Integer.class))), CoreMatchers.equalTo("single object"));
    }

    @Test(expected = ExecutorException.class)
    public void shouldFailWhenMutipleItemsInList() {
        final List list = Arrays.asList("first object", "second object");
        Assert.assertThat(((String) (resultExtractor.extractObjectFromList(list, String.class))), CoreMatchers.equalTo("single object"));
    }
}

