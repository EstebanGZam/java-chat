package model.calls;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;


public class Call {

	private enum Status {
		ON_HOLD, ACTIVE
	}

	private final ExecutorService pool;
	private Status status = Status.ON_HOLD;
	private final HashMap<String, CallMember> callMembers;

	public Call() {
		this.pool = java.util.concurrent.Executors.newFixedThreadPool(10);
		callMembers = new HashMap<>();
	}

	public void addCallMember(CallMember callMember) {
		callMembers.put(callMember.getUsername(), callMember);
		pool.execute(callMember);
	}

	public CallMember getCallMember(String username) {
		return callMembers.get(username);
	}

	public void removeCallMember(String username) {
		getCallMember(username).finishCall();
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
