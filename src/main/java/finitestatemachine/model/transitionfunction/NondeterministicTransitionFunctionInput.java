package finitestatemachine.model.transitionfunction;

import java.util.Set;

public class NondeterministicTransitionFunctionInput implements TransitionFunctionInput<Set<Character>> {

    private Character signal;
    private Set<Character> states;

    public NondeterministicTransitionFunctionInput() {
    }

    public NondeterministicTransitionFunctionInput(Character signal, Set<Character> states) {
        this.signal = signal;
        this.states = states;
    }

    @Override
    public Character getSignal() {
        return signal;
    }

    @Override
    public void setSignal(Character terminal) {
        this.signal = terminal;
    }

    @Override
    public Set<Character> getState() {
        return states;
    }

    @Override
    public void setState(Set<Character> states) {
        this.states = states;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NondeterministicTransitionFunctionInput that = (NondeterministicTransitionFunctionInput) o;

        return signal.equals(that.signal) && states.equals(that.states);
    }

    @Override
    public int hashCode() {
        int result = signal.hashCode();
        result = 31 * result + states.hashCode();
        return result;
    }
}
