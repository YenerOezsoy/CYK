import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public class CYK {

    private String[] word;
    private int size;
    private int stage;
    private int previousStage;
    private int stageCounter;
    private int previousStageCounter;
    private int offset;
    private int stageCounter2;
    private int i;
    private int previousI;
    private int counter = 0;
    private HashMap<Integer, ArrayList<String>[]> pyramid;
    private HashMap<String, Elem> map;
    private String rootValue = null;


    public void initialize(String w) {
        pyramid = new HashMap<>();
        size = w.length();
        stage = size;
        word = new String[size];
        for (int i = 0; i < w.length(); i++) {
            word[i] = String.valueOf(w.charAt(i));
        }
        buildPyramide(size);
        initLoop();
    }

    public int getSize() {
        return size;
    }

    public String getRoot() {
        return rootValue;
    }


    public int[] nextStep() {
        previousStageCounter = stageCounter;
        previousStage = stage;
        previousI = i;
        if (i == stage && stage != 0) {
            stage--;
            initLoop();
        }
        if(stage == 0) {
            return null;
        }
        return doLoop();
    }

    public int[] previousStep() {
        initLoop();
        stage = size;
        int tempPreviousStage = previousStage;
        int tempPreviousStageCounter = previousStageCounter;
        int tempI = previousI;
        int[] returnValue = null;
        while (stage != tempPreviousStage || stageCounter != tempPreviousStageCounter || i != tempI) {
            returnValue = nextStep();
        }
        return returnValue;
    }

    public HashMap<Integer, ArrayList<String>[]> getPyramid() {
        return pyramid;
    }

    private void initLoop() {
        initInnerLoop();
        i = 0;
    }

    private void initInnerLoop() {
        stageCounter = size;
        stageCounter2 = stage + 1;
        offset = 1;
    }

    private int[] doLoop() {
        return initializeStage(stage, i);
    }

    public void setMap(HashMap<String, Elem> map) {
        this.map = map;
    }

    private void buildPyramide(int size) {
        while (size != 0) {
            ArrayList[] stage = new ArrayList[size];
            for (int i = 0; i < stage.length; i++) {
                stage[i] = new ArrayList<String>();
            }
            pyramid.put(size--, stage);
        }
    }

    private int[] initializeStage(int stage, int i) {
        int[] analyze;
        boolean changed = true;
        int actualIndex = i;
        if (stage == size) {
            checkColumn(stage, i);
            analyze = initAnalyze(stage, i);
        }
        else if (stage == size - 1){
            checkColumn(stage, stage + 1, i, i + 1);
            analyze = initAnalyze(stage, i,stage + 1, i, i + 1);
        }
        else {
            checkColumn(stage,i, stageCounter, i, stageCounter2, i + offset);
            analyze = initAnalyze(stage,i, stageCounter, i, stageCounter2, i + offset);
            stageCounter--;
            stageCounter2++;
            offset++;
            if (stageCounter > stage) {
                this.i--;
                changed = false;
            }
            else initInnerLoop();
        }
        setRootValue(actualIndex);
        this.i++;
        checkCounter(changed);
        return analyze;
    }
    private void checkCounter(boolean changed) {
        if (changed) counter = 0;
    }

    private void setRootValue(int actualIndex) {
        if (pyramid.get(stage)[actualIndex].isEmpty()) {
            rootValue = null;
            counter = 0; //??necessary?
        }
        else {
            if (stage > size - 2) {
                concatRootValues(pyramid.get(stage)[actualIndex]);
            }
            else {
                if (pyramid.get(stage)[actualIndex].size() > counter) {
                    rootValue = pyramid.get(stage)[actualIndex].get(counter);
                    counter++;
                }
            }

        }
    }

    private void concatRootValues(ArrayList<String> list) {
        rootValue = "";
        HashSet<String> set = new HashSet<>(list);
        for (String values : set) {
            rootValue += values + ",";
        }
        rootValue = rootValue.substring(0, rootValue.length() - 1);
    }

    private int[] initAnalyze(int row, int column) {
        int[] analyze = {row, column};
        return analyze;
    }

    private int[] initAnalyze(int row, int column, int row1, int column1, int column2) {
        int[] analyze = {row, column, row1, column1, column2};
        return analyze;
    }

    private int[] initAnalyze(int row, int column, int row1, int column1, int row2, int column2) {
        int[] analyze = {row, column, row1, column1, row2, column2};
        return analyze;
    }

    private void checkColumn(int stage, int column) {
        //pyramid.get(stage)[column].add(map.get(word[column]).getString());
        copy(pyramid.get(stage)[column], getNonTerminals(word[column]));
    }

    private void checkColumn(int stage, int stage1, int column1, int column2) {
        checkCombinations(stage, column1, pyramid.get(stage1)[column1], pyramid.get(stage1)[column2]);
    }

    private void checkColumn(int stage, int column, int stage1, int column1, int stage2, int column2) {
        checkCombinations(stage, column, pyramid.get(stage1)[column1], pyramid.get(stage2)[column2]);
    }

    private ArrayList<String> getNonTerminals(String var1, String var2) {
        ArrayList<String> found = new ArrayList();
        ArrayList list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            if (map.get(list.get(i)).hasChildren(var1, var2)) found.add(map.get(list.get(i)).getString());
        }
        return found;
    }

    private ArrayList<String> getNonTerminals(String var) {
        ArrayList<String> found = new ArrayList();
        ArrayList list = new ArrayList<>(map.keySet());
        for (int i = 0; i < list.size(); i++) {
            if (map.get(list.get(i)).hasOnlyChildren(var)) found.add(map.get(list.get(i)).getString());
        }
        return found;
    }

    private void checkCombinations(int stage, int column, ArrayList<String> list1, ArrayList<String> list2) {
        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                copy(pyramid.get(stage)[column], getNonTerminals(list1.get(i), list2.get(j)));
            }
        }
    }

    private void copy(ArrayList<String> destination, ArrayList<String> source) {
        for (int i = 0; i < source.size(); i++) {
            if(!destination.contains(source.get(i))) destination.add(source.get(i));
        }
    }

    public void print() {
        for(int i = pyramid.size() ; i >= 0; i--) {
            ArrayList[] list = pyramid.get(i);
            if(list != null) {
                for(int j = 0; j < list.length; j++) {
                    ArrayList list2 = list[j];
                    for (int k = 0; k < list2.size(); k++) {
                        System.out.print(list2.get(k));
                        System.out.print(",");
                    }
                    System.out.print(" ; ");
                }
                System.out.println("");
            }

        }
    }

    public void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
        System.out.println("____________");
    }


}
