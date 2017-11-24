package chmura;

/**
 * Placeholder used for testing
 */
public class Byt implements Comparable<Byt> {
    // TODO: Dodać id chmury

    private int x;
    private int y;

    public Byt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Byt byt = (Byt) o;

        if (x != byt.x) return false;
        return y == byt.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public int compareTo(Byt that) {
        if(this.x < that.x) {
            return -1;
        } else if(this.x > that.x) {
            return 1;
        } else {
            return Integer.compare(this.y, that.y);
        }
    }
}
