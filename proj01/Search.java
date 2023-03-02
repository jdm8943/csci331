import java.io.*;
import java.util.*;
import java.nio.file.Paths;

public class Search {

        private static String abs_path;
        private final static String CITY_FILE = "\\proj01\\city.dat";
        private final static String EDGE_FILE = "\\proj01\\edge.dat";
        private static String input_file = "stdin";
        private static String out_file = "stdout";
        private static HashMap<City, ArrayList<City>> city_graph;

        private static HashMap<String, City> parse_cities(String fn) throws FileNotFoundException {
                HashMap<String, City> cities = new HashMap<>();
                Scanner in = new Scanner(new File(fn));
                while (in.hasNext()) {
                        City city = new City(
                                        in.next(), // name
                                        in.next(), // abbrev
                                        Double.parseDouble(in.next()), // lat
                                        Double.parseDouble(in.next()) // lon
                        );
                        cities.put(city.getName(), city);
                }
                in.close();
                return cities;
        }

        private static HashMap<City, ArrayList<City>> parse_graph(String fn, HashMap<String, City> cities)
                        throws FileNotFoundException {
                HashMap<City, ArrayList<City>> graph = new HashMap<City, ArrayList<City>>();
                Scanner in = new Scanner(new File(fn));
                while (in.hasNext()) {
                        City city1 = cities.get(in.next());
                        City city2 = cities.get(in.next());
                        if (graph.containsKey(city1)) {
                                graph.get(city1).add(city2);
                        } else {
                                ArrayList<City> neighbors = new ArrayList<>();
                                neighbors.add(city2);
                                neighbors.sort(
                                                (a, b) -> a.getName().compareTo(b.getName()));
                                graph.put(city1, neighbors);
                        }
                        if (graph.containsKey(city2)) {
                                graph.get(city2).add(city1);
                        } else {
                                ArrayList<City> neighbors = new ArrayList<>();
                                neighbors.add(city1);
                                neighbors.sort(
                                                (a, b) -> a.getName().compareTo(b.getName()));
                                graph.put(city2, neighbors);
                        }
                }

                in.close();
                return graph;
        }

        private static double find_distance(double lat1, double lat2, double lon1, double lon2) {
                double distance = Math.sqrt((lat1 - lat2) * (lat1 - lat2) + (lon1 - lon2) * (lon1 - lon2)) * 100;
                return distance;
        }

        private static double calculate_f(City city) {
                double fn = 0;
                return fn;
        }

        private static ArrayList<City> bfs(City start, City dest) {
                Queue<City> queue = new LinkedList<City>();
                HashMap<City, City> closed = new HashMap<City, City>();
                boolean success = false;
                queue.add(start);
                while (!queue.isEmpty()) {
                        City node = queue.remove();
                        // System.out.print(node.getName() + " ");
                        if (node.equals(dest)) {
                                success = true;
                                break;
                        } else {
                                ArrayList<City> neighbors = city_graph.get(node);
                                for (City neighbor : neighbors) {
                                        if (!closed.containsKey(neighbor)) {
                                                closed.put(neighbor, node);
                                                if (neighbor.equals(dest)) {
                                                        success = true;
                                                        break;
                                                } else {
                                                        if (!queue.contains(neighbor)) {
                                                                queue.add(neighbor);
                                                        }
                                                }
                                        }
                                }
                        }
                }

                ArrayList<City> path = new ArrayList<City>();
                if (success) {
                        City current = dest;
                        while (!current.equals(start)) {
                                path.add(current);
                                current = closed.get(current);
                        }
                        path.add(start);
                }
                return path;
        }

        // java comparator?

        // class distanceComparator implements Comparator<City> {
        // @Override
        // public int compare(City a, City b) {
        // return a.name.compareToIgnoreCase(b.name);
        // }
        // }

        public static void main(String[] args) {
                abs_path = new File(".").getAbsolutePath();
                HashMap<String, City> cities = new HashMap<String, City>();
                try {
                        cities = parse_cities(abs_path + CITY_FILE.trim());
                        // System.out.println(cities.values());
                        city_graph = parse_graph(abs_path + EDGE_FILE.trim(), cities);
                        // System.out.println(city_graph);
                        // System.out.println(city_graph.get(cities.get("Boston")));
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                }
                // bfs
                ArrayList<City> bfsResult = bfs(cities.get("Denver"), cities.get("Boston"));
                for (City c : bfsResult) {
                        System.out.println(c);
                }
        }
}