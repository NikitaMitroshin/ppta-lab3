package finitestatemachine.model;

import finitestatemachine.model.transitionfunction.TransitionFunctionInput;

public class TransitionFunction {

    private TransitionFunctionInput in;
    private Character out;

    public TransitionFunction() {
    }

    public TransitionFunction(TransitionFunctionInput in, Character out) {
        this.in = in;
        this.out = out;
    }

    public TransitionFunctionInput getIn() {
        return in;
    }

    public void setIn(TransitionFunctionInput in) {
        this.in = in;
    }

    public Character getOut() {
        return out;
    }

    public void setOut(Character out) {
        this.out = out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransitionFunction function = (TransitionFunction) o;

        if (!in.equals(function.in)) return false;
        return out.equals(function.out);
    }

    @Override
    public int hashCode() {
        int result = in.hashCode();
        result = 31 * result + out.hashCode();
        return result;
    }
}
