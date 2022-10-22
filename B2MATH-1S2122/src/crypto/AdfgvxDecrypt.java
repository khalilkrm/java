package crypto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AdfgvxDecrypt {

	private final String transpositionKey;
	private final Map<String, Character> mapOfDigramToChar;
	private final Set<Character> setOfSubstitutionChars;
	private final String sortedTranspositionKey;
	private final int[] indexes;

	public AdfgvxDecrypt(final Map<Character, String> mapOfCharToDigram, final String transpositionKey) {
		this.transpositionKey = transpositionKey;
		mapOfDigramToChar = reverseMapping(mapOfCharToDigram, 36 + AdfgvxConstants.numberOfAccentuateLetters);
		setOfSubstitutionChars = arrayToSet( AdfgvxConstants.substitutionChars);
		sortedTranspositionKey = sort(transpositionKey);
		indexes = getIndexInSorted(transpositionKey, sortedTranspositionKey);
	}
	
	/* Initialization */
	
	private int[] getIndexInSorted(final String transpositionKey, final String sortedTranspositionKey) {
		int[] indexes = new int[transpositionKey.length()];
		
		int i = 0;
		for (String s : transpositionKey.split("")) {
			indexes[i++] = sortedTranspositionKey.indexOf(s);
		}
		
		return indexes;
	}
	
	private Map<String, Character> reverseMapping(final Map<Character, String> source, final int initialSize) {
		final Map<String, Character> reversed = new HashMap<>(initialSize);
		source.forEach((key, value) -> reversed.put(value, key));
		return reversed;
	}

	private String sort(final String source) {
		return String.join("", new TreeSet<>(Arrays.asList(source.split(""))));
	}

	private Set<Character> arrayToSet(final char[] source) {
		final Set<Character> set = new HashSet<>();
		for (char c : source) set.add(c);
		return set;
	}
	
	/*
	 * Decryption
	 */
	
	public String decrypt(final String textToDecrypt) {
		final String withoutDash = keepOnlyADGVXCharacters(textToDecrypt);
		final int charactersCount = withoutDash.length();
		final int transpositionKeyLength  = transpositionKey.length();
		final int rowCount = charactersCount / transpositionKeyLength;
		
		if(charactersCount % transpositionKeyLength != 0)
			throw new IllegalArgumentException();
		
		final char[] decrypted = new char[withoutDash.length() / 2];
		
		char[] digram = new char[2];
		int countInserted = 0;
		int currentCharIndex = 0;
		int index;

		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < transpositionKeyLength; j++) {
				index = i + (indexes[j] * rowCount);
				char current = withoutDash.charAt(index);
				digram[countInserted] = current;
				countInserted++;
				if(countInserted == 2) {
					String digramAsString = new String(digram);
					char letter = mapOfDigramToChar.get(digramAsString);
					decrypted[currentCharIndex] = letter;
					currentCharIndex++;
					countInserted = 0;
				}
			}
		}
		// todo exception when non multiple of t key
		return new String(decrypted);
	}
	
	private String keepOnlyADGVXCharacters(String textToDecrypt) {
		final char[] builder = new char[textToDecrypt.length()];
		int index = 0;
		for (int i = 0; i < textToDecrypt.length(); i++) {
			char current = textToDecrypt.charAt(i);
			if(setOfSubstitutionChars.contains(current))
				builder[index++] = (textToDecrypt.charAt(i));
		}
		return new String(builder).trim();
	}
}
