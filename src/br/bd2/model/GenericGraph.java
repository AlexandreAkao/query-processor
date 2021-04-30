package br.bd2.model;

import java.util.ArrayList;
import java.util.List;

public class GenericGraph {
    private String algRelational;
    private List<GenericGraph> genericGraphList = null;

    public GenericGraph(String algRelational) {
        this.algRelational = algRelational;
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
}
