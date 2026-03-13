package network;

import com.fasterxml.jackson.annotation.JsonInclude;
import commands.CommandDef;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private String message;
    private boolean success;
    private ResponseType type;

    private Map<String, CommandDef> syncData;

    public Response() {}

    public Response(ResponseType type, String message, boolean success) {
        this.type = type;
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Map<String, CommandDef> getSyncData() {
        return syncData;
    }

    public ResponseType getType() {
        return type;
    }

    public void setSyncData(Map<String, CommandDef> syncData) {
        this.syncData = syncData;
    }
}
