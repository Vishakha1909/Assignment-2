/**
 * Project: Game Hub
 * File: ClaimEdge.java
 * Purpose: Action representing claiming one edge.
 */
package puzzles.dots;
public final class ClaimEdge {
    public final EdgePos edge;
    public ClaimEdge(EdgePos e){ this.edge=e; }
    @Override public String toString(){ return "claim " + edge; }
}
