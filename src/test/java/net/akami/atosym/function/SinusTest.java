package net.akami.atosym.function;

import net.akami.atosym.core.MaskContext;
import net.akami.atosym.utils.ReducerFactory;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class SinusTest {

    private final SineFunction function = new SineFunction(MaskContext.DEFAULT);

    @Test
    public void numericValuesTest() {
        assertSinus(String.valueOf(Math.toRadians(-90)), "-1.0");
        assertSinus("0.0", "0");
        assertSinus(String.valueOf(Math.PI), "0");
        assertSinus(String.valueOf(Math.PI/2), "1.0");
    }

    @Test
    public void algebraicValuesTest() {
        assertSinus("x", "sin(x)");
        assertSinus("x+1", "sin(x+1.0)");
        assertSinus("x-3^2+8.3", "sin(x-0.6999998)");
    }


    private void assertSinus(String input, String result) {
        Assertions.assertThat(function.rawOperate(ReducerFactory.reduce(input)).toString()).isEqualTo(result);
    }
}
