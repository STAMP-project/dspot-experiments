

package retrofit2;


public final class AmplHttpExceptionTest {
    @org.junit.Test
    public void response() {
        retrofit2.Response<java.lang.String> response = retrofit2.Response.success("Hi");
        retrofit2.HttpException exception = new retrofit2.HttpException(response);
        org.assertj.core.api.Assertions.assertThat(exception.code()).isEqualTo(200);
        org.assertj.core.api.Assertions.assertThat(exception.message()).isEqualTo("OK");
        org.assertj.core.api.Assertions.assertThat(exception.response()).isSameAs(response);
    }

    @org.junit.Test
    public void nullResponseThrows() {
        try {
            new retrofit2.HttpException(null);
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("response == null");
        }
    }
}

