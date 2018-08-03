
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;


public class Tree {

    private Elem rootElem;
    private Elem actualRoot;
    private Input input;
    private Node node;
    private ArrayList<String> toProcess;
    private HashMap<String, Elem> temp;

    public Tree(String path) {
        input = new Input(path);
    }


    public void buildTree() {
        //prüfe beim Erstellen der Datei ob StartSymbol auch in NichtTerminal vorhanden ist
        String root = input.getRootElement();
        rootElem = new Elem(root, Type.NonTerminal);
        toProcess = new ArrayList<>();
        temp = new HashMap<>();
        temp.put(root, rootElem);
        toProcess.add(root);
        process(toProcess);
    }

    public void setActiveStep(int step) {
        input.setActiveStep(step);
        buildTree();
    }

    private void process(ArrayList<String> toProcess) {
        for (int i = 0; i < toProcess.size(); i++) {
            processProduction(toProcess.get(i));
        }
    }

    //abfangen falls root Symbol nicht NonTerminal ist
    private void processProduction(String root) {
        if (temp.containsKey(root)) {
            actualRoot = temp.get(root);
        }
        else {
            actualRoot = new Elem(root, Type.NonTerminal);
            temp.put(root, actualRoot);
        }

        NodeList list = input.getProduction(root);
        for (int i = 1; i < list.getLength(); i++) {
            node = new Node();
            actualRoot.addNode(node);
            splitSymbol(list.item(i).getTextContent());
        }
    }

    //fange Fall ab falls Symbol nicht definiert in n oder t
    private void splitSymbol(String symbols) {
        String detected = "";
        for (int i = 0; i < symbols.length(); i++) {
            detected += symbols.charAt(i);
            if (checkSymbol(detected)) detected = "";
        }
        /*
        if (detected.length() > 0) ERROR
         */
    }

    private boolean checkSymbol(String detected) {
        if (input.isNonTerminal(detected)) {
            if (temp.containsKey(detected)) node.addElement(temp.get(detected));
            else {
                temp.put(detected, node.addNonTerminalToNode(detected));
                toProcess.add(detected);
            }

            return true;
        }
        else if (input.isTerminal(detected)) {
            node.addTerminalToNode(detected);
            return true;
        }
        else if (detected.equals("/Eps")) {
            node.addTerminalToNode(detected);
            return true;
        }
        return false;
    }

    public HashMap<String, Elem> getMap() {
        return temp;
    }

    public Elem getRootElem() {
        return rootElem;
    }

    public Node newNode(String character) {
        node = new Node();
        checkSymbol(character);
        return node;
    }

    public void printTree() {
        ArrayList<Elem> touched = new ArrayList<>();
        touched.add(rootElem);
        for (int i = 0; i < touched.size(); i++) {
            System.out.println("_______");
            System.out.println("Root: " +  touched.get(i).getString());
            for (int j = 0; j < touched.get(i).getList().size(); j++) {
                ArrayList<Elem> nodelist = touched.get(i).getList().get(j).getNodeList();
                for (int k = 0; k < nodelist.size(); k++) {
                    System.out.println("Index k: " + k + "   ::" + nodelist.get(k).getString());
                    if (nodelist.get(k).getType() == Type.NonTerminal && !touched.contains(nodelist.get(k))) touched.add(nodelist.get(k));
                }
            }
        }
    }

}
    /*public void buildTree() {
        root = new Elem(input.getRootElem(), Type.NonTerminal);
        ArrayList<String> toProcess = new ArrayList<>();
        toProcess.add(input.getRootElem());
        temp = new HashMap<>();
        temp.put(input.getRootElem(), root);

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


}
*/