/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku;

public enum SolvingTechnique {

    HiddenSingle("Hidden Single 1.0-1.5"),
    DirectPointing("Direct Pointing 1.7"),
    DirectHiddenPair("Direct Hidden Pair 2.0"),
    NakedSingle("Naked Single 2.3"),
    DirectHiddenTriplet("Direct Hidden Triplet 2.5"),
    PointingClaiming("Pointing & Claiming 2.6-2.8"),
    NakedPair("Naked Pair 3.0"),
    XWing("X-Wing 3.2"),
    HiddenPair("Hidden Pair 3.4"),
    NakedTriplet("Naked Triplet 3.6"),
    Swordfish("Swordfish 3.8"),
    HiddenTriplet("Hidden Triplet 4.0"),
    XYWing("XY-Wing 4.2"),
    XYZWing("XYZ-Wing 4.4"),
    UniqueLoop("Unique Rectangle / Loop 4.5+"),
    NakedQuad("Naked Quad 5.0"),
    Jellyfish("Jellyfish 5.2"),
    HiddenQuad("Hidden Quad 5.4"),
    BivalueUniversalGrave("Bivalue Universal Grave 5.6,5.7+"),
    AlignedPairExclusion("Aligned Pair Exclusion 6.2"),
    ForcingChainCycle("Forcing Chains & Cycles 6.6+,7.0+"),
    AlignedTripletExclusion("Aligned Triplet Exclusion 7.5"),
    NishioForcingChain("Nishio Forcing Chains 7.5+"),
    MultipleForcingChain("Multiple Forcing Chains 8.0+"),
    DynamicForcingChain("Dynamic Forcing Chains 8.5+"),
    DynamicForcingChainPlus("Dynamic Forcing Chains (+) 9.0+"),
    NestedForcingChain("Nested Forcing Chains 9.5+");

    private final String name;

    private SolvingTechnique(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
