package parser;

import java.util.HashMap;
import java.util.ListIterator;

import parser.Parser.COMMAND;
import parser.Parser.ARGTYPE;
import parser.Parser.CommandInfo;
import parser.Parser.CommandArgument;
//import parser.Parser.CommandInfo.CommandArgument;
//import parser.Parser.CommandInfo.CommandArgument.ARGTYPE;
import parser.Parser.ParserCompileException;
import util.FileHandler;

/**
 * CommandRules.java Handles Command Rules
 *
 */
public class CommandRules {

    private HashMap<COMMAND, CommandRule> rules;

    public CommandRules(String fname) {

        rules = new HashMap<COMMAND, CommandRule>();

        String text = FileHandler.readText(fname);
        System.out.println("Text: " + text);
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.length() == 0) continue;
            if (line.charAt(0) == '#' || line.charAt(0) == '\n') {
                continue;
            }
            String[] symbols = line.split(" ");

            String command = symbols[0];

            COMMAND c = Parser.toCommand(command);
//            System.out.println("rules: COMMAND: " + c);
            if (c == COMMAND.NULL) {
                //continue;
            }

            CommandRule rule = new CommandRule(symbols.length - 1);

            // Scroll through, skipping over the 1st symbol
            for (int i = 1; i < symbols.length; i++) {
                String argument = symbols[i];
                boolean isOptional = false;
                if (argument.indexOf('<') != -1 && argument.indexOf('>') != -1) {
                    isOptional = true;
                    argument = argument.replaceAll("<|>", "");
                }
//                System.out.println("rules: " + c + ", ARGUMENT: " + argument);
                argument = argument.replaceAll("\\s+", "");
                argument = argument.replaceAll("\n", "");
                ARGTYPE argType = ARGTYPE.valueOf(argument);
                rule.setArgument(i - 1, argType, isOptional);
            }

            rules.put(c, rule);
        }
    }

    /**
     * Is it legal for the "argIndex" 'th variable in command "c" to be of type
     * "type"?
     * 
     * ALSO fills in optional arguments to empty defaults
     */

    public void validateLegal(CommandInfo c) throws ParserCompileException {
        CommandRule rule = rules.get(c.getCommand());

        // Note, the commandRuleIndex does not correspond to the list index!
        // When encountering an "optional" command, this may skip over the optional
        // commands.
        int commandRuleIndex = 0;

        int commandIndex = 0;
        
        ListIterator<CommandArgument> l = c.listIterator();

        while(l.hasNext()) {
            CommandArgument arg = l.next();
            boolean valid = false;

//            System.out.println("Validate Legal Command Argument: \"" + arg + "\".");
            while (!valid) {
                if (commandRuleIndex >= rule.getNumArgs()) {
                    throw new ParserCompileException("Too many commands! At index " + commandIndex + " for Command " + c.getCommand() + ". Max Args were " + rule.getNumArgs() + ".");
                }
                ARGTYPE type = rule.getArgType(commandRuleIndex);

                if (arg.getType().equals(type)) {
                    valid = true;
                } else {
                // If we have a conflict, deal with optional cases. Otherwise, invalid argument!
                    if (rule.isOptional(commandRuleIndex)) {
                        // Insert null before this current command to fill in the optional parameters
                        l.set(null);
                        l.add(arg);
                    } else {
                        throw new ParserCompileException("INVALID COMMAND TYPE at index " + commandIndex + " for Command " + c.getCommand() + ". Expected arg of type " + type + ", but got " + arg.getType());
                    }
                }
                commandRuleIndex++;
            }
            commandIndex++;
        }
    }

    public ARGTYPE getArgType(COMMAND c, int argIndex) {
        CommandRule rule = rules.get(c);
        return rule.getArgType(argIndex);
    }

    public Object getObject(COMMAND c, int argIndex, String symbol) {
        ARGTYPE type = getArgType(c, argIndex);
        switch(type) {
            case NUMBER:
                return Float.parseFloat(symbol);
            case STRING:
                return symbol;
            case KNOB:
                return symbol;
            default:
                return null;
        }
    }

    public static class CommandRule {
        private boolean isOptional[];
        private ARGTYPE arg[];

        private int numArgs;

        public CommandRule(int numArgs) {
            this.numArgs = numArgs;
            isOptional = new boolean[numArgs];
            arg = new ARGTYPE[numArgs];
        }

        public void setArgument(int index, ARGTYPE arg, boolean isOptional) {
            this.isOptional[index] = isOptional;
            this.arg[index] = arg;
        }

        public ARGTYPE getArgType(int index) {
            return arg[index];
        }

        public boolean isOptional(int index) {
            return isOptional[index];
        }

        public int getNumArgs() {
            return numArgs;
        }
    }
}
