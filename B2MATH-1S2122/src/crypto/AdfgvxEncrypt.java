package crypto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AdfgvxEncrypt {
	
	private final String transpositionKey;
	
	private final Map<Character, String> mapOfCharToDigram;
	
	//Latin 1 - Latin Extended-B
	private static final String tab00c0 = "AAAAAAACEEEEIIII" +
			"DNOOOOO\u00d7\u00d8UUUUYI\u00df" +
			"AAAAAAACEEEEIIII" +
			"\u00f0nOOOOO\u00f7\u00f8UUUUY\u00feY" +
			"AAAAAACCCCCCCCDD" +
			"DDEEEEEEEEEEGGGG" +
			"GGGGHHHHIIIIIIII" +
			"IIJJJJKKKLLLLLLL" +
			"LLLNNNNNNNNNOOOO" +
			"OOOORRRRRRSSSSSS" +
			"SSTTTTTTUUUUUUUU" +
			"UUUUWWYYYZZZZZZF";
	
	public AdfgvxEncrypt(final Map<Character, String> mapOfCharToDigram, final String transpositionKey) {
		this.transpositionKey = transpositionKey;
		this.mapOfCharToDigram = mapOfCharToDigram;
	}
	
	public String encrypt(String textToEncrypt) {
		if(textToEncrypt.length() == 0) return textToEncrypt;
		final List<Character> substituted = new ArrayList<>(textToEncrypt.length() * 2);
		substitute(textToEncrypt, substituted);
		if(substituted.size() == 0) return "";
		makeSizeOfGivenListBecomesMultipleOfGivenLength(substituted, transpositionKey.length());
		final TreeMap<Character, char[]> transposed = transpose(substituted);
		return treeMapToString(transposed, substituted.size());
	}
	
	/* ---------- Substitution */

	private void substitute(final String textToEncrypt, final List<Character> substituted) {
		char currentLetterToSubstitute;
		String digram;
		for (int i = 0; i < textToEncrypt.length(); i++) {
			currentLetterToSubstitute = textToEncrypt.charAt(i);
			if((int)currentLetterToSubstitute >= 97 && (int)currentLetterToSubstitute <= 122) {
				currentLetterToSubstitute = (char)(currentLetterToSubstitute - 32);
			}
			int current = (int)currentLetterToSubstitute;
			if(current >= '\u00c0' && current <= '\u017f') {
				currentLetterToSubstitute = tab00c0.charAt((int) currentLetterToSubstitute - '\u00c0');
			}
			digram = mapOfCharToDigram.get(currentLetterToSubstitute);
			if(digram != null) {
				substituted.add(digram.charAt(0));
				substituted.add(digram.charAt(1));
			}
		}
	}

	/* ---------- Transposition */

	private void makeSizeOfGivenListBecomesMultipleOfGivenLength(final List<Character> substituted, final int length) {
		int left = substituted.size() % length;
		if(left > 0) {
			for (int i = 0; i < length - left; i++) {
				substituted.add('X');
			}
		}
	}

	private TreeMap<Character, char[]> transpose(final List<Character> substituted) {
		final int rowsCount = substituted.size() / transpositionKey.length();
		final int columnsCount = transpositionKey.length();
		char currentLetterToTranspose;
		final TreeMap<Character, char[]> columns = new TreeMap<>();

		for (int currentColumn = 0; currentColumn < columnsCount; currentColumn++) {
			char transpositionKeyLetter = transpositionKey.charAt(currentColumn);
			final char[] column = new char[rowsCount];
			currentLetterToTranspose = substituted.get(currentColumn);
			columns.put(transpositionKeyLetter, column);
			column[0] = currentLetterToTranspose;
			int currentRow = 0;
			int newIndexOfLetter = 0;
			while(currentRow < (rowsCount - 1)) {
				newIndexOfLetter += (currentColumn + columnsCount);
				currentLetterToTranspose = substituted.get(newIndexOfLetter - (currentColumn * currentRow)); // Minus to correct the found position, the found position is always too far from the right position by currentColumn * currentRow positions. don't know why
				column[currentRow + 1] = currentLetterToTranspose;
				currentRow++;
			}
		}
		return columns;
	}

	private String treeMapToString(final TreeMap<Character, char[]> transposed, final int finalLength) {
		final char[] dashed = new char[finalLength + (finalLength / 5)];
		int laps = 0;
		int current = 0;
		
		for (final char[] chars : transposed.values()) {
			for (char aChar : chars) {
				if (laps % 5 == 0 && laps > 0) {
					dashed[current] = '-';
					current++;
				}
				laps++;
				dashed[current] = aChar;
				current++;
			}
		}
		//System.out.println(Arrays.toString(dashed));
		return new String(dashed).trim();
	}
}
