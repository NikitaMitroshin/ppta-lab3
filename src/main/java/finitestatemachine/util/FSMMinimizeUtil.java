package finitestatemachine.util;

import finitestatemachine.model.FiniteStateMachine;
import finitestatemachine.model.TransitionFunction;
import finitestatemachine.model.minimize.Group;
import finitestatemachine.model.transitionfunction.TransitionFunctionInput;
import javaslang.Tuple;

import java.util.*;
import java.util.stream.Collectors;

public class FSMMinimizeUtil {

	public static FiniteStateMachine minimize(FiniteStateMachine finiteStateMachine) {
		Set<Character> unreachableStates = getUnreachableStates(finiteStateMachine);

		removeStates(finiteStateMachine, unreachableStates);

		List<Set<Character>> equalStates = getEqualStates(finiteStateMachine);

		Map<Set<Character>, Character> newNotations = NewNotationsProvider.forEqualStates(equalStates, finiteStateMachine.getStates());

		List<TransitionFunction> transitionFunctions = replaceWithNewNotations(finiteStateMachine, newNotations);

		FiniteStateMachine result = new FiniteStateMachine();
		result.setFiniteStates(FSMBuilder.getFiniteStates(transitionFunctions));
		result.setTransitionFunctions(transitionFunctions);
		result.setStates(FSMBuilder.getStates(transitionFunctions));
		result.setInitialStates(finiteStateMachine.getInitialStates());
		result.setInputSymbols(finiteStateMachine.getInputSymbols());

		return result;
	}

	private static Set<Character> getUnreachableStates(FiniteStateMachine finiteStateMachine) {
		Set<Character> reachableStates = new HashSet<Character>() {{
			addAll(finiteStateMachine.getInitialStates());
		}};
		Set<Character> prevReachableStates = new HashSet<Character>() {{
			addAll(finiteStateMachine.getInitialStates());
		}};
		Set<Character> newReachableStatesStep = new HashSet<>();

		while (true) {
			for (Character state : prevReachableStates) {
				for (TransitionFunction function : finiteStateMachine.getTransitionFunctions()) {
					if (function.getIn()
							.getState()
							.equals(state)) {
						newReachableStatesStep.add(function.getOut());
					}
				}
			}
			prevReachableStates = newReachableStatesStep;
			newReachableStatesStep.removeAll(reachableStates);
			if (newReachableStatesStep.size() == 0) break;
			else {
				reachableStates.addAll(prevReachableStates);
				newReachableStatesStep = new HashSet<>();
			}
		}

		Set<Character> unreachableStates = new HashSet<Character>() {{
			addAll(finiteStateMachine.getStates());
		}};
		unreachableStates.removeAll(reachableStates);
		return unreachableStates;
	}

	private static void removeStates(FiniteStateMachine finiteStateMachine, Set<Character> states) {
		finiteStateMachine.getStates()
				.removeAll(states);
		finiteStateMachine.getFiniteStates()
				.removeAll(states);
		List<TransitionFunction> unneededFunctions = new ArrayList<>();

		for (TransitionFunction function : finiteStateMachine.getTransitionFunctions()) {
			if (states.contains(function.getIn()
					.getState()) || states.contains(function.getOut())) {
				unneededFunctions.add(function);
			}
		}

		finiteStateMachine.getTransitionFunctions()
				.removeAll(unneededFunctions);
	}

	private static List<Set<Character>> getEqualStates(FiniteStateMachine finiteStateMachine) {
		Set<Group> groups = new HashSet<>();
		groups.add(new Group(finiteStateMachine.getFiniteStates()));

		Set<Character> otherStates = finiteStateMachine.getStates();
		otherStates.removeAll(finiteStateMachine.getFiniteStates());
		groups.add(new Group(otherStates));

		while (true) {
			final Map<TransitionFunctionInput, Group> table = getTransitionTable(finiteStateMachine, groups);
			final Set<Group> groupsStep = splitIntoGroups(table);
			if (groups.equals(groupsStep)) {
				break;
			} else {
				groups = groupsStep;
			}
		}
		return groups.stream()
				.filter(e -> e.getStates()
						.size() > 1)
				.map(Group::getStates)
				.collect(Collectors.toList());
	}

	private static Set<Group> splitIntoGroups(Map<TransitionFunctionInput, Group> table) {
		return table.entrySet()
				.stream()
				.collect(Collectors.groupingBy(e -> Tuple.of(e.getKey()
						.getSignal(), e.getValue()
						.getNumber()), Collectors.mapping(e -> Tuple.of(e.getKey(), e.getValue()), Collectors.toSet())))
				.entrySet()
				.stream()
				.map(e -> new Group(e.getValue()
						.stream()
						.map(v -> v._1()
								.getState())
						.map(Character.class::cast)
						.collect(Collectors.toSet())))
				.collect(Collectors.toSet());
	}

	private static Map<TransitionFunctionInput, Group> getTransitionTable(FiniteStateMachine finiteStateMachine, Set<Group> groups) {
		final Map<TransitionFunctionInput, Group> table = new HashMap<>();
		for (TransitionFunction function : finiteStateMachine.getTransitionFunctions()) {
			for (Group group : groups) {
				if (group.getStates()
						.contains(function.getOut())) {
					table.put(function.getIn(), group);
				}
			}
		}
		return table;
	}

	private static List<TransitionFunction> replaceWithNewNotations(FiniteStateMachine finiteStateMachine, Map<Set<Character>, Character> newNotations) {
		List<TransitionFunction> transitionFunctions = finiteStateMachine.getTransitionFunctions();

		for (TransitionFunction transitionFunction : transitionFunctions) {
			for (Set<Character> equalStates : newNotations.keySet()) {
				if (equalStates.contains(transitionFunction.getIn()
						.getState())) transitionFunction.getIn()
						.setState(newNotations.get(equalStates));
				if (equalStates.contains(transitionFunction.getOut())) transitionFunction.setOut(newNotations.get(equalStates));
			}
		}
		Set<TransitionFunction> uniqueFunctions = new HashSet<>(transitionFunctions);
		return new ArrayList<>(uniqueFunctions);
	}
}
