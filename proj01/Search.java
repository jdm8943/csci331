import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Search {

        private static String abs_path;
        private final static String CITY_FILE = "\\proj01\\city.dat";
        private final static String EDGE_FILE = "\\proj01\\edge.dat";
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
                                (a.getLat() - b.getLat()) *(a.getLat() - b.getLat()) +
                                (a.getLon() - b.getLon()) *(a.getLon() - b.getLon())) * 100;
                return distance;
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
                                ArrayList<City> neighbors = getNeighbors(node, dest, closed);
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
                                ArrayList<City> neighbors = getNeighbors(node, dest, closed);
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
                                ArrayList<City> neighbors = getNeighbors(node, dest, closed);
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

        private static ArrayList<City> getNeighbors(City parent, City dest, HashMap<City, City> closed) {
                ArrayList<City> neighbors = city_graph.get(parent);
                // System.out.println(parent.getName() + "'s G: " + parent.getG());
                for (City c : neighbors) {
                        if (!closed.containsKey(c)){
                                c.setG(parent.getG() + find_distance(parent, c));
                                // System.out.println("Child: " + c.getName() + "'s G : " + c.getG());
                                c.setF(c.getG() + find_distance(c, dest));
                        }
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

                // for (City key : city_graph.keySet()){
                //         System.out.println(key.getName() + ": " + city_graph.get(key));
                // }
                

                String start = "";
                String dest = "";

                try {
                        Scanner in;
                        BufferedWriter out;
                        if (!args[0].equals("-")){
                                in = new Scanner(new File(abs_path + "\\proj01\\" + args[0]));
                        } else {
                                in = new Scanner(System.in);
                        }
                        if (!args[1].equals("-")){
                                out = new BufferedWriter(new FileWriter((new File(abs_path + "\\proj01\\" + args[1]))));
                        } else {
                                out = new BufferedWriter(new OutputStreamWriter(System.out));
                        }
                        start = in.next();
                        dest = in.next();
                        ArrayList<City> bfsResult = new ArrayList<>();
                        double bfsDist = 0;
                        ArrayList<City> dfsResult = new ArrayList<>();
                        double dfsDist = 0;
                        ArrayList<City> astarResult = new ArrayList<>();
                        double astarDist = 0;

                        if (!cities.containsKey(start)){
                                System.err.println("No such city: "+start+"");
                                System.exit(0);
                        } else if (!cities.containsKey(dest)){
                                System.err.println("No such city: "+dest+"");
                                System.exit(0);
                        } else {
                                bfsResult = bfs(cities.get(start), cities.get(dest));
                                bfsDist = bfsResult.get(bfsResult.size()-1).getG();
                                for (City c: cities.values()){
                                        c.setG(0);
                                        c.setF(0);
                                }
                                dfsResult = dfs(cities.get(start), cities.get(dest));
                                dfsDist = dfsResult.get(dfsResult.size()-1).getG();
                                for (City c: cities.values()){
                                        c.setG(0);
                                        c.setF(0);
                                }
                                astarResult = astar(cities.get(start), cities.get(dest));
                                astarDist = astarResult.get(astarResult.size()-1).getG();
                                for (City c: cities.values()){
                                        c.setG(0);
                                        c.setF(0);
                                }
                        }

                        out.write("Breadth-First Search Results:");
                        out.newLine();
                        out.flush();
                        for (City c : bfsResult){
                                out.write(c.getName());
                                out.newLine();
                                out.flush();
                        }
                        out.write("That took "+ (bfsResult.size()-1) +" hops to find.");
                        out.newLine();
                        out.write("Total Distance = " + bfsDist + " miles.");
                        out.newLine();
                        out.newLine();
                        out.flush();

                        out.write("Depth-First Search Results:");
                        out.newLine();
                        out.flush();
                        for (City c : dfsResult){
                                out.write(c.getName());
                                out.newLine();
                                out.flush();
                        }
                        out.write("That took "+ (dfsResult.size()-1) +" hops to find.");
                        out.newLine();
                        out.write("Total Distance = " + dfsDist + " miles.");
                        out.newLine();
                        out.newLine();
                        out.flush();

                        out.write("A* Search Results:");
                        out.newLine();
                        out.flush();
                        for (City c : astarResult){
                                out.write(c.getName());
                                out.newLine();
                                out.flush();
                        }
                        out.write("That took "+ (astarResult.size()-1) +" hops to find.");
                        out.newLine();
                        out.write("Total Distance = " + astarDist + " miles.");
                        out.newLine();
                        out.flush();


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