package chmura;

import javafx.util.Pair;

import java.util.*;
import java.util.function.BiPredicate;

/**
 * Chmura bytów wymiaru n przyporządkowuje bytom miejsca, jednoznacznie identyfikowane przez ciąg n współrzędnych całkowitych. Zachowuje przy tym niezmiennik chmury: każdy byt jest w innym miejscu.
 * Chmura bytów może służyć do synchronizacji procesów współbieżnych.
 */
public class Chmura {
    private class BytChmury implements Comparable {
        private Byt byt;
        private int x;
        private int y;

        public BytChmury(Byt byt, int x, int y) {
            this.byt = byt;
            this.x = x;
            this.y = y;
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
        public int compareTo(Object o) {
            return 0;
        }
    }

    /**
     * Kolekcja w której przechowywane są wszystkie byty.
     */
    private List<BytChmury> byty = Collections.synchronizedList(new ArrayList<BytChmury>());

    /**
     * W przypadku tworzenia chmury za pomocą domyślnego konstruktora stan nie jest używany.
     */
    private BiPredicate<Integer, Integer> stan;

    /**
     * Mapa pamiętająca które z elementów stanu zostały przeniesione (aby nie można było się do nich dostać 2 raz)
     */
    private Map<Pair<Integer, Integer>, Boolean> zainicjalizowany = new HashMap<>();

    private void oznaczZainicjalizowany(int x, int y) {
        zainicjalizowany.put(new Pair<>(x, y), true);
    }

    private boolean jestNiezainicjalizowany(int x, int y) {
        return stan.test(x, y) && !(zainicjalizowany.get(new Pair<>(x, y)));
    }

    private boolean miejsceJestWolne(int x, int y) {
        if(jestNiezainicjalizowany(x, y)) {
            byty.add(new BytChmury(new Byt(), x, y));
            oznaczZainicjalizowany(x, y);
        }
        return !byty.contains(new BytChmury(new Byt(), x, y));
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
        // TODO
        return null;
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
        // TODO
    }

    /**
     * Daje dwuelementową tablicę ze współrzędnymi x i y bytu, lub null, jeśli byt nie jest w chmurze.
     */
    public int[] miejsce(Byt byt) {
        // TODO
        return new int[0];
    }
}
