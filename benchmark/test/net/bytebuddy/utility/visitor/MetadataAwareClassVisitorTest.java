package net.bytebuddy.utility.visitor;


import OpenedClassReader.ASM_API;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class MetadataAwareClassVisitorTest {
    private MetadataAwareClassVisitorTest.DelegatingMetadataAwareClassVisitor classVisitor;

    @Test
    public void testVisit() {
        visit(0, 0, null, null, null, null);
        MatcherAssert.assertThat(classVisitor.nestHostVisited, Is.is(false));
        MatcherAssert.assertThat(classVisitor.outerClassVisited, Is.is(false));
        MatcherAssert.assertThat(classVisitor.afterAttributesVisited, Is.is(false));
    }

    @Test
    public void testVisitSource() {
        visitSource(null, null);
        MatcherAssert.assertThat(classVisitor.nestHostVisited, Is.is(false));
        MatcherAssert.assertThat(classVisitor.outerClassVisited, Is.is(false));
        MatcherAssert.assertThat(classVisitor.afterAttributesVisited, Is.is(false));
    }

    @Test
    public void testVisitNestHost() {
        visitNestHost(null);
        MatcherAssert.assertThat(classVisitor.nestHostVisited, Is.is(false));
        MatcherAssert.assertThat(classVisitor.outerClassVisited, Is.is(false));
        MatcherAssert.assertThat(classVisitor.afterAttributesVisited, Is.is(false));
    }

    @Test
    public void testVisitOuterClass() {
        visitOuterClass(null, null, null);
        MatcherAssert.assertThat(classVisitor.nestHostVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.outerClassVisited, Is.is(false));
        MatcherAssert.assertThat(classVisitor.afterAttributesVisited, Is.is(false));
    }

    @Test
    public void testVisitAnnotation() {
        visitAnnotation(null, false);
        MatcherAssert.assertThat(classVisitor.nestHostVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.outerClassVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.afterAttributesVisited, Is.is(false));
    }

    @Test
    public void testVisitTypeAnnotation() {
        visitTypeAnnotation(0, null, null, false);
        MatcherAssert.assertThat(classVisitor.nestHostVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.outerClassVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.afterAttributesVisited, Is.is(false));
    }

    @Test
    public void testVisitAttribute() {
        visitAttribute(null);
        MatcherAssert.assertThat(classVisitor.nestHostVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.outerClassVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.afterAttributesVisited, Is.is(false));
    }

    @Test
    public void testVisitInnerClass() {
        visitInnerClass(null, null, null, 0);
        MatcherAssert.assertThat(classVisitor.nestHostVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.outerClassVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.afterAttributesVisited, Is.is(true));
    }

    @Test
    public void testVisitNestMember() {
        visitNestMember(null);
        MatcherAssert.assertThat(classVisitor.nestHostVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.outerClassVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.afterAttributesVisited, Is.is(true));
    }

    @Test
    public void testVisitField() {
        visitField(0, null, null, null, null);
        MatcherAssert.assertThat(classVisitor.nestHostVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.outerClassVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.afterAttributesVisited, Is.is(true));
    }

    @Test
    public void testVisitMethod() {
        visitMethod(0, null, null, null, null);
        MatcherAssert.assertThat(classVisitor.nestHostVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.outerClassVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.afterAttributesVisited, Is.is(true));
    }

    @Test
    public void testVisitEnd() {
        visitEnd();
        MatcherAssert.assertThat(classVisitor.nestHostVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.outerClassVisited, Is.is(true));
        MatcherAssert.assertThat(classVisitor.afterAttributesVisited, Is.is(true));
    }

    private static class DelegatingMetadataAwareClassVisitor extends MetadataAwareClassVisitor {
        private boolean nestHostVisited;

        private boolean outerClassVisited;

        private boolean afterAttributesVisited;

        private DelegatingMetadataAwareClassVisitor() {
            super(ASM_API, null);
        }

        @Override
        protected void onNestHost() {
            nestHostVisited = true;
        }

        @Override
        protected void onOuterType() {
            outerClassVisited = true;
        }

        @Override
        protected void onAfterAttributes() {
            afterAttributesVisited = true;
        }
    }
}

