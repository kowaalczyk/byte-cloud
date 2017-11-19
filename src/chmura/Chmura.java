package chmura;

import javafx.util.Pair;

import java.util.*;
import java.util.function.BiPredicate;

// UWAGA: Byty nie moga byc wyciagane po wspolrzednych wiec jedyne co robi predykat to ogranicza przedział
// TODO: Zaimplementować wszystko od nowa wykorzystując powyższy wniosek
// TODO: ConcurrentCollections pozwolą łatwo zaimplementować operacje czytania dostępne bez oczekiwania
// TODO: Metody rzucające InterruptedException mają być współbieżne (synchronizacja sekcji krytycznej)

/**
 * Chmura bytów wymiaru n przyporządkowuje bytom miejsca, jednoznacznie identyfikowane przez ciąg n współrzędnych całkowitych. Zachowuje przy tym niezmiennik chmury: każdy byt jest w innym miejscu.
 * Chmura bytów może służyć do synchronizacji procesów współbieżnych.
 */
public class Chmura {
    private class BytChmury implements Comparable<BytChmury> {
        private Byt byt;
        private int x;
        private int y;

        BytChmury(Byt byt, int x, int y) {
            this.byt = byt;
            this.x = x;
            this.y = y;
        }

        public boolean bytEquals(Byt that) {
            return this.byt == that;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BytChmury bytChmury = (BytChmury) o;

            if (x != bytChmury.x) return false;
            if (y != bytChmury.y) return false;
            return byt != null ? byt.equals(bytChmury.byt) : bytChmury.byt == null;
        }

        @Override
        public int hashCode() {
            int result = byt != null ? byt.hashCode() : 0;
            result = 31 * result + x;
            result = 31 * result + y;
            return result;
        }

        @Override
        public int compareTo(BytChmury that) {
            if(this.x < that.x) {
                return -1;
            } else if(this.x > that.x) {
                return 1;
            } else {
                return Integer.compare(this.y, that.y);
            }
        }
    }

    /**
     * Kolekcja w której przechowywane są wszystkie byty.
     */
    private Set<BytChmury> byty = Collections.synchronizedSet(new TreeSet<BytChmury>());
    private BiPredicate<Integer, Integer> stan;
    private Set<Pair<Integer, Integer>> zainicjalizowany = new HashSet<>();

    private void oznaczZainicjalizowany(int x, int y) {
        zainicjalizowany.add(new Pair<>(x, y));
    }

    private boolean jestNiezainicjalizowany(int x, int y) {
        return stan.test(x, y) && !(zainicjalizowany.contains(new Pair<>(x, y)));
    }

    private boolean miejsceJestWolne(int x, int y) {
        if(jestNiezainicjalizowany(x, y)) {
            byty.add(new BytChmury(new Byt(), x, y));
            oznaczZainicjalizowany(x, y);
        }
        return !byty.contains(new BytChmury(new Byt(), x, y));
    }

    private BytChmury znajdzByt(Byt szukanyByt) {
        for(BytChmury b : byty) {
            if(b.bytEquals(szukanyByt)) {
                return b;
            }
        }
        return null;
    }

    // TODO: Find a way to reduce complexity below O(n^2)
    private Collection<BytChmury> znajdzByty(Collection<Byt> szukaneByty) {
        Collection<BytChmury> znalezione = new ArrayList<>();
        for(BytChmury b : byty) {
            for(Byt byt : szukaneByty) {
                if(b.bytEquals(byt)) {
                    znalezione.add(b);
                }
            }
        }
        return znalezione;
    }

    /**
     * Buduje chmurę, która w stanie początkowym nie ma żadnego bytu.
     */
    public Chmura() {
        this.stan = (x, y) -> false;
    }

    /**
     * Buduje chmurę, której początkową zawartość określa dwuargumentowy predykat stan.
     * W miejscu (x, y) jest byt wtedy i tylko wtedy, gdy stan.test(x, y) ma wartość true.
     */
    public Chmura(BiPredicate<Integer, Integer> stan) {
        this.stan = stan;
    }

    /**
     * Daje jako wynik nowy byt, dodany do chmury w miejscu (x, y).
     */
    public Byt ustaw(int x, int y) throws InterruptedException {
        Byt nowy = new Byt();
        byty.add(new BytChmury(nowy, x, y));
        return nowy;
    }

    /**
     * Przemieszcza na raz wszystkie byty kolekcji byty o wektor (dx, dy). Byt z miejsca (x, y) trafia na miejsce (x + dx, y + dy).
     * Jeśli którykolwiek z bytów kolekcji byty nie jest w chmurze, metoda zgłasza wyjątek NiebytException.
     * Jeżeli wymaga tego niezmiennik chmury, metody ustaw() i przestaw() wstrzymują wątek do czasu, gdy ich wykonanie będzie możliwe. W przypadku przerwania zgłaszają wyjątek InterruptedException.
     */
    public void przestaw(Collection<Byt> byty, int dx, int dy) throws NiebytException, InterruptedException {
        // TODO
    }

    /**
     * Usuwa byt z chmury.
     * Jeśli byt nie jest w chmurze, metoda zgłasza wyjątek NiebytException.
     */
    public void kasuj(Byt byt) throws NiebytException {
        BytChmury znaleziony = znajdzByt(byt);
        if(znaleziony == null) {
            throw new NiebytException();
        }
        byty.remove(znaleziony);
    }

    /**
     * Daje dwuelementową tablicę ze współrzędnymi x i y bytu, lub null, jeśli byt nie jest w chmurze.
     */
    public int[] miejsce(Byt byt) {
        BytChmury znaleziony = znajdzByt(byt);
        if(znaleziony == null) {
            return null;
        }
        return new int[] {znaleziony.x, znaleziony.y};
    }
}
