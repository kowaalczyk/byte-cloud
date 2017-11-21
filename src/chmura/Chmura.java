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
    private Set<Byt> byty = Collections.synchronizedSet(new TreeSet<Byt>());
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
            byty.add(new Byt(x, y));
            oznaczZainicjalizowany(x, y);
        }
        return !byty.contains(new Byt(x, y));
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
    public synchronized Byt ustaw(int x, int y) throws InterruptedException {
        Byt nowy = new Byt(x, y);
        // TODO: Wait until the place is free
        byty.add(nowy);
        return nowy;
    }

    /**
     * Przemieszcza na raz wszystkie byty kolekcji byty o wektor (dx, dy). Byt z miejsca (x, y) trafia na miejsce (x + dx, y + dy).
     * Jeśli którykolwiek z bytów kolekcji byty nie jest w chmurze, metoda zgłasza wyjątek NiebytException.
     * Jeżeli wymaga tego niezmiennik chmury, metody ustaw() i przestaw() wstrzymują wątek do czasu, gdy ich wykonanie będzie możliwe. W przypadku przerwania zgłaszają wyjątek InterruptedException.
     */
    public synchronized void przestaw(Collection<Byt> byty, int dx, int dy) throws NiebytException, InterruptedException {
        if(!this.byty.containsAll(byty)) {
            throw new NiebytException();
        }
        // TODO: Wait until places are free
        byty.forEach(b -> b.move(dx, dy));
    }

    /**
     * Usuwa byt z chmury.
     * Jeśli byt nie jest w chmurze, metoda zgłasza wyjątek NiebytException.
     */
    public void kasuj(Byt byt) throws NiebytException {
        if(!byty.contains(byt)) {
            throw new NiebytException();
        }
        byty.remove(byt);
    }

    /**
     * Daje dwuelementową tablicę ze współrzędnymi x i y bytu, lub null, jeśli byt nie jest w chmurze.
     */
    public int[] miejsce(Byt byt) {
        if(!byty.contains(byt)) {
            return null;
        }
        return new int[] {byt.getX(), byt.getY()};
    }
}
