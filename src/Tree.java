import java.util.ArrayList;
import java.util.HashMap;

public class Tree {
    private ArrayList<String> touched = new ArrayList<String>();
    private Input input = new Input();
    private HashMap<String, Element> temp = new HashMap<>();

    public void buildTree() {
        String start = input.getNextProduction();
        Element root = new Element(start, input, touched, temp);
        temp.put(root.getElement(), root);

        System.out.println(root.getElement());
        System.out.println("____");

        ArrayList<Element> elements = new ArrayList<Element>();
        elements.add(root);
        ArrayList<Node> nodelist;
        ArrayList<Element> nextlevel;
        int i = 4;
        while (i != 0) {
            nodelist = new ArrayList<>();
            nextlevel = new ArrayList<>();
            for (int j = 0; j < elements.size(); j++) {
                for (int k = 0; k < elements.get(j).getList().size(); k++) {
                    nodelist.add(elements.get(j).getList().get(k));
                }
            }
            for (int j = 0; j < nodelist.size(); j++) {
                System.out.println("Nodes Anzahl: " + nodelist.size());
                for (int k = 0; k < nodelist.get(j).getList().size(); k++) {
                    System.out.println("Elemente Anzahl: " + nodelist.get(j).getList().size());
                    nextlevel.add(nodelist.get(j).getList().get(k));
                    System.out.println(nodelist.get(j).getList().get(k).getElement());
                }
            }
            elements = nextlevel;
            System.out.println(":_:_:_:_:_:");
            for (int j = 0; j < nextlevel.size(); j++) {
                System.out.println(nextlevel.get(j).getElement());
            }
            i--;
            System.out.println("______");
        }
    }
}
