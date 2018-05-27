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

    public String getString() {
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

    //Epsilon-Übergang nutzt diese Abfrage
    public boolean hasChildrenWSize(String element) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).getNodeList().size(); j++) {
                //liefere true nur wenn einzelnes Symbol in Liste
                if (list.get(i).getNodeList().get(j).getString().equals(element) && list.get(i).getNodeList().size() > 1) return true;
            }
        }
        return false;
    }

    public boolean hasOnlyChildren(String element) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).getNodeList().size(); j++) {
                //liefere true nur wenn einzelnes Symbol in Liste
                if (list.get(i).getNodeList().get(j).getString().equals(element) && list.get(i).getNodeList().size() == 1) return true;
            }
        }
        return false;
    }

    public boolean hasChildren(String var1, String var2) {
        for (int i = 0; i< list.size(); i++) {
            Node node = list.get(i);
            if (node.getNodeList().size() == 2) {
                String element1 = node.getNodeList().get(0).getString();
                String element2 = node.getNodeList().get(1).getString();
                if (element1.equals(var1) && element2.equals(var2)) return true;
            }
        }
        return false;
    }
}
