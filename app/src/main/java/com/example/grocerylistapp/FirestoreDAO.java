package com.example.grocerylistapp;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirestoreDAO { //data access object
    private static final String COLLECTION_NAME = "groceries";

    public void createDocument(String name, int amount)
    {
        Map<String, Object> groceryList = new HashMap<>();
        groceryList.put("name", name);
        groceryList.put("amount",amount);

        DocumentReference docRef = FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(name);
        docRef.set(groceryList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DAR_FIREBASE","SUCCESSFUL_WRITE");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DAR_FIREBASE",e.getMessage().toString());
            }
        });

    }

    public void readDocumentById(String docID, IFirestoreCallback IFirestoreCallback)
    {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(docID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("DAR_FIREBASE","SUCCESSFUL_READ");
                Grocery grocery = documentSnapshot.toObject(Grocery.class);


                ArrayList<Grocery> groceries = new ArrayList<>();
                if(grocery!=null) {
                    groceries.add(grocery);
                }
                IFirestoreCallback.onDataReceived(groceries);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DAR_FIREBASE",e.getMessage().toString());

            }
        });

    }

        public void readAllDocuments(IFirestoreCallback IFirestoreCallback)
        {
            CollectionReference colRef = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
            colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    ArrayList<Grocery> groceries = new ArrayList<>();
                    for(DocumentSnapshot docSS : queryDocumentSnapshots.getDocuments()){
                        Grocery grocery = docSS.toObject(Grocery.class);
                        groceries.add(grocery);
                        Log.d("DAR_FIREBASE","grocery: " + grocery);
                    }
                    IFirestoreCallback.onDataReceived(groceries);
                }

            });
        }
    public void updateDocument(String docID, int newAmount)
    {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(docID);
        docRef.update("amount",newAmount).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DAR_FIREBASE","new amount: " + newAmount);
            }
        });
    }

    public void deleteDocument(String docName){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(docName);

        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DAR_FIREBASE","deleted: " + docName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DAR_FIREBASE", e.getMessage());
            }
        });
    }
}