package com.example.imagepro;

public class WordsCounter {

    private double alphaCount;
    private double numCount;
    private double greetCount;

    public WordsCounter(double alphaCount, double numCount, double greetCount) {
        this.alphaCount = alphaCount;
        this.numCount = numCount;
        this.greetCount = greetCount;
    }

    public double getAlphaCount() {
        return alphaCount;
    }

    public void setAlphaCount(double alphaCount) {
        this.alphaCount = alphaCount;
    }

    public double getNumCount() {
        return numCount;
    }

    public void setNumCount(double numCount) {
        this.numCount = numCount;
    }

    public double getGreetCount() {
        return greetCount;
    }

    public void setGreetCount(double greetCount) {
        this.greetCount = greetCount;
    }
}
