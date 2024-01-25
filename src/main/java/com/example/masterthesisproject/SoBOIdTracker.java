package com.example.masterthesisproject;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoBOIdTracker {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String FILE_NAME = "sobo_obj.json";

    public static List<String> loadSoBOIds() {
        try {
            File file = new File(FILE_NAME);
            if (file.exists() && file.length() != 0) {
                return objectMapper.readValue(file, new TypeReference<>() {});
            } else {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            System.err.println("Error loading SoBO IDs from " + FILE_NAME + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void clearSoBOFile() {
        try {
            FileWriter writer = new FileWriter(FILE_NAME, false);
            writer.write(""); // Clearing the content
            writer.close();
        } catch (IOException e) {
            System.err.println("Error clearing SoBO file: " + e.getMessage());
        }
    }

    public static void saveSoBOIds(List<String> soboIds) {
        try {
            objectMapper.writeValue(new File(FILE_NAME), soboIds);
        } catch (IOException e) {
            System.err.println("Error saving SoBO IDs to " + FILE_NAME + ": " + e.getMessage());
        }
    }
    public static void appendSoBOId(String soboId) {
        try {
            List<String> existingIds = loadSoBOIds();
            existingIds.add(soboId);
            saveSoBOIds(existingIds);
        } catch (Exception e) {
            System.err.println("Error appending SoBO ID to " + FILE_NAME + ": " + e.getMessage());
        }
    }
}