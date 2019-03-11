package uk.co.real_logic.sbe.generation.rust;


import java.io.IOException;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class RustGeneratorTest {
    private static final String BROAD_USE_CASES_SCHEMA = "code-generation-schema";

    private static final String BASIC_TYPES_SCHEMA = "basic-types-schema";

    private static final String NESTED_GROUP_SCHEMA = "nested-group-schema";

    private SingleStringOutputManager outputManager;

    @Rule
    public final TemporaryFolder folderRule = new TemporaryFolder();

    @Test(expected = NullPointerException.class)
    public void nullOutputManagerTossesNpe() {
        new RustGenerator(RustTest.minimalDummyIr(), null);
    }

    @Test(expected = NullPointerException.class)
    public void nullIrTossesNpe() {
        new RustGenerator(null, outputManager);
    }

    @Test
    public void generateSharedImports() throws IOException {
        RustGenerator.generateSharedImports(RustGeneratorTest.generateIrForResource(RustGeneratorTest.BROAD_USE_CASES_SCHEMA), outputManager);
        RustGeneratorTest.assertContainsSharedImports(outputManager.toString());
    }

    @Test
    public void generateBasicEnum() throws IOException {
        RustGenerator.generateSharedImports(RustGeneratorTest.generateIrForResource(RustGeneratorTest.BASIC_TYPES_SCHEMA), outputManager);
        RustGeneratorTest.assertContainsSharedImports(outputManager.toString());
    }

    @Test
    public void fullGenerateBasicTypes() {
        final String generatedRust = RustGeneratorTest.fullGenerateForResource(outputManager, RustGeneratorTest.BASIC_TYPES_SCHEMA);
        RustGeneratorTest.assertContainsSharedImports(generatedRust);
        assertContainsNumericEnum(generatedRust);
    }

    @Test
    public void fullGenerateBroadUseCase() throws IOException, InterruptedException {
        final String generatedRust = RustGeneratorTest.fullGenerateForResource(outputManager, "example-schema");
        RustGeneratorTest.assertContainsSharedImports(generatedRust);
        RustGeneratorTest.assertContains(generatedRust, ("pub fn car_fields(mut self) -> CodecResult<(&\'d CarFields, CarFuelFiguresHeaderDecoder<\'d>)> {\n" + (("    let v = self.scratch.read_type::<CarFields>(49)?;\n" + "    Ok((v, CarFuelFiguresHeaderDecoder::wrap(self.scratch)))\n") + "  }")));
        final String expectedBooleanTypeDeclaration = "#[derive(Clone,Copy,Debug,PartialEq,Eq,PartialOrd,Ord,Hash)]\n" + (((("#[repr(u8)]\n" + "pub enum BooleanType {\n") + "  F = 0u8,\n") + "  T = 1u8,\n") + "}\n");
        Assert.assertTrue(generatedRust.contains(expectedBooleanTypeDeclaration));
        final String expectedCharTypeDeclaration = "#[derive(Clone,Copy,Debug,PartialEq,Eq,PartialOrd,Ord,Hash)]\n" + ((((("#[repr(i8)]\n" + "pub enum Model {\n") + "  A = 65i8,\n") + "  B = 66i8,\n") + "  C = 67i8,\n") + "}\n");
        Assert.assertTrue(generatedRust.contains(expectedCharTypeDeclaration));
        assertRustBuildable(generatedRust, Optional.of("example-schema"));
    }

    @Test
    public void checkValidRustFromAllExampleSchema() throws IOException, InterruptedException {
        final String[] schema = new String[]{ "basic-group-schema", RustGeneratorTest.BASIC_TYPES_SCHEMA, "basic-variable-length-schema", "block-length-schema", RustGeneratorTest.BROAD_USE_CASES_SCHEMA, "composite-elements-schema", "composite-elements-schema-rc4", "composite-offsets-schema", "encoding-types-schema", "example-schema", "FixBinary", "group-with-data-schema", "group-with-constant-fields", "issue435", "message-block-length-test", RustGeneratorTest.NESTED_GROUP_SCHEMA, "new-order-single-schema" };
        for (final String s : schema) {
            assertSchemaInterpretableAsRust(s);
        }
    }

    @Test
    public void constantEnumFields() throws IOException, InterruptedException {
        final String rust = RustGeneratorTest.fullGenerateForResource(outputManager, "constant-enum-fields");
        RustGeneratorTest.assertContainsSharedImports(rust);
        final String expectedCharTypeDeclaration = "#[derive(Clone,Copy,Debug,PartialEq,Eq,PartialOrd,Ord,Hash)]\n" + ((((("#[repr(i8)]\n" + "pub enum Model {\n") + "  A = 65i8,\n") + "  B = 66i8,\n") + "  C = 67i8,\n") + "}\n");
        RustGeneratorTest.assertContains(rust, expectedCharTypeDeclaration);
        RustGeneratorTest.assertContains(rust, "pub struct ConstantEnumsFields {\n}");
        RustGeneratorTest.assertContains(rust, "impl ConstantEnumsFields {");
        RustGeneratorTest.assertContains(rust, ("  pub fn c() -> Model {\n" + "    Model::C\n  }"));
        RustGeneratorTest.assertContains(rust, "impl ConstantEnumsFMember {");
        RustGeneratorTest.assertContains(rust, ("  pub fn k() -> Model {\n" + "    Model::C\n  }"));
        RustGeneratorTest.assertContains(rust, ("pub fn constant_enums_fields(mut self) -> " + ((("CodecResult<(&\'d ConstantEnumsFields, ConstantEnumsFHeaderDecoder<\'d>)> {\n" + "    let v = self.scratch.read_type::<ConstantEnumsFields>(0)?;\n") + "    Ok((v, ConstantEnumsFHeaderDecoder::wrap(self.scratch)))\n") + "  }")));
        assertRustBuildable(rust, Optional.of("constant-enum-fields"));
    }

    @Test
    public void constantFieldsCase() throws IOException, InterruptedException {
        final String rust = RustGeneratorTest.fullGenerateForResource(outputManager, "group-with-constant-fields");
        RustGeneratorTest.assertContainsSharedImports(rust);
        final String expectedCharTypeDeclaration = "#[derive(Clone,Copy,Debug,PartialEq,Eq,PartialOrd,Ord,Hash)]\n" + ((((("#[repr(i8)]\n" + "pub enum Model {\n") + "  A = 65i8,\n") + "  B = 66i8,\n") + "  C = 67i8,\n") + "}\n");
        RustGeneratorTest.assertContains(rust, expectedCharTypeDeclaration);
        final String expectedComposite = "pub struct CompositeWithConst {\n" + ("  pub w:u8,\n" + "}");
        RustGeneratorTest.assertContains(rust, expectedComposite);
        RustGeneratorTest.assertContains(rust, "impl CompositeWithConst {");
        RustGeneratorTest.assertContains(rust, ("  pub fn x() -> u8 {\n" + "    250u8\n  }"));
        RustGeneratorTest.assertContains(rust, ("  pub fn y() -> u16 {\n" + "    9000u16\n  }"));
        RustGeneratorTest.assertContains(rust, ("pub struct ConstantsGaloreFields {\n" + ("  pub a:u8,\n" + "  pub e:CompositeWithConst,\n}")));
        RustGeneratorTest.assertContains(rust, "impl ConstantsGaloreFields {");
        RustGeneratorTest.assertContains(rust, ("  pub fn b() -> u16 {\n" + "    9000u16\n  }"));
        RustGeneratorTest.assertContains(rust, ("  pub fn c() -> Model {\n" + "    Model::C\n  }"));
        RustGeneratorTest.assertContains(rust, ("  pub fn d() -> u16 {\n" + "    9000u16\n  }"));
        RustGeneratorTest.assertContains(rust, ("pub struct ConstantsGaloreFMember {\n" + ("  pub g:u8,\n" + "  pub h:CompositeWithConst,\n}")));
        RustGeneratorTest.assertContains(rust, "impl ConstantsGaloreFMember {");
        RustGeneratorTest.assertContains(rust, ("  pub fn i() -> u16 {\n" + "    9000u16\n  }"));
        RustGeneratorTest.assertContains(rust, ("  pub fn j() -> u16 {\n" + "    9000u16\n  }"));
        RustGeneratorTest.assertContains(rust, ("  pub fn k() -> Model {\n" + "    Model::C\n  }"));
        RustGeneratorTest.assertContains(rust, ("  pub fn l() -> &\'static str {\n" + "    \"Huzzah\"\n  }"));
        assertRustBuildable(rust, Optional.of("group-with-constant-fields"));
    }
}

