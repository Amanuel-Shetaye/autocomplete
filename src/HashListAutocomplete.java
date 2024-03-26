import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashListAutocomplete implements Autocompletor {

    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize;
    private static final int BYTES_PER_CHAR = 2;
    private static final int BYTES_PER_DOUBLE = 8;


    public HashListAutocomplete(String[] terms, double[] weights) {
        if (terms == null || weights == null) {
			throw new NullPointerException("One or more arguments null");
		}

		if (terms.length != weights.length) {
			throw new IllegalArgumentException("terms and weights are not the same length");
		}
        myMap = new HashMap<>();
        mySize = 0;
		initialize(terms,weights);
    }

    @Override
    public List<Term> topMatches(String prefix, int k) {
        if (prefix.length() > MAX_PREFIX) {
            prefix = prefix.substring(0, MAX_PREFIX);
        }
        List<Term> list = new ArrayList<>();
        if (myMap.containsKey(prefix)) {
            List<Term> all = myMap.get(prefix);
            list = all.subList(0, Math.min(k, all.size()));
        }
        return list;
    }

    @Override
    public void initialize(String[] terms, double[] weights) {
        for(int k = 0; k < terms.length; k++){
            String t = terms[k];
            double w = weights[k];
            Term term = new Term(t, w);
            for(int i = 0; i < MAX_PREFIX && i < t.length(); i++){
                String substring = t.substring(0, i);
                if (!myMap.containsKey(substring)) {
                    mySize += BYTES_PER_CHAR * substring.length();
                    myMap.put(substring, new ArrayList<Term>());
                }
                myMap.get(substring).add(term);
                mySize += BYTES_PER_CHAR * t.length() + BYTES_PER_DOUBLE;
            }
            if (t.length() > MAX_PREFIX) {
                myMap.putIfAbsent(t.substring(0, 10), new ArrayList<>());
                myMap.get(t.substring(0,10)).add(term);
                mySize += 10 * BYTES_PER_CHAR;
            }
            myMap.putIfAbsent(t, new ArrayList<>());
            myMap.get(t).add(term);
            mySize += (2*BYTES_PER_CHAR * t.length()) + BYTES_PER_DOUBLE;
        }
        for(String key:myMap.keySet()){
            Collections.sort(myMap.get(key), Comparator.comparing(Term::getWeight).reversed());
        }
    }
    

    @Override
    public int sizeInBytes() {
        return mySize;
    }  
}

