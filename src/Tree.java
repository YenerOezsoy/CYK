
import java.util.ArrayList;
import java.util.HashMap;


public class Tree {

    private Element root;
    private Input input;
    private Node node;
    private HashMap<String, Element> temp;

    public Tree() {
        input = new Input();
    }

    public void buildTree() {
        root = new Element(input.getRootElement(), Type.NonTerminal);
        ArrayList<String> toProcess = new ArrayList<>();
        toProcess.add(input.getRootElement());
        temp = new HashMap<>();
        temp.put(input.getRootElement(), root);

        while (!toProcess.isEmpty()) {
            System.out.println("To Process: " + toProcess.get(0));
            createNodes(toProcess.get(0), toProcess);
            toProcess.remove(0);
        }
    }

    private void createNodes(String s, ArrayList<String> toProcess) {
        int size = input.getProductionSize(s);
        int diff = 1;
        System.out.println("Anzahl Elemente: " + size);
        while (diff != size) {
            node = new Node(input, temp, toProcess);
            temp.get(s).addNode(node);
            node.addToNode(input.getProduction(s, diff));
            diff++;
            System.out.println("__________");
        }
    }

    public HashMap<String, Element> getMap() {
        return temp;
    }

    public Node newNode(String character) {
        node = new Node(input, temp, null);
        node.addToNode(character);
        return node;
    }

    public void printTree() {
        ArrayList<Element> touched = new ArrayList<>();
        touched.add(root);
        for (int i = 0; i < touched.size(); i++) {
            System.out.println("_______");
            System.out.println("Root: " +  touched.get(i).getElement());
            for (int j = 0; j < touched.get(i).getList().size(); j++) {
                ArrayList<Element> nodelist = touched.get(i).getList().get(j).getNodeList();
                for (int k = 0; k < nodelist.size(); k++) {
                    System.out.println("Index k: " + k + "   ::" + nodelist.get(k).getElement());
                    if (nodelist.get(k).getType() == Type.NonTerminal && !touched.contains(nodelist.get(k))) touched.add(nodelist.get(k));
                }
            }
        }
    }

}
