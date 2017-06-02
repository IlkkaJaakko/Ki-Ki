package com.bricenangue.insyconn.ki_ki;

/**
 * Created by bricenangue on 01.06.17.
 */

public class UserPersonalData {
    private float size, weight;
    private int age, activity_level;
    private boolean gender;


    public UserPersonalData() {

    }

    public UserPersonalData(float size, float weight, int age, int activity_level, boolean gender) {
        this.size = size;
        this.weight = weight;
        this.age = age;
        this.activity_level = activity_level;
        this.gender = gender;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
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
