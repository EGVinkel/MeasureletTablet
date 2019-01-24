package measurelet.tablet.Model;

import com.google.firebase.database.Exclude;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Patient {

    private Boolean checked = false;
    private Boolean marked = true;
    public String uuid;
    private String name;
    private int bedNum;
    private List<Weight> weights = new ArrayList<>();
    private List<Intake> registrations = new ArrayList<>();

    public Patient() {

    }

    public Patient(String name, int bedNum) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.bedNum = bedNum;
    }

    public Patient(String name, int bedNum, String uuid, ArrayList<Intake> registrations, ArrayList<Weight> weights) {
        this.uuid = uuid;
        this.name = name;
        this.bedNum = bedNum;
        this.registrations = registrations;
        this.weights = weights;
    }

    @Exclude
    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBedNum() {
        return bedNum;
    }

    public void setBedNum(int bedNum) {
        this.bedNum = bedNum;
    }

    public List<Weight> getWeights() {
        return weights;
    }

    public void setWeights(List<Weight> weights) {
        this.weights = weights;
    }

    public List<Intake> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Intake> registrations) {
        this.registrations = registrations;
    }
    @Exclude
    public List<Weight> getSortedWeights() {
        weights.removeIf(Objects::isNull);

        //SORT
        Collections.sort(weights, (o1, o2) -> {
            if (o1.getDatetime().isEqual(o2.getDatetime())) {
                return 0;
            }
            return o1.getDatetime().isAfter(o2.getDatetime()) ? -1 : 1;
        });

        return weights;
    }

    @Exclude
    public List<Intake> getSortedRegs() {
        registrations.removeIf(Objects::isNull);

        //SORT
        Collections.sort(registrations, (o1, o2) -> {
            if (o1.getDateTime().isEqual(o2.getDateTime())) {
                return 0;
            }
            return o1.getDateTime().isAfter(o2.getDateTime()) ? -1 : 1;
        });

        return registrations;
    }


    @Exclude
    public List<Intake> getIntakesForDate(LocalDate date) {
        registrations.removeIf(Objects::isNull);
        ArrayList<Intake> intakesCurrentDate = new ArrayList<>();
        for (Intake i : registrations) {
            if (i.getDateTime().getDayOfMonth() == date.getDayOfMonth() && i.getDateTime().getMonthValue() == date.getMonthValue()) {
                intakesCurrentDate.add(i);
            }
        }
        //SORT
        Collections.sort(intakesCurrentDate, (o1, o2) -> {
            if (o1.getDateTime().isEqual(o2.getDateTime())) {
                return 0;
            }
            return o1.getDateTime().isAfter(o2.getDateTime()) ? -1 : 1;
        });

        return intakesCurrentDate;
    }

    @Exclude
    public Weight getWeightForDate(LocalDate date) {
        weights.removeIf(Objects::isNull);
        Weight WeightCurrentDate = null;
        for (Weight i : weights) {
            if (i.getDatetime().getDayOfMonth() == date.getDayOfMonth() && i.getDatetime().getMonthValue() == date.getMonthValue()) {
                WeightCurrentDate = i;
            }
        }


        return WeightCurrentDate;
    }

    public Boolean getMarked() {
        return marked;
    }

    public void setMarked(Boolean marked) {
        this.marked = marked;
    }
}


