package com.google.ar.sceneform.samples.hellosceneform;

import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.time.Instant;

public class Food {
    private String name;
    private Instant exirationDate;
    private int resource;
    private float xOffset;
    private float yOffset;
    private float scale;

    public Food(String name, Instant exirationDate, int resource, float xOffset, float yOffset, float scale) {
        this.name = name;
        this.exirationDate = exirationDate;
        this.resource = resource;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.scale = scale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getExirationDate() {
        return exirationDate;
    }

    public void setExirationDate(Instant exirationDate) {
        this.exirationDate = exirationDate;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3 getOffsetVector() {
        return new Vector3(xOffset, 0, yOffset);
    }
}
