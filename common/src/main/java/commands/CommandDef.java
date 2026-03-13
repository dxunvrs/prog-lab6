package commands;

public record CommandDef(String name, String description, int expectedArgs, CommandType commandType) {}
