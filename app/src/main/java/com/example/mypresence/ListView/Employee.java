package com.example.mypresence.ListView;

public class Employee {
    private String name, come, comelate, gohome, gohomefirst;

    public Employee(String name, String come, String comelate, String gohome, String gohomefirst) {
        this.name = name;
        this.come = come;
        this.comelate = comelate;
        this.gohome = gohome;
        this.gohomefirst = gohomefirst;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCome() {
        return come;
    }

    public void setCome(String come) {
        this.come = come;
    }

    public String getComelate() {
        return comelate;
    }

    public void setComelate(String comelate) {
        this.comelate = comelate;
    }

    public String getGohome() {
        return gohome;
    }

    public void setGohome(String gohome) {
        this.gohome = gohome;
    }

    public String getGohomefirst() {
        return gohomefirst;
    }

    public void setGohomefirst(String gohomefirst) {
        this.gohomefirst = gohomefirst;
    }
}
