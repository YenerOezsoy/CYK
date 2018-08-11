
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


    public boolean buildTree() {
        //prüfe beim Erstellen der Datei ob StartSymbol auch in NichtTerminal vorhanden ist
        String root = input.getRootElement();
        rootElem = new Elem(root, Type.NonTerminal);
        toProcess = new ArrayList<>();
        temp = new HashMap<>();
        temp.put(root, rootElem);
        toProcess.add(root);
        return process(toProcess);
    }


    public void setActiveStep(int step) {
        input.setActiveStep(step);
        buildTree();
    }

    private boolean process(ArrayList<String> toProcess) {
        for (int i = 0; i < toProcess.size(); i++) {
            if(!processProduction(toProcess.get(i))) return false;
        }
        return true;
    }

    private boolean processProduction(String root) {
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
            if(!splitSymbol(list.item(i).getTextContent())) return false;
        }
        return true;
    }

    //fange Fall ab falls Symbol nicht definiert in n oder t
    private boolean splitSymbol(String symbols) {
        symbols = symbols.trim();
        String[] detected = symbols.split("\\s+");
        for (int i = 0; i < detected.length; i++) {
            if(!checkSymbol(detected[i])) return false;
        }
        return true;
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