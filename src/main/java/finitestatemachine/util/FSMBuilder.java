package finitestatemachine.util;

import finitestatemachine.model.FiniteStateMachine;
import finitestatemachine.model.TransitionFunction;
import grammar.model.Grammar;
import grammar.model.GrammarType;
import grammar.model.Rule;
import grammar.util.GrammarUtil;
import grammar.util.RuleAnalyzer;
import grammar.util.StringUtil;

import java.util.*;

public class FSMBuilder {

    public static final String ADDITIONAL_NONTERMINAL = "N";

    public static final String EMPTY_STRING = "ε";

    public static FiniteStateMachine buildFromGrammar(Grammar grammar) {

        if (GrammarUtil.getType(grammar) != GrammarType.TYPE_3)
            throw new IllegalArgumentException("Grammar is not type 3 (regular)!");

        List<Rule> completedRules = completeRules(grammar);

        FiniteStateMachine finiteStateMachine = new FiniteStateMachine();
        finiteStateMachine.setStates(RuleAnalyzer.getNonterminals(completedRules));
        finiteStateMachine.setInitialStates(new HashSet<>(StringUtil.splitToChars(grammar.getS())));
        finiteStateMachine.setInputSymbols(grammar.getT());

        List<TransitionFunction> transitionFunctions = GrammarRulesParser.toTransitionFunctions(completedRules);

        finiteStateMachine.setTransitionFunctions(transitionFunctions);

        Set<Character> finiteStates = getFiniteStates(transitionFunctions);
        finiteStates.addAll(getFiniteStatesEmptyString(completedRules));

        finiteStateMachine.setFiniteStates(finiteStates);

        return finiteStateMachine;
    }

    private static List<Rule> completeRules(Grammar grammar) {
        List<Rule> rules = new ArrayList<>();
        for (Rule rule : grammar.getRules()) {
            String rightPart = rule.getRight();
            if (rightPart.length() == 1) {
                StringBuilder sb = new StringBuilder(rightPart);
                rule.setRight(sb.append(ADDITIONAL_NONTERMINAL).toString());
            }
            rules.add(rule);
        }
        return rules;
    }

    public static Set<Character> getFiniteStates(List<TransitionFunction> functions) {
        Set<Character> finiteStates = new HashSet<>();
        for (TransitionFunction outer : functions) {
            boolean isFinite = true;
            for (TransitionFunction inner : functions) {
                if (outer.getOut().equals(inner.getIn().getState())) {
                    isFinite = false;
                }
            }
            if (isFinite)
                finiteStates.add(outer.getOut());
        }
//        Map<Character, TransitionFunction> statesWithOneTransitionFunction = new HashMap<>();
//        for (TransitionFunction outer : functions) {
//            int count = 0;
//            for (TransitionFunction inner : functions) {
//                if (outer.getIn().getState().equals(inner.getIn().getState()))
//                    count++;
//            }
//            if (count == 1) {
//                statesWithOneTransitionFunction.put((Character) outer.getIn().getState(), outer);
//            }
//        }
//        for (Character state : statesWithOneTransitionFunction.keySet()) {
//            TransitionFunction function = statesWithOneTransitionFunction.get(state);
//            for (TransitionFunction f : functions) {
//                if (f.getIn().getState().equals(function.getOut())) {
//                    if (statesWithOneTransitionFunction.keySet().contains(f.getIn().getState()))
//                        finiteStates.add(state);
//                }
//            }
//        }
        return finiteStates;
    }

    private static Set<Character> getFiniteStatesEmptyString(List<Rule> rules) {
        Set<Character> finiteStates = new HashSet<>();
        for (Rule rule : rules) {
            String right = rule.getRight();

            if (right.length() == 2 && right.contains(EMPTY_STRING)) {
                finiteStates.add(rule.getLeft().toCharArray()[0]);
            }
        }
        return finiteStates;
    }

    public static Set<Character> getStates(List<TransitionFunction> functions) {
        Set<Character> states = new HashSet<>();

        for (TransitionFunction function : functions) {
            states.add((Character) function.getIn().getState());
            states.add(function.getOut());
        }

        return states;
    }
}
