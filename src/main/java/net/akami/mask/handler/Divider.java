package net.akami.mask.handler;

import com.sun.org.apache.bcel.internal.generic.MONITORENTER;
import net.akami.mask.expression.Expression;
import net.akami.mask.expression.ExpressionElement;
import net.akami.mask.expression.Monomial;
import net.akami.mask.operation.MaskContext;
import net.akami.mask.utils.ExpressionUtils;
import net.akami.mask.utils.FormatterFactory;
import net.akami.mask.utils.MathUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class Divider extends BinaryOperationHandler<Expression> {

    private static final MathContext CONTEXT = new MathContext(120);

    public Divider(MaskContext context) {
        super(context);
    }

    @Override
    public Expression operate(Expression a, Expression b) {
        LOGGER.info("Divider process of {} |/| {}: \n", a, b);

        // Avoids division by zero error.
        if(a.equals(b))
            return new Expression(1);

        if (ExpressionUtils.isANumber(a) && ExpressionUtils.isANumber(b)) {
            // We are guaranteed that both expression have only one element, which is a monomial
            return numericalDivision((Monomial) a.get(0), (Monomial) b.get(0));
        }

        if(b.length() > 1) {
            LOGGER.error("Unable to calculate the division, the denominator being a polynomial. Returns a/b");
            throw new RuntimeException("Unsupported operation for now. Denominator will need to be an expression rather than a monomial");
        }

        ExpressionElement[] finalElements = new ExpressionElement[a.length()];
        int i = 0;

        for(ExpressionElement numPart : a.getElements()) {
            finalElements[i++] = simpleDivision(numPart, b.get(0));
        }

        return new Expression(finalElements);
    }

    private Expression numericalDivision(Monomial a, Monomial b) {
        BigDecimal bigA = new BigDecimal(a.getNumericValue());
        BigDecimal bigB = new BigDecimal(b.getNumericValue());
        float result = bigA.divide(bigB, CONTEXT).floatValue();
        LOGGER.info("Numeric division. Result of {} / {} : {}", a, b, result);
        return new Expression(result);
    }

    public ExpressionElement simpleDivision(ExpressionElement a, ExpressionElement b) {
        
    }

    public String simpleDivision(String a, String b) {
        List<String> numFactors = ExpressionUtils.decompose(a);
        List<String> denFactors = ExpressionUtils.decompose(b);

        LOGGER.info("NumFactors : {}, DenFactors : {}", numFactors, denFactors);
        for (int i = 0; i < numFactors.size(); i++) {
            String numFactor = numFactors.get(i);
            if (numFactor == null) continue;

            for (int j = 0; j < denFactors.size(); j++) {
                String denFactor = denFactors.get(j);
                if (denFactor == null) continue;

                divideTwoFactors(numFactor, denFactor, i, j, numFactors, denFactors);
                numFactor = numFactors.get(i);
                if(numFactor == null)
                    break;
            }
        }
        LOGGER.info("Simple division proceeded. NumFactors : {}, DenFactors : {}", numFactors, denFactors);
        String finalNum = assembleFactors(numFactors);
        String finalDen = assembleFactors(denFactors);
        LOGGER.debug("Raw findResult : {} / {}", finalNum, finalDen);
        if(finalDen.isEmpty() || finalDen.equals("1") || finalDen.equals("1.0")) {
            return finalNum;
        }
        return finalNum + "/" + finalDen;
    }

    private void divideTwoFactors(String numFactor, String denFactor, int i, int j,
                                         List<String> numFactors, List<String> denFactors) {
        if (ExpressionUtils.isANumber(numFactor) && ExpressionUtils.isANumber(denFactor)) {
            LOGGER.info("{} and {} are numbers. Dividing them", numFactor, denFactor);
            proceedForNumericalDivision(numFactor, denFactor, i, j, numFactors, denFactors);
            LOGGER.info("Deleted at index {} and {} : NumFactors : {}, DenFactors : {}", i, j, numFactors, denFactors);

        } else {
            LOGGER.info("{} or {} isn't a number", numFactor, denFactor);
            proceedForVarDivision(numFactor, denFactor, i, j, numFactors, denFactors);
            LOGGER.debug("Deleted at index {} and {} : NumFactors : {}, DenFactors : {}", i, j, numFactors, denFactors);
        }
    }

    private void proceedForNumericalDivision(String num, String den, int i, int j, List<String> nums, List<String> dens) {
        LOGGER.debug("Numeric value found");
        if (MathUtils.cutSignificantZero(num).equals(MathUtils.cutSignificantZero(den))) {
            LOGGER.debug("Equal values at index {} and {} found. Deletes them", i, j);
            nums.set(i, null);
            dens.set(j, null);
        }
        LOGGER.info("Current state : Nums -> {}, Dens -> {}", nums, dens);
        LOGGER.debug("DenFactors : {}", dens);
        LOGGER.info("Both factors are numeric but not equal : {} and {}", num, den);
        float numValue = Float.parseFloat(num);
        float denValue = Float.parseFloat(den);
        float[] values = simplifyNumericalFraction(numValue, denValue);
        LOGGER.debug("Nums {}, Dens {}, i = {}, j = {}", nums, dens, i, j);
        nums.set(i, String.valueOf(values[0]));
        dens.set(j, String.valueOf(values[1]));
        LOGGER.info("Current state 2 : Nums -> {}, Dens -> {}", nums, dens);
    }

    private boolean proceedForVarDivision(String num, String den, int i, int j, List<String> nums, List<String> dens) {
        LOGGER.debug("Vars : {} and {}", num, den);
        String nVar = ExpressionUtils.toVariablesType(num);
        String dVar = ExpressionUtils.toVariablesType(den);

        if(!nVar.equals(dVar))
            return false;

        if(num.equals(den)) {
            LOGGER.debug("Both var values are equal. Deletes them");
            nums.set(i, null);
            dens.set(j, null);
            return true;
        }

        String nPow = num.replace(nVar+"^", "");
        String dPow = den.replace(dVar+"^", "");
        nPow = nPow.isEmpty() ? "1" : nPow;
        dPow = dPow.isEmpty() ? "1" : dPow;

        String subResult = null;//MathUtils.subtract(nPow, dPow, context);

        if(ExpressionUtils.isANumber(subResult)) {
            float subNumericResult = Float.parseFloat(subResult);
            if(subNumericResult < 0) {
                nums.set(i, null);
                dens.set(j, dVar + -subNumericResult);
            } else {
                dens.set(j, null);
                nums.set(i, nVar + subNumericResult);
            }
        } else {
            LOGGER.error("Unsupported operation for now");
        }
        return false;
    }

    private String assembleFactors(List<String> factors) {
        clearBuilder();
        BUILDER.append(1);
        for(String factor : factors) {
            if(factor != null && !factor.equals("1") && !factor.equals("1.0")) {
                Multiplier handler = context.getBinaryOperation(Multiplier.class);
                //TODO BUILDER.replace(0, BUILDER.length(), handler.simpleMult(BUILDER.toString(), factor));
            }
        }
        return BUILDER.toString();
    }

    private float[] simplifyNumericalFraction(float numerator, float denominator) {
        float[] values = new float[2];
        float numericResult;
        boolean simplified = false;
        if (Math.abs(numerator) > Math.abs(denominator)) {
            numericResult = Float.parseFloat(rawOperate("" + numerator, "" + denominator));
            if (numericResult % 1 == 0) {
                values[0] = numericResult;
                values[1] = 1;
                simplified = true;
            }
        } else {
            numericResult = Float.parseFloat(rawOperate("" + denominator, "" + numerator));
            if (numericResult % 1 == 0) {
                values[0] = 1;
                values[1] = numericResult;
                simplified = true;
            }
        }

        if (!simplified) {
            LOGGER.info("Couldn't simplify the numerical fraction {}/{}", numerator, denominator);
            values[0] = numerator;
            values[1] = denominator;
        }
        return values;
    }

    @Override
    public Expression inFormat(Expression origin) {
        return origin;
    }

    @Override
    public Expression outFormat(Expression origin) {
        return origin;
    }
}
