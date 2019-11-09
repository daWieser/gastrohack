package com.google.ar.sceneform.samples.hellosceneform;

import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class FoodNode extends TransformableNode {

    private boolean selected = false;

    public void selected(){
        selected = !selected;

    }

    public FoodNode(TransformationSystem transformationSystem) {
        super(transformationSystem);
    }

}
