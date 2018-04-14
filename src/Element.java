import java.util.ArrayList;

public class Element {
    private String character;
    private Type type;
    private ArrayList<Node> list;
    private int nodeIterator;


    public Element(String character, Type type) {
        this.character = character;
        this.type = type;
        list = new ArrayList<>();
        //nodeIterator = 0;
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

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /*public Element getNextChildren() {
        if (type == Type.Terminal) return null;
        if (list.size() > nodeIterator) {
            Element element = list.get(nodeIterator).getNextElement();
            if (element == null) {
                nodeIterator++;
                return getNextChildren();
            }
            return element;
        }
        return null;
    }*/
}
