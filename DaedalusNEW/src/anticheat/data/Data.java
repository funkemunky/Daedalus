package anticheat.data;

import anticheat.detections.Checks;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public class Data {

    private static List<Checks> detections;
    private Player p;

    public Data(Player p) {
        this.p = p;
        detections = new ArrayList<>();
    }

    public static void addDetection(Checks detection) {
        detections.add(detection);
    }

    public static void clearDetections() {
        detections.clear();
    }

    public Player getPlayer() {
        return p;
    }

    public boolean needKick() {
        return getWeight() >= 80;
    }

    public int getWeight() {
        int weight = 0;
        for (int i = 0; i < detections.size(); i++) {
            Checks detection = detections.get(i);
            weight += detection.getWeight();

        }
        return weight;
    }
}
