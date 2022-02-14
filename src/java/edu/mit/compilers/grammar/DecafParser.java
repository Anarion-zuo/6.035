// YOU CAN MODIFY ANYTHING IN THIS FILE, THIS IS JUST A SUGGESTED CLASS

package edu.mit.compilers.grammar;


public class DecafParser
{
    private DecafScanner scanner;

    public boolean hasError () {
        return false;
    }

    /** Whether to display debug information. */
    public void setTrace(boolean shouldTrace) {

    }

    public DecafParser(DecafScanner scanner_) {
        scanner = scanner_;
    }

    public final void program() throws Exception {
    }
}
