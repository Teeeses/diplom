package com.library.example.cifar10.layers;

public interface LayerInterface {
    // the method responsible for the mathematical computations of a layer
    public Object compute(Object input);
}
