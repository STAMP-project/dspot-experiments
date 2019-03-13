package org.robobinding.binder;


import android.view.View;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.PendingAttributesForView;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class BindingAttributeResolverTest {
    @Mock
    private ByBindingAttributeMappingsResolverFinder byBindingAttributeMappingsResolverFinder;

    @Test
    public void givenTwoCandidateResolvers_whenResolve_thenBothResolversShouldInvolveResolving() {
        ByBindingAttributeMappingsResolver candidateResolver1 = Mockito.mock(ByBindingAttributeMappingsResolver.class);
        ByBindingAttributeMappingsResolver candidateResolver2 = Mockito.mock(ByBindingAttributeMappingsResolver.class);
        Mockito.when(byBindingAttributeMappingsResolverFinder.findCandidates(ArgumentMatchers.any(View.class))).thenReturn(Lists.newArrayList(candidateResolver1, candidateResolver2));
        BindingAttributeResolver bindingAttributeResolver = new BindingAttributeResolver(byBindingAttributeMappingsResolverFinder);
        PendingAttributesForView pendingAttributesForView = Mockito.mock(PendingAttributesForView.class);
        Mockito.when(pendingAttributesForView.isEmpty()).thenReturn(false);
        bindingAttributeResolver.resolve(pendingAttributesForView);
        Mockito.verify(candidateResolver1).resolve(pendingAttributesForView);
        Mockito.verify(candidateResolver2).resolve(pendingAttributesForView);
    }

    @Test
    public void givenTwoCandidateResolvers_whenResolveCompletedAtFirstResolver_thenSecondResolverShouldBeSkipped() {
        ByBindingAttributeMappingsResolver candidateResolver1 = Mockito.mock(ByBindingAttributeMappingsResolver.class);
        ByBindingAttributeMappingsResolver candidateResolver2 = Mockito.mock(ByBindingAttributeMappingsResolver.class);
        Mockito.when(byBindingAttributeMappingsResolverFinder.findCandidates(ArgumentMatchers.any(View.class))).thenReturn(Lists.newArrayList(candidateResolver1, candidateResolver2));
        BindingAttributeResolver bindingAttributeResolver = new BindingAttributeResolver(byBindingAttributeMappingsResolverFinder);
        PendingAttributesForView pendingAttributesForView = Mockito.mock(PendingAttributesForView.class);
        Mockito.when(pendingAttributesForView.isEmpty()).thenReturn(true);
        bindingAttributeResolver.resolve(pendingAttributesForView);
        Mockito.verify(candidateResolver1).resolve(pendingAttributesForView);
        Mockito.verify(candidateResolver2, Mockito.never()).resolve(pendingAttributesForView);
    }
}

