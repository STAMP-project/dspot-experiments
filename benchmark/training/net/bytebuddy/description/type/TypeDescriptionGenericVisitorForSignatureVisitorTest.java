package net.bytebuddy.description.type;


import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.signature.SignatureVisitor;

import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.ForSignatureVisitor.<init>;


public class TypeDescriptionGenericVisitorForSignatureVisitorTest {
    @Test(expected = IllegalStateException.class)
    public void testSignatureVisitorTypeVariableThrowsException() throws Exception {
        new TypeDescription.Generic.Visitor.ForSignatureVisitor(Mockito.mock(SignatureVisitor.class)).onWildcard(Mockito.mock(TypeDescription.Generic.class));
    }
}

