package grammar.model;

public enum  GrammarType {

    TYPE_0(0, "Тип 0"),
    TYPE_1(1, "Контекстно-зависимая"),
    TYPE_2(2, "Контекстно-свободная"),
    TYPE_3(3, "Регулярная");

    private int typeNumber;
    private String type;

    GrammarType(int number, String type) {
        this.typeNumber = number;
        this.type = type;
    }

    public int getTypeNumber() {
        return typeNumber;
    }

    public String getType() {
        return type;
    }
}
