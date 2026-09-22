package dev.trie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Префиксное дерево (Trie).
 *
 * <p>Операции:
 * <ul>
 *   <li>{@link #insert(String)}          — вставка слова,                  O(L)</li>
 *   <li>{@link #search(String)}          — точный поиск слова,              O(L)</li>
 *   <li>{@link #startsWith(String)}      — проверка наличия префикса,       O(L)</li>
 *   <li>{@link #wordsWithPrefix(String)} — все слова с данным префиксом,    O(P + W)</li>
 *   <li>{@link #delete(String)}          — удаление одного вхождения слова, O(L)</li>
 *   <li>{@link #size()}                  — количество слов (с дубликатами)</li>
 * </ul>
 *
 * <p>L — длина слова/префикса, P — число узлов поддерева, W — число найденных слов.
 */
public class Trie {

    // ------------------------------------------------------------------ узел

    private static final class Node {

        // HashMap вместо char[26] — поддерживает любой алфавит (кириллица, unicode)
        final Map<Character, Node> children = new HashMap<>();

        // Сколько слов проходит через узел — нужно для корректного удаления
        int passCount = 0;

        // Сколько слов заканчивается именно здесь (> 1 если слово вставлено несколько раз)
        int endCount = 0;
    }

    // ------------------------------------------------------------------ поля

    private final Node root = new Node();
    private int size = 0;

    // ------------------------------------------------------------------ insert

    /**
     * Вставляет слово в дерево.
     * Дубликаты разрешены — {@code endCount} считает количество вставок одного слова.
     *
     * @param word слово для вставки; null и пустая строка игнорируются
     */
    public void insert(String word) {
        if (word == null || word.isEmpty()) return;

        Node current = root;
        for (char ch : word.toCharArray()) {
            current.children.putIfAbsent(ch, new Node());
            current = current.children.get(ch);
            current.passCount++;
        }
        current.endCount++;
        size++;
    }

    // ------------------------------------------------------------------ search

    /**
     * Возвращает {@code true} если слово было вставлено.
     */
    public boolean search(String word) {
        Node node = findNode(word);
        return node != null && node.endCount > 0;
    }

    // ------------------------------------------------------------------ startsWith

    /**
     * Возвращает {@code true} если хотя бы одно слово начинается с данного префикса.
     */
    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    // ------------------------------------------------------------------ wordsWithPrefix

    /**
     * Возвращает все слова, начинающиеся с данного префикса.
     * Порядок результатов не гарантирован.
     *
     * @param prefix префикс для поиска
     * @return список слов; пустой список если таких слов нет
     */
    public List<String> wordsWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        Node prefixEnd = findNode(prefix);
        if (prefixEnd == null) return result;

        collectWords(prefixEnd, new StringBuilder(prefix), result);
        return result;
    }

    // ------------------------------------------------------------------ delete

    /**
     * Удаляет одно вхождение слова.
     * Узлы без проходящих слов удаляются для освобождения памяти.
     *
     * @param word слово для удаления
     * @return {@code true} если слово было найдено и удалено
     */
    public boolean delete(String word) {
        if (!search(word)) return false;
        deleteHelper(root, word, 0);
        size--;
        return true;
    }

    // ------------------------------------------------------------------ size

    /** Возвращает количество слов в дереве (с учётом дубликатов). */
    public int size() {
        return size;
    }

    // ------------------------------------------------------------------ private

    private Node findNode(String s) {
        if (s == null) return null;
        Node current = root;
        for (char ch : s.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) return null;
        }
        return current;
    }

    private void collectWords(Node node, StringBuilder prefix, List<String> result) {
        if (node.endCount > 0) {
            String word = prefix.toString();
            for (int i = 0; i < node.endCount; i++) {
                result.add(word);
            }
        }
        for (var entry : node.children.entrySet()) {
            prefix.append(entry.getKey());
            collectWords(entry.getValue(), prefix, result);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    private void deleteHelper(Node node, String word, int depth) {
        if (depth == word.length()) {
            node.endCount--;
            return;
        }
        char ch = word.charAt(depth);
        Node child = node.children.get(ch);
        child.passCount--;
        deleteHelper(child, word, depth + 1);

        if (child.passCount == 0) {
            node.children.remove(ch);
        }
    }
}
