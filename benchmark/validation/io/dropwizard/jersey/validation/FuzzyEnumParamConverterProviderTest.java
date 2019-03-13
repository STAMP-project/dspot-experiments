package io.dropwizard.jersey.validation;


import Response.Status.Family;
import io.dropwizard.jersey.errors.ErrorMessage;
import java.lang.annotation.Annotation;
import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ParamConverter;
import org.junit.jupiter.api.Test;


public class FuzzyEnumParamConverterProviderTest {
    private final FuzzyEnumParamConverterProvider paramConverterProvider = new FuzzyEnumParamConverterProvider();

    private enum Fuzzy {

        A_1,
        A_2;}

    private enum WithToString {

        A_1,
        A_2;
        @Override
        public String toString() {
            return ("<" + (this.name())) + ">";
        }
    }

    private enum ExplicitFromString {

        A("1"),
        B("2");
        private final String code;

        ExplicitFromString(String code) {
            this.code = code;
        }

        @Nullable
        public static FuzzyEnumParamConverterProviderTest.ExplicitFromString fromString(String str) {
            for (FuzzyEnumParamConverterProviderTest.ExplicitFromString e : FuzzyEnumParamConverterProviderTest.ExplicitFromString.values()) {
                if (str.equals(e.code)) {
                    return e;
                }
            }
            return null;
        }
    }

    private enum ExplicitFromStringThrowsWebApplicationException {

        A("1"),
        B("2");
        private final String code;

        ExplicitFromStringThrowsWebApplicationException(String code) {
            this.code = code;
        }

        @SuppressWarnings("unused")
        public String getCode() {
            return this.code;
        }

        @SuppressWarnings("unused")
        public static FuzzyEnumParamConverterProviderTest.ExplicitFromStringThrowsWebApplicationException fromString(String str) {
            throw new WebApplicationException(Response.status(new Response.StatusType() {
                @Override
                public int getStatusCode() {
                    return 418;
                }

                @Override
                public Family getFamily() {
                    return Family.CLIENT_ERROR;
                }

                @Override
                public String getReasonPhrase() {
                    return "I am a teapot";
                }
            }).build());
        }
    }

    private enum ExplicitFromStringThrowsOtherException {

        A("1"),
        B("2");
        private final String code;

        ExplicitFromStringThrowsOtherException(String code) {
            this.code = code;
        }

        @SuppressWarnings("unused")
        public String getCode() {
            return this.code;
        }

        @SuppressWarnings("unused")
        public static FuzzyEnumParamConverterProviderTest.ExplicitFromStringThrowsOtherException fromString(String str) {
            throw new RuntimeException("Boo!");
        }
    }

    private enum ExplicitFromStringNonStatic {

        A("1"),
        B("2");
        private final String code;

        ExplicitFromStringNonStatic(String code) {
            this.code = code;
        }

        @Nullable
        public FuzzyEnumParamConverterProviderTest.ExplicitFromStringNonStatic fromString(String str) {
            for (FuzzyEnumParamConverterProviderTest.ExplicitFromStringNonStatic e : FuzzyEnumParamConverterProviderTest.ExplicitFromStringNonStatic.values()) {
                if (str.equals(e.code)) {
                    return e;
                }
            }
            return null;
        }
    }

    private enum ExplicitFromStringPrivate {

        A("1"),
        B("2");
        private final String code;

        ExplicitFromStringPrivate(String code) {
            this.code = code;
        }

        @Nullable
        private static FuzzyEnumParamConverterProviderTest.ExplicitFromStringPrivate fromString(String str) {
            for (FuzzyEnumParamConverterProviderTest.ExplicitFromStringPrivate e : FuzzyEnumParamConverterProviderTest.ExplicitFromStringPrivate.values()) {
                if (str.equals(e.code)) {
                    return e;
                }
            }
            return null;
        }
    }

    static class Klass {}

    @Test
    public void testFuzzyEnum() {
        final ParamConverter<FuzzyEnumParamConverterProviderTest.Fuzzy> converter = getConverter(FuzzyEnumParamConverterProviderTest.Fuzzy.class);
        assertThat(converter.fromString(null)).isNull();
        assertThat(converter.fromString("A.1")).isSameAs(FuzzyEnumParamConverterProviderTest.Fuzzy.A_1);
        assertThat(converter.fromString("A-1")).isSameAs(FuzzyEnumParamConverterProviderTest.Fuzzy.A_1);
        assertThat(converter.fromString("A_1")).isSameAs(FuzzyEnumParamConverterProviderTest.Fuzzy.A_1);
        assertThat(converter.fromString(" A_1")).isSameAs(FuzzyEnumParamConverterProviderTest.Fuzzy.A_1);
        assertThat(converter.fromString("A_1 ")).isSameAs(FuzzyEnumParamConverterProviderTest.Fuzzy.A_1);
        assertThat(converter.fromString("A_2")).isSameAs(FuzzyEnumParamConverterProviderTest.Fuzzy.A_2);
        final WebApplicationException throwable = catchThrowableOfType(() -> converter.fromString("B"), WebApplicationException.class);
        assertThat(throwable.getResponse()).extracting(Response::getEntity).matches(( e) -> (((ErrorMessage) (e)).getCode()) == 400).matches(( e) -> ((ErrorMessage) (e)).getMessage().contains("A_1")).matches(( e) -> ((ErrorMessage) (e)).getMessage().contains("A_2"));
    }

    @Test
    public void testToString() {
        final ParamConverter<FuzzyEnumParamConverterProviderTest.WithToString> converter = getConverter(FuzzyEnumParamConverterProviderTest.WithToString.class);
        assertThat(converter.toString(FuzzyEnumParamConverterProviderTest.WithToString.A_1)).isEqualTo("<A_1>");
    }

    @Test
    public void testNonEnum() {
        assertThat(paramConverterProvider.getConverter(FuzzyEnumParamConverterProviderTest.Klass.class, null, new Annotation[]{  })).isNull();
    }

    @Test
    public void testEnumViaExplicitFromString() {
        final ParamConverter<FuzzyEnumParamConverterProviderTest.ExplicitFromString> converter = getConverter(FuzzyEnumParamConverterProviderTest.ExplicitFromString.class);
        assertThat(converter.fromString("1")).isSameAs(FuzzyEnumParamConverterProviderTest.ExplicitFromString.A);
        assertThat(converter.fromString("2")).isSameAs(FuzzyEnumParamConverterProviderTest.ExplicitFromString.B);
        final WebApplicationException throwable = catchThrowableOfType(() -> converter.fromString("3"), WebApplicationException.class);
        assertThat(throwable.getResponse()).extracting(Response::getEntity).matches(( e) -> (((ErrorMessage) (e)).getCode()) == 400).matches(( e) -> ((ErrorMessage) (e)).getMessage().contains("is not a valid ExplicitFromString"));
    }

    @Test
    public void testEnumViaExplicitFromStringThatThrowsWebApplicationException() {
        final ParamConverter<FuzzyEnumParamConverterProviderTest.ExplicitFromStringThrowsWebApplicationException> converter = getConverter(FuzzyEnumParamConverterProviderTest.ExplicitFromStringThrowsWebApplicationException.class);
        final WebApplicationException throwable = catchThrowableOfType(() -> converter.fromString("3"), WebApplicationException.class);
        assertThat(throwable.getResponse()).extracting(Response::getStatusInfo).matches(( e) -> (((Response.StatusType) (e)).getStatusCode()) == 418).matches(( e) -> ((Response.StatusType) (e)).getReasonPhrase().contains("I am a teapot"));
    }

    @Test
    public void testEnumViaExplicitFromStringThatThrowsOtherException() {
        final ParamConverter<FuzzyEnumParamConverterProviderTest.ExplicitFromStringThrowsOtherException> converter = getConverter(FuzzyEnumParamConverterProviderTest.ExplicitFromStringThrowsOtherException.class);
        final WebApplicationException throwable = catchThrowableOfType(() -> converter.fromString("1"), WebApplicationException.class);
        assertThat(throwable.getResponse()).extracting(Response::getEntity).matches(( e) -> (((ErrorMessage) (e)).getCode()) == 400).matches(( e) -> ((ErrorMessage) (e)).getMessage().contains("Failed to convert"));
    }

    @Test
    public void testEnumViaExplicitFromStringNonStatic() {
        final ParamConverter<FuzzyEnumParamConverterProviderTest.ExplicitFromStringNonStatic> converter = getConverter(FuzzyEnumParamConverterProviderTest.ExplicitFromStringNonStatic.class);
        final WebApplicationException throwable = catchThrowableOfType(() -> converter.fromString("1"), WebApplicationException.class);
        assertThat(throwable.getResponse()).extracting(Response::getEntity).matches(( e) -> (((ErrorMessage) (e)).getCode()) == 400).matches(( e) -> ((ErrorMessage) (e)).getMessage().contains("A")).matches(( e) -> ((ErrorMessage) (e)).getMessage().contains("B"));
        assertThat(converter.fromString("A")).isSameAs(FuzzyEnumParamConverterProviderTest.ExplicitFromStringNonStatic.A);
    }

    @Test
    public void testEnumViaExplicitFromStringPrivate() {
        final ParamConverter<FuzzyEnumParamConverterProviderTest.ExplicitFromStringPrivate> converter = getConverter(FuzzyEnumParamConverterProviderTest.ExplicitFromStringPrivate.class);
        final WebApplicationException throwable = catchThrowableOfType(() -> converter.fromString("1"), WebApplicationException.class);
        assertThat(throwable.getResponse()).extracting(Response::getEntity).matches(( e) -> (((ErrorMessage) (e)).getCode()) == 400).matches(( e) -> ((ErrorMessage) (e)).getMessage().contains("Not permitted to call"));
    }
}

