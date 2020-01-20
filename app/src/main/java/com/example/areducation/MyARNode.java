package com.example.areducation;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;



import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.CompletableFuture;

public class MyARNode extends AnchorNode {

    private AugmentedImage image;
    private  static CompletableFuture<ModelRenderable> ModelRenderableCompletableFuture;

    public  MyARNode(Context context, Uri modelId){
        if (ModelRenderableCompletableFuture==null){
            ModelRenderableCompletableFuture=ModelRenderable.builder()
                    .setRegistryId("my_model")
                    .setSource(context, modelId)
                    .build();

        }
    }

    public void setImage(AugmentedImage image){
        this.image=image;
        if(!ModelRenderableCompletableFuture.isDone()){
            CompletableFuture.allOf(ModelRenderableCompletableFuture)
                    .thenAccept((Void aVoid)->{setImage(image);
                    }).exceptionally(throwable -> {
                        return null;
            });
        }

        setAnchor(image.createAnchor(image.getCenterPose()));

        Node node=new Node();
        Pose pose=Pose.makeTranslation(0.0f,0.0f,0.0f);

        node.setParent(this);
        node.setLocalPosition(new Vector3(pose.tx(),pose.ty(),pose.tz()));
        node.setLocalRotation(new Quaternion(pose.qx(),pose.qy(),pose.qz(),pose.qw()));
        node.setRenderable(ModelRenderableCompletableFuture.getNow(null));

    }

    public AugmentedImage getImage() {
        return image;
    }
}
