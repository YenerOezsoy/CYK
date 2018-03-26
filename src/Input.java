import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Input {
    private File fXmlFile;
    private DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;
    private Document doc;
    private int[] size;

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
        initializeArray();
    }

    private void initializeArray() {
        size = new int[doc.getElementsByTagName("zeile").getLength()];
        for (int i = 0; i < size.length; i++) {
            size[i] = 1;
        }
    }
    /*public void read() {
        try {

            File fXmlFile = new File("grammatik.dtd");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            //Nonterminal ausgeben

            System.out.println("Nicht Terminale: ");

            NodeList nonterminalList = doc.getElementsByTagName("n");
            String nonterminal = nonterminalList.item(0).getTextContent();
            String[] split = nonterminal.split(";");
            for (int i = 0; i < split.length; i++) {
                System.out.println(split[i]);
            }

            System.out.println("______________");
            System.out.println("Terminale:");

            NodeList terminalList = doc.getElementsByTagName("t");
            String terminal = terminalList.item(0).getTextContent();
            split = terminal.split(";");
            for (int i = 0; i < split.length; i++) {
                System.out.println(split[i]);
            }

            System.out.println("______________");
            System.out.println("Startsymbol: ");

            String start = doc.getElementsByTagName("s").item(0).getTextContent();

            System.out.println(start);

            System.out.println("______________");
            System.out.println("Produktionen:");

            NodeList productionList = doc.getElementsByTagName("p");
            NodeList lineList = productionList.item(0).getChildNodes();
            for (int zeile = 0; zeile < lineList.getLength(); zeile++) {
                for (int zelle = 0; zelle < lineList.item(zeile).getChildNodes().getLength(); zelle++) {
                    System.out.print(lineList.item(zeile).getChildNodes().item(zelle).getTextContent());
                }
            }



        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public String getNextProduction(String s) {
        NodeList productions = doc.getElementsByTagName("zelleStart");
        for (int i = 0 ; i < productions.getLength(); i++) {
            if (productions.item(i).getTextContent().equals(s)) {
                NodeList productionList = doc.getElementsByTagName("zeile").item(i).getChildNodes();
                if (productionList.getLength() > size[i]) {
                    return productionList.item(size[i]++).getTextContent();
                }
                return null;
            }
        }
        return null;

    }

    public String getNextProduction() {
        return doc.getElementsByTagName("zeile").item(0).getChildNodes().item(0).getTextContent();
    }

    public int getSize(String s) {
        NodeList list = doc.getElementsByTagName("zeile");
        for(int i = 0; i < list.getLength(); i++) {
             if (list.item(i).getChildNodes().item(0).getTextContent().equals(s)) return list.item(i).getChildNodes().getLength()-1;
        }
        return 0;
    }

    public Type getType(String s) {
        NodeList terminal = doc.getElementsByTagName("t");
        if (terminal.item(0).getTextContent().contains(s)) return Type.Terminal;
        NodeList nonterminal = doc.getElementsByTagName("n");
        if (nonterminal.item(0).getTextContent().contains(s)) return Type.NonTerminal;
        if(s.equals("Epsilon")) return Type.Terminal;
        return null;
    }

    public void printSize() {
        for (int i = 0; i < size.length; i++) {
            System.out.println(size[i]);
        }
    }

}
