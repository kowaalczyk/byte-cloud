package chmura;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
public class ChmuraTest {
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
}
