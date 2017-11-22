import chmura.Byt;
import chmura.Chmura;
import chmura.NiebytException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ProducenciKonsumenci {
    private final static int N_KONSUMENTOW = 10;

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
        // wszyscy konsumenci próbują przesunąć jeden byt i to załatwia synchronizację
        private int id;
        private int nrProduktu = 1;
        private int maxCzasKonsumpcji;

        Konsument(int id, int maxCzasKonsumpcji) {
            this.maxCzasKonsumpcji = maxCzasKonsumpcji;
            this.id = id;
        }

        @Override
        public void run() {
            while(nrProduktu <= maxProduktow) {
                int czekajSekund = (int)(1000 * maxCzasKonsumpcji * Math.random());
                try {
                    sleep(czekajSekund);
                    rynek.przestaw(bytKonsumentow, 1, 0);
                } catch (InterruptedException | NiebytException e) {
                    e.printStackTrace();
                }
                System.out.println("Konsument nr " + Integer.toString(id) + "skonsumował " + Integer.toString(nrProduktu));
                nrProduktu++;
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
                Konsument k = new Konsument(i, 4);
//                k.start();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
