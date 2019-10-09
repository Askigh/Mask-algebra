package net.akami.atosym.merge.property.sum;

import net.akami.atosym.core.MaskContext;
import net.akami.atosym.expression.MathObject;
import net.akami.atosym.expression.MathObjectType;
import net.akami.atosym.merge.MonomialAdditionMerge;
import net.akami.atosym.merge.SequencedMerge;
import net.akami.atosym.merge.property.global.ChainOperationProperty;
import net.akami.atosym.utils.NumericUtils;

import java.util.function.Predicate;

public class ChainSumProperty extends ChainOperationProperty {

    public ChainSumProperty(MathObject p1, MathObject p2, MaskContext context) {
        super(p1, p2, context, false);
    }

    @Override
    protected MathObjectType getWorkingType() {
        return MathObjectType.SUM;
    }

    @Override
    protected SequencedMerge<MathObject> generateMergeTool(MaskContext context) {
        return new MonomialAdditionMerge(context);
    }

    @Override
    protected Predicate<MathObject> significantElementCondition() {
        return NumericUtils::isNotZero;
    }
}