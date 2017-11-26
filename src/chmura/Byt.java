package chmura;

/**
 * Placeholder used for testing
 */
public class Byt {
    private int x;
    private int y;
    private Chmura chmura;

    Byt(int x, int y, Chmura chmura) {
        this.x = x;
        this.y = y;
        this.chmura = chmura;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    Chmura getChmura() {
        return chmura;
    }

    void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    void kasuj() {
        this.chmura = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Byt byt = (Byt) o;

        if (x != byt.x) return false;
        if (y != byt.y) return false;
        return chmura != null ? chmura.equals(byt.chmura) : byt.chmura == null;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + (chmura != null ? chmura.hashCode() : 0);
        return result;
    }
}
