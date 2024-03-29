/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.samples.hellosceneform;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
    private static final String TAG = HelloSceneformActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;

    private HashMap<Integer, ModelRenderable> renderableMep = new HashMap();

    private ViewRenderable dateRenderable;

    private ArrayList<Food> foods = new ArrayList<>();
    private boolean isFirst = true;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        foods.add(new Food("Apple", Instant.now(), R.raw.andy, 0.0f, 0.0f, 1));
        foods.add(new Food("Orange", Instant.now(), R.raw.egg, 0.0f, 0.4f, 0.2f));
        foods.add(new Food("Milk", Instant.now(), R.raw.watermelon, 0.4f, 0.0f, 0.2f));
        foods.add(new Food("Chicken", Instant.now(), R.raw.chicken, 0.4f, 0.4f, 0.2f));


        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_ux);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        ModelRenderable.builder()
                .setSource(this, R.raw.andy)
                .build()
                .thenAccept(renderable -> renderableMep.put(R.raw.andy, renderable));

        ModelRenderable.builder()
                .setSource(this, R.raw.egg)
                .build()
                .thenAccept(renderable -> renderableMep.put(R.raw.egg, renderable));

        ModelRenderable.builder()
                .setSource(this, R.raw.watermelon)
                .build()
                .thenAccept(renderable -> renderableMep.put(R.raw.watermelon, renderable));

        ModelRenderable.builder()
                .setSource(this, R.raw.chicken)
                .build()
                .thenAccept(renderable -> renderableMep.put(R.raw.chicken, renderable));

        ViewRenderable.builder()
                .setView(this, R.layout.expiration_date)
                .build()
                .thenAccept(renderable -> dateRenderable = renderable);

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

                    if (!isFirst) return;
                    isFirst = false;
                    for (Food food : foods) {
                        // Create the Anchor.
                        Anchor anchor = hitResult.createAnchor();
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arFragment.getArSceneView().getScene());

                        // Create the transformable andy and add it to the anchor.
                        FoodNode andy = new FoodNode(arFragment.getTransformationSystem());
                        andy.setParent(anchorNode);
                        andy.setRenderable(renderableMep.get(food.getResource()));
                        andy.getScaleController().setMinScale(food.getScale());
                        andy.getScaleController().setMaxScale(food.getScale() + 0.001f);
                        andy.setLocalPosition(Vector3.add(andy.getLocalPosition(), food.getOffsetVector()));
                        andy.setOnTapListener((hitTestResult,motionEvent1) ->{andy.selected();});

                        Node textNode = new TransformableNode(arFragment.getTransformationSystem());
                        textNode.setRenderable(dateRenderable);
                        textNode.setLocalPosition(Vector3.add(textNode.getLocalPosition(),new Vector3(0.1f,0.2f,0.1f)));
                        textNode.setParent(anchorNode);
                    }

                });
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
}
