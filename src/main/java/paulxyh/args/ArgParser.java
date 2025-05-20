package paulxyh.args;

import paulxyh.exception.ArgParsingException;

import java.util.*;

public class ArgParser {
    private static class Argument {
        String name;
        String description;
        boolean required;
        String value;

        Argument(String name, String description, boolean required) {
            this.name = name;
            this.description = description;
            this.required = required;
            this.value = null;
        }

        boolean isSet() {
            return value != null;
        }
    }

    private final Map<String, Argument> arguments = new HashMap<>();
    private final List<String> trailingArgs = new ArrayList<>();
    private boolean allowTrailingArgs = false;
    private boolean trailingArgsRequired = false;
    private String trailingArgsDescription = null;
    private String trailingArgsName = null;

    public void addArgument(String name, String description, boolean required) {
        if(arguments.containsKey(name)) {
            throw new IllegalArgumentException("Argument " + name + " already exists");
        }
        arguments.put(name, new Argument(name, description, required));
    }
    public void addTrailingArgs(String name, String description, boolean required) {
        this.trailingArgsName = name;
        this.trailingArgsDescription = description;
        this.trailingArgsRequired = required;
        this.allowTrailingArgs = true;
    }

    public void parse(String[] args) throws ArgParsingException {
        int i = 0;

        // Parse named arguments
        while (i < args.length && args[i].startsWith("--")) {
            String raw = args[i].substring(2);
            String[] parts = raw.split("=", 2);
            String name = parts[0];

            Argument arg = arguments.get(name);
            if (arg == null) {
                throw new ArgParsingException("Unknown argument: " + name);
            }

            // --key=value or --flag (implied boolean true)
            arg.value = (parts.length > 1) ? parts[1] : "true";
            i++;
        }

        for (Argument arg : arguments.values()) {
            if (arg.required && !arg.isSet()) {
                throw new ArgParsingException("Required argument --" + arg.name + " not set");
            }
        }

        // Parse trailing arguments (unnamed)
        if (i < args.length) {
            if (!allowTrailingArgs) {
                throw new ArgParsingException("Trailing arguments not allowed");
            }

            for (; i < args.length; i++) {
                trailingArgs.add(args[i]);
            }

            if (trailingArgsRequired && trailingArgs.isEmpty()) {
                throw new ArgParsingException("Trailing arguments required but not set");
            }
        }
    }


    public String getSynopsis(){
        StringBuilder sb = new StringBuilder();
        sb.append("Usage: [options] ");
        if(allowTrailingArgs) {
            sb
                    .append("[")
                    .append(trailingArgsName)
                    .append(" | [")
                    .append(trailingArgsName)
                    .append("] ...]");
        }
        sb.append("\nOptions:\n");
        for(Argument arg : arguments.values()) {
            sb.append("  --").append(arg.name).append("  ").append(arg.description);
            if(arg.required) {
                sb.append(" (required)");
            }
            sb.append("\n");
        }
        sb.append("  ").append(trailingArgsName).append("(s) ").append(trailingArgsDescription);
        if(trailingArgsRequired) {
            sb.append(" (required)");
        }
        sb.append("\n");
        return sb.toString();
    }

    public String get(String name){
        Argument arg = arguments.get(name);
        if(arg == null || !arg.isSet()) return null;
        return arg.value;
    }

    public boolean has(String name){
        Argument arg = arguments.get(name);
        return arg != null && arg.isSet();
    }

    public List<String> getTrailingArgs() {
        return Collections.unmodifiableList(trailingArgs);
    }

}
