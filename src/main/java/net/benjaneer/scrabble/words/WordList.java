package net.benjaneer.scrabble.words;

import net.benjaneer.scrabble.characters.CharacterSet;
import net.benjaneer.scrabble.characters.SingleCaseNoWhitespaceCharacterSet;
import net.benjaneer.scrabble.util.WrappedImmutableSet;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordList extends WrappedImmutableSet<Word> {

    private static final CharacterSet DEFAULT_CHARSET = SingleCaseNoWhitespaceCharacterSet.ENGLISH_CHARSET;
    private static WordList scrabble2019List = null;

    private final CharacterSet characterSet;
    private final Map<Word, Set<Word>> prefixLookupMap = new ConcurrentHashMap<>();
    private final Set<Word>[] firstcharLookupMap;

    private WordList(CharacterSet characterSet, Stream<String> words) {
        super(words.map(characterSet::transform).filter(Objects::nonNull).map(x -> new Word(x.toCharArray())).collect(Collectors.toSet()));
        this.characterSet = characterSet;
        int max = characterSet.stream().map(x -> (int) x).max(Integer::compareTo).orElse(0);
        firstcharLookupMap = new Set[max + 1];
        for(char c : characterSet) {
            firstcharLookupMap[c] = new HashSet<>();
        }
        for(Word word : this) {
            firstcharLookupMap[word.chars()[0]].add(word);
        }
    }

    public static WordList fromArray(CharacterSet characterSet, String... words) {
        return new WordList(characterSet, Arrays.stream(words).parallel());
    }

    public static WordList fromCollection(CharacterSet characterSet, Collection<String> words) {
        return new WordList(characterSet, words.parallelStream());
    }

    public static WordList fromFile(CharacterSet characterSet, File file) {
        if(file == null) {
            throw new IllegalArgumentException("Null file");
        }
        if(!file.exists()) {
            throw new IllegalArgumentException(String.format("File %s does not exist", file.getAbsolutePath()));
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            return new WordList(characterSet, br.lines().parallel());
        } catch (IOException e) {
            throw new IllegalArgumentException("IO issue reading file", e);
        }
    }

    public static WordList fromArray(String... words) {
        return fromArray(DEFAULT_CHARSET, words);
    }

    public static WordList fromCollection(Collection<String> words) {
        return fromCollection(DEFAULT_CHARSET, words);
    }

    private WordList fromCollection(CharacterSet characterSet, Set<Word> words) {
        return new WordList(characterSet, words.stream().map(x -> new String(x.chars())));
    }

    public static WordList fromFile(File file) {
        return fromFile(DEFAULT_CHARSET, file);
    }

    public static WordList scrabble2019List() {
        if(scrabble2019List == null) {
            scrabble2019List = fromFile(new File(WordListConstants.COLLINS_SCRABBLE_2019_LIST_FILEPATH));
        }
        return scrabble2019List;
    }

    /**
     * Returns a thread safe map of word lists seperated by word length
     */
    public Map<Integer, WordList> lengthMap() {
        Map<Integer, Set<Word>> stringSetMap = new ConcurrentHashMap<>();

        parallelStream().forEach(word -> {
            Set<Word> wordSet;
            synchronized (stringSetMap) {
                wordSet = stringSetMap.computeIfAbsent(word.chars().length, k -> ConcurrentHashMap.newKeySet());
            }
            wordSet.add(word);
        });

        Map<Integer, WordList> returnMap = new HashMap<>();
        stringSetMap.keySet().parallelStream().forEach(key -> returnMap.put(key, fromCollection(characterSet, stringSetMap.get(key))));
        return returnMap;
    }

    /**
     * Returns a thread safe map of word lists seperated by thier first character
     */
    public Map<Character, WordList> firstCharacterMap() {
        Map<Character, Set<Word>> stringSetMap = new ConcurrentHashMap<>();

        parallelStream().forEach(word -> {
            Set<Word> wordSet;
            synchronized (stringSetMap) {
                wordSet = stringSetMap.computeIfAbsent(word.chars()[0], k -> ConcurrentHashMap.newKeySet());
            }
            wordSet.add(word);
        });

        Map<Character, WordList> returnMap = new HashMap<>();
        stringSetMap.keySet().parallelStream().forEach(key -> returnMap.put(key, fromCollection(characterSet, stringSetMap.get(key))));
        return returnMap;
    }

    public Set<Word> startsWith(Word key) {
        if(key.chars().length == 1) {
            return firstcharLookupMap[key.chars()[0]];
        }
        Set<Word> startsWith = prefixLookupMap.get(key);
        if(startsWith != null) {
            return startsWith;
        }
        synchronized (key) {
            startsWith = prefixLookupMap.get(key);
            if(startsWith != null) {
                return startsWith;
            }
            if(key.chars().length > 1) {
                startsWith = startsWith(reverseTail(key)).stream().filter(word -> doesStartsWith(key, word)).collect(Collectors.toSet());
            } else {
                startsWith = stream().filter(word -> doesStartsWith(key, word)).collect(Collectors.toSet());
            }
            prefixLookupMap.put(key, startsWith);
        }
        return startsWith;
    }

    public Set<Word> startsWith(char startsWith) {
        return firstcharLookupMap[startsWith];
    }

    private Word reverseTail(Word word) {
        char[] reverseTail = new char[word.chars().length - 1];
        System.arraycopy(word.chars(), 0, reverseTail, 0, reverseTail.length);
        return new Word(reverseTail);
    }

    private boolean doesStartsWith(Word key, Word word) {
        for(int i = 0; i < key.chars().length; i++) {
            if(key.chars()[i] != word.chars()[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return super.contains(o);
    }
}
