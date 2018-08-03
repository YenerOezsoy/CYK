import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CNF {

    private boolean startedSearch = false;
    private Tree tree;
    private HashMap<String, Elem> map;
    private Elem actualRoot;
    private Elem rootOfCycle;
    private ArrayList<Elem> epsilonStern = new ArrayList<>();
    private ArrayList<Elem> visited = new ArrayList<>();
    private ArrayList<Elem> possibleCycle = new ArrayList<>();
    private ArrayList<Elem> delete = new ArrayList<>();
    private int nameSuffix;
    private Change change = new Change();
    private int nodeId;
    private Output output;// = new Output();


    public CNF(Tree tree) {
        this.tree = tree;
        output = new Output();
        map = tree.getMap();
        visited.add(tree.getRootElem());
    }

    public void terminalRule() {
        change.createNewChangeSet();
        startIteration(0);
        tree.printTree();
        change.test();
        write("terminalRule");
    }

    public void lengthRule() {
        change.createNewChangeSet();
        startIteration(1);
        tree.printTree();
        change.test();
        write("lengthRule");
    }

    public void epsilonRule() {
        change.createNewChangeSet();
        startIteration(2);
        tree.printTree();
        change.test();
        write("epsilonRule");
    }

    public void chainRule() {
        change.createNewChangeSet();
        startIteration(3);
        startIteration(5);
        tree.printTree();
        delete();
        change.test();
        write("chainRule");
    }

    private void startIteration(int rule) {
        List<String> list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            inspectNonTerminal(map.get(list.get(i)),rule);
            nameSuffix = 2;
        }
    }

    private void inspectNonTerminal(Elem element, int rule) {
        //jeder Node bildet eine Ableitungsregel
        ArrayList<Node> nodes = element.getList();
        for (int i = 0; i < nodes.size(); i++) {
            //einzelnes Symbol von Regel nicht betroffen
            // 1. Regel
            if (rule == 0 && nodes.get(i).getNodeList().size() > 1) {
                actualRoot = element;
                inspectChildren(nodes.get(i), rule);
            }
            //2. Regel
            else if (rule == 1 && nodes.get(i).getNodeList().size() > 2) {
                actualRoot = element;
                change.mark(actualRoot.getString());
                nodeId = i;
                shortenNode(element, nodes.get(i));
            }
            //3. Regel
            else if (rule == 2) {
                actualRoot = element;
                nodeId = i;
                inspectChildren(nodes.get(i), rule);
            }
            //Kettenregel Zyklus
            else if (rule == 3) {
                inspectChildren(nodes.get(i), rule);
            }
            else if (rule == 4) {
                actualRoot = element;
                inspectChildren(nodes.get(i), rule);
                cleanUp(element);
            }
            //Kettenregel
            else if (rule == 5) {
                if (nodes.get(i).getNodeList().size() == 1 && nodes.get(i).getNodeList().get(0).getType() == Type.NonTerminal) {
                    Elem elem = nodes.get(i).getNodeList().get(0);
                    for (int j = 0; j < elem.getList().size(); j++) {
                        element.addNode(elem.getList().get(j));
                    }
                    nodes.remove(i);
                }
                cleanUp(element);
            }
        }
    }

    private void inspectChildren(Node node, int rule) {
        for (int i = 0; i < node.getNodeList().size(); i++) {
            if (rule == 0 && node.getNodeList().get(i).getType() == Type.Terminal) {
                change.mark(actualRoot.getString() + "," + node.getNodeList().get(i).getString());
                replaceNonTerminal(node.getNodeList().get(i), node.getNodeList(), i);
            }
            else if(rule == 2) {
                if (node.getNodeList().get(i).getString().equals("/Eps")) {
                    change.mark(actualRoot.getString() + ",/Eps");
                    epsilonStern.clear();
                    removeEpsilon(node, i);
                }
            }
            //Kettenregel Zyklus
            else if (rule == 3) {
                actualRoot = node.getNodeList().get(i);
                if (visited.contains(node.getNodeList().get(i))) checkCycle(node.getNodeList().get(i));
                else visited.add(node.getNodeList().get(i));
            }
            //gehoert zu chainRule -> Methode replaceElement
            else if (rule == 4) {
                if (possibleCycle.contains(node.getNodeList().get(i))) {
                    change.replace(rootOfCycle.getString() + "," + node.getNodeList().get(i).getString());
                    node.getNodeList().add(i, rootOfCycle);
                    node.getNodeList().remove(i + 1);
                }
            }
        }
    }

    // 1. Regel _____________

    private void replaceNonTerminal(Elem elem, ArrayList<Elem> list , int j) {
        String prev;
        String newChar;
        if (map.containsKey(elem.getString())) {
            prev = elem.getString();
            newChar = map.get(elem.getString()).getString();
            list.set(j, map.get(elem.getString()));
        }
        else {
            newChar = elem.getString().toUpperCase() + "1";
            prev = elem.getString();
            elem.setType(Type.NonTerminal);
            elem.setCharacter(newChar);
            elem.addNode(tree.newNode(prev));
            map.put(prev, elem);
            change.add(newChar);
            change.addTo(newChar + "," + prev);
        }
        change.replace(actualRoot.getString() + "," + prev + "," + newChar);
    }


    // 2. Regel _____________

    private void shortenNode(Elem elem, Node node) {
        String name;
        ArrayList<Elem> list = new ArrayList<>(node.getNodeList());
        int size = list.size();
        Node actualNode = node;
        node.getNodeList().clear();
        change.deleteNode(actualRoot.getString() + "," + nodeId);
        for (int i = 0; i < size - 2; i++) {
            name = elem.getString() + nameSuffix++;
            Elem newElem = new Elem(name, Type.NonTerminal);
            map.put(name, newElem);
            actualNode.addElement(list.get(i));
            actualNode.addElement(newElem);
            actualNode = new Node();
            newElem.addNode(actualNode);

            change.addTo(actualRoot.getString() + "," + list.get(i).getString());
            change.addTo(actualRoot.getString() + "," + name);
            change.add(name);
            change.highlight(name);
            actualRoot = newElem;
        }
        actualNode.addElement(list.get(size - 2));
        actualNode.addElement(list.get(size - 1));

        change.addTo(actualRoot.getString() + "," + list.get(size - 2).getString());
        change.addTo(actualRoot.getString() + "," + list.get(size - 1).getString());
    }


    // 3. Regel _____________

    private void removeEpsilon(Node node, int i) {
        change.delete(actualRoot.getString() + "," + node.getNodeList().get(i).getString());

        node.getNodeList().remove(i);
        epsilonStern.add(actualRoot);
        searchBackwards();
        if (node.getNodeList().isEmpty()) {
            actualRoot.getList().remove(node);
            change.deleteNode(actualRoot.getString() + "," + nodeId);
        }
        if (actualRoot.getList().isEmpty()) map.remove(actualRoot.getString());
        else replaceEpsilon();

    }

    //Suche nach Elementen, die Wurzel von Elem aus Liste epsilonStern sind
    private void searchBackwards() {
        for (int i = 0; i < epsilonStern.size(); i++) {
            Elem elem = epsilonStern.get(i);
            List<String> list = new ArrayList<>(map.keySet());
            for (int j = 0; j < list.size(); j++) {
                Elem nonTerminal = map.get(list.get(j));
                if (nonTerminal.hasOnlyChildren(elem.getString()) && !epsilonStern.contains(nonTerminal)) {
                    epsilonStern.add(nonTerminal);
                    change.highlight(nonTerminal.getString());
                }
            }
        }
    }

    private void replaceEpsilon() {
        List<String> list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            Elem root = map.get(list.get(i));
            for (int j = 0; j < epsilonStern.size(); j++) {
                if (root.hasChildrenWSize(epsilonStern.get(j).getString())) addNodes(root, epsilonStern.get(j));
            }
        }
    }

    private void addNodes(Elem root, Elem elem) {
        for (int i = 0; i < root.getList().size(); i++) {
            if (root.getList().get(i).hasElement(elem)) {
                root.addNode(copyWithoutEpsilonTrans(root ,root.getList().get(i), elem));
            }
        }

    }

    private Node copyWithoutEpsilonTrans(Elem root, Node node, Elem elem) {
        Node copy = new Node();
        for(int i = 0; i < node.getNodeList().size(); i++) {
            if(node.getNodeList().get(i) != elem) {
                copy.addElement(node.getNodeList().get(i));
                change.addTo(root.getString() + "," + node.getNodeList().get(i).getString());
            }
        }
        return copy;
    }


    // 4. Regel _____________

    private void checkCycle(Elem elem) {
        startedSearch = false;
        epsilonStern.clear();
        if (isCycle(elem)) copyNodes(findRootOfCycle());
    }

    private boolean isCycle(Elem elem) {
        if (elem.getType() == Type.NonTerminal) {
            if (elem == actualRoot && startedSearch) return true;
            else {
                startedSearch = true;
                boolean result = elementIterator(elem);
                if (result) possibleCycle.add(elem);
                return result;
            }
        }
        else return false;
    }

    private boolean elementIterator(Elem elem) {
        boolean foundElement = false;
        for (int i = 0; i < elem.getList().size(); i++) {
            Node nodeList = elem.getList().get(i);
            //nur Abbildungen der Laenge sind von der Regel betroffen
            if (nodeList.getNodeList().size() == 1 && nodeList.getNodeList().get(0).getType() == Type.NonTerminal) {
                if (!epsilonStern.contains(nodeList.getNodeList().get(0))) {
                    epsilonStern.add(nodeList.getNodeList().get(0));
                    if (isCycle(nodeList.getNodeList().get(0)) && !foundElement) foundElement = true;
                }
            }
        }
        return foundElement;
    }

    private Elem findRootOfCycle() {
        ArrayList<Elem>  touched = new ArrayList<>();
        touched.add(tree.getRootElem());
        return breadthFirstSearch(touched);
    }

    private Elem breadthFirstSearch(ArrayList<Elem> touched) {
        for (int i = 0; i < touched.size(); i++) {
            for (int j = 0; j < touched.get(i).getList().size(); j++) {
                Node node = touched.get(i).getList().get(j);
                Elem foundRoot = cycleIterator(node);
                if (foundRoot != null) return foundRoot;
            }
        }
        return null;
    }

    private Elem cycleIterator(Node node) {
        for (int i = 0; i < possibleCycle.size(); i++) {
            if (node.getNodeList().contains(possibleCycle.get(i))) return possibleCycle.get(i);
        }
        return null;
    }

    private void copyNodes(Elem root) {
        change.mark(root.getString());
        possibleCycle.remove(root);
        for (int i = 0; i < possibleCycle.size(); i++) {
            change.highlight(possibleCycle.get(i).getString());
            for (int j = 0; j < possibleCycle.get(i).getList().size(); j++) {
                root.addNode(possibleCycle.get(i).getList().get(j));
                change.addNode(root.getString() + "," + possibleCycle.get(i).getString() + "," + j);
                change.delete(possibleCycle.get(i).getString());
            }
            delete.add(possibleCycle.get(i));
        }
        replaceElement(root);
        possibleCycle.clear();
    }

    private void replaceElement(Elem root) {
        rootOfCycle = root;
        startIteration(4);
    }

    private void cleanUp(Elem root) {
        for (int i = 0; i < root.getList().size(); i++) {
            if (root.getList().get(i).getNodeList().size() == 1) checkIdentity(root, root.getList().get(i));
            else checkDouble(root, root.getList().get(i));
        }
    }

    private void checkIdentity(Elem root, Node node) {
        if (node.getNodeList().get(0) == root) root.getList().remove(node);
        else checkDouble(root, node);
    }

    private void checkDouble(Elem root, Node node) {
        if (node.getNodeList().size() == 0) root.getList().remove(node);
        else {
            for (int i = root.getList().indexOf(node) + 1; i < root.getList().size(); i++) {
                if (node.getNodeList().size() == root.getList().get(i).getNodeList().size()) {
                    if (isNodeEqual(root.getList().get(i), node)) root.getList().remove(node);
                }

            }
        }

    }

    private boolean isNodeEqual(Node node1, Node node2) {
        for (int i = 0; i < node1.getNodeList().size(); i++) {
            if (node1.getNodeList().get(i) != node2.getNodeList().get(i)) return false;
        }
        return true;
    }

    private void delete() {
        for (int i = 0; i < delete.size(); i++) {
            map.remove(delete.get(i).getString());
        }
    }

    private void write(String name) {
        output.addChange(output.createChangeTag(), change.getChanges());
        output.writeTree(map, tree.getRootElem().getString(),name);
    }

    //CHECKE OB ES GLEICHE ABBILDUNGEN GIBT -> IDENTISCHE NONTERMINALE
}
