package com.example.realestatehub.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Database {
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private final DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Registered Users");
    private final DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference("Users Posts");
    private final DatabaseReference favoritesReference = FirebaseDatabase.getInstance().getReference("Users Favorites");
    private Context context;
    private ReadWriteUserDetails readWriteUserDetails;
    private static final String TAG = "Database";

    public interface GeneralCallback {
        void onSuccess();

        void onFailure(int errorCode, String errorMessage);
    }

    public interface GoogleRegistrationCallback {
        void onSuccess(boolean newUser, HashMap<String, Object> map);

        void onFailure(String errorMessage);
    }

    public interface EmailValidationCallback {
        void onSuccess(boolean valid);
    }

    public interface PostsCallback {
        void onSuccess(HashMap<String, HashMap<String, String>> postList);

        void onFailure(int errorCode, String errorMessage);
    }

    public Database(Context context) {
        this.context = context;
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        readWriteUserDetails = ReadWriteUserDetails.getInstance(context);
    }


    public ReadWriteUserDetails getReadWriteUserDetails() {
        return readWriteUserDetails;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    //=========================================================
    //==========================LogIn==========================
    //=========================================================

    //==========================SignUpActivity==========================

    /**
     * <p>Sign Up process with Google
     *
     * @param firstName   first name
     * @param lastName    last name
     * @param email       email
     * @param password    password
     * @param birthday    birthday
     * @param phoneNumber phone number
     * @param gender      gender
     * @param address     address
     * @param callback    callback
     */
    public void registerUserFromGoogle(String firstName, String lastName, String email, String password, String birthday, String phoneNumber, String gender, String address, GeneralCallback callback) {
        if (firebaseUser != null) {
            //Enter User data into the firebase Realtime Database
            ReadWriteUserDetails writeUserDetails = ReadWriteUserDetails.getInstance(context);
            writeUserDetails.setFirstName(firstName);
            writeUserDetails.setLastName(lastName);
            writeUserDetails.setEmail(email);
            writeUserDetails.setPassword(password);
            writeUserDetails.setBirthday(birthday);
            writeUserDetails.setPhoneNumber(phoneNumber);
            writeUserDetails.setAddress(address);
            writeUserDetails.setGender(gender);
            writeUserDetails.setId(firebaseUser.getUid());

            // Store user data into Firebase Realtime Database under "Registered Users" node
            usersReference.child(firebaseUser.getUid()).setValue(writeUserDetails)
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            // Registration successful
                            callback.onSuccess();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onFailure(0, "Failed to store user data: " + e.getMessage());
                        }
                    });
        } else {
            callback.onFailure(1, "User is null");
        }
    }

    /**
     * <p>Regular SignUp with Email and Password
     *
     * @param firstName   first name
     * @param lastName    last name
     * @param email       email
     * @param password    password
     * @param birthday    birthday
     * @param phoneNumber phone number
     * @param gender      gender
     * @param address     address
     * @param callback    callback
     */
    public void registerUser(String firstName, String lastName, String email, String password, String birthday, String phoneNumber, String gender, String address, GeneralCallback callback) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "User has been registered successfully", Toast.LENGTH_SHORT).show();
                firebaseUser = auth.getCurrentUser();
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(firstName + " " + lastName).build();
                firebaseUser.updateProfile(profileChangeRequest);

                //Enter User data into the firebase Realtime Database
                ReadWriteUserDetails writeUserDetails = ReadWriteUserDetails.getInstance(context);
                writeUserDetails.setFirstName(firstName);
                writeUserDetails.setLastName(lastName);
                writeUserDetails.setEmail(email);
                writeUserDetails.setPassword(password);
                writeUserDetails.setBirthday(birthday);
                writeUserDetails.setPhoneNumber(phoneNumber);
                writeUserDetails.setAddress(address);
                writeUserDetails.setGender(gender);
                writeUserDetails.setId(firebaseUser.getUid());

                //Extracting User reference from Database from "Registered Users"
                usersReference.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseUser.sendEmailVerification();
                            callback.onSuccess();
                        } else {
                            callback.onFailure(0, "User registration failed. Please try again");
                        }
                    }
                });

            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    callback.onFailure(1, "Your password should be at least 6 digits");
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    callback.onFailure(2, "Valid email is required or already in use");
                } catch (FirebaseAuthUserCollisionException e) {
                    callback.onFailure(3, "Email is already registered");
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    callback.onFailure(0, "User registration failed: " + e.getMessage());
                }
            }
        });
    }

    //==========================ConnectingActivity==========================

    /**
     * <p>Login with Email and Password
     *
     * @param email    email
     * @param password password
     * @param callback callback
     */
    public void login(String email, String password, GeneralCallback callback) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseUser = auth.getCurrentUser();
                    checkVerification(callback);
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        callback.onFailure(1, "Email doesn't exist. Please register again.");
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        callback.onFailure(2, "Email and password doesn't match. Please re-enter again.");
                    } catch (Exception e) {
                        callback.onFailure(0, e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * <p>Check if email is verified
     *
     * @param callback callback
     */
    public void checkVerification(GeneralCallback callback) {
        if (firebaseUser.isEmailVerified()) {
            updateReadWriteUserDetails();
            callback.onSuccess();
        } else {
            callback.onFailure(3, "Please verify your email");
            firebaseUser.sendEmailVerification();
            auth.signOut();
        }
    }

    /**
     * <p>Update ReadWriteUserDetails
     */
    private void updateReadWriteUserDetails() {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        String uid = firebaseUser.getUid();
        usersReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails userDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (userDetails != null) {
                    readWriteUserDetails = ReadWriteUserDetails.getInstance(context);
                    // Update the fields of the userDetails object
                    readWriteUserDetails.setFirstName(snapshot.child("firstName").getValue(String.class));
                    readWriteUserDetails.setLastName(snapshot.child("lastName").getValue(String.class));
                    readWriteUserDetails.setEmail(snapshot.child("email").getValue(String.class));
                    readWriteUserDetails.setBirthday(snapshot.child("birthday").getValue(String.class));
                    readWriteUserDetails.setPhoneNumber(snapshot.child("phoneNumber").getValue(String.class));
                    readWriteUserDetails.setAddress(snapshot.child("address").getValue(String.class));
                    readWriteUserDetails.setGender(snapshot.child("gender").getValue(String.class));
                    readWriteUserDetails.setPassword(snapshot.child("password").getValue(String.class));
                    readWriteUserDetails.setPurpose(snapshot.child("purpose").getValue(String.class));
                    readWriteUserDetails.setId(snapshot.child("id").getValue(String.class));
                    // Save the updated userDetails object
                    readWriteUserDetails.saveUserDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * <p>Sign in for an existing Google user or Sign up new user
     *
     * @param idToken  idToken
     * @param callback callback
     */
    public void firebaseAuthWithGoogle(String idToken, GoogleRegistrationCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    short epsTime = 10000;
                    long dateCreate = Objects.requireNonNull(user.getMetadata()).getCreationTimestamp();
                    long currTime = System.currentTimeMillis();

                    HashMap<String, Object> map = new HashMap<>();
                    String[] parts = user.getDisplayName().split(" ");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < parts.length; i++) {
                        sb.append(parts[i]);
                    }
                    map.put("firstName", parts[0].toString());
                    map.put("lastName", sb.toString());
                    map.put("profilePic", user.getPhotoUrl().toString());
                    map.put("email", user.getEmail());

                    boolean isNewUser = (currTime < dateCreate + epsTime);

                    if (isNewUser) {
                        callback.onSuccess(true, map);

                    } else {
                        updateReadWriteUserDetails();
                        callback.onSuccess(false, null);
                    }
                } else {
                    callback.onFailure(task.getException().getMessage());
                }
            }
        });
    }

    //==========================ForgotPasswordActivity==========================

    /**
     * <p>Check if email is valid or not
     *
     * @param email    email
     * @param callback callback
     */
    public void sendPasswordRestLink(String email, final GeneralCallback callback) {
        checkEmailValidation(email, new EmailValidationCallback() {
            @Override
            public void onSuccess(boolean valid) {
                if (valid) {
                    // Email is valid, continue with password reset
                    auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                callback.onSuccess();
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    callback.onFailure(1, "User doesn't exist.\nPlease register again.");
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                    callback.onFailure(2, e.getMessage());
                                }
                                callback.onFailure(3, "Failed to send Email");
                            }
                        }
                    });
                } else {
                    callback.onFailure(4, "Email doesn't exist");
                }
            }

        });

    }


    /**
     * <p>Check if email is valid or not
     *
     * @param email    email
     * @param callback callback
     */
    private void checkEmailValidation(String email, EmailValidationCallback callback) {
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isValidEmail = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userEmail = userSnapshot.child("email").getValue(String.class);
                    if (userEmail != null && userEmail.equals(email)) {
                        isValidEmail = true;
                        break;
                    }
                }
                callback.onSuccess(isValidEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    //==========================SetIntentActivity==========================

    /**
     * <p>Set purpose for user [Seller, Buyer, Seller and Buyer]
     *
     * @param fromGoogle fromGoogle
     * @param callback   callback
     */
    public void setPurpose(boolean fromGoogle, final GeneralCallback callback) {
        if (fromGoogle) {
            if (firebaseUser != null) {
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("purpose", readWriteUserDetails.getPurpose());
                usersReference.child(firebaseUser.getUid()).updateChildren(userData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                callback.onSuccess();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                callback.onFailure(0, "User registration failed. Please try again");
                            }
                        });
            }
        } else {
            usersReference.child(firebaseUser.getUid()).setValue(readWriteUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(1, "User registration failed. Please try again");
                    }
                }
            });
        }
    }


    //=================================================================
    //==========================HomeFragments==========================
    //=================================================================
    private HashMap<String, HashMap<String, String>> postList = new HashMap<>();
    private List<HashMap<String, Object>> userList = new ArrayList<>();

    //==========================HomeFragment==========================

    /**
     * <p>Read user data from Firebase
     *
     * @param callback callback
     */
    public void readUsersData(GeneralCallback callback) {
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear(); // Clear existing data to avoid duplicates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        HashMap<String, Object> userMap = (HashMap<String, Object>) snapshot.getValue();
                        if (readWriteUserDetails.getPurpose().equals("Seller")) {
                            if (!userMap.get("id").equals(readWriteUserDetails.getId())) continue;
                            else {
                                userMap.put("id", snapshot.getKey()); // Add user ID to the map
                                userList.add(userMap); // Add the map to the list of users
                                break;
                            }
                        }
                        if (userMap != null) {
                            userMap.put("id", snapshot.getKey()); // Add user ID to the map
                            userList.add(userMap); // Add the map to the list of users
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // Handle any exceptions
                    }
                }
                callback.onSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(0, "Failed to read user value: " + error.getMessage());
            }
        });
    }


    /**
     * <p>Read posts data from Firebase
     *
     * @param callback callback
     */
    public void readPostsData(PostsCallback callback) {
        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear(); // Clear existing data to avoid duplicates
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (readWriteUserDetails.getPurpose().equals("Seller") && !userSnapshot.getKey().equals(readWriteUserDetails.getId())) {
                        continue; // Skip if the purpose is "Seller" but user ID doesn't match
                    }
                    for (DataSnapshot postSnapshot : userSnapshot.getChildren()) {
                        HashMap<String, String> postDetails = (HashMap<String, String>) postSnapshot.child("Property Details").getValue();
                        if (postDetails == null) continue; // Skip if no post details are found
                        String userId = userSnapshot.getKey();
                        String postId = postSnapshot.getKey();

                        if (callback.toString().contains("Favorite")) {
                            if (!favoriteMap.contains(postId)) {
                                continue;
                            }
                        }
                        // Match user ID with the post and add user details to post details
                        for (HashMap<String, Object> userMap : userList) {
                            if (userMap.get("id").equals(userId)) {
                                postDetails.put("userName", userMap.get("firstName") + " " + userMap.get("lastName"));
                                postDetails.put("phoneNumber", userMap.get("phoneNumber") + "");
                                break;
                            }
                        }
                        // Add first photo URL to post details, if available
                        DataSnapshot photosSnapshot = postSnapshot.child("Photos");
                        if (photosSnapshot.exists()) {
                            for (DataSnapshot photoSnapshot : photosSnapshot.getChildren()) {
                                postDetails.put("photoUrl", photoSnapshot.getValue(String.class));
                                break;
                            }
                        }
                        // Add the post details map to the list of posts
                        postList.put(postSnapshot.getKey(), postDetails);
                    }
                }
                callback.onSuccess(postList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(0, "Failed to read post value: " + error.getMessage());
            }
        });
    }


    /**
     * <p>Check if the current user exists in the database
     *
     * @param callback callback
     */
    public void CheckCurrUserIfExists(GeneralCallback callback) {
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            usersReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails UserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    if (UserDetails != null) {
                        callback.onSuccess();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onFailure(0, "Something Went Wrong!");
                }
            });
        }
    }

    //==========================FavoriteFragment==========================
    private List<String> favoriteMap = new ArrayList<>();


    /**
     * <p>Shows the favorite posts in FavoriteFragment
     *
     * @param callback callback
     */
    public void updateFavoriteData(PostsCallback callback) {
        String uid = firebaseUser.getUid();
        favoritesReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        HashMap<String, String> favMap = (HashMap<String, String>) snapshot.getValue();
                        if (favMap != null) {
                            favoriteMap.add(favMap.get("Post Id"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                readPostsData(callback);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}


