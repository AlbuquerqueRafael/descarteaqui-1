package com.descarteaqui.descarteaqui.controllers;

import android.content.Context;

import com.descarteaqui.descarteaqui.model.Petition;
import com.descarteaqui.descarteaqui.database.PetitionsDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel on 16/09/2016.
 */
public abstract class PetitionController {

    private static PetitionsDB PetitionsDB;

    public static void createPetition(Context ctx, Petition petition) {
        PetitionsDB = new PetitionsDB(ctx);

        PetitionsDB.addPetition(petition);

    }

    public static void updatePetition(Context ctx, Petition petition){
        PetitionsDB = new PetitionsDB(ctx);

        PetitionsDB.updatePetition(petition);

    }

    public static List<Petition> getAllPetitions(Context ctx, String creatorEmail){
        List<Petition> petitions = new ArrayList<>();

        PetitionsDB = new PetitionsDB(ctx);

        List<Petition> allPetitions = PetitionsDB.getPetitions();

        for (Petition petition : allPetitions) {
            if (!petition.getCreator().equals(creatorEmail)){
                petitions.add(petition);
            }
        }

        return petitions;
    }

    public static List<Petition> getMyPetitions(Context ctx, String creatorEmail){
        List<Petition> petitions = new ArrayList<>();

        PetitionsDB = new PetitionsDB(ctx);

        List<Petition> allPetitions = PetitionsDB.getPetitions();

        for (Petition petition : allPetitions) {
            if (petition.getCreator().equals(creatorEmail)){
                petitions.add(petition);
            }
        }

        return petitions;
    }

    public static int getLastID(Context ctx){
        PetitionsDB = new PetitionsDB(ctx);

        int lastID = PetitionsDB.getLastID();

        return lastID;
    }


}
