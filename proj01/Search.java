import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Search {

        private final static String CITY_FILE = "city.dat";
        private final static String EDGE_FILE = "edge.dat";
        private static HashMap<City, ArrayList<City>> city_graph;

        /**
         *  Generates a city hashmap with their locations
         * @param fn the name of the city file
         * @return hashmap of city names to their city info
         * @throws FileNotFoundException
         */
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

        /**
         * Generates a graph of city nodes for pathfinding
         * @param fn name of edge file with route information
         * @param cities hashmap of cities
         * @return the generated graph
         * @throws FileNotFoundException
         */
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

        /**
         * Finds the distance in miles between city a and b
         * @param a first city
         * @param b second city
         * @return distance in miles between the two cities
         */
        private static double find_distance(City a, City b) {
                double distance = Math.sqrt(
                                (a.getLat() - b.getLat()) *(a.getLat() - b.getLat()) +
                                (a.getLon() - b.getLon()) *(a.getLon() - b.getLon())) * 100;
                return distance;
        }

        /**
         * Finds the smallest number of hops using BFS between start and dest
         * @param start starting city
         * @param dest destination city
         * @return the path between start and dest
         */
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

        /**
         * Finds a possible route between start and dest using DFS
         * @param start starting city
         * @param dest destination city
         * @return generated path between start and dest
         */
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

        /**
         * Finds the shortest distance between start and dest using A* search
         * @param start the starting city
         * @param dest the destination city
         * @return generated path between start and dest
         */
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

        /**
         * Finds the cities adjacent to the parent city that are not in the closed set,
         * and calculates their cost-so-far and evaluation function (used in A*)
         * @param parent the parent city to expand
         * @param dest the end destination city
         * @param closed the set of cities already visited by the pathfinder
         * @return list of possible cities to check in the pathfinder, with updated g and f
         */
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

        /**
         * Searches through the generated city graph using BFS, DFS, and A*
         * Outputs the result of each algorihtm into specified file
         * @param args the input file and output filenames
         */
        public static void main(String[] args) {
                // abs_path = new File(".").getAbsolutePath();
                HashMap<String, City> cities = new HashMap<String, City>();
                try {
                        cities = parse_cities(CITY_FILE.trim());
                } catch (FileNotFoundException e) {
                        System.err.println("File not found: city.dat");
                        // e.printStackTrace();
                        System.exit(0);
                }
                try {
                        city_graph = parse_graph(EDGE_FILE.trim(), cities);
                } catch (FileNotFoundException e) {
                        System.err.println("File not found: edge.dat");
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
                                try {
                                        in = new Scanner(new File(args[0]));
                                        start = in.next();
                                        dest = in.next();
                                } catch (FileNotFoundException e) {
                                        System.err.println("File not found: " + args[0]);
                                        System.exit(0);
                                }
                                
                        } else {
                                in = new Scanner(System.in);
                                start = in.next();
                                dest = in.next();
                        }
                        if (!args[1].equals("-")){
                                out = new BufferedWriter(new FileWriter((new File(args[1]))));
                        } else {
                                out = new BufferedWriter(new OutputStreamWriter(System.out));
                        }
                        
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

                        out.newLine();
                        out.write("Breadth-First Search Results: ");
                        out.newLine();
                        out.flush();
                        for (City c : bfsResult){
                                out.write(c.getName());
                                out.newLine();
                                out.flush();
                        }
                        out.write("That took "+ (bfsResult.size()-1) +" hops to find.");
                        out.newLine();
                        out.write(String.format("Total distance = %.0f miles.",bfsDist));
                        out.newLine();
                        out.newLine();
                        out.flush();
                        out.newLine();
                        out.write("Depth-First Search Results: ");
                        out.newLine();
                        out.flush();
                        for (City c : dfsResult){
                                out.write(c.getName());
                                out.newLine();
                                out.flush();
                        }
                        out.write("That took "+ (dfsResult.size()-1) +" hops to find.");
                        out.newLine();
                        out.write(String.format("Total distance = %.0f miles.",dfsDist));
                        out.newLine();
                        out.newLine();
                        out.flush();
                        out.newLine();
                        out.write("A* Search Results: ");
                        out.newLine();
                        out.flush();
                        for (City c : astarResult){
                                out.write(c.getName());
                                out.newLine();
                                out.flush();
                        }
                        out.write("That took "+ (astarResult.size()-1) +" hops to find.");
                        out.newLine();
                        out.write(String.format("Total distance = %.0f miles.",astarDist));
                        out.newLine();
                        out.newLine();
                        out.flush();


                } catch (IOException e) {
                        System.err.println(e.getMessage());
                        // e.printStackTrace();
                }

        }
}