package su.j2e.rvjoiner.demo;

import java.util.Random;

public class Util {

	private static final Random sRandom = new Random();
	private static final String sPossibleWordLetters = "abcdefghijklmnopqrstuvwxyz";
	private static final int sMaxWordLength = 10;

	public static String getRandomWord() {
		int wordLength = sRandom.nextInt(sMaxWordLength) + 1;
		char[] letters = new char[wordLength];
		for (int i = 0; i < letters.length; i++) {
			letters[i] = sPossibleWordLetters.charAt(sRandom.nextInt(sPossibleWordLetters.length()));
		}
		return new String(letters);
	}

	public static String getRandomSentence(int maxWordsCount) {
		StringBuilder sentenceBuilder = new StringBuilder();
		int wordsCount = sRandom.nextInt(maxWordsCount) + 1;
		for (int i = 0; i < wordsCount; i++) {
			sentenceBuilder.append(getRandomWord());
			sentenceBuilder.append(" ");
		}
		return sentenceBuilder.toString();
	}

	public static int getRandomColor() {
		return sRandom.nextInt();
	}

}
