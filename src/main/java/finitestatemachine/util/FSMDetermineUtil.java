package finitestatemachine.util;


import finitestatemachine.model.FiniteStateMachine;
import finitestatemachine.model.TransitionFunction;
import finitestatemachine.model.transitionfunction.DeterministicTransitionFunctionInput;
import finitestatemachine.model.transitionfunction.NondeterministicTransitionFunctionInput;
import finitestatemachine.model.transitionfunction.TransitionFunctionInput;
import javaslang.Tuple;
import javaslang.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

public class FSMDetermineUtil {

	public static void determine(FiniteStateMachine finiteStateMachine) {
		Map<TransitionFunctionInput, Set<Character>> map = getTransitionTable(finiteStateMachine.getTransitionFunctions());
		map = determineTransitionFunctions(map);
		Map<Set<Character>, Character> newNotations = NewNotationsProvider.forTransitionTable(map, finiteStateMachine.getStates());
		map = replaceWithNewNotations(map, newNotations);
		final List<TransitionFunction> determinedFunctions = toTransitionFunctions(map);
		finiteStateMachine.setTransitionFunctions(determinedFunctions);
		finiteStateMachine.setFiniteStates(FSMBuilder.getFiniteStates(determinedFunctions));
		for (Character state : newNotations.values()) {
			finiteStateMachine.addState(state);
		}
	}


	public static Map<TransitionFunctionInput, Set<Character>> getTransitionTable(List<TransitionFunction> functions) {
		final Map<TransitionFunctionInput, Set<Character>> result = new HashMap<>();
		for (TransitionFunction outer : functions) {
			TransitionFunctionInput in = outer.getIn();
			final Set<Character> states = new HashSet<>();
			for (TransitionFunction inner : functions) {
				if (inner.getIn()
						.equals(in)) {
					states.add(inner.getOut());
				}
			}
			states.add(outer.getOut());
			result.put(in, states);
		}
		return result;
	}

	public static Map<TransitionFunctionInput, Set<Character>> determineTransitionFunctions(Map<TransitionFunctionInput, Set<Character>> transitionTable) {
		final HashMap<TransitionFunctionInput, Set<Character>> result = new HashMap<>();
		result.putAll(transitionTable);

		for (Map.Entry<TransitionFunctionInput, Set<Character>> outerEntry : transitionTable.entrySet()) {
			if (outerEntry.getValue()
					.size() > 1) {

				final Map<Character, Set<Set<Character>>> collect = transitionTable.entrySet()
						.stream()
						.filter(e -> {
							if (e.getKey()
									.getState() instanceof Character) {
								final Character state = (Character) e.getKey()
										.getState();
								return outerEntry.getValue()
										.contains(state);
							} else {
								return false;
							}
						})
						.map(e -> Tuple.of(e.getKey()
								.getSignal(), e.getValue()))
						.collect(Collectors.groupingBy(Tuple2::_1, Collectors.mapping(Tuple2::_2, Collectors.toSet())));


				collect.entrySet()
						.stream()
						.map(e -> Tuple.of(e.getKey(), e.getValue()
								.stream()
								.flatMap(Collection::stream)
								.collect(Collectors.toSet())))
						.forEach(e -> {
							final NondeterministicTransitionFunctionInput input = new NondeterministicTransitionFunctionInput();
							input.setState(outerEntry.getValue());
							input.setSignal(e._1);
							result.put(input, e._2);
						});
			}
		}

		final List<Set<Character>> transitionOutputValues = result.values()
				.stream()
				.filter(e -> e.size() > 1)
				.distinct()
				.collect(Collectors.toList());


		final List<Set<Character>> transitionInputValues = result.keySet()
				.stream()
				.map(e -> e.getClass()
						.equals(NondeterministicTransitionFunctionInput.class) ? (Set<Character>) e.getState() :
						Collections.singleton((Character) e.getState()))
				.filter(e -> e.size() > 1)
				.distinct()
				.collect(Collectors.toList());

		transitionOutputValues.removeAll(transitionInputValues);

		return transitionOutputValues.isEmpty() ? result : determineTransitionFunctions(result);
	}


	public static Map<TransitionFunctionInput, Set<Character>> replaceWithNewNotations(Map<TransitionFunctionInput, Set<Character>> transitionTable,
			Map<Set<Character>, Character> newNotations) {
		Map<TransitionFunctionInput, Set<Character>> result = new HashMap<>();

		for (Map.Entry<TransitionFunctionInput, Set<Character>> entry : transitionTable.entrySet()) {
			TransitionFunctionInput input = new DeterministicTransitionFunctionInput();
			Set<Character> states = new HashSet<>();
			input.setSignal(entry.getKey()
					.getSignal());

			if (entry.getKey() instanceof NondeterministicTransitionFunctionInput) {
				Character newState = newNotations.get(entry.getKey()
						.getState());
				input.setState(newState);
			} else {
				input.setState(entry.getKey()
						.getState());
			}
			if (entry.getValue()
					.size() > 1) {
				states.add(newNotations.get(entry.getValue()));
			} else {
				states = entry.getValue();
			}
			result.put(input, states);
		}

		return result;
	}

	public static List<TransitionFunction> toTransitionFunctions(Map<TransitionFunctionInput, Set<Character>> transitionTable) {
		List<TransitionFunction> result = new ArrayList<>();
		for (Map.Entry<TransitionFunctionInput, Set<Character>> entry : transitionTable.entrySet()) {
			TransitionFunction function = new TransitionFunction();
			function.setIn(entry.getKey());
			function.setOut((Character) (entry.getValue()
					.toArray()[0]));

			result.add(function);
		}
		return result;
	}
}
