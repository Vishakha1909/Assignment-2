/**
 * Project: Game Hub
 * File: EdgePos.java
 * Purpose: Immutable edge coordinate and orientation.
 */
package puzzles.dots;

public final class EdgePos {
    public final Orientation o; public final int r, c;
    public EdgePos(Orientation o, int r, int c) { this.o=o; this.r=r; this.c=c; }
    @Override public String toString(){ return o+"("+r+","+c+")"; }
    @Override public boolean equals(Object x){ if(!(x instanceof EdgePos)) return false;
        EdgePos e=(EdgePos)x; return o==e.o && r==e.r && c==e.c; }
    @Override public int hashCode(){ return (o.ordinal()*31 + r)*31 + c; }
}
