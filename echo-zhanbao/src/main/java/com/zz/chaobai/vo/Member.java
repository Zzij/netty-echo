package com.zz.chaobai.vo;

import org.msgpack.annotation.Message;

import java.io.Serializable;

@Message
public class Member implements Serializable {


    private int age;

    private String name;

    private double salary;

    public Member(int age, String name, double salary) {
        this.age = age;
        this.name = name;
        this.salary = salary;
    }

    public Member(){}

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    @Override
    public String toString() {
        return "Member{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                '}';
    }
}
