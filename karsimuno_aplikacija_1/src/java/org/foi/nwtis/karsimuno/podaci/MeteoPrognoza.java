package org.foi.nwtis.karsimuno.podaci;

/**
 *
 * @author Karlo
 */
public class MeteoPrognoza {

    private int id;
    private int dan;
    private MeteoPodaci prognoza;

    public MeteoPrognoza() {
    }

    public MeteoPrognoza(int id, int dan, MeteoPodaci prognoza) {
        this.id = id;
        this.dan = dan;
        this.prognoza = prognoza;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDan() {
        return dan;
    }

    public void setDan(int dan) {
        this.dan = dan;
    }

    public MeteoPodaci getPrognoza() {
        return prognoza;
    }

    public void setPrognoza(MeteoPodaci prognoza) {
        this.prognoza = prognoza;
    }

}
