import chmura.Byt;
import chmura.Chmura;
import chmura.NiebytException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Przykład ilustrujący rozwiązanie problemu producenta i konsumentów za pomocą chmury bytów:
 * Producent posiada 1, będący przed drugim bytem wspólnym dla wszystkich konsumentów.
 * Producent i konsument próbują przesunąć byty odpowiedające ich klasom do przodu, dzięki czemu
 * konsumenci nie wyprzedzą producenta.
 */
public class ProducenciKonsumenci {
    private final static int N_KONSUMENTOW = 20;

    private static Chmura rynek = new Chmura();
    private final static int maxProduktow = 10000;
    private static Set<Byt> bytProducenta = new HashSet<>();
    private static Set<Byt> bytKonsumentow = new HashSet<>();

    static class Producent extends Thread {
        // jako pierwszy przesuwa byt
        private int nrProduktu = 1;
        private int maxCzasProdukcji;

        Producent(int maxCzasProdukcji) {
            this.maxCzasProdukcji = maxCzasProdukcji;
        }

        @Override
        public void run() {
            while(nrProduktu <= maxProduktow) {
                int czekajSekund = (int)(1000 * maxCzasProdukcji * Math.random());
                try {
                    sleep(czekajSekund);
                    rynek.przestaw(bytProducenta, 1, 0);
                } catch (InterruptedException | NiebytException e) {
                    e.printStackTrace();
                }
                System.out.println("Producent wyprodukował " + Integer.toString(nrProduktu));
                nrProduktu++;
            }
        }
    }

    static class Konsument extends Thread {
        // wszyscy konsumenci próbują przesunąć jeden byt i to rozwiązuje problem synchronizacji
        private int id;
        private static AtomicInteger nrProduktu = new AtomicInteger(0);
        private int maxCzasKonsumpcji;

        Konsument(int id, int maxCzasKonsumpcji) {
            this.maxCzasKonsumpcji = maxCzasKonsumpcji;
            this.id = id;
        }

        @Override
        public void run() {
            int nrProd = 0;
            while(nrProd < maxProduktow) {
                int czekajSekund = (int)(1000 * maxCzasKonsumpcji * Math.random());
                try {
                    sleep(czekajSekund);
                    rynek.przestaw(bytKonsumentow, 1, 0);
                    nrProd = nrProduktu.incrementAndGet();
                } catch (InterruptedException | NiebytException e) {
                    e.printStackTrace();
                }
                System.out.println("Konsument nr " + Integer.toString(id) + " skonsumował produkt " + Integer.toString(nrProd));
            }
        }
    }

    public static void main(String[] args) {
        try {
            bytProducenta.add(rynek.ustaw(1, 0));
            bytKonsumentow.add(rynek.ustaw(0,0));
            Producent p = new Producent(1);
            p.start();
            for(int i=0; i<N_KONSUMENTOW; i++) {
                Konsument k = new Konsument(i, 20);
                k.start();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
