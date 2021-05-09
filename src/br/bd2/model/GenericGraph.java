package br.bd2.model;

import java.util.ArrayList;
import java.util.List;

public class GenericGraph {
    private String algRelational;
    private String type;
    private List<GenericGraph> genericGraphList = null;

    public GenericGraph(String algRelational, String type) {
        this.algRelational = algRelational;
        this.type = type;
    }

    public String getAlgRelational() {
        return algRelational;
    }

    public void setAlgRelational(String algRelational) {
        this.algRelational = algRelational;
    }

    public List<GenericGraph> getGenericGraphList() {
        return genericGraphList;
    }

    public void addGenericGraphList(GenericGraph genericGraph) {
        if (this.genericGraphList == null) this.genericGraphList = new ArrayList<>();
        this.genericGraphList.add(genericGraph);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
