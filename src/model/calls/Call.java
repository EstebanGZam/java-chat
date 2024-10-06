package model.calls;

import java.util.HashMap;

import model.server.ClientHandler;

public class Call {

    private enum Status {
        ON_HOLD, ACTIVE;
    }

    private Status status = Status.ON_HOLD;
    private HashMap<String, CallMember> callMembers;

    public Call(ClientHandler callHost) {
        callMembers = new HashMap<>();
    }

    public void addCallMember(CallMember callMember) {
        callMembers.put(callMember.getUsername(), callMember);
    }

    public CallMember getCallMember(String username) {
        return callMembers.get(username);
    }

    public void removeCallMember(String username) {
        callMembers.remove(username);
    }

    public boolean hasCallMember(String username) {
        return callMembers.containsKey(username);
    }

    public HashMap<String, CallMember> getCallMembers() {
        return callMembers;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int numberOfCallMembers() {
        return callMembers.size();
    }

    public CallMember getCallHost() {
        for (CallMember callMember : callMembers.values()) {
            if (callMember.isHost()) {
                return callMember;
            }
        }
        return null;
    }

}
