package com.example.masterthesisproject;


import com.example.masterthesisproject.entities.Edge;
import com.example.masterthesisproject.entities.SoBO;

import java.util.*;

public class SoBOGenerator {

    private static final List<String> KEY_NAMES = Arrays.asList("name", "email", "age", "outlook", "yahoo");
    private static final List<String> EDGE_TYPES = Arrays.asList("RELATED_TO", "FRIENDS_WITH", "WORKS_WITH");
    private static final Random RANDOM = new Random();
    private static final List<SoBO> GENERATED_SoBOs = new ArrayList<>();

    public static SoBO generateRandomSoBO() {
        List<String> idKeys = new ArrayList<>(KEY_NAMES.subList(0, RANDOM.nextInt(KEY_NAMES.size()) + 1));
        SoBO sobo = new SoBO(idKeys);

        for (String key : idKeys) {
            Object value = getRandomValue();
            sobo.addProperty(key, value);
        }

        sobo.addProperty("age", RANDOM.nextInt(100));
        sobo.addProperty("isActive", RANDOM.nextBoolean());

        GENERATED_SoBOs.add(sobo);
        return sobo;
    }

    private static Object getRandomValue() {
        int type = RANDOM.nextInt(3);
        switch (type) {
            case 0:
                return UUID.randomUUID().toString();
            case 1:
                return RANDOM.nextInt(1000);
            case 2:
                return RANDOM.nextBoolean();
        }
        return null;
    }


    public static Edge generateRandomEdge() {
        if (GENERATED_SoBOs.size() < 2) {
            throw new IllegalStateException("At least two SoBO objects must be created before generating an edge");
        }

        SoBO sobo1 = getRandomSoBO();
        SoBO sobo2 = null;
        do {
            sobo2 = getRandomSoBO();
        } while (sobo2.getId().equals(sobo1.getId())); // the two SoBOs are different

        String edgeType = EDGE_TYPES.get(RANDOM.nextInt(EDGE_TYPES.size()));

        return new Edge(sobo1, sobo2, edgeType);
    }

    public static SoBO getRandomSoBO() {
        if (GENERATED_SoBOs.isEmpty()) {
            // if no SoBOs have been generated yet, generate one and return it
            return generateRandomSoBO();
        }
        return GENERATED_SoBOs.get(RANDOM.nextInt(GENERATED_SoBOs.size()));
    }
    public static void removeSoBO(SoBO sobo) {
        GENERATED_SoBOs.remove(sobo);
    }

}