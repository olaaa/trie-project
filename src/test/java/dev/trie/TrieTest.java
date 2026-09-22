package dev.trie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Trie")
class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
    }

    // ------------------------------------------------------------------ insert / size
    @Nested
    @DisplayName("insert и size")
    class InsertAndSize {

        @Test
        @DisplayName("пустое дерево имеет size = 0")
        void emptyTrieHasSizeZero() {
            assertThat(trie.size()).isZero();
        }

        @Test
        @DisplayName("size увеличивается после каждого insert")
        void sizeIncreasesAfterInsert() {
            trie.insert("кот");
            assertThat(trie.size()).isEqualTo(1);

            trie.insert("ком");
            assertThat(trie.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("дубликат увеличивает size")
        void duplicateIncrementsSize() {
            trie.insert("кот");
            trie.insert("кот");
            assertThat(trie.size()).isEqualTo(2);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null и пустая строка игнорируются")
        void nullAndEmptyAreIgnored(String word) {
            trie.insert(word);
            assertThat(trie.size()).isZero();
        }
    }

    // ------------------------------------------------------------------ search
    @Nested
    @DisplayName("search")
    class Search {

        @BeforeEach
        void insertWords() {
            List.of("кот", "кора", "ком", "код", "дом").forEach(trie::insert);
        }

        @Test
        @DisplayName("возвращает true для вставленного слова")
        void returnsTrueForInsertedWord() {
            assertThat(trie.search("кот")).isTrue();
            assertThat(trie.search("дом")).isTrue();
        }

        @Test
        @DisplayName("возвращает false для слова которого нет")
        void returnsFalseForAbsentWord() {
            assertThat(trie.search("кофе")).isFalse();
        }

        @Test
        @DisplayName("возвращает false для префикса который не является словом")
        void returnsFalseForPrefixThatIsNotWord() {
            assertThat(trie.search("ко")).isFalse();
        }

        @Test
        @DisplayName("поиск чувствителен к регистру")
        void searchIsCaseSensitive() {
            assertThat(trie.search("КОТ")).isFalse();
            assertThat(trie.search("Кот")).isFalse();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null и пустая строка возвращают false")
        void nullAndEmptyReturnFalse(String word) {
            assertThat(trie.search(word)).isFalse();
        }
    }

    // ------------------------------------------------------------------ startsWith
    @Nested
    @DisplayName("startsWith")
    class StartsWith {

        @BeforeEach
        void insertWords() {
            List.of("кот", "кора", "ком", "дом").forEach(trie::insert);
        }

        @Test
        @DisplayName("возвращает true для существующего префикса")
        void returnsTrueForExistingPrefix() {
            assertThat(trie.startsWith("к")).isTrue();
            assertThat(trie.startsWith("ко")).isTrue();
            assertThat(trie.startsWith("кор")).isTrue();
        }

        @Test
        @DisplayName("возвращает true если префикс == целое слово")
        void returnsTrueWhenPrefixEqualsWord() {
            assertThat(trie.startsWith("кот")).isTrue();
        }

        @Test
        @DisplayName("возвращает false для несуществующего префикса")
        void returnsFalseForAbsentPrefix() {
            assertThat(trie.startsWith("за")).isFalse();
            assertThat(trie.startsWith("кофе")).isFalse();
        }
    }

    // ------------------------------------------------------------------ wordsWithPrefix
    @Nested
    @DisplayName("wordsWithPrefix")
    class WordsWithPrefix {

        @BeforeEach
        void insertWords() {
            List.of("кот", "кора", "ком", "код", "дом", "кофе").forEach(trie::insert);
        }

        @Test
        @DisplayName("возвращает все слова с данным префиксом")
        void returnsAllWordsWithPrefix() {
            assertThat(trie.wordsWithPrefix("ко"))
                    .containsExactlyInAnyOrder("кот", "кора", "ком", "код", "кофе");
        }

        @Test
        @DisplayName("возвращает одно слово если префикс уникален")
        void returnsOneWordForUniquePrefix() {
            assertThat(trie.wordsWithPrefix("до"))
                    .containsExactly("дом");
        }

        @Test
        @DisplayName("пустой префикс возвращает все слова")
        void emptyPrefixReturnsAllWords() {
            assertThat(trie.wordsWithPrefix(""))
                    .containsExactlyInAnyOrder("кот", "кора", "ком", "код", "дом", "кофе");
        }

        @Test
        @DisplayName("возвращает пустой список если префикса нет")
        void returnsEmptyListForAbsentPrefix() {
            assertThat(trie.wordsWithPrefix("за")).isEmpty();
        }

        @Test
        @DisplayName("дубликат слова присутствует в результате дважды")
        void duplicateWordAppearsMultipleTimes() {
            trie.insert("кот");
            assertThat(trie.wordsWithPrefix("кот"))
                    .hasSize(2)
                    .containsOnly("кот");
        }
    }

    // ------------------------------------------------------------------ delete
    @Nested
    @DisplayName("delete")
    class Delete {

        @BeforeEach
        void insertWords() {
            List.of("кот", "кора", "ком", "код").forEach(trie::insert);
        }

        @Test
        @DisplayName("удалённое слово больше не находится через search")
        void deletedWordNotFoundBySearch() {
            trie.delete("кот");
            assertThat(trie.search("кот")).isFalse();
        }

        @Test
        @DisplayName("delete возвращает true если слово было")
        void deletingExistingWordReturnsTrue() {
            assertThat(trie.delete("кот")).isTrue();
        }

        @Test
        @DisplayName("delete возвращает false если слова не было")
        void deletingAbsentWordReturnsFalse() {
            assertThat(trie.delete("кофе")).isFalse();
        }

        @Test
        @DisplayName("delete уменьшает size")
        void deleteDecreasesSize() {
            int before = trie.size();
            trie.delete("кот");
            assertThat(trie.size()).isEqualTo(before - 1);
        }

        @Test
        @DisplayName("удаление слова не ломает другие слова с тем же префиксом")
        void deletingWordDoesNotBreakWordsWithSamePrefix() {
            trie.delete("ком");
            assertThat(trie.search("код")).isTrue();
            assertThat(trie.search("кора")).isTrue();
        }

        @Test
        @DisplayName("удаление одного дубликата оставляет второй")
        void deletingOneDuplicateLeavesAnother() {
            trie.insert("кот");
            trie.delete("кот");
            assertThat(trie.search("кот")).isTrue();
            assertThat(trie.size()).isEqualTo(4);
        }

        @Test
        @DisplayName("удаление последнего вхождения убирает слово и его узлы")
        void deletingLastOccurrenceRemovesNodeFromTree() {
            trie.delete("кот");
            assertThat(trie.search("кот")).isFalse();
            assertThat(trie.startsWith("кот")).isFalse();
        }
    }

    // ------------------------------------------------------------------ интеграционные
    @Nested
    @DisplayName("интеграционные сценарии")
    class Integration {

        @Test
        @DisplayName("латиница и кириллица сосуществуют в одном дереве")
        void latinAndCyrillicCoexist() {
            trie.insert("cat");
            trie.insert("кот");
            assertThat(trie.search("cat")).isTrue();
            assertThat(trie.search("кот")).isTrue();
            assertThat(trie.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("однобуквенное слово работает корректно")
        void singleCharWordWorks() {
            trie.insert("а");
            assertThat(trie.search("а")).isTrue();
            trie.delete("а");
            assertThat(trie.search("а")).isFalse();
        }

        @Test
        @DisplayName("слово которое является префиксом другого — независимо при удалении")
        void wordThatIsPrefixOfAnotherIsIndependentOnDelete() {
            trie.insert("код");
            trie.insert("кодекс");
            trie.delete("код");
            assertThat(trie.search("код")).isFalse();
            assertThat(trie.search("кодекс")).isTrue();
        }

        @Test
        @DisplayName("insert после delete восстанавливает слово")
        void insertAfterDeleteRestoresWord() {
            trie.insert("кот");
            trie.delete("кот");
            trie.insert("кот");
            assertThat(trie.search("кот")).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"банк", "банкомат", "банкир", "банкрот"})
        @DisplayName("все слова с префиксом 'банк' находятся через search")
        void bankPrefixWords(String word) {
            List.of("банк", "банкомат", "банкир", "банкрот").forEach(trie::insert);
            assertThat(trie.search(word)).isTrue();
        }
    }
}
