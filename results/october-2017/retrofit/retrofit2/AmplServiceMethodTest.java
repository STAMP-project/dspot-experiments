package retrofit2;


public final class AmplServiceMethodTest {
    @org.junit.Test
    public void pathParameterParsing() throws java.lang.Exception {
        retrofit2.AmplServiceMethodTest.expectParams("/");
        retrofit2.AmplServiceMethodTest.expectParams("/foo");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{}");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{taco}", "taco");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{t}", "t");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{!!!}/");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{}/{taco}", "taco");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{taco}/or/{burrito}", "taco", "burrito");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{taco}/or/{taco}", "taco");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{taco-shell}", "taco-shell");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{taco_shell}", "taco_shell");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{sha256}", "sha256");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{TACO}", "TACO");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{taco}/{tAco}/{taCo}", "taco", "tAco", "taCo");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{1}");
    }

    private static void expectParams(java.lang.String path, java.lang.String... expected) {
        java.util.Set<java.lang.String> calculated = retrofit2.retrofit2.ServiceMethod.parsePathParameters(path);
        org.assertj.core.api.Assertions.assertThat(calculated).containsExactly(expected);
    }
}

