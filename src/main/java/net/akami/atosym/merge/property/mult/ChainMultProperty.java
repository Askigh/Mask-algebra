package net.akami.atosym.merge.property.mult;

import net.akami.atosym.core.MaskContext;
import net.akami.atosym.expression.MathObject;
import net.akami.atosym.expression.MathObjectType;
import net.akami.atosym.merge.MonomialMultiplicationMerge;
import net.akami.atosym.merge.SequencedMerge;
import net.akami.atosym.merge.property.global.ChainOperationProperty;
import net.akami.atosym.utils.NumericUtils;

import java.util.function.Predicate;

public class ChainMultProperty extends ChainOperationProperty {

    public ChainMultProperty(MathObject p1, MathObject p2, MaskContext context) {
        super(p1, p2, context, false);
    }

    @Override
    protected MathObjectType getWorkingType() {
        return MathObjectType.MULT;
    }

    @Override
    protected SequencedMerge<MathObject> generateMergeTool(MaskContext context) {
        return new MonomialMultiplicationMerge(context);
    }

    @Override
    protected Predicate<MathObject> significantElementCondition() {
        return NumericUtils::isNotOne;
    }
}