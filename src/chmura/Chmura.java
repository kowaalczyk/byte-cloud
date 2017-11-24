package chmura;

import java.util.*;
import java.util.function.BiPredicate;

/**
 * Chmura bytów wymiaru n przyporządkowuje bytom miejsca, jednoznacznie identyfikowane przez ciąg n współrzędnych całkowitych. Zachowuje przy tym niezmiennik chmury: każdy byt jest w innym miejscu.
 * Chmura bytów może służyć do synchronizacji procesów współbieżnych.
 * @author Krzysztof Kowalczyk kk385830@students.mimuw.edu.pl / k.kowaalczyk@gmail.com
 */
public class Chmura {
    private BiPredicate<Integer, Integer> stan;

    private boolean jestByt(int x, int y) {
        return stan.test(x, y);
    }

    private void dodajBytDoStanu(Byt byt) {
        stan = stan.or((x, y) -> x==byt.getX() && y==byt.getY());
    }

    private void usunBytZeStanu(Byt byt) {
        stan = stan.and((x, y) -> x != byt.getX() || y != byt.getY());
    }

    private boolean bytNieIstnieje(Byt byt) {
        return byt == null || byt.getChmura() != this || !jestByt(byt.getX(), byt.getY());
    }

    private boolean moznaPrzeniesc(Collection<Byt> byty, int dx, int dy) {
        boolean ans = true;
        BiPredicate<Integer, Integer> stanBezKolekcji = stan;
        for(Byt byt : byty) {
            stanBezKolekcji = stanBezKolekcji.and((x, y) -> x != byt.getX() || y != byt.getY());
        }
        for(Byt byt : byty) {
            // byt można przenieść jeśli miejsce jest wolne lub zostanie zwolnione przez inny byt z kolekcji
            ans = ans && !stanBezKolekcji.test(byt.getX()+dx, byt.getY()+dy);
        }
        return ans;
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
        Byt nowy = new Byt(x, y, this);
        while(jestByt(x, y)) {
            wait();
        }
        dodajBytDoStanu(nowy);
        return nowy;
    }

    /**
     * Przemieszcza na raz wszystkie byty kolekcji byty o wektor (dx, dy). Byt z miejsca (x, y) trafia na miejsce (x + dx, y + dy).
     * Jeśli którykolwiek z bytów kolekcji byty nie jest w chmurze, metoda zgłasza wyjątek NiebytException.
     * Jeżeli wymaga tego niezmiennik chmury, metody ustaw() i przestaw() wstrzymują wątek do czasu, gdy ich wykonanie będzie możliwe. W przypadku przerwania zgłaszają wyjątek InterruptedException.
     */
    public synchronized void przestaw(Collection<Byt> byty, int dx, int dy) throws NiebytException, InterruptedException {
        for(Byt byt : byty) {
            if(bytNieIstnieje(byt)) {
                throw new NiebytException();
            }
        }
        while(!moznaPrzeniesc(byty, dx, dy)) {
            wait();
        }
        for(Byt byt : byty) {
            usunBytZeStanu(byt);
            byt.move(dx, dy);
            dodajBytDoStanu(byt);
        }
        notifyAll();
    }

    /**
     * Usuwa byt z chmury.
     * Jeśli byt nie jest w chmurze, metoda zgłasza wyjątek NiebytException.
     */
    public synchronized void kasuj(Byt byt) throws NiebytException {
        if(bytNieIstnieje(byt)) {
            throw new NiebytException();
        }
        usunBytZeStanu(byt);
        byt.kasuj();
        notifyAll();
    }

    /**
     * Daje dwuelementową tablicę ze współrzędnymi x i y bytu, lub null, jeśli byt nie jest w chmurze.
     * Treść zadania sugeruje że metoda nie powinna sprawdzać czy byt należy do chmury.
     */
    public int[] miejsce(Byt byt) {
        if(byt == null) {
            return null;
        }
        return new int[] {byt.getX(), byt.getY()};
    }
}
