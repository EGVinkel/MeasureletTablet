package measurelet.tablet.Model;

import com.google.firebase.database.Exclude;

import org.threeten.bp.LocalDateTime;

import java.sql.Timestamp;
import java.util.UUID;

public class Weight {

    private double weightKG;
    private String timestamp;
    public String uuid;

    public Weight(){

    }

    public Weight(double weightKG){
        this.weightKG = weightKG;
        this.timestamp = LocalDateTime.now().toString();
        this.uuid = UUID.randomUUID().toString();
    }
    public Weight(double weightKG, String uuid, String timestamp){
        this.weightKG = weightKG;
        this.timestamp = timestamp;
        this.uuid = uuid;
    }

    public double getWeightKG() {
        return weightKG;
    }

    public void setWeightKG(double weightKG) {
        this.weightKG = weightKG;
    }

    @Exclude
    public LocalDateTime getDatetime() {
        return LocalDateTime.parse(timestamp);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
