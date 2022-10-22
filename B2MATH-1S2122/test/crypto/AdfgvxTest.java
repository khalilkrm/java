package crypto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AdfgvxTest {
	/*
	 * CONSTRUCTOR TESTS
	 */
	@Test
	void testAdfgvxExample() {
		assertNotNull(new Adfgvx("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C", "BRUTES"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
	"BJLZ4PWWAUVI0H3Y5MK8FEXQGDO16T9NSR2C",
	"",
	"BJLZ4PWWAUVI0H3Y5MK8FEXQGD116T9NSR2C",
	"BJLZ4PWWAUVI0H3Y5MK8FEXQGDO16T9NSR,C",
	"BJLZ4PWWaUVI0H3Y5MK8FEXQGDO16T9NSR2C"})
	void testAdfgvxWithNonCorrectSubKeyExample(final String key) {
		assertThrows(IllegalArgumentException.class, () -> new Adfgvx(key, "BRUTES"));
	}

	/*
	 * INITIALIZATION TESTS
	 */

	@ParameterizedTest
	@ValueSource(strings = {"BRUUTE", "BRUT3", ""})
	public void testAdfgvxWithNonCorrectTranspoKey(final String key) {
		assertThrows(IllegalArgumentException.class, () -> new Adfgvx("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C", key));
	}

	/*
	 * ENCRYPT TESTS
	 */

	@ParameterizedTest
	@MethodSource("encryptCasesProvider")
	void testEncryptExample(String substitutionKey, String transpositionKey, String message, String expected) {
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(expected, cypher.encrypt(message));
	}

	public static Stream<Arguments> encryptCasesProvider() {
		return Stream.of(
				Arguments.of("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","BRUTES","DEMANDE RENFORTS D'URGENCE","VDGXX-VVXFV-GVXXX-XDFGD-GDAXX-DGFFG-DXGDG-FXGGG-GXXGV-DGG"),
				Arguments.of("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","SALUT","DEMANDE RENFORTS D'URGENCE","DDDGF-VDVDX-GFGGV-XDAXX-VXVGG-GVGXG-FDXDX-FXGGX-GXGXF-XGGXX"),
				Arguments.of("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","OUAIJES","DEMANDE RENFORTS D'URGENCE","GDGFV-AGXGD-VGXXG-VGXDG-GFDXG-DGXVF-XFXGX-DGGXX-DXDXG-VFVX"),
				Arguments.of("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","BRUTE","DEMANDE RENFORTS","VXVGG-GFDXD-XFDDD-GFVGX-GXFXG-FGGVX"),
				Arguments.of("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","BRUTE","",""),
				Arguments.of("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","BRUTE","SALUT","XFAXF-DFVDG"),
				Arguments.of("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","BRUTE","$",""),
				Arguments.of("BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C","BRUTE","$","")
		);
	}

	/*
	 * DECRYPT TESTS
	 */
	@Test
	void testDecryptExample() {
		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTES";
		String message = "VDGXX-VVXFV-GVXXX-XDFGD-GDAXX-DGFFG-DXGDG-FXGGG-GXXGV-DGG";
		String decrypted = "DEMANDERENFORTSDURGENCEC";
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		assertEquals(decrypted, cypher.decrypt(message));
	}
}
