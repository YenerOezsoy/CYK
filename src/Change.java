public class Change {

    private Output output;
    String changes;

    public Change(Output output) {
        this.output = output;
    }

    public void createNewChangeSet() {
        changes = "";
    }

    public void add(String change) {
        seperator();
        changes += "add(" + change + ")";
    }

    public void addTo(String change) {
        seperator();
        changes += "addTo(" + change + ")";
    }

    /*
    copy node from x to y
    Destination , Source , Node Identifier
     */
    public void addNode(String change) {
        seperator();
        changes += "addNode(" + change + ")";
    }

    public void replace(String change) {
        seperator();
        changes += "replace(" + change + ")";
    }

    public void delete(String change) {
        seperator();
        changes += "delete(" + change + ")";
    }

    public void deleteNode(String change) {
        seperator();
        changes += "deleteNode(" + change + ")";
    }

    /*
    change besteht aus:
        Root und Elem
             oder
        Root
     */
    public void mark(String change) {
        seperator();
        changes += "mark(" + change + ")";
    }

    public void highlight(String change, int i) {
        seperator();
        changes += "highlight(" + change + "," + i + ")";
    }

    public void highlight(String change) {
        seperator();
        changes += "highlight(" + change + ")";
    }

    public void highlightAll(String change) {
        seperator();
        changes += "highlightAll(" + change +")";
    }

    public void test () {
        System.out.println(changes);
    }

    /*public void write() {
        output.createChangeTag();
        output.addChange(changes);
    }*/

    public String getChanges() {
        return changes;
    }

    private void seperator() {
        if (changes.length() != 0) changes += "; ";
    }

}
