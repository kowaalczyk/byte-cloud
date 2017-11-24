package chmura;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
public class ChmuraTest {

    /**
     * Walidacja z treści zadania
     */
    @Test
    public void walidacjaChmury() throws Exception {
        Chmura chmura = new Chmura((x, y) -> x < y);
        Byt byt = null;

        byt = chmura.ustaw(0, 0);

        Collection<Byt> byty = Arrays.asList(byt);
        chmura.przestaw(byty, 1, -1);

        @SuppressWarnings("unused")
        int[] miejsce = chmura.miejsce(byt);
        chmura.kasuj(byt);

        new Chmura();
        // passes if no exception is thrown
    }

    /**
     * Sprawdza czy po wybudzeniu wątku ponownie sprawdzany jest warunek na przesunięcie bytów
     */
    @Test
    public void przesunieciePoWybudzeniu() throws Exception {
        Chmura chmura = new Chmura();

        try {
            Byt b1 = chmura.ustaw(0, 6);
            Byt b2 = chmura.ustaw(0, 3);
            Byt b3 = chmura.ustaw(0, 0);
            Byt b4 = chmura.ustaw(3, 6);

            List<Byt> byty1 = Arrays.asList(b1, b2, b3);
            List<Byt> byty2 = Arrays.asList(b2, b3);
            List<Byt> byty3 = Arrays.asList(b4);

            Thread t1 = new Thread(() -> {
                try {
                    chmura.przestaw(byty1, 3, 0);
                } catch (InterruptedException | NiebytException ignored) {}
            });
            Thread t2 = new Thread(() -> {
                try {
                    chmura.przestaw(byty2, 3, 0);
                    Thread.sleep(2000);
                    chmura.przestaw(byty3, 1, 0);
                } catch (InterruptedException | NiebytException ignored) {}
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            assertArrayEquals(chmura.miejsce(b1), new int[]{3, 6});
            assertArrayEquals(chmura.miejsce(b2), new int[]{6, 3});
            assertArrayEquals(chmura.miejsce(b3), new int[]{6, 0});
            assertArrayEquals(chmura.miejsce(b4), new int[]{4, 6});
        } catch (InterruptedException ignored) {}
    }

    /**
     * Sprawdza czy skasowany Byt nie może usunąć z chmury innego bytu.
     */
    @Test(expected = NiebytException.class)
    public void testCzyszczeniaBytu() throws Exception {
        Chmura chmura = new Chmura();
        Byt byt1 = chmura.ustaw(0, 0);
        chmura.kasuj(byt1);
        Byt byt2 = chmura.ustaw(0, 0);
        chmura.kasuj(byt1);
    }

    /**
     * Sprawdza czy Byt z chmury A nie usunie bytu z chmury B na tej samej pozycji.
     */
    @Test(expected = NiebytException.class)
    public void testPrzynaleznosciDoChmury() throws Exception {
        Chmura chmura1 = new Chmura();
        Chmura chmura2 = new Chmura();
        Byt byt1 = chmura1.ustaw(0, 0);
        Byt byt2 = chmura2.ustaw(0, 0);
        chmura2.kasuj(byt1);
    }

}
