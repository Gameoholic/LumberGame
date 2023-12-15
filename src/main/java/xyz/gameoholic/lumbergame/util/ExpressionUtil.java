package xyz.gameoholic.lumbergame.util;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.util.Map;

/**
 * Utility class for evaluating expressions.
 */
public class ExpressionUtil {

    /**
     * Evaluates the expression.
     * @param expression The exp4j mathematical expression.
     * @param variables Variable values mapped to their names.
     * @return
     */
    public static double evaluateExpression(String expression, Map<String, Double> variables) {
        Function max = new Function("max", 2) {
            @Override
            public double apply(double... args) {
                return Math.max(args[0], args[1]);
            }
        };
        Function min = new Function("min", 2) {
            @Override
            public double apply(double... args) {
                return Math.min(args[0], args[1]);
            }
        };

        Expression exp = new ExpressionBuilder(expression)
            .functions(max, min)
            .variables(variables.keySet())
            .build();
        variables.entrySet().forEach(variable -> exp.setVariable(variable.getKey(), variable.getValue()));

        return exp.evaluate();
    }

}
