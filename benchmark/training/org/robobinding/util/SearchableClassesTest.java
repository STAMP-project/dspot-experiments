package org.robobinding.util;


import com.google.common.collect.Lists;
import java.util.Queue;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class SearchableClassesTest {
    private SearchableClasses searchableClasses;

    @Test
    public void whenFindNearestAssignableFromUnknownClazz_thenNullIsReturned() {
        Class<?> foundClazz = searchableClasses.findNearestAssignableFrom(SearchableClassesTest.UnknownClazz.class);
        Assert.assertThat(foundClazz, Matchers.is(Matchers.nullValue()));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void whenfindNearestAssignableFrom_thenNearestAssignableIsReturned() {
        Assert.assertThat(searchableClasses.findNearestAssignableFrom(SearchableClassesTest.Clazz.class), Matchers.sameInstance(((Class) (SearchableClassesTest.Clazz.class))));
        Assert.assertThat(searchableClasses.findNearestAssignableFrom(SearchableClassesTest.UnregisteredParentClazz.class), Matchers.sameInstance(((Class) (SearchableClassesTest.GrandparentSuperInterface.class))));
        Assert.assertThat(searchableClasses.findNearestAssignableFrom(SearchableClassesTest.UnregisteredGrandparentInterface.class), Matchers.sameInstance(((Class) (SearchableClassesTest.GrandparentSuperInterface.class))));
    }

    @Test
    public void whenFindAssignablesInOrderFromUnknownClazz_thenEmptyIsReturned() {
        Queue<Class<?>> foundClasses = searchableClasses.findAssignablesInOrderFrom(SearchableClassesTest.UnknownClazz.class);
        Assert.assertThat(foundClasses, Matchers.hasSize(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void whenFindAssignablesInOrderFromClazz_thenAssignablesIsReturnedInOrder() {
        Queue<Class<?>> foundClasses = searchableClasses.findAssignablesInOrderFrom(SearchableClassesTest.Clazz.class);
        Queue<Class<?>> expectedAssignablesInOrder = Lists.newLinkedList(Lists.newArrayList(SearchableClassesTest.Clazz.class, SearchableClassesTest.ParentInterface.class, SearchableClassesTest.GrandparentSuperInterface.class, SearchableClassesTest.GrandparentClazz.class));
        Assert.assertThat(foundClasses, CoreMatchers.equalTo(expectedAssignablesInOrder));
    }

    public static class UnknownClazz {}

    public static class Clazz extends SearchableClassesTest.UnregisteredParentClazz implements SearchableClassesTest.ParentInterface {}

    public interface ParentInterface extends SearchableClassesTest.UnregisteredParentSuperInterface {}

    public interface UnregisteredParentSuperInterface {}

    public static class UnregisteredParentClazz extends SearchableClassesTest.GrandparentClazz implements SearchableClassesTest.UnregisteredGrandparentInterface {}

    public interface UnregisteredGrandparentInterface extends SearchableClassesTest.GrandparentSuperInterface {}

    public interface GrandparentSuperInterface {}

    public static class GrandparentClazz {}
}

