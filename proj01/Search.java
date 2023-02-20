import java.util.*;

public class Search {

        private final static String CITY_FILE = "city.dat";
        private final static String EDGE_FILE = "edge.dat";
        private static String input_file = "stdin";
        private static String out_file = "stdout";
        private static HashMap<City,ArrayList<City>> city_graph;

        private static HashMap<String,City> parse_cities(String fn){
                HashMap<String,City> cities = new HashMap<>();
                Scanner in = new Scanner(fn);
                while(in.hasNext()){
                        City city = new City(in.next(),in.next(),Double.parseDouble(in.next()),Double.parseDouble(in.next()));
                        cities.put(city.getName(), city);
                }
                in.close();
                return cities;
        }

        private static HashMap<City,ArrayList<City>> parse_graph(String fn, HashMap<String, City> cities){
                HashMap<City,ArrayList<City>> graph = new HashMap<City,ArrayList<City>>();
                Scanner in = new Scanner(fn);
                while(in.hasNext()){
                        String 
                }
                return graph;
        }

        public static void main(String[] args) {
                HashMap<String,City> cities = parse_cities(CITY_FILE);
                city_graph = parse_graph(EDGE_FILE, cities);

        }
}