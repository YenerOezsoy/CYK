import java.util.ArrayList;

public class Element {
    private String character;
    private Type type;
    private ArrayList<Node> list;


    public Element(String character, Type type) {
        this.character = character;
        this.type = type;
        list = new ArrayList<>();
    }

    public void addNode(Node node) {
        list.add(node);
    }

    public String getElement() {
        return character;
    }

    public ArrayList<Node> getList() {
        return list;
    }

    public Type getType() {
        return type;
    }


}
