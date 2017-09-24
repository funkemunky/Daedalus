package anticheat.detections;

import anticheat.Daedalus;
import anticheat.checks.movement.SpeedA;

import org.bukkit.event.Event;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class ChecksManager {

	private static List<Checks> detections = new ArrayList<>();

	private Daedalus ac;
	public ChecksManager(Daedalus ac) {
	      this.ac = ac;
	}

	public static List<Checks> getDetections() {
		return detections;
	}

	public static Checks getCheckByName(String name) {
		for (Checks check : getDetections()) {
			if (check.getName().equalsIgnoreCase(name)) {
				return check;
			}
		}
		return null;
	}

	public void init() {
		new SpeedA(ac);
	}

	public void event(Event event) {
		for (int i = 0; i < detections.size(); i++) {
			Checks detection = detections.get(i);
			Class<? extends Checks> clazz = detection.getClass();
			if (clazz.isAnnotationPresent(ChecksListener.class)) {
				Annotation annotation = clazz.getAnnotation(ChecksListener.class);
				ChecksListener handler = (ChecksListener) annotation;
				for (Class<?> type : handler.events()) {
					if (type == event.getClass()) {
						detection.onEvent(event);
					}
				}
			}
		}
	}
}
