package net.akami.mask.core;

import net.akami.mask.utils.ReducerFactory;

import java.util.Scanner;

public class MainTester {

    public static void main(String... args) {

        System.out.println(ReducerFactory.reduce("cos(pi())"));

        MaskOperatorHandler handler = MaskOperatorHandler.DEFAULT;

        Scanner sc = new Scanner(System.in);
        String expression;

        System.out.println("Next expression to reduce : ");
        while(!(expression = sc.nextLine()).isEmpty()) {
            long time = System.nanoTime();
            System.out.println("Result : "+ ReducerFactory.reduce(expression));
            float deltaTime = (System.nanoTime() - time) / 1000000000f;
            System.out.println("Calculations ended after "+deltaTime+" seconds");
            System.out.println("Next expression to reduce : ");
        }
        while(!(expression = sc.nextLine()).isEmpty()) {
            Mask.TEMP.reload(expression);
            handler.begin(Mask.TEMP);
            long time = System.nanoTime();
            System.out.println("Result : "+ handler.compute(MaskDerivativeCalculator.class, null, 'x').asExpression());
            float deltaTime = (System.nanoTime() - time) / 1000000000f;
            System.out.println("Calculations ended after "+deltaTime+" seconds");
            System.out.println("Next expression to reduce : ");
        }
    }
}
