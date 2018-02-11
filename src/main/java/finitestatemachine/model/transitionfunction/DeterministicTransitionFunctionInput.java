package finitestatemachine.model.transitionfunction;

public class DeterministicTransitionFunctionInput implements TransitionFunctionInput<Character> {

    private Character signal;
    private Character state;

    public DeterministicTransitionFunctionInput() {
    }

    public DeterministicTransitionFunctionInput(Character signal, Character state) {
        this.signal = signal;
        this.state = state;
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
    public Character getState() {
        return state;
    }

    @Override
    public void setState(Character state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeterministicTransitionFunctionInput that = (DeterministicTransitionFunctionInput) o;

        return signal.equals(that.signal) && state.equals(that.state);
    }

    @Override
    public int hashCode() {
        int result = signal.hashCode();
        result = 31 * result + state.hashCode();
        return result;
    }
}
