package com.library.example.cifar10;

/**
 * Created by develop on 30.05.2017.
 */

public class ResponseOption {

    private String name;
    private float option;

    public ResponseOption(String name, float option) {
        this.name = name;
        this.option = option;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getOption() {
        return option;
    }

    public void setOption(float option) {
        this.option = option;
    }
}
