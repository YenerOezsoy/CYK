import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Output {

    private DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;
    Document doc;
    Element rootElement;
    int iterator;

    public Output() {
        iterator = 1;
        dbFactory = DocumentBuilderFactory.newInstance();

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    public void createChangeTag() {
        rootElement = doc.createElement("change");
    }

    public void  addChange(String change) {
        Attr add = doc.createAttribute("" + iterator);
        rootElement.appendChild(add);
        add.setValue(change);
    }

    public void createRule(String rule) {
        rootElement = doc.createElement(rule);
    }
}
