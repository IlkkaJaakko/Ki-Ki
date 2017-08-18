package com.bricenangue.insyconn.ki_ki;

/**
 * Created by bricenangue on 01.06.17.
 */

public class UserPersonalData {
    private double size, weight;
    private int age, activity_level;
    private boolean gender;


    public UserPersonalData() {

    }

    public UserPersonalData(double size, double weight, int age, int activity_level, boolean gender) {
        this.size = size;
        this.weight = weight;
        this.age = age;
        this.activity_level = activity_level;
        this.gender = gender;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getActivity_level() {
        return activity_level;
    }

    public void setActivity_level(int activity_level) {
        this.activity_level = activity_level;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }
}
