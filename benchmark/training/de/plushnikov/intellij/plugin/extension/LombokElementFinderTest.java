package de.plushnikov.intellij.plugin.extension;


import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.impl.file.impl.JavaFileManager;
import com.intellij.psi.search.GlobalSearchScope;
import lombok.Builder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class LombokElementFinderTest {
    private static final String BASE_CLASS = "de.test.SomeClass";

    private static final String SOME_CLASS_BUILDER = ".SomeClassBuilder";

    @Spy
    private LombokElementFinder elementFinder;

    @Mock
    private JavaFileManager javaFileManager;

    @Mock
    private GlobalSearchScope scope;

    @Test
    public void findClass() {
        final PsiClass psiBaseClass = Mockito.mock(PsiClass.class);
        Mockito.when(javaFileManager.findClass(LombokElementFinderTest.BASE_CLASS, scope)).thenReturn(psiBaseClass);
        final PsiModifierList psiModifierList = Mockito.mock(PsiModifierList.class);
        Mockito.when(psiBaseClass.getModifierList()).thenReturn(psiModifierList);
        final PsiAnnotation psiAnnotation = Mockito.mock(PsiAnnotation.class);
        Mockito.when(psiModifierList.getAnnotations()).thenReturn(new PsiAnnotation[]{ psiAnnotation });
        Mockito.when(psiAnnotation.getQualifiedName()).thenReturn(Builder.class.getName());
        final PsiJavaCodeReferenceElement referenceElement = Mockito.mock(PsiJavaCodeReferenceElement.class);
        Mockito.when(psiAnnotation.getNameReferenceElement()).thenReturn(referenceElement);
        Mockito.when(referenceElement.getReferenceName()).thenReturn(Builder.class.getSimpleName());
        final PsiClass psiBuilderClass = Mockito.mock(PsiClass.class);
        Mockito.when(psiBaseClass.findInnerClassByName(ArgumentMatchers.eq(LombokElementFinderTest.SOME_CLASS_BUILDER.substring(1)), ArgumentMatchers.anyBoolean())).thenReturn(psiBuilderClass);
        final PsiClass psiClass = elementFinder.findClass(((LombokElementFinderTest.BASE_CLASS) + (LombokElementFinderTest.SOME_CLASS_BUILDER)), scope);
        Assert.assertNotNull(psiClass);
        Mockito.verify(javaFileManager).findClass(LombokElementFinderTest.BASE_CLASS, scope);
    }

    @Test
    public void findClassRecursion() {
        // setup recursive calls of elementFinder
        Mockito.when(javaFileManager.findClass(LombokElementFinderTest.BASE_CLASS, scope)).thenAnswer(new Answer<PsiClass>() {
            @Override
            public PsiClass answer(InvocationOnMock invocation) {
                final String fqn = ((String) (invocation.getArguments()[0]));
                final GlobalSearchScope searchScope = ((GlobalSearchScope) (invocation.getArguments()[1]));
                return elementFinder.findClass((fqn + (LombokElementFinderTest.SOME_CLASS_BUILDER)), searchScope);
            }
        });
        final PsiClass psiClass = elementFinder.findClass(((LombokElementFinderTest.BASE_CLASS) + (LombokElementFinderTest.SOME_CLASS_BUILDER)), scope);
        Assert.assertNull(psiClass);
        Mockito.verify(javaFileManager).findClass(LombokElementFinderTest.BASE_CLASS, scope);
    }
}

