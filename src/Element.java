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
                if (list.get(i).getNodeList().get(j).getElement().equals(element) && list.get(i).getNodeList().size() > 1) return true;
            }
        }
        return false;
    }

    /*public boolean hasChildren(String element) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).getNodeList().size(); j++) {
                //liefere true nur wenn einzelnes Symbol in Liste
                if (list.get(i).getNodeList().get(j).getElement().equals(element)) return true;
            }
        }
        return false;
    }*/

    public boolean hasOnlyChildren(String element) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).getNodeList().size(); j++) {
                //liefere true nur wenn einzelnes Symbol in Liste
                if (list.get(i).getNodeList().get(j).getElement().equals(element) && list.get(i).getNodeList().size() == 1) return true;
            }
        }
        return false;
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
