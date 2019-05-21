package net.akami.mask.expression;

import net.akami.mask.core.MaskContext;

public class FunctionSign extends Monomial {

    private final char binding;

    public FunctionSign(char binding, MaskContext context) {
        super(1, new SingleCharVariable(binding, context));
        this.binding = binding;
    }

    @Override
    public String getExpression() {
        return String.valueOf(binding);
    }
}
