package model.group;

import java.util.HashSet;
import java.util.Set;

public class Group {
	private final String name;
	private final Set<String> members;

	public Group(String name) {
		this.name = name;
		this.members = new HashSet<>();
	}

	public void addMember(String username) {
		members.add(username);
	}

	public void removeMember(String username) {
		members.remove(username);
	}

	public boolean isMember(String username) {
		return members.contains(username);
	}

	public String getName() {
		return name;
	}

	public Set<String> getMembers() {
		return new HashSet<>(members);
	}
}