import persistent_collections.PersistentMap;

public class Main {
    public static void main(String[] args) {
        PersistentMap<Integer, PersistentMap<Integer, String>> map = new PersistentMap<>();
        PersistentMap<Integer, String> subMap = new PersistentMap<>();
        subMap.put(5, "5");
        subMap.put(6, "6");
        System.out.println(subMap.entrySet());
        map.put(1, subMap);
        map.entrySet().forEach(e -> System.out.println(e.getKey().toString() + "->" + e.getValue().entrySet()));
        map.undo();
        map.entrySet().forEach(e -> System.out.println(e.getKey().toString() + "->" + e.getValue().entrySet()));
    }
}
