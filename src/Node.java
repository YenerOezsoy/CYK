import java.util.ArrayList;

public class Node {

    private ArrayList<Elem> list;

    public Node() {
        list = new ArrayList<>();
    }

    public Node(Node node) {
        list = new ArrayList<>(node.getNodeList());
    }

    public void addTerminalToNode(String element) {
        Elem elem = new Elem(element, Type.Terminal);
        list.add(elem);
    }

    public Elem addNonTerminalToNode(String element) {
        Elem elem = new Elem(element, Type.NonTerminal);
        list.add(elem);
        return elem;
    }

    public ArrayList<Elem> getNodeList() {
        return list;
    }

    public void addElement(Elem elem) {
        list.add(elem);
    }

    public boolean hasElement(Elem elem) {
        return(list.contains(elem));
    }
}
