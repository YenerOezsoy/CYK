import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Input {
    private File fXmlFile;
    private DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;
    private Document doc;
    private int[] size;
    private NodeList production;

    public Input () {
        try {
            fXmlFile = new File("grammatik.dtd");
            dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setIgnoringElementContentWhitespace(true);
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        setActiveStep(0);
        initializeArray();
    }

    private void initializeArray() {
        size = new int[doc.getElementsByTagName("zeile").getLength()];
        for (int i = 0; i < size.length; i++) {
            size[i] = 1;
        }
    }

    public String getRootElement() {
        return production.item(3).getTextContent();
    }

    public int getProductionSize(String s) {
        ArrayList<Node> list = getTags("zelleStart");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTextContent().equals(s)) {
                return getTags("zeile").get(i).getChildNodes().getLength();
            }
        }
        return 0;
    }

    public Type getType(String s) {
        ArrayList<Node> terminal = getTags("t");
        if (terminal.get(0).getTextContent().contains(s)) return Type.Terminal;
        ArrayList<Node> nonterminal = getTags("n");
        if (nonterminal.get(0).getTextContent().contains(s)) return Type.NonTerminal;
        if(s.equals("Epsilon")) return Type.Terminal;
        return null;
    }

    public String getProduction(String s, int column) {
        ArrayList<Node> productions = getTags("zelleStart");
        for (int i = 0; i < productions.size(); i++) {
            if (productions.get(i).getTextContent().equals(s)) return getTags("zeile").get(i).getChildNodes().item(column).getTextContent();
        }
        return null;
    }

    public void setActiveStep(int step) {
        if (step == 0) production = doc.getElementsByTagName("grammatik").item(0).getChildNodes();
        else production = doc.getElementsByTagName("produktion").item(step).getChildNodes();
    }

    public ArrayList<Node> getTags(String tag) {
       ArrayList<Node> toReturn = new ArrayList<>();
       addChilds(toReturn, production, tag);
       return toReturn;
    }

    private void addChilds(ArrayList<Node> list,NodeList node, String tag) {
        for (int j = 0; j < node.getLength(); j++) {
            if (node.item(j).getNodeName().equals(tag)) list.add(node.item(j));
            if (node.item(j).hasChildNodes()) addChilds(list, node.item(j).getChildNodes(), tag);
        }
    }

    public String[] getNonTerminal() {
        String[] split = getTags("n").get(0).getTextContent().split(";");
        return split;
    }

}
