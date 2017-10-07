package anticheat.user;

import java.util.ArrayList;
import java.util.UUID;

public class UserManager {

	public ArrayList<User> allUsers = new ArrayList<User>();

	public User getUser(UUID uuid) {
		for (User user : allUsers) {
			if (user.getUUID() == uuid) {
				return user;
			}
		}
		return null;
	}

	public void add(User user) {
		allUsers.add(user);
	}

	public void remove(User user) {
		allUsers.remove(user);
	}

}
