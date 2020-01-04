package au.com.advent.intcodeprogram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class IntcodeProgram {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntcodeProgram.class);

    private static HashMap<Integer,Integer> intCodeCommands = new HashMap<>();

    public static void main(String[] args) {

        getCommands();

        startIntCodeProgram(5);
    }

    private static void getCommands() {

        String[] intCodes;
        String[] intCodeTest1 = {"3","12","6","12","15","1","13","14","13","4","13","99","-1","0","1","9"};
        String[] intCodeTest2 = {"3","3","1105","-1","9","1101","0","0","12","4","12","99","1"};
        String[] intCodeTest3 = {"3","21","1008","21","8","20","1005","20","22","107","8","21","20","1006","20",
                "31","1106","0","36","98","0","0","1002","21","125","20","4","20","1105","1","46","104","999",
                "1105","1","46","1101","1000","1","20","4","20","1105","1","46","98","99"};
        final File commands = new File("src/main/resources/input.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(commands))) {

            intCodes = br.readLine().split(",");
            initialiseMemory(intCodes);

        } catch (Exception ex) {
            LOGGER.error("Exception occurred: {0}", ex);
            throw new RuntimeException("Exception occurred");
        }
    }

    private static int startIntCodeProgram(int input) {

        calculateIntCode(input);

        return intCodeCommands.get(0);
    }

    private static void calculateIntCode(int input) {

        int totalValue = 0;

        int instructionPointer = 0;

        int opCode;
        int firstParameter;
        int secondParameter;
//        int thirdParameter;

        int firstValue;
        int secondValue = 0;
        int thirdValue = 0;


        for (int i = 0;  i <= intCodeCommands.size(); i+=instructionPointer) {

            List<Integer> parameterList = new ArrayList<>();

            for (int k = 1; k <= 3; k++) {
                parameterList.add(0);
            }

            if (intCodeCommands.get(i).toString().length() >= 3) {
                char[] parameterModes = String.valueOf(intCodeCommands.get(i)).toCharArray();
                opCode = Integer.parseInt(String.valueOf(parameterModes[parameterModes.length-2]) + String.valueOf(parameterModes[parameterModes.length-1]));

                int counter = 0;
                for (int j = parameterModes.length-3; j >= 0; j--) {
                    parameterList.set(counter, Integer.parseInt(String.valueOf(parameterModes[j])));
                    counter++;
                }

            } else {
                opCode = intCodeCommands.get(i);
            }

            if (opCode == 99) {
                LOGGER.info("Opcode command 99 has been detected. Terminating program...");
                return;
            }

            firstParameter = Objects.nonNull(parameterList.get(0)) ? parameterList.get(0) : 0;
            secondParameter =  Objects.nonNull(parameterList.get(1)) ? parameterList.get(1) : 0;
//            thirdParameter = Objects.nonNull(parameterList.get(2)) ? parameterList.get(2) : 0;

            if (firstParameter == 0) {
                firstValue = intCodeCommands.get(intCodeCommands.get(i + 1));
            } else {
                firstValue = intCodeCommands.get(i+1);
            }

            if (opCode != 4) {
                if (opCode != 3) {
                    if (secondParameter == 0) {
                        secondValue = intCodeCommands.get(intCodeCommands.get(i + 2));
                    } else {
                        secondValue = intCodeCommands.get(i + 2);
                    }
                }

                if (opCode <= 2) {
                    thirdValue = intCodeCommands.get(intCodeCommands.get(i + 3));
                }
            }

            switch (opCode) {

                /*
                 * Opcode 1 adds together numbers read from two positions and stores the result in a third position.
                 * The three integers immediately after the opcode tell you these three positions - the first two indicate the
                 * positions from which you should read the input values, and the third indicates the position at which the output should be stored.
                 */
                case 1:
                    totalValue = firstValue + secondValue;
                    LOGGER.info("Opcode 1: Replacing value in position {} from {} to {}", (i + 3), thirdValue, totalValue);
                    intCodeCommands.put(intCodeCommands.get(i + 3),totalValue);
                    instructionPointer = 4;
                    break;

                /*
                 * Opcode 2 works exactly like opcode 1, except it multiplies the two inputs instead of adding them.
                 * Again, the three integers after the opcode indicate where the inputs and outputs are, not their values.
                 */
                case 2:
                    totalValue = firstValue * secondValue;
                    LOGGER.info("Opcode 2: Replacing value in position {} from {} to {}", (i + 3), thirdValue, totalValue);
                    intCodeCommands.put(intCodeCommands.get(i + 3),totalValue);
                    instructionPointer = 4;
                    break;

                /*
                 * Opcode 3 takes a single integer as input and saves it to the position given by its only parameter.
                 * For example, the instruction 3,50 would take an input value and store it at address 50.
                 */
                case 3:
                    LOGGER.info("Opcode 3: Replacing position {} from {} to {}", intCodeCommands.get(i+1), intCodeCommands.get(intCodeCommands.get(i+1)), input);
                    intCodeCommands.put(intCodeCommands.get(i+1),input);
                    instructionPointer = 2;
                    break;

                /*
                 * Opcode 4 outputs the value of its only parameter.
                 * For example, the instruction 4,50 would output the value at address 50.
                 */
                case 4:
                    LOGGER.info("Opcode 4: Value in position {}: {}", (i + 1), firstValue);
                    instructionPointer = 2;
                    break;

                /*
                 * Opcode 5 is jump-if-true:
                 *
                 * if the first parameter is non-zero, it sets the instruction pointer to the value from the second parameter.
                 * Otherwise, it does nothing.
                 */
                case 5:
                    if (firstValue != 0) {
                        LOGGER.info("Opcode 5: Setting instruction pointer to {}", secondValue);
                        i = 0;
                        instructionPointer = secondValue;
                    } else {
                        LOGGER.info("Opcode 5: Setting instruction pointer to 3");
                        instructionPointer = 3;
                    }
                    break;

                /*
                 * Opcode 6 is jump-if-false:
                 *
                 * if the first parameter is zero, it sets the instruction pointer to the value from the second parameter.
                 * Otherwise, it does nothing.
                 */
                case 6:
                    if (firstValue == 0) {
                        LOGGER.info("Opcode 6: Setting instruction pointer to {}", secondValue);
                        i = 0;
                        instructionPointer = secondValue;
                    } else {
                        LOGGER.info("Opcode 6: Setting instruction pointer to 3");
                        instructionPointer = 3;
                    }
                    break;

                /*
                 * Opcode 7 is less than:
                 *
                 * if the first parameter is less than the second parameter,
                 * it stores 1 in the position given by the third parameter. Otherwise, it stores 0.
                 */
                case 7:

                    thirdValue = intCodeCommands.get(intCodeCommands.get(i + 3));

                    if (firstValue < secondValue) {
                        LOGGER.info("Opcode 7: Changing position value of {} from {} to 1", intCodeCommands.get(i + 3), thirdValue);
                        intCodeCommands.put(intCodeCommands.get(i + 3), 1);
                    } else {
                        LOGGER.info("Opcode 7: Changing position value of {} from {} to 0", intCodeCommands.get(i + 3), thirdValue);
                        intCodeCommands.put(intCodeCommands.get(i + 3), 0);
                    }
                    instructionPointer = 4;
                    break;

                /*
                 * Opcode 8 is equals:
                 *
                 * if the first parameter is equal to the second parameter,
                 * it stores 1 in the position given by the third parameter. Otherwise, it stores 0.
                 */
                case 8:

                    thirdValue = intCodeCommands.get(intCodeCommands.get(i + 3));

                    if (firstValue == secondValue) {
                        LOGGER.info("Opcode 8: Changing position value of {} from {} to 1", intCodeCommands.get(i + 3), thirdValue);
                        intCodeCommands.put(intCodeCommands.get(i + 3), 1);
                    } else {
                        LOGGER.info("Opcode 8: Changing position value of {} from {} to 1", intCodeCommands.get(i + 3), thirdValue);
                        intCodeCommands.put(intCodeCommands.get(i + 3), 0);
                    }
                    instructionPointer = 4;
                    break;
            }

        }
    }

    private static void initialiseMemory(String[] intCodes) {

        LOGGER.debug("Initialising memory...");

        int iterator = 0;

        for (String intCode : intCodes) {
            intCodeCommands.put(iterator, Integer.parseInt(intCode));
            iterator++;
        }
    }
}
