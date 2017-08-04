package es.dsrroma.garantator.data.model;

public abstract class AbstractModel {
    protected String SEP = ":";

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getClass().getName() + SEP + getId() + SEP + getName() ;
    }
}
