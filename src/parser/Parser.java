package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import animation.Knob;
import animation.LinearKnob;
import image.Color;
import math.Vector3f;
import parser.Parser.CommandInfo.CommandArgument;
import parser.Parser.CommandInfo.CommandArgument.ARGTYPE;
import util.FileHandler;

/**
 * Dear anyone viewing this:
 *  God have mercy on your soul
 *  
 *  This is both pretty clever and super stupid. Use it at your own  risk
 * 
 * 
 */

public class Parser {

    // Make sure to update this in commandrules!
    public enum COMMAND {
        NULL,
        FRAMES,
        BASENAME,
        BASESIZE,
        BACKGROUND,
        VARY,
        LIGHT,
        AMBIENT,
        PUSH,
        POP,
        MOVE,
        SCALE,
        ROTATE,
        SPHERE,
        BOX,
        TORUS,
        SAVE,
    };

//    private enum DATATYPE {
//        NUMBER,
//        STRING
//    };

    /** toCommand(String command);
     *      Parses (Strictly) a string command and returns its corresponding enum
     */
    public static COMMAND toCommand(String command) {
        String converted = command = command.replaceAll("\\s+", "");
        converted = command.replaceAll("\n", "");

        try {
            return COMMAND.valueOf(converted);
        } catch (IllegalArgumentException e) {
//            System.out.println("INVALID COMMAND: \"" + command + "\".");
            return COMMAND.NULL;
        }
    }

    /* toDataType(String var);
     *      Parses the variable and determines how it should be treated
     */
    public static Object toDataType(String var, COMMAND c) {
        try {
            // It's a float if this returns
            return Float.parseFloat(var);
        } catch (NumberFormatException e) {
            // It's a string if this returns
            return var;
        }
    }

    /**
     *      This is sort of a lexer portion of this class.
     *      Given a raw string with our commands, turn it into command data.
     *      <br><br>
     *      This also checks to make sure our commands are valid (right arguments)
     * @param rawText
     * @return
     * @throws ParserCompileException 
     */
    public static List<CommandInfo> compileCommands(String rawText) throws ParserCompileException {
        CommandRules rules = new CommandRules("src/parser/commandrules");

        rawText = rawText.toUpperCase();

        String regex = "([A-Z0-9]+)|(\n)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(rawText);

        LinkedList<CommandInfo> commands = new LinkedList<CommandInfo>();
        CommandInfo currentCommand = null;

        int lineNumber = 0;

        while(matcher.find()) {
            String symbol = matcher.group();
//            System.out.println("[Parser.java] Raw Symobol: \"" + symbol + "\".");
            if (symbol.charAt(0) == '\n' || symbol.equals("\r\n") || symbol.charAt(0) == '\r') {
                lineNumber++;
                continue;
            }

//            symbol = symbol.replaceAll("\\s+", "");
//            symbol = symbol.replaceAll("\n", "");

//            System.out.println("[symbol] \"" + symbol + "\".");

            // Try to parse this as a command
            //System.out.println("symbol: " + symbol);
            COMMAND c = toCommand(symbol);

            if (c != COMMAND.NULL) {
                // We have a command! wrap up our previous one
                if (currentCommand != null) {
                    try {
                        rules.validateLegal(currentCommand);
                    } catch (ParserCompileException e) {
                        e.printStackTrace();
                        throw new ParserCompileException("^^ This Parser error was ON LINE " + lineNumber);
                    }
                    commands.add(currentCommand);
                }
                currentCommand = new CommandInfo(c);
            } else if (currentCommand != null) {
                // Otherwise, we're dealing with variables
//                System.out.println("[Parser.java] symbol: " + symbol);
//                System.out.println("Symbol: " + symbol + ", command: " + c);
                Object var = toDataType(symbol, c);//rules.getObject(currentCommand.getCommand(), currentCommand.getArgCount(), symbol);
                if (rules.getArgType(currentCommand.getCommand(), currentCommand.getArgCount()) == ARGTYPE.KNOB) {
                    currentCommand.setKnob((String) var);
                }
                currentCommand.addArgument(var);
            }
        }

        // Compile the last command
        try {
            rules.validateLegal(currentCommand);
        } catch (ParserCompileException e) {
            e.printStackTrace();
            throw new ParserCompileException("^^ This Parser error was ON LINE " + lineNumber);
        }
        commands.add(currentCommand);

        return commands;
    }

    public static void main(String[] args) throws ParserCompileException, ParserRuntimeException {

        Engine engine = new Engine();

        // By default, just read the script.
        String fname = "src/script";
        if (args.length != 0) {
            fname = args[0];
        }

        String text = FileHandler.readText(fname);

        List<CommandInfo> commands = compileCommands(text);

        //TODO: Split these into different methods

        /// FIRST PASS (pre animation)

        // Now we have a list of commands that shooould be valid.
        for(CommandInfo command : commands) {
            switch(command.getCommand()) {
                case BASENAME:
                    String bname = command.getString(0);
                    engine.setBaseName(bname);
                    break;
                case BASESIZE:
                    int w = (int)command.getNumber(0);
                    int h = (int)command.getNumber(1);
                    engine.setImage(w, h);
                    break;
                case BACKGROUND:
                    Color background = new Color(
                            (int) command.getNumber(0),
                            (int) command.getNumber(1),
                            (int) command.getNumber(2)
                            );
                    engine.setBackground(background);
                    break;
                case FRAMES:
                    engine.setFrames((int) command.getNumber(0));
                    break;
                case AMBIENT:
                    Color ambient = new Color(
                            (int) command.getNumber(0),
                            (int) command.getNumber(1),
                            (int) command.getNumber(2)
                            );
                    engine.setAmbient(ambient);
                    break;
                case VARY:
                    String name = command.getString(0);
                    Knob k = new LinearKnob(
                            (int) command.getNumber(1),
                            (int) command.getNumber(2),
                                  command.getNumber(3),
                                  command.getNumber(4)                            
                            );
                    engine.addKnob(name, k);
                default:
                    break;
            }
        }

        // After the precompiler, fill in the gaps!
        if (engine.getBaseName().equals("")) {
            System.err.println("Precompiler warning: No name given! Defaulting to \"image\"");
            System.err.println("\t Please use BASENAME <name> to give it a name!");
            engine.setBaseName("image");
        }
        if (engine.getImage() == null) {
            System.err.println("Precompiler warning: No image size given! Defaulting to \"500 x 500\"");
            System.err.println("\t Please use BASESIZE <width> <height> to give it a size!");
            engine.setImage(500, 500);
        }
        if (engine.getAmbient() == null) {
            System.err.println("Precompiler warning: No ambient defined! Defaulting to Color(10, 10, 10)");
            System.err.println("\t Please use AMBIENT <r> <g> <b> to give it a color!");
            engine.setAmbient(new Color(10, 10, 10));
        }
        if (engine.getBackground() == null) {
            System.err.println("Precompiler warning: No background color defined! Defaulting to Color(0, 0, 0)");
            System.err.println("\t Please use BACKGROUND <r> <g> <b> to give it a color!");
            engine.setBackground(new Color(0, 0, 0));
        }
        if (engine.getAnimation() == null) {
            System.err.println("Precompiler warning: Frame count not set! Defaulting to one frame.");
            System.err.println("\t Please use FRAMES <frame count> to give it a color!");
            engine.setFrames(1);
        }

        // Make sure everything is set
        engine.fillBackground();

        /// 2ND PASS (animation)

        for(int frame = 0; frame < engine.getAnimation().getFrameCount(); frame++) {

            // Reset the frame
            engine.getRenderer().setColor(engine.getBackground());
            engine.getRenderer().refill();
            engine.getRenderer().clearLights();
            engine.getBuffer().clear();

            for(CommandInfo command : commands) {
                System.out.println("[Parser.java] Command: " + command);
                switch(command.getCommand()) {
                    case LIGHT:
                        //String lightName = command.getString(0);
                        Vector3f direction = new Vector3f(
                                command.getNumber(1),
                                command.getNumber(2),
                                command.getNumber(3)
                                );
                        Color color = new Color(
                                (int) command.getNumber(4),
                                (int) command.getNumber(5),
                                (int) command.getNumber(6)
                                );

                        // These aren't really adjusted...
                        Vector3f areflect = new Vector3f(0.1f, 0.1f, 0.1f);
                        Vector3f dreflect = new Vector3f(0.5f, 0.5f, 0.5f);
                        Vector3f sreflect = new Vector3f(0.7f, 0.7f, 0.7f);

                        engine.getRenderer().addLight(
                                direction,
                                engine.getAmbient(),
                                color,
                                areflect,
                                dreflect,
                                sreflect
                        );
                        break;

                    case PUSH:
                        engine.getBuffer().transformPush();
                        break;
                    case POP:
                        engine.getBuffer().transformPop();
                        break;
                    case MOVE:
                        engine.getBuffer().translate(
                                command.getNumberKnobbed(0, frame, engine.getKnobMap(), 0),
                                command.getNumberKnobbed(1, frame, engine.getKnobMap(), 0),
                                command.getNumberKnobbed(2, frame, engine.getKnobMap(), 0)
                                );
                        break;
                    case SCALE:
                        engine.getBuffer().scale(
                                command.getNumberKnobbed(0, frame, engine.getKnobMap(), 1),
                                command.getNumberKnobbed(1, frame, engine.getKnobMap(), 1),
                                command.getNumberKnobbed(2, frame, engine.getKnobMap(), 1)
                                );
                        break;
                    case ROTATE:
                        String axis = command.getString(0).toLowerCase();
                        if (axis.equals("x")) {
                            engine.getBuffer().rotateX(command.getNumberKnobbed(1, frame, engine.getKnobMap(), 0));
                        } else if (axis.equals("y")) {
                            engine.getBuffer().rotateY(command.getNumberKnobbed(1, frame, engine.getKnobMap(), 0));
                        } else if (axis.equals("z")) {
                            engine.getBuffer().rotateZ(command.getNumberKnobbed(1, frame, engine.getKnobMap(), 0));
                        } else {
                            throw new ParserRuntimeException("Rotation error: Unidentified axis " + axis + ".");
                        }
                        break;
                    case SPHERE:
                        engine.getBuffer().addSphere(
                                command.getNumber(0),
                                command.getNumber(1),
                                command.getNumber(2),
                                command.getNumber(3)
                                );
                        break;
                    case BOX:
                        engine.getBuffer().addBox(
                                command.getNumber(0),
                                command.getNumber(1),
                                command.getNumber(2),
                                command.getNumber(3),
                                command.getNumber(4),
                                command.getNumber(5)
                                );
                        break;
                    case TORUS:
                        engine.getBuffer().addTorus(
                                command.getNumber(0),
                                command.getNumber(1),
                                command.getNumber(2),
                                command.getNumber(3),
                                command.getNumber(4)
                                );
                        break;
                    case BASENAME:
                    case BASESIZE:
                    case FRAMES:
                    case AMBIENT:
                    case BACKGROUND:
                    case SAVE:
                    case VARY:
                        // We've already processed these, or we'll do so later. Ignore these!
                        break;
                    default:
                        System.out.println("Parser unparsed command: " + command.getCommand());
                        break;
                }
            }
            
            engine.getRenderer().drawTriangleBufferMesh(engine.getBuffer());

            engine.getAnimation().setFrame(frame, engine.getImage());
        }

        /// THIRD PASS (post animation)

        for(CommandInfo command : commands) {
            switch(command.getCommand()) {
                case SAVE:
//                    engine.getImage().writeToPPM("images/" + engine.getBaseName() + ".ppm");
                    engine.getAnimation().saveToGIF("images/" + engine.getBaseName() + ".gif");
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
    }

    /** CommandInfo
     *      Holds information for a single command
     */
    static class CommandInfo implements Iterable<CommandArgument> {

        private COMMAND command;
        private ArrayList<CommandArgument> data;
        private int length;

        private String knobName;

        public CommandInfo(COMMAND command) {
            this.command = command;
            data = new ArrayList<CommandArgument>();
            knobName = null;
            length = 0;
        }

        @Override
        public Iterator<CommandArgument> iterator() {
            return data.iterator();
        }

        public ListIterator<CommandArgument> listIterator() {
            return data.listIterator();
        }

        public void addArgument(Object o) {
            data.add(new CommandArgument(o));
            length++;
        }

        public void setKnob(String knobName) {
            this.knobName = knobName;
        }

//        public void insertDefault(int index) {
//            data.add(index, null);
//        }

        public int getArgCount() {
            return length;
        }
        
        public COMMAND getCommand() {
            return command;
        }

        /** 
         * Only use this after verifying this command with CommandRules.java!
         */
        public float getNumber(int index) {
            return data.get(index).getNumber();
        }

        /** 
         * Only use this after verifying this command with CommandRules.java!
         * 
         * This multiplies the number by our knob value, if available.
         */
        public float getNumberKnobbed(int index, int frame, HashMap<String, Knob> knobMap, float defaultValue) {
            if (knobName == null || !knobMap.containsKey(knobName)) {
                return getNumber(index);
            } else if (knobMap.get(knobName).isActive(frame)) {
                return knobMap.get(knobName).getValue(frame) * getNumber(index);
            } else {
                return defaultValue;
            }
        }

        /** 
         * Only use this after verifying this command with CommandRules.java!
         */
        public String getString(int index) {
            return data.get(index).getString();
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder("[COMMAND] \"");
            b.append(getCommand().toString()).append("\". Args: {");
            int counter = 0;
            for(CommandArgument arg : this) {
                if (arg == null) {
                    b.append("null");
                } else {
                    b.append(arg.toString());
                }
                if (counter < data.size() - 1) {
                    b.append(", ");
                }
                counter++;
            }
            b.append("}.");
            return b.toString();
        }

        /** CommandArgument
         *      Holds one argument in a command, with it's type.
         */
        public static class CommandArgument {
            enum ARGTYPE {
                NULL,
                STRING,
                NUMBER,
                KNOB; // TODO: BAD PRACTICE: Never assigned! Please just use a string for a knob
                // And this is why:
                public boolean equals(ARGTYPE type) {
                    return type == this || (type == STRING && this == KNOB) || (type == KNOB && this == STRING);
                }
            }

            private ARGTYPE type;
            private Object object;

            public CommandArgument(Object object) {
                this.object = object;
                if (object instanceof String) {
                    type = ARGTYPE.STRING;
                } else if (object instanceof Float) {
                    type = ARGTYPE.NUMBER;
                } else {
                    type = ARGTYPE.NULL;
                }
            }

            public ARGTYPE getType() {
                return type;
            }

            public Object getObject() {
                return object;
            }

            /** 
             * Only use this after verifying this command with CommandRules.java!
             */
            public Float getNumber() {
                return (Float) object;
            }

            /** 
             * Only use this after verifying this command with CommandRules.java!
             */
            public String getString() {
                return (String) object;
            }

            @Override
            public String toString() {
                switch (type) {
                    case STRING:
                        return getString();
                    case NUMBER:
                        return getNumber().toString();
                    default:
                        return "[NULL]";
                }
            }

        }
    }

    @SuppressWarnings("serial")
    public static class ParserCompileException extends Exception {
        public ParserCompileException(String problem) {
            super(problem);
        }
    }

    @SuppressWarnings("serial")
    public static class ParserRuntimeException extends Exception {
        public ParserRuntimeException(String problem) {
            super(problem);
        }
    }
}
