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
        // Obtener los dos miembros de la llamada
        if (callMembers.size() != 2) {
            System.out.println("Error: Se requiere exactamente dos miembros para una llamada.");
            return;
        }

        // Asumimos que solo hay dos miembros en la llamada
        CallMember caller = callMembers.values().toArray(new CallMember[0])[0];
        CallMember receiver = callMembers.values().toArray(new CallMember[0])[1];

        // Crear hilos para manejar el audio entre ambos usuarios
        Thread callerToReceiver = new Thread(() -> {
            try {
                caller.talk(receiver.getSocket());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread receiverToCaller = new Thread(() -> {
            try {
                receiver.talk(caller.getSocket());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Iniciar ambas transmisiones de audio
        callerToReceiver.start();
        receiverToCaller.start();

        try {
            // Esperar a que ambos hilos terminen
            callerToReceiver.join();
            receiverToCaller.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
