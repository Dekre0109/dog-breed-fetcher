package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) {
        String key = breed.toLowerCase();

        // Cache hit: return a defensive copy
        if (cache.containsKey(key)) {
            return new ArrayList<>(cache.get(key));
        }

        // Cache miss: call underlying fetcher and count it (even if it fails)
        callsMade++;
        List<String> subs = fetcher.getSubBreeds(key);  // may throw BreedNotFoundException

        // Cache only successful result and return a defensive copy
        List<String> copy = new ArrayList<>(subs);
        cache.put(key, copy);
        return new ArrayList<>(copy);
    }

    public int getCallsMade() {
        return callsMade;
    }
}