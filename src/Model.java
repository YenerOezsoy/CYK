/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Model {

    private Tree tree;
    private CNF cnf;

    public Model() {
        tree = new Tree();
    }

    public void start() {
        tree.buildTree();
        tree.buildTree();
        System.out.println("FIRST RULE");
        System.out.println("___________");
        cnf = new CNF(tree);
        cnf.terminalRule();


    }


}
