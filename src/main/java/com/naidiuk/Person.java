package com.naidiuk;

@Table(name = "person")
public class Person {
    @Column
    @Id
    private int id;
    @Column(name = "person_name")
    private String name;
    @Column
    private double salary;

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

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
