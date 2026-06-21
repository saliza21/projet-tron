package com.tronai.algo;


public class AlgoFactory {
    public static IAI createAlgorithm(String type) {
        switch (type.toLowerCase()) {
            case "maxn":
                return new MaxN();
            case "paranoid":
                return new Paranoid();
            case "sos":
                return new SOS();
            default:
                throw new IllegalArgumentException("Unknown algorithm type: " + type);
        }
    }
}
