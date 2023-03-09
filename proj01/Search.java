import java.io.*;
import java.lang.reflect.Array;
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
                                        Double.parseDouble(in.next()), // lon
                                        0,
                                        0);
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

                                graph.put(city1, neighbors);
                        }
                        if (graph.containsKey(city2)) {
                                graph.get(city2).add(city1);
                        } else {
                                ArrayList<City> neighbors = new ArrayList<>();
                                neighbors.add(city1);
                                graph.put(city2, neighbors);
                        }
                }

                in.close();
                return graph;
        }

        private static double find_distance(City a, City b) {
                double distance = Math.sqrt(
                                Math.pow((a.getLat() - b.getLat()), 2) + Math.pow((a.getLon() - b.getLon()), 2)) * 100;
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
                                neighbors.sort(
                                                (a, b) -> a.getName().compareTo(b.getName()));
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
                                path.add(0, current);
                                current = closed.get(current);
                        }
                        path.add(0, start);
                }
                return path;
        }

        private static ArrayList<City> dfs(City start, City dest) {
                Stack<City> stack = new Stack<City>();
                HashMap<City, City> closed = new HashMap<City, City>();
                boolean success = false;
                stack.push(start);
                while (!stack.isEmpty()) {
                        City node = stack.pop();
                        // System.out.print(node.getName() + " ");
                        if (node.equals(dest)) {
                                success = true;
                                break;
                        } else {
                                ArrayList<City> neighbors = city_graph.get(node);
                                neighbors.sort(
                                                (a, b) -> b.getName().compareTo(a.getName()));
                                for (City neighbor : neighbors) {
                                        if (!closed.containsKey(neighbor)) {
                                                closed.put(neighbor, node);
                                                if (neighbor.equals(dest)) {
                                                        success = true;
                                                        break;
                                                } else {
                                                        if (!stack.contains(neighbor)) {
                                                                stack.push(neighbor);
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
                                path.add(0, current);
                                current = closed.get(current);
                        }
                        path.add(0, start);
                }
                return path;
        }

        private static ArrayList<City> astar(City start, City dest) {
                PriorityQueue<City> open = new PriorityQueue<City>(
                                new Comparator<City>() {
                                        public int compare(City a, City b) {
                                                return (int) (a.getF() - b.getF());
                                        }
                                });
                HashMap<City, City> closed = new HashMap<City, City>();
                boolean success = false;
                open.add(start);
                while (!open.isEmpty()) {
                        City node = open.remove();
                        // System.out.print(node.getName() + " ");
                        if (node.equals(dest)) {
                                success = true;
                                break;
                        } else {
                                ArrayList<City> neighbors = getNeighbors(node, dest);
                                neighbors.sort(
                                                (a, b) -> (int) (a.getF() - b.getF()));
                                for (City neighbor : neighbors) {
                                        if (!closed.containsKey(neighbor)) {
                                                closed.put(neighbor, node);
                                                if (neighbor.equals(dest)) {
                                                        success = true;
                                                        break;
                                                } else {
                                                        if (!open.contains(neighbor)) {
                                                                open.add(neighbor);
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
                                path.add(0, current);
                                current = closed.get(current);
                        }
                        path.add(0, start);
                }
                return path;
        }

        private static ArrayList<City> getNeighbors(City node, City dest) {
                ArrayList<City> neighbors = city_graph.get(node);
                for (City c : neighbors) {
                        c.setG(node.getG() + find_distance(node, c));
                        c.setF(c.getG() + find_distance(c, dest));
                }
                return neighbors;
        }

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
                        System.err.println(e.getMessage());
                        // e.printStackTrace();
                        System.exit(0);
                }
                if (Array.getLength(args) < 2){
                        System.err.println("Usage: java Search inputFile outputFile");
                        System.exit(0);
                }
                String input = args[0];
                String output = args[1];

                if (input.equals("-")){
                        input = "stdin";
                }
                if (output.equals("-")){
                        output= "stdout";
                }

                String start = "";
                String dest = "";

                try( Scanner in = new Scanner(new File(input));
                     BufferedWriter out = new BufferedWriter(new FileWriter(new File(output)))){
                        start = in.next();
                        dest = in.next();
                        ArrayList<City> bfsResult = new ArrayList<>();
                        ArrayList<City> dfsResult = new ArrayList<>();
                        ArrayList<City> astarResult = new ArrayList<>();

                        if (!cities.containsKey(start)){
                                System.err.println("No such city: "+start+"");
                                System.exit(0);
                        } else if (!cities.containsKey(dest)){
                                System.err.println("No such city: "+dest+"");
                                System.exit(0);
                        } else {
                                bfsResult = bfs(cities.get(start), cities.get(dest));
                                dfsResult = dfs(cities.get(start), cities.get(dest));
                                astarResult = astar(cities.get(start), cities.get(dest));
                        }
                        

                } catch (IOException e) {
                        System.err.println(e.getMessage());
                        // e.printStackTrace();
                }

                // ArrayList<City> bfsResult = bfs(cities.get("Denver"), cities.get("Boston"));
                // for (City c : bfsResult) {
                //         System.out.println(c);
                // }
                // System.out.println();
                // ArrayList<City> dfsResult = dfs(cities.get("Denver"), cities.get("Boston"));
                // for (City c : dfsResult) {
                //         System.out.println(c);
                // }
                // System.out.println();
                // ArrayList<City> astarResult = astar(cities.get("Denver"), cities.get("Boston"));
                // for (City c : astarResult) {
                //         System.out.println(c);
                // }
        }
}