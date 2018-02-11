package finitestatemachine.model.minimize;

import java.util.Set;

public class Group {
    private static int counter = 0;

    private int number;
    private Set<Character> states;

    public Group() {
        this.number = counter++;
    }

    public Group(Set<Character> states) {
        number = counter++;
        this.states = states;
    }

    public int getNumber() {
        return number;
    }

    public Set<Character> getStates() {
        return states;
    }

    public void setStates(Set<Character> states) {
        this.states = states;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        return states.equals(group.states);
    }

    @Override
    public int hashCode() {
        return states.hashCode();
    }
}
