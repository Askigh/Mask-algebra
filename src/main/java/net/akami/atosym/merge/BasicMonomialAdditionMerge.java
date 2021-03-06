package net.akami.atosym.merge;

import net.akami.atosym.core.MaskContext;
import net.akami.atosym.expression.Monomial;
import net.akami.atosym.merge.property.*;

import java.util.Arrays;
import java.util.List;

/**
 * A {@link MonomialAdditionMerge} with a property replacement, being {@link CommonDenominatorAdditionProperty} to
 * {@link BasicCommonDenominatorAdditionProperty}. Used for division only, which does not require any extra division after computing.
 */
public class BasicMonomialAdditionMerge extends MonomialAdditionMerge {

    public BasicMonomialAdditionMerge(MaskContext context) {
        super(context);
    }

    @Override
    public List<ElementSequencedMergeProperty<Monomial>> generateElementProperties(Monomial p1, Monomial p2) {
        return Arrays.asList(
                new CosineSinusSquaredProperty(p1, p2),
                new BasicCommonDenominatorAdditionProperty(p1, p2, context),
                new IdenticalVariablePartProperty(p1, p2, context)
        );
    }
}
