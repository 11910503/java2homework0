import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieAnalyzer {
    String location;
    public static ArrayList<Movid> allmovid = new ArrayList<>();

    public static class Movid {
        public String Poster_Link;
        public String Series_Title;
        public int Released_Year;
        public String Certificate;
        public String Runtime;
        public String[] Genre;
        public String allGenre;
        public String IMDB_Rating;
        public String Overview;
        public String Meta_score;
        public String Director;
        public String Star1;
        public String Star2;
        public String Star3;
        public String Star4;
        public String No_of_Votes;
        public String Gross;
        public static int numbercount = 0;

        public Movid(
                String[] a
                //String Gross
        ) {
            this.Poster_Link = a[0];
            this.Series_Title = a[1].replaceAll("\"", "");
            this.Released_Year = Integer.parseInt(a[2]);
            this.Certificate = a[3];
            this.Runtime = a[4];
            this.Genre = a[5].replaceAll("\"", "").replaceAll(" ", "").split(",");
            this.allGenre = a[5].replaceAll("\"", "").replaceAll(" ", "");
            this.IMDB_Rating = a[6];
            this.Overview = a[7];
//            if(a[7].substring(a[7].length()-1).equals("\"")){
//                this.Overview = a[7].substring(0,a[7].length()-1);
//            }else {
//                this.Overview = a[7];
//            }
            this.Meta_score = a[8];
            this.Director = a[9];
            this.Star1 = a[10];
            this.Star2 = a[11];
            this.Star3 = a[12];
            this.Star4 = a[13];
            this.No_of_Votes = a[14];
            this.Gross = a[15];
            //counting();
        }

        public static void counting() {
            numbercount++;
            if (numbercount % 10 == 0) {
                System.out.println("ready" + numbercount + "movie");
            }
        }

        public Integer getReleased_Year() {
            return Released_Year;
        }

        public String[] getGenre() {
            return Genre;
        }

        public String getAllGenre() {
            return allGenre;
        }
    }

    public MovieAnalyzer(String dataset_path) throws IOException {
        location = dataset_path;
        Files.lines(Paths.get(location))
                .skip(1)
                .map(l -> getline(l))
                .map(Movid::new).forEach(a -> {
                    allmovid.add(a);
                });
    }

    public Map<Integer, Integer> getMovieCountByYear() throws IOException {
        Stream<Movid> movidStream = readCities(location);

        Map<Integer, Long> map = movidStream.collect(Collectors.groupingBy(Movid::getReleased_Year, Collectors.counting()));
        Set<Integer> set = map.keySet();
        Map<Integer, Integer> ans = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
        set.forEach(a -> {
            Long an = map.get(a);
            Integer ani = an.intValue();
            ans.put(a, ani);
        });

        //System.out.println(map);
        //System.out.println(ans);
        return ans;
    }

    public Map<String, Integer> getMovieCountByGenre() throws IOException {
        Stream<Movid> movidStream = readCities(location);
        Map<String, Integer> stringIntegerMap = new LinkedHashMap<>();
        ArrayList<String> arrayList1 = new ArrayList<>();
        movidStream.forEach(a -> {
            String[] s = a.getGenre();
            for (int j = 0; j < s.length; j++) {
                if (stringIntegerMap.containsKey(s[j])) {
                    stringIntegerMap.replace(s[j], stringIntegerMap.get(s[j]), stringIntegerMap.get(s[j]) + 1);
                } else {
                    stringIntegerMap.put(s[j], 1);
                }
            }

        });
        stringIntegerMap.forEach((k, v) -> {
            arrayList1.add(k + ";" + v);
        });
        arrayList1.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int o1n = Integer.parseInt(o1.split(";")[1]);
                int o2n = Integer.parseInt(o2.split(";")[1]);
                if (o1n == o2n) {
                    return o1.compareTo(o2);
                }
                return o2n - o1n;
            }
        });
        Map<String, Integer> ans = new LinkedHashMap<>();
        for (int i = 0; i < arrayList1.size(); i++) {
            String[] sa = arrayList1.get(i).split(";");
            ans.put(sa[0], Integer.parseInt(sa[1]));
        }
        return ans;
    }

    public Map<List<String>, Integer> getCoStarCount() throws IOException {
        Stream<Movid> movidStream = readCities(location);
        Map<String, Integer> map = new HashMap<String, Integer>();
        movidStream.forEach(a -> {
            add(map, a.Star1, a.Star2);
            add(map, a.Star1, a.Star3);
            add(map, a.Star1, a.Star4);
            add(map, a.Star2, a.Star3);
            add(map, a.Star2, a.Star4);
            add(map, a.Star3, a.Star4);
        });
        ArrayList<String> strings = new ArrayList<String>();
        map.forEach((k, v) -> {
            strings.add(k + ";" + v);
        });
        strings.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i1 = Integer.parseInt(o1.split(";")[1]);
                int i2 = Integer.parseInt(o2.split(";")[1]);
                if (i1 == i2) {
                    String[] s1 = o1.split(",");
                    String[] s2 = o2.split(",");
                    //if(s1[0].compareTo(s2[0])==0){return s1[1].compareTo(s2[1]);}
                    return 0;
                } else {
                    return i2 - i1;
                }
            }
        });
        Map<List<String>, Integer> ans = new LinkedHashMap<>();
        strings.forEach(a -> {
            List<String> list = new ArrayList<>();
            String[] ss = a.split(";");
            list.add(ss[0].split(",")[0]);
            list.add(ss[0].split(",")[1]);
            ans.put(list, Integer.parseInt(ss[1]));
        });
        return ans;
    }

    public static void add(Map<String, Integer> map, String s1, String s2) {
        if (s1.length() == 0 || s2.length() == 0) {
            return;
        }
        String s;
        if (s2.compareTo(s1) >= 0) {
            s = s1 + "," + s2;
        } else {
            s = s2 + "," + s1;
        }
        if (map.containsKey(s)) {
            int data = map.get(s);
            map.replace(s, data, data + 1);
        } else {

            map.put(s, 1);
        }

    }

    public static Stream<Movid> readCities(String filename) throws IOException {
        return allmovid.stream();
    }

    public static String[] getline(String line) {
        //String[] ans=line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        String[] s = new String[16];
        char[] characters = line.toCharArray();
        int j = -1;
        int k = 0;
        int index = 0;
        boolean is = true;
        int num = 0;
        for (int i = 0; i < characters.length; i++) {
            if (characters[i] == '\"') {
                k = i;
                num++;
                is = true;
                continue;
            }
            if (characters[i] == ',') {
                if (num % 2 == 0 || !is) {
                    s[index] = line.substring(j + 1, i);
                    index++;
                    is = false;
                    j = i;
                    num = 0;
                }

            }
        }
        s[15] = line.substring(j + 1);
        return s;
    }

    public List<String> getTopMovies(int top_k, String by) throws IOException {
        Stream<Movid> movidStream = readCities(location);
        List<String> ans = new ArrayList<>();
        if (by.equals("runtime")) {
            movidStream.sorted(new Comparator<Movid>() {
                @Override
                public int compare(Movid o1, Movid o2) {
                    int o1n = Integer.parseInt(o1.Runtime.split(" ")[0]);
                    int o2n = Integer.parseInt(o2.Runtime.split(" ")[0]);
                    if (o1n == o2n) {
                        return o1.Series_Title.compareTo(o2.Series_Title);
                    }
                    return o2n - o1n;
                }
            }).limit(top_k).forEach(a -> {
                ans.add(a.Series_Title);
            });
            return ans;
        }
        if (by.equals("overview")) {
            movidStream.sorted(new Comparator<Movid>() {
                @Override
                public int compare(Movid o1, Movid o2) {
                    int o1n = o1.Overview.length();
                    int o2n = o2.Overview.length();
                    if (o1.Overview.substring(o1n - 1).equals("\"")) {
                        o1n -= 2;
                    }
                    if (o2.Overview.substring(o2n - 1).equals("\"")) {
                        o2n -= 2;
                    }
                    if (o1n == o2n) {
                        return o1.Series_Title.compareTo(o2.Series_Title);
                    }
                    return o2n - o1n;
                }
            }).limit(top_k).forEach(a -> {
                ans.add(a.Series_Title);
                //System.out.println(a.Overview.length()+" "+a.Series_Title+" "+a.Overview);
            });
            return ans;
        }
        return null;
    }

    public List<String> getTopStars(int top_k, String by) throws IOException {
        Stream<Movid> movidStream = readCities(location);
        List<String> ans = new ArrayList<String>();
        Map<String, List<String>> listMap = new HashMap<>();
        movidStream.forEach(a -> {
            if (!listMap.containsKey(a.Star1)) {
                listMap.put(a.Star1, new ArrayList<String>());
            }
            if (!listMap.containsKey(a.Star2)) {
                listMap.put(a.Star2, new ArrayList<String>());
            }
            if (!listMap.containsKey(a.Star3)) {
                listMap.put(a.Star3, new ArrayList<String>());
            }
            if (!listMap.containsKey(a.Star4)) {
                listMap.put(a.Star4, new ArrayList<String>());
            }
        });
        if (by.equals("rating")) {
            movidStream = readCities(location);
            movidStream.forEach(a -> {
                listMap.get(a.Star1).add(a.IMDB_Rating);
                listMap.get(a.Star2).add(a.IMDB_Rating);
                listMap.get(a.Star3).add(a.IMDB_Rating);
                listMap.get(a.Star4).add(a.IMDB_Rating);
            });
            ArrayList<String> arrayList = new ArrayList<>();
            listMap.forEach((k, v) -> {
                Double all = 0.0;
                for (int i = 0; i < v.size(); i++) {
                    all += (double) Float.parseFloat(v.get(i));
                }
                all /= v.size();
                String s = k + ";" + all;
                arrayList.add(s);
            });
            arrayList.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    Double o1n = Double.parseDouble(o1.split(";")[1]);
                    Double o2n = Double.parseDouble(o2.split(";")[1]);
                    if (o1n.equals(o2n)) {
                        return o1.compareTo(o2);
                    }
                    return o2n.compareTo(o1n);
                }
            });
            for (int i = 0; i < top_k; i++) {
                ans.add(arrayList.get(i).split(";")[0]);
                System.out.println(arrayList.get(i));
            }
        }
        if (by.equals("gross")) {
            movidStream = readCities(location);
            movidStream.forEach(a -> {
                if (!a.Gross.equals("")) {
                    listMap.get(a.Star1).add(a.Gross);
                    listMap.get(a.Star2).add(a.Gross);
                    listMap.get(a.Star3).add(a.Gross);
                    listMap.get(a.Star4).add(a.Gross);
                }

            });
            ArrayList<String> arrayList = new ArrayList<>();
            listMap.forEach((k, v) -> {
                Long all = 0L;
                for (int i = 0; i < v.size(); i++) {
                    all += Long.parseLong(v.get(i).replaceAll(",", "").replaceAll("\"", ""));
                }
                if (all != 0) {
                    all /= v.size();
                }

                String s = k + ";" + all;
                arrayList.add(s);
            });
            arrayList.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int o1n = Integer.parseInt(o1.split(";")[1]);
                    int o2n = Integer.parseInt(o2.split(";")[1]);
                    if (o1n == o2n) {
                        return o1.compareTo(o2);
                    }
                    return o2n - o1n;
                }
            });
            for (int i = 0; i < top_k; i++) {
                ans.add(arrayList.get(i).split(";")[0]);
            }

        }
        return ans;
    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime) throws IOException {
        Stream<Movid> movidStream = readCities(location);
        List<String> ans = new ArrayList<>();
        movidStream.filter(new Predicate<Movid>() {
                    @Override
                    public boolean test(Movid movid) {
                        for (int i = 0; i < movid.getGenre().length; i++) {
                            if (movid.getGenre()[i].equals(genre)) {
                                return true;
                            }
                        }
                        return false;
                    }
                })
                .filter(a -> Float.parseFloat(a.IMDB_Rating) >= min_rating)
                .filter(a -> Integer.parseInt(a.Runtime.split(" ")[0]) < max_runtime).forEach(
                        a -> {
                            ans.add(a.Series_Title);
                        }
                );
        ans.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return ans;
    }

    public static void main(String args[]) throws IOException {
        MovieAnalyzer movieAnalyzer = new MovieAnalyzer("E:\\java2\\A1_Sample\\A1_Sample\\resources\\imdb_top_500.csv");
        Map<String, Integer> ansmap = new LinkedHashMap<>();
        Files.lines(Paths.get("E:\\java2\\A1_Sample\\A1_Sample\\resources\\answers_local\\Q3.txt"))
                .forEach(a -> {
                    String[] strings = a.split(" == ");
                    strings[0] = strings[0].replaceAll("\\[", "").replaceAll("\\]", "");
                    ansmap.put(strings[0], Integer.parseInt(strings[1]));
                });
        Map<List<String>, Integer> ans = movieAnalyzer.getCoStarCount();
        ans.forEach((k, v) -> {
            String s = k.get(0) + ", " + k.get(1);
            if (!ansmap.containsKey(s)) {
                System.out.println(s);
                s = k.get(1) + ", " + k.get(0);
                if (!ansmap.containsKey(s)) {
                    System.out.println("aaaaaaaaa");
                }
                return;
            }
            int i = ansmap.get(s);
            if (i != v) {
                System.out.println(k + " " + v + "true: " + i);
            }
        });
        Movid m = allmovid.get(152);
        System.out.println(m.Star1);
    }

}
