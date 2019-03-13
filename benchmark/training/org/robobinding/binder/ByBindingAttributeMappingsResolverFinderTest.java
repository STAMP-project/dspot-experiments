package org.robobinding.binder;


import android.view.View;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.viewattribute.grouped.ViewAttributeBinderFactories;
import org.robobinding.viewbinding.InitializedBindingAttributeMappingsProvider;
import org.robobinding.viewbinding.InitializedBindingAttributeMappingsProviders;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class ByBindingAttributeMappingsResolverFinderTest {
    @Mock
    private InitializedBindingAttributeMappingsProviders providerMap;

    @Mock
    private ViewAttributeBinderFactories viewAttributeBinderFactories;

    @Test
    public void givenTwoCandidateProviders_whenFindCandidateResolvers_thenTwoResolversShouldBeReturned() {
        View view = Mockito.mock(View.class);
        Collection<InitializedBindingAttributeMappingsProvider> candiateProviders = Lists.<InitializedBindingAttributeMappingsProvider>newArrayList(Mockito.mock(InitializedBindingAttributeMappingsProvider.class), Mockito.mock(InitializedBindingAttributeMappingsProvider.class));
        Mockito.when(providerMap.findCandidates(view.getClass())).thenReturn(candiateProviders);
        ByBindingAttributeMappingsResolverFinder finder = new ByBindingAttributeMappingsResolverFinder(providerMap, viewAttributeBinderFactories);
        Iterable<ByBindingAttributeMappingsResolver> candidateResolvers = finder.findCandidates(view);
        Assert.assertThat(Iterables.size(candidateResolvers), Matchers.is(2));
    }
}

