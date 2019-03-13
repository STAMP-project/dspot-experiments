package io.dropwizard.jackson;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ClientInfoStatus;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;


public class FuzzyEnumModuleTest {
    private final ObjectMapper mapper = new ObjectMapper();

    private enum EnumWithLowercase {

        lower_case_enum,
        mixedCaseEnum;}

    private enum EnumWithCreator {

        TEST;
        @JsonCreator
        public static FuzzyEnumModuleTest.EnumWithCreator fromString(String value) {
            return FuzzyEnumModuleTest.EnumWithCreator.TEST;
        }
    }

    private enum CurrencyCode {

        USD("United States dollar"),
        AUD("a_u_d"),
        CAD("c-a-d"),
        BLA("b.l.a"),
        EUR("Euro"),
        GBP("Pound sterling");
        private final String description;

        CurrencyCode(String name) {
            this.description = name;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    enum EnumWithPropertyAnno {

        @JsonProperty("a a")
        A,
        @JsonProperty("b b")
        // For this value, force use of anonymous sub-class, to ensure things still work
        B() {
            @Override
            public String toString() {
                return "bb";
            }
        },
        @JsonProperty("forgot password")
        FORGOT_PASSWORD,
        @JsonEnumDefaultValue
        DEFAULT;}

    @Test
    public void mapsUpperCaseEnums() throws Exception {
        assertThat(mapper.readValue("\"SECONDS\"", TimeUnit.class)).isEqualTo(TimeUnit.SECONDS);
    }

    @Test
    public void mapsLowerCaseEnums() throws Exception {
        assertThat(mapper.readValue("\"milliseconds\"", TimeUnit.class)).isEqualTo(TimeUnit.MILLISECONDS);
    }

    @Test
    public void mapsPaddedEnums() throws Exception {
        assertThat(mapper.readValue("\"   MINUTES \"", TimeUnit.class)).isEqualTo(TimeUnit.MINUTES);
    }

    @Test
    public void mapsSpacedEnums() throws Exception {
        assertThat(mapper.readValue("\"   MILLI SECONDS \"", TimeUnit.class)).isEqualTo(TimeUnit.MILLISECONDS);
    }

    @Test
    public void mapsDashedEnums() throws Exception {
        assertThat(mapper.readValue("\"REASON-UNKNOWN\"", ClientInfoStatus.class)).isEqualTo(ClientInfoStatus.REASON_UNKNOWN);
    }

    @Test
    public void mapsDottedEnums() throws Exception {
        assertThat(mapper.readValue("\"REASON.UNKNOWN\"", ClientInfoStatus.class)).isEqualTo(ClientInfoStatus.REASON_UNKNOWN);
    }

    @Test
    public void mapsWhenEnumHasCreator() throws Exception {
        assertThat(mapper.readValue("\"BLA\"", FuzzyEnumModuleTest.EnumWithCreator.class)).isEqualTo(FuzzyEnumModuleTest.EnumWithCreator.TEST);
    }

    @Test
    public void failsOnIncorrectValue() throws Exception {
        try {
            mapper.readValue("\"wrong\"", TimeUnit.class);
            failBecauseExceptionWasNotThrown(JsonMappingException.class);
        } catch (JsonMappingException e) {
            assertThat(e.getOriginalMessage()).isEqualTo(("Cannot deserialize value of type `java.util.concurrent.TimeUnit` from String \"wrong\": " + "wrong was not one of [NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS]"));
        }
    }

    @Test
    public void mapsToLowerCaseEnums() throws Exception {
        assertThat(mapper.readValue("\"lower_case_enum\"", FuzzyEnumModuleTest.EnumWithLowercase.class)).isEqualTo(FuzzyEnumModuleTest.EnumWithLowercase.lower_case_enum);
    }

    @Test
    public void mapsMixedCaseEnums() throws Exception {
        assertThat(mapper.readValue("\"mixedCaseEnum\"", FuzzyEnumModuleTest.EnumWithLowercase.class)).isEqualTo(FuzzyEnumModuleTest.EnumWithLowercase.mixedCaseEnum);
    }

    @Test
    public void readsEnumsUsingToString() throws Exception {
        final ObjectMapper toStringEnumsMapper = mapper.copy().configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        assertThat(toStringEnumsMapper.readValue("\"Pound sterling\"", FuzzyEnumModuleTest.CurrencyCode.class)).isEqualTo(FuzzyEnumModuleTest.CurrencyCode.GBP);
    }

    @Test
    public void readsUnknownEnumValuesAsNull() throws Exception {
        final ObjectMapper toStringEnumsMapper = mapper.copy().configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        assertThat(toStringEnumsMapper.readValue("\"Pound sterling\"", FuzzyEnumModuleTest.CurrencyCode.class)).isNull();
    }

    @Test
    public void readsUnknownEnumValuesUsingDefaultValue() throws Exception {
        final ObjectMapper toStringEnumsMapper = mapper.copy().configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
        assertThat(toStringEnumsMapper.readValue("\"Pound sterling\"", FuzzyEnumModuleTest.EnumWithPropertyAnno.class)).isEqualTo(FuzzyEnumModuleTest.EnumWithPropertyAnno.DEFAULT);
    }

    @Test
    public void readsEnumsUsingToStringWithDeserializationFeatureOff() throws Exception {
        assertThat(mapper.readValue("\"Pound sterling\"", FuzzyEnumModuleTest.CurrencyCode.class)).isEqualTo(FuzzyEnumModuleTest.CurrencyCode.GBP);
        assertThat(mapper.readValue("\"a_u_d\"", FuzzyEnumModuleTest.CurrencyCode.class)).isEqualTo(FuzzyEnumModuleTest.CurrencyCode.AUD);
        assertThat(mapper.readValue("\"c-a-d\"", FuzzyEnumModuleTest.CurrencyCode.class)).isEqualTo(FuzzyEnumModuleTest.CurrencyCode.CAD);
        assertThat(mapper.readValue("\"b.l.a\"", FuzzyEnumModuleTest.CurrencyCode.class)).isEqualTo(FuzzyEnumModuleTest.CurrencyCode.BLA);
    }

    @Test
    public void testEnumWithJsonPropertyRename() throws Exception {
        final String json = mapper.writeValueAsString(new FuzzyEnumModuleTest.EnumWithPropertyAnno[]{ FuzzyEnumModuleTest.EnumWithPropertyAnno.B, FuzzyEnumModuleTest.EnumWithPropertyAnno.A, FuzzyEnumModuleTest.EnumWithPropertyAnno.FORGOT_PASSWORD });
        assertThat(json).isEqualTo("[\"b b\",\"a a\",\"forgot password\"]");
        final FuzzyEnumModuleTest.EnumWithPropertyAnno[] result = mapper.readValue(json, FuzzyEnumModuleTest.EnumWithPropertyAnno[].class);
        assertThat(result).isNotNull().hasSize(3);
        assertThat(result[0]).isEqualTo(FuzzyEnumModuleTest.EnumWithPropertyAnno.B);
        assertThat(result[1]).isEqualTo(FuzzyEnumModuleTest.EnumWithPropertyAnno.A);
        assertThat(result[2]).isEqualTo(FuzzyEnumModuleTest.EnumWithPropertyAnno.FORGOT_PASSWORD);
    }
}

