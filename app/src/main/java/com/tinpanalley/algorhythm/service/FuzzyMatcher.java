package com.tinpanalley.algorhythm.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
public class FuzzyMatcher {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern SUFFIXES = Pattern.compile("\\s*[\\(\\[\\-].*$");
    private static final Pattern ARTICLES = Pattern.compile("^(the|a|an|il|lo|la|i|gli|le|un|una)\\s+");

    public boolean matches(String guess, String actual) {
        if (guess == null || actual == null) return false;
        String cleanGuess = normalize(guess);
        String cleanActual = normalize(actual);

        if (cleanGuess.isEmpty() || cleanActual.isEmpty()) return false;
        if (cleanGuess.equals(cleanActual)) return true;

        // Tolleranza proporzionale alla lunghezza (1 refuso ogni ~5 caratteri, max 2)
        int maxDistance = cleanActual.length() <= 4 ? 0 : (cleanActual.length() <= 8 ? 1 : 2);
        return levenshtein(cleanGuess, cleanActual) <= maxDistance;
    }

    public boolean matchesDecade(String guessDecade, String actualDecade) {
        if (guessDecade == null || actualDecade == null) return false;
        return guessDecade.trim().equals(actualDecade.trim());
    }

    private String normalize(String input) {
        String s = input.trim().toLowerCase();
        // Rimuove eventuali "(Remastered)", "- Live", "[feat. ...]"
        s = SUFFIXES.matcher(s).replaceAll("");
        // Rimuove accenti (é -> e)
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = DIACRITICS.matcher(s).replaceAll("");
        // Rimuove punteggiatura e spazi doppi
        s = s.replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
        // Rimuove articolo iniziale
        s = ARTICLES.matcher(s).replaceAll("").trim();
        return s;
    }

    private int levenshtein(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) costs[j] = j;

        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}

