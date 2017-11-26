import chmura.Byt;
import chmura.Chmura;
import chmura.NiebytException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Przykład rozwiązujący problem czytelników-pisarzy za pomocą chmyury bytów.
 * Używa zaimplementowanych przy użyciu chmury semaforów.
 * Czas pisania przez pisarza jest znacznie dłuższy niż czas czytania przez czytelnika
 * aby podkreślić że nikt wtedy nie korzysta z biblioteki.
 * (czasy wykonywania czynności można zmieniać w funkcji main() przy tworzeniu czytelników i pisarzy.
 */
public class CzytelnicyPisarze {
    // parametry przykładu
    private static AtomicInteger odliczanie = new AtomicInteger(100000); // każde wejście zmniejsza licznik aby program nie wykonywał się w nieskończoność
    private static final int N_CZYTELNIKOW = 100;
    private static final int N_PISARZY = 10;

    // implementacja semaforów za pomocą chmury
    private static Chmura semafory = new Chmura((x, y) -> x!=0 && x!=1);
    private static void P(List<Byt> semafor) throws InterruptedException, NiebytException {
        semafory.przestaw(semafor, -1, 0);
    }
    private static void V(List<Byt> semafor) throws InterruptedException, NiebytException {
        semafory.przestaw(semafor, 1, 0);
    }

    private static List<Byt> semaforOchrona = new ArrayList<>();
    private static List<Byt> semaforCzytelnikow = new ArrayList<>();
    private static List<Byt> semaforPisarzy = new ArrayList<>();

    private static int iluCzyta, iluPisze, czekaCzyt, czekaPis;

    static class Czytelnik extends Thread {
        private int id;
        private int maxCzasCzytania;
        private int maxCzasWlasnychSpraw;

        Czytelnik(int id, int maxCzasCzytania, int maxCzasWlasnychSpraw) {
            this.id = id;
            this.maxCzasCzytania = maxCzasCzytania;
            this.maxCzasWlasnychSpraw = maxCzasWlasnychSpraw;
        }

        private void czytaj() throws InterruptedException {
            System.out.println("Czytelnik " + Integer.toString(id) + " czyta...");
            int czekajSekund = (int)(1000 * maxCzasCzytania * Math.random());
            sleep(czekajSekund);
            System.out.println("Czytelnik " + Integer.toString(id) + " skończył czytać");
        }

        private void wlasneSprawy() throws InterruptedException {
            int czekajSekund = (int)(1000 * maxCzasWlasnychSpraw * Math.random());
            sleep(czekajSekund);
        }

        @SuppressWarnings("Duplicates")
        @Override
        public void run() {
            while(odliczanie.decrementAndGet() > 0) {
                try {
                    wlasneSprawy();
                    P(semaforOchrona);
                    if(iluPisze + czekaPis > 0) {
                        czekaCzyt++;
                        V(semaforOchrona);
                        P(semaforCzytelnikow);
                        czekaCzyt--;
                    }
                } catch (InterruptedException | NiebytException e) {
                    e.printStackTrace();
                    interrupt();
                }
                try {
                    iluCzyta++;
                    if(czekaCzyt > 0) {
                        V(semaforCzytelnikow);
                    } else {
                        V(semaforOchrona);
                    }
                    czytaj();
                    P(semaforOchrona);
                    iluCzyta--;
                    if(iluCzyta == 0 &&  czekaPis > 0) {
                        V(semaforPisarzy);
                    } else {
                        V(semaforOchrona);
                    }
                } catch (InterruptedException | NiebytException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    static class Pisarz extends Thread {
        private int id;
        private int maxCzasPisania;
        private int maxCzasWlasnychSpraw;

        Pisarz(int id, int maxCzasPisania, int maxCzasWlasnychSpraw) {
            this.id = id;
            this.maxCzasPisania = maxCzasPisania;
            this.maxCzasWlasnychSpraw = maxCzasWlasnychSpraw;
        }

        private void pisz() throws InterruptedException {
            System.out.println("Pisarz " + Integer.toString(id) + " pisze...");
            int czekajSekund = (int)(1000 * maxCzasPisania * Math.random());
            sleep(czekajSekund);
            System.out.println("Pisarz " + Integer.toString(id) + " skończył pisać");
        }

        private void wlasneSprawy() throws InterruptedException {
            int czekajSekund = (int)(1000 * maxCzasWlasnychSpraw * Math.random());
            sleep(czekajSekund);
        }

        @SuppressWarnings("Duplicates")
        @Override
        public void run() {
            while(odliczanie.decrementAndGet() > 0) {
                try {
                    wlasneSprawy();
                    P(semaforOchrona);
                    if(iluPisze + iluCzyta > 0) {
                        czekaPis++;
                        V(semaforOchrona);
                        P(semaforPisarzy);
                        czekaPis--;
                    }
                } catch (InterruptedException | NiebytException e) {
                    e.printStackTrace();
                    interrupt();
                }
                try {
                    iluPisze++;
                    V(semaforOchrona);
                    pisz();
                    P(semaforOchrona);
                    iluPisze--;
                    if(czekaCzyt > 0) {
                        V(semaforCzytelnikow);
                    } else if(czekaPis > 0) {
                        V(semaforPisarzy);
                    } else {
                        V(semaforOchrona);
                    }
                } catch (InterruptedException | NiebytException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            semaforOchrona.add(semafory.ustaw(1,0));
            semaforCzytelnikow.add(semafory.ustaw(0, 1));
            semaforPisarzy.add(semafory.ustaw(0,2));
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        for(int i=0; i<N_PISARZY; i++) {
            Pisarz p = new Pisarz(i, 21, 37);
            p.start();
        }
        for(int i=0; i<N_CZYTELNIKOW; i++) {
            Czytelnik c = new Czytelnik(i, 9, 11);
            c.start();
        }
    }
}
