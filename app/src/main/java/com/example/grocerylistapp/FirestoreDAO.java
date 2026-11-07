package com.example.grocerylistapp;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirestoreDAO { //data access object
    private static final String COLLECTION_NAME = "groceries";
    private ListenerRegistration listenerRegistration;

    public void createDocument(String name, int amount,String whoAdded ,String imgData)
    {
        Map<String, Object> groceryList = new HashMap<>();
        groceryList.put("name", name);
        groceryList.put("amount",amount);
        groceryList.put("whoAdded",whoAdded);
        groceryList.put("imgData", imgData);

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

    public void listenToDocumentChange(String name)
    {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document();
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null && value.exists()){
                    Grocery grocery = value.toObject(Grocery.class);
                    Log.d("CHANGED_DAO", grocery.toString());
                }
            }
        });
    }

    public void listenToAllDocumentChange()
    {
        CollectionReference colRef = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
            listenerRegistration = colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error !=null ||value == null) return;
                for(DocumentChange docC : value.getDocumentChanges()){
                    Grocery grocery = docC.getDocument().toObject(Grocery.class);
                    Log.d("CHANGED_DAO",docC.getType() + " :"  + grocery.toString());
                }
            }
        });
    }

    public void stopListening(){
        if(listenerRegistration!=null){
            listenerRegistration.remove();
            listenerRegistration=null;
        }
    }


}