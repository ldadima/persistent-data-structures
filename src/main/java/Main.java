import base_structure.PersistentBTree;
import base_structure.PersistentTree;

public class Main {
    public static void main(String[] args) {
        PersistentTree<Integer, String> tree = new PersistentBTree<>(2);
        for (int i = 0; i < 20; i++) {
            tree = tree.put(i, String.valueOf(i));
        }
        System.out.println(tree.get(14));
        PersistentTree<Integer, String> tree2 = tree.put(14, "new");
        System.out.println(tree2.get(14));
        tree2 = tree2.remove(15);
        System.out.println(tree2.get(15));
        System.out.println(tree2.get(14));
    }
}
