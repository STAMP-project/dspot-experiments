/**
 * Copyright 2009-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.executor;


@org.junit.runner.RunWith(value = org.mockito.junit.MockitoJUnitRunner.class)
public class AmplResultExtractorTest {
    private org.apache.ibatis.executor.ResultExtractor resultExtractor;

    @org.mockito.Mock
    private org.apache.ibatis.session.Configuration configuration;

    @org.mockito.Mock
    private org.apache.ibatis.reflection.factory.ObjectFactory objectFactory;

    @org.junit.Before
    public void setUp() throws java.lang.Exception {
        resultExtractor = new org.apache.ibatis.executor.ResultExtractor(configuration, objectFactory);
    }

    @org.junit.Test
    public void shouldExtractNullForNullTargetType() {
        final java.lang.Object result = resultExtractor.extractObjectFromList(null, null);
        org.junit.Assert.assertThat(result, org.hamcrest.CoreMatchers.nullValue());
    }

    @org.junit.Test
    public void shouldExtractList() {
        final java.util.List list = java.util.Arrays.asList(1, 2, 3);
        final java.lang.Object result = resultExtractor.extractObjectFromList(list, java.util.List.class);
        org.junit.Assert.assertThat(result, org.hamcrest.CoreMatchers.instanceOf(java.util.List.class));
        final java.util.List resultList = ((java.util.List) (result));
        org.junit.Assert.assertThat(resultList, org.hamcrest.CoreMatchers.equalTo(list));
    }

    @org.junit.Test
    public void shouldExtractArray() {
        final java.util.List list = java.util.Arrays.asList(1, 2, 3);
        final java.lang.Object result = resultExtractor.extractObjectFromList(list, java.lang.Integer[].class);
        org.junit.Assert.assertThat(result, org.hamcrest.CoreMatchers.instanceOf(java.lang.Integer[].class));
        final java.lang.Integer[] resultArray = ((java.lang.Integer[]) (result));
        org.junit.Assert.assertThat(resultArray, org.hamcrest.CoreMatchers.equalTo(new java.lang.Integer[]{ 1 , 2 , 3 }));
    }

    @org.junit.Test
    public void shouldExtractSet() {
        final java.util.List list = java.util.Arrays.asList(1, 2, 3);
        final java.lang.Class<java.util.Set> targetType = java.util.Set.class;
        final java.util.Set set = new java.util.HashSet();
        final org.apache.ibatis.reflection.MetaObject metaObject = org.mockito.Mockito.mock(org.apache.ibatis.reflection.MetaObject.class);
        org.mockito.Mockito.when(objectFactory.isCollection(targetType)).thenReturn(true);
        org.mockito.Mockito.when(objectFactory.create(targetType)).thenReturn(set);
        org.mockito.Mockito.when(configuration.newMetaObject(set)).thenReturn(metaObject);
        final java.util.Set result = ((java.util.Set) (resultExtractor.extractObjectFromList(list, targetType)));
        org.junit.Assert.assertThat(result, org.hamcrest.CoreMatchers.sameInstance(set));
        org.mockito.Mockito.verify(metaObject).addAll(list);
    }

    @org.junit.Test
    public void shouldExtractSingleObject() {
        final java.util.List list = java.util.Collections.singletonList("single object");
        org.junit.Assert.assertThat(((java.lang.String) (resultExtractor.extractObjectFromList(list, java.lang.String.class))), org.hamcrest.CoreMatchers.equalTo("single object"));
        org.junit.Assert.assertThat(((java.lang.String) (resultExtractor.extractObjectFromList(list, null))), org.hamcrest.CoreMatchers.equalTo("single object"));
        org.junit.Assert.assertThat(((java.lang.String) (resultExtractor.extractObjectFromList(list, java.lang.Integer.class))), org.hamcrest.CoreMatchers.equalTo("single object"));
    }

    @org.junit.Test(expected = org.apache.ibatis.executor.ExecutorException.class)
    public void shouldFailWhenMutipleItemsInList() {
        final java.util.List list = java.util.Arrays.asList("first object", "second object");
        org.junit.Assert.assertThat(((java.lang.String) (resultExtractor.extractObjectFromList(list, java.lang.String.class))), org.hamcrest.CoreMatchers.equalTo("single object"));
    }
}

