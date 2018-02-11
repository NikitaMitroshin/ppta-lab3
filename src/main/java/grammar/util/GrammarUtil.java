package grammar.util;

import grammar.model.Grammar;
import grammar.model.GrammarType;
import grammar.model.Rule;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

public class GrammarUtil {

    public static Grammar fromFile(String filename) {
        List<String> rulesStrings;

        try {
            rulesStrings = FileUtil.readFile(filename);
        } catch (FileNotFoundException e) {
            System.out.println("Wrong file name");
            throw new IllegalArgumentException("Wrong file name");
        }

        List<Rule> rules = RuleParser.parseAll(rulesStrings);

        Set<Character> terminals = RuleAnalyzer.getTerminals(rules);
        Set<Character> nonTerminals = RuleAnalyzer.getNonterminals(rules);

        return new Grammar(rules, terminals, nonTerminals);
    }

    public static GrammarType getType(Grammar grammar) {
        if (!isType1(grammar)) return GrammarType.TYPE_0;
        if (!isType2(grammar)) return GrammarType.TYPE_1;
        if (!isType3(grammar)) return GrammarType.TYPE_2;
        return GrammarType.TYPE_3;
    }

    private static boolean isType1(Grammar grammar) {
        for (Rule rule : grammar.getRules()) {
            if (rule.getLeft().length() > rule.getRight().length())
                return false;
        }

        return true;
    }

    private static boolean isType2(Grammar grammar) {
        for (Rule rule : grammar.getRules()) {
            List<Character> leftChars = StringUtil.splitToChars(rule.getLeft());
            if (leftChars.size() > 1)
                return false;
            if (grammar.getT().contains(leftChars.get(0))) {
                return false;
            }
        }

        return true;
    }

    // for state machines
    private static boolean isType3(Grammar grammar) {
        for (Rule rule : grammar.getRules()) {
            List<Character> rightChars = StringUtil.splitToChars(rule.getRight());
            if (rightChars.size() > 2)
                return false;
            int nonterminalsCount = 0;
            for (Character rightChar : rightChars)
                if (grammar.getN().contains(rightChar))
                    nonterminalsCount++;

            if (nonterminalsCount != 0) {
                if (rightChars.size() == 1)
                    return false;
                if (nonterminalsCount > 1)
                    return false;
            }
        }

        return true;
    }

    // for grammars
//    private static boolean isType3(Grammar grammar) {
//        for (Rule rule : grammar.getRules()) {
//            List<Character> rightChars = StringUtil.splitToChars(rule.getRight());
//            List<Integer> nonterminalsPositions = new ArrayList<>();
//            for (int i = 0; i < rightChars.size(); i++)
//                if (grammar.getN().contains(rightChars.get(i)))
//                    nonterminalsPositions.add(i);
//
//            if (nonterminalsPositions.size() > 1)
//                return false;
//            if (nonterminalsPositions.size() == 1 && rightChars.size() == 1)
//                return false;
//            if (nonterminalsPositions.size() != 0
//                    && nonterminalsPositions.get(0) != 0
//                    && nonterminalsPositions.get(0) != rightChars.size() - 1)
//                return false;
//        }
//
//        return true;
//    }
}
