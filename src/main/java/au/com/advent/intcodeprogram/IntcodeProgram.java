package au.com.advent.intcodeprogram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntcodeProgram {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntcodeProgram.class);

    private static HashMap<Integer,Integer> intCodeCommands = new HashMap<>();

    public static void main(String[] args) {

        getCommands();

        startIntCodeProgram(1);
    }

    private static void getCommands() {

        String[] intCodes;

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
        int firstParameter = 0;
        int secondParameter = 0;
        int thirdParameter = 0;

        int firstValue =0;
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

            firstParameter = parameterList.get(0);
            secondParameter = parameterList.get(1);
            thirdParameter = parameterList.get(2);

            if (firstParameter == 0) {
                firstValue = intCodeCommands.get(intCodeCommands.get(i + 1));
            } else {
                firstValue = intCodeCommands.get(i+1);
            }

            if (opCode <= 2) {
                if (secondParameter == 0) {
                    secondValue = intCodeCommands.get(intCodeCommands.get(i + 2));
                } else {
                    secondValue = intCodeCommands.get(i + 2);
                }

                if (thirdParameter == 0) {
                    thirdValue = intCodeCommands.get(intCodeCommands.get(i + 3));
                }
            }

            switch (opCode) {
                case 1:
                    totalValue = firstValue + secondValue;
                    LOGGER.debug("Opcode 1: Replacing value in position {} from {} to {}", (i + 3), thirdValue, totalValue);
                    intCodeCommands.put(intCodeCommands.get(i + 3),totalValue);
                    instructionPointer = 4;
                    break;

                case 2:
                    totalValue = firstValue * secondValue;
                    LOGGER.debug("Opcode 2: Replacing value in position {} from {} to {}", (i + 3), thirdValue, totalValue);
                    intCodeCommands.put(intCodeCommands.get(i + 3),totalValue);
                    instructionPointer = 4;
                    break;

                case 3:
                    intCodeCommands.put(intCodeCommands.get(i+1),input);
                    LOGGER.debug("Opcode 3: Replacing position {} from {} to {}", (i+1), intCodeCommands.get(i+1), input);
                    instructionPointer = 2;
                    break;

                case 4:
                    LOGGER.info("Opcode 4: Value in position {}: {}", (i + 1), firstValue);
                    instructionPointer = 2;
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
