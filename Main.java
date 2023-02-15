package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {

        for (String arg : args) {
            File file = new File(arg);
            try (Scanner scannerFile = new Scanner(file)) {

                String wholeText = scannerFile.useDelimiter("\\A").next(); //whole text from file to one String

                int numOfWords = wordsCounter(wholeText);
                System.out.println("Words: " + numOfWords);
                int numOfSentences = sentencesCouter(wholeText);
                System.out.println("Sentences: " + numOfSentences);
                int numOfCharacters = charactersCouter(wholeText);
                System.out.println("Characters: "+numOfCharacters);
                int numOfSyllables = syllabelsCouter(wholeText);
                System.out.println("Syllables: "+numOfSyllables);
                int numOfPolysyllables = polysyllablesWordCounting(wholeText);
                System.out.println("Polysyllables: "+numOfPolysyllables);

                Scanner scannerUser = new Scanner(System.in);
                System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
                String userInput = scannerUser.nextLine();
                userInput = userInput.toUpperCase(); //user can input name in lower case
                switch (userInput){
                    case "ARI":
                        double[] ari = couterARIscore(numOfSentences, numOfWords, numOfCharacters);
                        System.out.println("Automated Readability Index: " + ari[0] + "(about "
                                + ari[1] + "-years-olds).");
                        break;
                    case "FK":
                        double[] fk = counterFKscore(numOfSentences, numOfSyllables, numOfWords);
                        System.out.println("Flesch–Kincaid readability tests: " + fk[0] + "(about "
                                + fk[1] + "-years-olds).");
                        break;
                    case "SMOG":
                        double[] smog = counterSMOGscore(numOfSentences, numOfPolysyllables);
                        System.out.println("Simple Measure of Gobbledygook: " + smog[0] + "(about "
                                + smog[1] + "-years-olds).");
                        break;
                    case "CL":
                        double[] cl = counterCLscore(numOfSentences, numOfWords, numOfCharacters);
                        System.out.println("Coleman–Liau index: " + cl[0] + "(about "
                                + cl[1] + "-years-olds).");
                        break;
                    case "ALL":

                        ari = couterARIscore(numOfSentences, numOfWords, numOfCharacters);
                        System.out.println("Automated Readability Index: " + ari[0] + "(about "
                                + ari[1] + "-years-olds).");

                        fk = counterFKscore(numOfSentences, numOfSyllables, numOfWords);
                        System.out.println("Flesch–Kincaid readability tests: " + fk[0] + "(about "
                                + fk[1] + "-years-olds).");

                        smog = counterSMOGscore(numOfSentences, numOfPolysyllables);
                        System.out.println("Simple Measure of Gobbledygook: " + smog[0] + "(about "
                                + smog[1] + "-years-olds).");

                        cl = counterCLscore(numOfSentences, numOfWords, numOfCharacters);
                        System.out.println("Coleman–Liau index: " + cl[0] + "(about "
                                + cl[1] + "-years-olds).");

                        float avarage = (float)(ari[1] + fk[1] + smog[1] + cl[1]) / (float)4.0;

                        System.out.println("This text should be understood in average by " + avarage + "-year-olds.");

                        break;
                    default:
                        System.out.println("you entered the wrong value");
                        break;
                }


                }
            catch (FileNotFoundException e) {
                System.out.println("path not found");
            }
        }
    }


    public static int wordsCounter(String text){
        int numOfWords =0;

        String[] words = text.split("[\\s\\n\\r]");
        for (int j=0; j<words.length; j++){
            if (words[j].isBlank()){
                continue;
            }
            numOfWords++;
        }

        return numOfWords;
    }

    public static int sentencesCouter(String text){
        String[] sentences = text.split("(?<=[.!?])+[\\s\\r\\n]+");
        return sentences.length;
    }

    public static int charactersCouter(String text){
        int numOfCharacters =0;
        char[] characters = text.toCharArray();
        for (int j = 0; j<characters.length ; j++){
            if (Pattern.matches("[\\S]", String.valueOf(characters[j]))){
                numOfCharacters++;
            }
        }
        return numOfCharacters;
    }

    public static double[] couterARIscore(int sentences, int words, int characters) {

        double[] result = new double[2];
        double score = ((4.71 * characters / words) + (0.5 * words / sentences)) - 21.43;
        result[0] = score;
        result[1] = scoreToAgeConverter(score);

        return result;
    }

    public static double[] counterFKscore(int sentences, int syllables, int words){
        double[] result = new double[2];
        double score = (0.39 * words / sentences) + (11.8 * syllables / words) - 15.59;
        result[0] = score;
        result[1] = scoreToAgeConverter(score);

        return  result;
    }

    public static double[] counterSMOGscore(int sentences, int polysyllables){
        double[] result = new double[2];
        double score = 1.043 * Math.sqrt(polysyllables*30/sentences) + 3.1291;
        result[0] = score;
        result[1] = scoreToAgeConverter(score);
        return  result;
    }

    public static double[] counterCLscore(int sentences, int words, int characters){
        double[] result = new double[2];
        double L =  (double) characters/(double)words * 100.0;
        double S = (double) sentences/(double)words*100.0;
        double score = (0.0588 * L) - (0.296 * S) - 15.8;
        result[0] = score;
        result[1] = scoreToAgeConverter(score);
        return  result;
    }

    public static int syllabelsCouter(String text) {

        int counter = 0;
        String[] words = text.split("[\\s\\n\\r\\.*\\!*\\?*]");     //deviding to array of words
        for (int j =0; j<words.length; j++) {                // loop on array of words
            if (words[j].isBlank()) {                     //if element of array is empty, there is no word-continue
                continue;
            }
            if (Pattern.matches("[^aeiouyAEIOUY]+e?", words[j])) { //no vowels in word= 1 syllable
                counter++;
                continue;
            }

            if (Pattern.matches("[aeiouyAEIOUY]+", words[j])) { //only vowels in word= 1 syllable
                if(Pattern.matches("[^a-zA-Z]+", words[j])){continue;}
                counter++;
                continue;
            }

            if (Pattern.matches(".*[aeiouyAEIOUY].*", words[j])) { //at least 1 vowel in word
                char[] letters = words[j].toCharArray(); //deviding word from String to char array
                for (int k=0; k<letters.length; k++) { //loop on letters of current word
                    if(Pattern.matches("[aeiouyAEIOUY]", String.valueOf(letters[k]))) {//searching of vowel
                        if (k==letters.length-1 && letters[k] =='e' /*&& letters[k-1]!='l'*/) { // if e is last letter break loop
                            break;
                        }
                        counter++;
                        if (k<letters.length-1 && Pattern.matches("[aeiouyAEIOUY]", String.valueOf(letters[k+1]))) {
                            k++; //second vowel in row, no syllable
                        }
                    }
                }
            }
        }
    return counter;
    }

    public static int polysyllablesWordCounting(String text) {

        int polysyllablesWordCounter = 0;
        String[] words = text.split("[\\s\\n\\r\\.*\\!*\\?*]");

        for (int j=0; j<words.length; j++) {
            if (syllabelsCouter(words[j])>2){
                polysyllablesWordCounter++;
            }
        }
    return polysyllablesWordCounter;
    }

    public static int scoreToAgeConverter(double score){
        int finalScore =(int) score +1;
        int result = 0;
        switch (finalScore) {
            case 1:
                result = 6;
                break;
            case 2:
                result = 7;
                break;
            case 3:
                result = 8;
                break;
            case 4:
                result = 9;
                break;
            case 5:
                result = 10;
                break;
            case 6:
                result = 11;
                break;
            case 7:
                result = 12;
                break;
            case 8:
                result = 13;
                break;
            case 9:
                result = 14;
                break;
            case 10:
                result = 15;
                break;
            case 11:
                result = 16;
                break;
            case 12:
                result = 17;
                break;
            case 13:
                result = 18;
                break;
            case 14:
                result = 22;
                break;
            default:
                System.out.println("score is unbelievable");
                break;
        }
        return result;
    }

}

