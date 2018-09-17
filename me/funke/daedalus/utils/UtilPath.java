/*
 * Decompiled with CFR 0_121.
 */
package me.funke.daedalus.utils;

import java.beans.ConstructorProperties;
import java.util.*;

public final class UtilPath {
    private static final int[][] ADJACENT = new int[][]{{-1, 0, 0}, {0, -1, 0}, {0, 0, -1}, {1, 0, 0},
            {0, 1, 0}, {0, 0, 1}};
    private Tile[][][] area;
    private Tile start;
    private Tile end;
    private List<Tile> open = new ArrayList<>();
    private List<Tile> closed = new ArrayList<>();

    @ConstructorProperties(value = {"area", "start", "end"})
    public UtilPath(Tile[][][] area, Tile start, Tile end) {
        this.area = area;
        this.start = start;
        this.end = end;
    }

    public static boolean hasPath(Tile[][][] area, Tile start, Tile end) {
        return new UtilPath(area, start, end).process();
    }

    public static List<Tile> getPath(Tile[][][] area, Tile start, Tile end) {
        Tile parent;
        UtilPath pathFinder = new UtilPath(area, start, end);
        if (!pathFinder.process()) {
            return null;
        }
        LinkedList<Tile> route = new LinkedList<>();
        route.add(end);
        while ((parent = end.getParent()) != null) {
            route.add(parent);
            end = parent;
        }
        Collections.reverse(route);
        return new ArrayList<>(route);
    }

    private boolean process() {
        for (int x = 0; x < this.area.length; ++x) {
            for (int y = 0; y < this.area[x].length; ++y) {
                for (int z = 0; z < this.area[x][y].length; ++z) {
                    this.area[x][y][z].setH(Math.abs(this.end.getX() - x) + Math.abs(this.end.getY() - y)
                            + Math.abs(this.end.getZ() - z));
                }
            }
        }
        this.open.add(this.start);
        this.start.setG(0.0);
        while (!this.closed.contains(this.end)) {
            Tile current = this.getNextTile();
            if (current == null) {
                return false;
            }
            this.processAdjacentTiles(current);
        }
        return true;
    }

    private Tile getNextTile() {
        double f = Double.MAX_VALUE;
        Tile next = null;
        for (Tile tile : this.open) {
            if (tile.getF() >= f && f != Double.MAX_VALUE)
                continue;
            f = tile.getF();
            next = tile;
        }
        if (next == null) {
            return null;
        }
        this.open.remove(next);
        this.closed.add(next);
        return next;
    }

    private void processAdjacentTiles(Tile base) {
        for (int[] modifier : ADJACENT) {
            Tile current;
            int x = base.getX() + modifier[0];
            int y = base.getY() + modifier[1];
            int z = base.getZ() + modifier[2];
            if (x < 0 || y < 0 || z < 0 || this.area.length <= x || this.area[x].length <= y + 1
                    || this.area[x][y].length <= z || !(current = this.area[x][y][z]).isPassable()
                    || !this.area[x][y + 1][z].isPassable() || current.getG() <= base.getG() + 1.0)
                continue;
            current.setG(base.getG() + 1.0);
            current.setParent(base);
            this.open.add(current);
        }
    }

    public Tile[][][] getArea() {
        return this.area;
    }

    public Tile getStart() {
        return this.start;
    }

    public Tile getEnd() {
        return this.end;
    }

    public List<Tile> getOpen() {
        return this.open;
    }

    public List<Tile> getClosed() {
        return this.closed;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UtilPath)) {
            return false;
        }
        UtilPath other = (UtilPath) o;
        if (!Arrays.deepEquals(this.getArea(), other.getArea())) {
            return false;
        }
        Tile this$start = this.getStart();
        Tile other$start = other.getStart();
        if (this$start == null ? other$start != null : !this$start.equals(other$start)) {
            return false;
        }
        Tile this$end = this.getEnd();
        Tile other$end = other.getEnd();
        if (this$end == null ? other$end != null : !this$end.equals(other$end)) {
            return false;
        }
        List<Tile> this$open = this.getOpen();
        List<Tile> other$open = other.getOpen();
        if (this$open == null ? other$open != null : !this$open.equals(other$open)) {
            return false;
        }
        List<Tile> this$closed = this.getClosed();
        List<Tile> other$closed = other.getClosed();
        return this$closed == null ? other$closed == null : this$closed.equals(other$closed);
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Arrays.deepHashCode(this.getArea());
        Tile $start = this.getStart();
        result = result * 59 + ($start == null ? 43 : $start.hashCode());
        Tile $end = this.getEnd();
        result = result * 59 + ($end == null ? 43 : $end.hashCode());
        List<Tile> $open = this.getOpen();
        result = result * 59 + ($open == null ? 43 : $open.hashCode());
        List<Tile> $closed = this.getClosed();
        result = result * 59 + ($closed == null ? 43 : $closed.hashCode());
        return result;
    }

    public String toString() {
        return "UtilPath(area=" + Arrays.deepToString(this.getArea()) + ", start=" + this.getStart()
                + ", end=" + this.getEnd() + ", open=" + this.getOpen() + ", closed=" + this.getClosed() + ")";
    }
}
