package org.xwiki.component.internal.namespace;


import org.junit.Assert;
import org.junit.Test;
import org.xwiki.component.namespace.Namespace;
import org.xwiki.component.namespace.NamespaceUtils;


public class AmplNamespaceUtilsTest {
    @Test(timeout = 10000)
    public void toNamespace_literalMutationString139() throws Exception {
        Namespace o_toNamespace_literalMutationString139__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString139__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString139__3 = NamespaceUtils.toNamespace("namespace");
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString139__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString139__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString139__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString139__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString139__5 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString139__5)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString139__5)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString139__5)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString139__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString139__7 = NamespaceUtils.toNamespace("t\\:pe:val\\ue");
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString139__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString139__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString139__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString139__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString139__9 = NamespaceUtils.toNamespace(":value");
        Assert.assertEquals(":value", ((Namespace) (o_toNamespace_literalMutationString139__9)).toString());
        Assert.assertEquals(111995994, ((int) (((Namespace) (o_toNamespace_literalMutationString139__9)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString139__9)).getValue());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString139__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString139__1);
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString139__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString139__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString139__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString139__3)).getType());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString139__5)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString139__5)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString139__5)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString139__5)).getType());
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString139__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString139__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString139__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString139__7)).getType());
    }

    @Test(timeout = 10000)
    public void toNamespace_literalMutationString133() throws Exception {
        Namespace o_toNamespace_literalMutationString133__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString133__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString133__3 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString133__3)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString133__3)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString133__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString133__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString133__5 = NamespaceUtils.toNamespace("type:value");
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString133__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString133__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString133__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString133__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString133__7 = NamespaceUtils.toNamespace("t\\:pe:val\\ue");
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString133__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString133__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString133__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString133__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString133__9 = NamespaceUtils.toNamespace(":value");
        Assert.assertEquals(":value", ((Namespace) (o_toNamespace_literalMutationString133__9)).toString());
        Assert.assertEquals(111995994, ((int) (((Namespace) (o_toNamespace_literalMutationString133__9)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString133__9)).getValue());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString133__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString133__1);
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString133__3)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString133__3)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString133__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString133__3)).getType());
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString133__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString133__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString133__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString133__5)).getType());
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString133__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString133__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString133__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString133__7)).getType());
    }

    @Test(timeout = 10000)
    public void toNamespace_literalMutationString146() throws Exception {
        Namespace o_toNamespace_literalMutationString146__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString146__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString146__3 = NamespaceUtils.toNamespace("namespace");
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString146__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString146__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString146__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString146__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString146__5 = NamespaceUtils.toNamespace("type:value");
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString146__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString146__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString146__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString146__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString146__7 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString146__7)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString146__7)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString146__7)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString146__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString146__9 = NamespaceUtils.toNamespace(":value");
        Assert.assertEquals(":value", ((Namespace) (o_toNamespace_literalMutationString146__9)).toString());
        Assert.assertEquals(111995994, ((int) (((Namespace) (o_toNamespace_literalMutationString146__9)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString146__9)).getValue());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString146__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString146__1);
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString146__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString146__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString146__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString146__3)).getType());
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString146__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString146__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString146__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString146__5)).getType());
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString146__7)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString146__7)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString146__7)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString146__7)).getType());
    }

    @Test(timeout = 10000)
    public void toNamespace_literalMutationString151() throws Exception {
        Namespace o_toNamespace_literalMutationString151__1 = NamespaceUtils.toNamespace(null);
        Assert.assertNull(o_toNamespace_literalMutationString151__1);
        new Namespace(null, "namespace");
        Namespace o_toNamespace_literalMutationString151__3 = NamespaceUtils.toNamespace("namespace");
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString151__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString151__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString151__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString151__3)).getType());
        new Namespace("type", "value");
        Namespace o_toNamespace_literalMutationString151__5 = NamespaceUtils.toNamespace("type:value");
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString151__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString151__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString151__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString151__5)).getType());
        new Namespace("t:pe", "val\\ue");
        Namespace o_toNamespace_literalMutationString151__7 = NamespaceUtils.toNamespace("t\\:pe:val\\ue");
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString151__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString151__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString151__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString151__7)).getType());
        new Namespace("", "value");
        Namespace o_toNamespace_literalMutationString151__9 = NamespaceUtils.toNamespace("");
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString151__9)).toString());
        Assert.assertEquals(23273, ((int) (((Namespace) (o_toNamespace_literalMutationString151__9)).hashCode())));
        Assert.assertEquals("", ((Namespace) (o_toNamespace_literalMutationString151__9)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString151__9)).getType());
        Assert.assertNull(o_toNamespace_literalMutationString151__1);
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString151__3)).toString());
        Assert.assertEquals(1252241476, ((int) (((Namespace) (o_toNamespace_literalMutationString151__3)).hashCode())));
        Assert.assertEquals("namespace", ((Namespace) (o_toNamespace_literalMutationString151__3)).getValue());
        Assert.assertNull(((Namespace) (o_toNamespace_literalMutationString151__3)).getType());
        Assert.assertEquals("type:value", ((Namespace) (o_toNamespace_literalMutationString151__5)).toString());
        Assert.assertEquals(244293564, ((int) (((Namespace) (o_toNamespace_literalMutationString151__5)).hashCode())));
        Assert.assertEquals("value", ((Namespace) (o_toNamespace_literalMutationString151__5)).getValue());
        Assert.assertEquals("type", ((Namespace) (o_toNamespace_literalMutationString151__5)).getType());
        Assert.assertEquals("t\\:pe:val\\ue", ((Namespace) (o_toNamespace_literalMutationString151__7)).toString());
        Assert.assertEquals(-693755621, ((int) (((Namespace) (o_toNamespace_literalMutationString151__7)).hashCode())));
        Assert.assertEquals("val\\ue", ((Namespace) (o_toNamespace_literalMutationString151__7)).getValue());
        Assert.assertEquals("t:pe", ((Namespace) (o_toNamespace_literalMutationString151__7)).getType());
    }
}

