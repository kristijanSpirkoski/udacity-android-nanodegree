package com.example.dough.firebase;

import java.util.ArrayList;

public class FirebaseConstants {
    public final static String USERS_KEY = "users";
    public final static String TRANSACTIONS_KEY = "transactions";
    public final static String CATEGORIES_KEY = "categories";
    public final static String SCHEDULED_KEY = "scheduled";

    public final static String EXTRA_CATEGORIES_KEY = "user_categories";

    public static final int RC_SIGN_IN = 1;

    public static final String[] defaultCategories = {
            "Food",
            "Entertainment",
            "Bills",
            "Transportation",
            "Clothing",
            "Insurance",
            "Necessity",
            "Education",
            "Travel"
    };
}
