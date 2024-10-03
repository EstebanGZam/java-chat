package model.calls;

import java.io.IOException;
import java.util.HashMap;

public class Call implements Runnable {

    private HashMap<String, CallMember> callMembers;
    public boolean isRunning = true;

    public Call() {
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

    @Override
    public void run() {
        for (CallMember callMember : callMembers.values()) {
            Thread microphoneThread = new Thread(() -> {
                try {
                    callMember.talk(callMember.getSocket());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Thread speakerThread = new Thread(() -> {
                try {
                    callMember.listen(callMember.getSocket());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            microphoneThread.start();
            speakerThread.start();
        }
    }

}
