package measurelet.tablet;

public class Testpatient {
    private Boolean checked;
    private int bednumber;


    Testpatient(Boolean checked, int bednumber){
        this.checked=checked;
        this.bednumber=bednumber;

    }
    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }


    public int getBednumber() {
        return bednumber;
    }

    public void setBednumber(int bednumber) {
        this.bednumber = bednumber;
    }

}
