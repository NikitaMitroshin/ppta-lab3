import finitestatemachine.model.FiniteStateMachine;
import finitestatemachine.util.FSMBuilder;
import finitestatemachine.util.FSMDetermineUtil;
import finitestatemachine.util.FSMMinimizeUtil;
import finitestatemachine.util.GraphDrawerUtil;
import grammar.model.Grammar;
import grammar.util.GrammarUtil;

import java.util.Collections;

public class Main {

	public static void main(String[] args) {
		final String filePath = "my_rules.txt";
		final Grammar grammar = GrammarUtil.fromFile(filePath);
		final FiniteStateMachine finiteStateMachine = FSMBuilder.buildFromGrammar(grammar);
		GraphDrawerUtil.drawGraph(finiteStateMachine);
		final FiniteStateMachine minimized = FSMMinimizeUtil.minimize(finiteStateMachine);
		GraphDrawerUtil.drawGraph(minimized);
	}
}
