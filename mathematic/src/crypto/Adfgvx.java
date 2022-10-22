package crypto;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class Adfgvx {

	/**
	 * Constructor
	 */
	
	private final AdfgvxEncrypt encrypter;
	private final AdfgvxDecrypt decrypter;
	
	public Adfgvx(String substitutionKey, String transpositionKey) {
		validateSubstitutionKeyOrThrow(substitutionKey);
		validateTranspositionKeyOrThrow(transpositionKey);
		final Map<Character, String> mapOfCharToDigram = createMappingOfCharToDigram(substitutionKey);
		encrypter = new AdfgvxEncrypt(mapOfCharToDigram, transpositionKey);
		decrypter = new AdfgvxDecrypt(mapOfCharToDigram, transpositionKey);
	}

	/* Initialization */
	private void validateSubstitutionKeyOrThrow(final String substitutionKey) {
		if(substitutionKey.length() < 36)
			throw new IllegalArgumentException("La clé secrète de substitution doit être une chaîne de caractères contenant les 26 lettres majuscules (A à Z) et les 10 chiffres (0 à 9) dans un ordre quelconque et sans répétitions");

		final long lengthOfFilteredKey = substitutionKey
				.chars()
				.filter(value -> (value >= 'A' && value <= 'Z') || (value >= 48 && value <= 57))
				.distinct()
				.count();

		if(!(lengthOfFilteredKey == substitutionKey.length()))
			throw new IllegalArgumentException("La clé secrète de substitution doit être une chaîne de caractères contenant les 26 lettres majuscules (A à Z) et les 10 chiffres (0 à 9) dans un ordre quelconque et sans répétitions");
	}

	public void validateTranspositionKeyOrThrow(final String transpositionKey) {

		if(transpositionKey.length() <= 0)
			throw new IllegalArgumentException("La clé secrète de transposition est doit être chaîne de caractères contenant uniquement des lettres majuscules (A à Z) sans répétitions.");

		final long lengthOfFilteredKey = transpositionKey
				.chars()
				.filter(value -> (value >= 'A' && value <= 'Z'))
				.distinct()
				.count();

		if(!(lengthOfFilteredKey == transpositionKey.length()))
			throw new IllegalArgumentException("La clé secrète de transposition est doit être chaîne de caractères contenant uniquement des lettres majuscules (A à Z) sans répétitions.");

	}
	
	private Map<Character, String> createMappingOfCharToDigram(final String substitutionKey) {
		Map<Character, String> mapOfCharToDigram = new HashMap<>(36 + AdfgvxConstants.numberOfAccentuateLetters);
		
		String digram;
		int column = 0;
		int row = 0;
		int indexOfCurrentSubKeyLetter = 0;
		final int startOfNextColumn = 0;
		final Predicate<Integer> firstRowComputed = index -> index > AdfgvxConstants.substitutionChars.length - 1;

		for(indexOfCurrentSubKeyLetter = 0; indexOfCurrentSubKeyLetter < substitutionKey.length(); indexOfCurrentSubKeyLetter++) {
			column = indexOfCurrentSubKeyLetter % AdfgvxConstants.substitutionChars.length;
			row = (column == startOfNextColumn && firstRowComputed.test(indexOfCurrentSubKeyLetter)) ? row + 1 : row;
			digram = String.format("%c%c", AdfgvxConstants.substitutionChars[row], AdfgvxConstants.substitutionChars[column]);
			mapOfCharToDigram.put(substitutionKey.charAt(indexOfCurrentSubKeyLetter), digram);
		}
		
		return mapOfCharToDigram;
	}
	
	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Encrypts a text into an ADFGVX cryptogram. The cryptogram is formatted with
	 * an hyphen '-' after each group of 5 characters. Ex: VXVGG-GFDXD-XFDDD
	 * 
	 * @param textToEncrypt A text to encrypt
	 * @return the ADFGVX cryptogram
	 */
	public String encrypt(String textToEncrypt) {
		return encrypter.encrypt(textToEncrypt);
	}

	/**
	 * Decrypts an ADFGVX cryptogram.
	 * 
	 * @param textToDecrypt An ADFGVX cryptogram
	 * @return the decrypted text
	 */
	public String decrypt(String textToDecrypt) {
		return decrypter.decrypt(textToDecrypt);
	}

	/*
	 * MAIN - TESTS
	 */
	/**
	 * The main method illustrates the use of the ADFGVX class.
	 * 
	 * @param args None
	 */
	public static void main(String[] args) {
		System.out.println("ADFGVX - Exemple de l'énoncé");
		System.out.println();

		String substitutionKey = "BJLZ4PW7AUVI0H3Y5MK8FEXQGDO16T9NSR2C";
		String transpositionKey = "BRUTE";
		//DEMANDE RENFORTS D'URGENCE
		//DéMäNDè RëNFöRTS D'üRGENCE
		String message = "DEMANDE RENFORTS";
		System.out.println("Message : " + message);
		Adfgvx cypher = new Adfgvx(substitutionKey, transpositionKey);
		String encrypted = cypher.encrypt(message);
		System.out.println("Message chiffré : " + encrypted);
		String decrypted = cypher.decrypt(encrypted);
		System.out.println("Message déchiffré : " + decrypted);

		System.out.println();
		System.out.println("-------------------------------------");
		System.out.println();
		
		//20_000_000

		int repeatCount = 20_000_000;

		System.out.println(">>> PERFORMANCE - ENCRYPT x " + repeatCount);
		long time = System.currentTimeMillis();
		for (int i = 0; i < repeatCount; i++) {
			cypher.encrypt(message);
		}
		
		System.out.printf("Elapsed time = %.2f seconds\n", (System.currentTimeMillis() - time) / 1000.0);
		System.out.println();

		System.out.println(">>> PERFORMANCE - DECRYPT x " + repeatCount);
		time = System.currentTimeMillis();
		for (int i = 0; i < repeatCount; i++) {
			cypher.decrypt(encrypted);
		}
		System.out.printf("Elapsed time = %.2f seconds\n", (System.currentTimeMillis() - time) / 1000.0);
	}
}
