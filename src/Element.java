import java.util.ArrayList;
import java.util.HashMap;

public class Element {
    private String character;
    private Type type;
    private ArrayList<Node> list = new ArrayList<Node>();
    private Node node;
    private Input input;


    public Element(String c, Input input, ArrayList<String> touched, HashMap<String, Element> temp) {
        System.out.println("NonTerminal: " + c + " erkannt");
        this.character = c;
        this.input = input;
        this.type = Type.NonTerminal;
        int size = input.getSize(c);
        for (int i = 0; i < size; i ++) {
            node = new Node(temp);
            list.add(node);
            node.addToNode(input.getNextProduction(c), input, touched);
        }
    }

    public Element(String c) {
        System.out.println("Terminal: " + c + " erkannt");
        this.character = c;
        this.type = Type.Terminal;
    }

    public void addNode() {
        //node = new Node();
        list.add(node);
    }

    public Node getNode(int i) {
        return list.get(i);
    }

    public int getListSize() {
        return list.size();
    }

    public ArrayList<Node> getList() {
        return list;
    }

    public String getElement() {
        return character;
    }
}
