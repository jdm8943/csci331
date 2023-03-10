import java.util.*;

public class City {
        private String name;
        private String abbrev;
        private double lat;
        private double lon;
        private double f;
        private double g;

        /**
         * Holds information for a city, also acts as node in the city graph
         * @param name name of city     
         * @param abbrev abbreviation of a city's state
         * @param lat lattitude of city
         * @param lon longitude of city
         * @param f   evaluation function of city
         * @param g   cost so far of city
         */
        public City(String name, String abbrev, double lat, double lon, double f,double g) {
                this.name = name;
                this.abbrev = abbrev;
                this.lat = lat;
                this.lon = lon;
                this.g = g;
                this.f = f;
        }

        public double getF() {
                return this.f;
        }

        public void setF(double f) {
                this.f = f;
        }

        public double getG() {
                return this.g;
        }

        public void setG(double g) {
                this.g = g;
        }


        public String getName() {
                return this.name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getAbbrev() {
                return this.abbrev;
        }

        public void setAbbrev(String abbrev) {
                this.abbrev = abbrev;
        }

        public double getLat() {
                return this.lat;
        }

        public void setLat(double lat) {
                this.lat = lat;
        }

        public double getLon() {
                return this.lon;
        }

        public void setLon(double lon) {
                this.lon = lon;
        }

        public City name(String name) {
                setName(name);
                return this;
        }

        public City abbrev(String abbrev) {
                setAbbrev(abbrev);
                return this;
        }

        public City lat(double lat) {
                setLat(lat);
                return this;
        }

        public City lon(double lon) {
                setLon(lon);
                return this;
        }

        @Override
        public boolean equals(Object o) {
                if (o == this)
                        return true;
                if (!(o instanceof City)) {
                        return false;
                }
                City city = (City) o;
                return Objects.equals(name, city.name) && Objects.equals(abbrev, city.abbrev) && lat == city.lat
                                && lon == city.lon;
        }

        @Override
        public int hashCode() {
                return Objects.hash(name, abbrev, lat, lon);
        }

        @Override
        public String toString() {
                return "{" +
                                " name='" + getName() + "'" +
                                ", abbrev='" + getAbbrev() + "'" +
                                ", lat='" + getLat() + "'" +
                                ", lon='" + getLon() + "'" +
                                "}";
        }

}
