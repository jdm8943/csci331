import java.io.*;
import java.util.*;


public class Search {

        private final static String CITY_FILE = "city.dat";
        private final static String EDGE_FILE = "edge.dat";
        private static String input_file = "stdin";
        private static String out_file = "stdout";
        private static HashMap<City,ArrayList<City>> city_graph;

        private static HashMap<String,City> parse_cities(String fn) throws FileNotFoundException{
                HashMap<String,City> cities = new HashMap<>();
                Scanner in = new Scanner(new File(fn));
                while(in.hasNext()){
                        City city = new City(
                                in.next(),                      //name
                                in.next(),                      //abbrev
                                Double.parseDouble(in.next()),  //lat
                                Double.parseDouble(in.next())   //lon
                                );
                        cities.put(city.getName(), city);
                }
                in.close();
                return cities;
        }

        private static HashMap<City,ArrayList<City>> parse_graph(String fn, HashMap<String, City> cities) throws FileNotFoundException{
                HashMap<City,ArrayList<City>> graph = new HashMap<City,ArrayList<City>>();
                Scanner in = new Scanner(new File(fn));
                while(in.hasNext()){
                        City city1 = cities.get(in.next());
                        City city2 = cities.get(in.next());
                        if (graph.containsKey(city1)){
                                graph.get(city1).add(city2);
                        } else {
                                ArrayList<City> neighbors = new ArrayList<>();
                                neighbors.add(city2);
                                graph.put(city1,neighbors);
                        }
                }
                return graph;
        }

        public static void main(String[] args) {
                HashMap<String, City> cities;
                try {
                        cities = parse_cities(CITY_FILE);
                        System.out.println(cities.values());
                        city_graph = parse_graph(EDGE_FILE, cities);
                        System.out.println(city_graph);

                } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                
        }
}