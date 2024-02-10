package com.example.realestatehub.HomeFragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realestatehub.FillDetails.ReadWriteUserDetails;
import com.example.realestatehub.HomeFragments.ProfileFragmentLayouts.InsideProfileFragment;
import com.example.realestatehub.HomeFragments.ProfileFragmentLayouts.LanguageUtils;
import com.example.realestatehub.LogIn.ConnectingActivity;
import com.example.realestatehub.HomeFragments.ProfileFragmentLayouts.NotificationFragment;
import com.example.realestatehub.HomeFragments.ProfileFragmentLayouts.PaymentsFragment;
import com.example.realestatehub.R;
import com.example.realestatehub.HomeFragments.ProfileFragmentLayouts.RecentlyReachedFragment;
import com.example.realestatehub.HomeFragments.ProfileFragmentLayouts.RecentlySearchedFragment;
import com.example.realestatehub.HomeFragments.ProfileFragmentLayouts.RecentlyViewedFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private ImageView userImageView;
    private TextView UserFullNameTextView;
    private RelativeLayout RecentlyViewedLayout, RecentlyReachedLayout, RecentlySearchedLayout,
            ProfileViewedLayout, PaymentViewedLayout, NotificationLayout,
            LanguageLayout, InviteFriendsLayout, LogoutViewedLayout;
    private View view;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private Uri uriImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateProfileUI();
                } else {
                    Toast.makeText(getContext(), "Failed to refresh user profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateProfileUI() {
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            reference = FirebaseDatabase.getInstance().getReference("Registered Users");
            reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    if (readWriteUserDetails != null) {
                        UserFullNameTextView.setText(String.format("Welcome\n" + readWriteUserDetails.getFirstName() + " " + readWriteUserDetails.getLastName()));
                        //userImageView
                        uriImage = firebaseUser.getPhotoUrl();
                        Picasso.get().load(uriImage).into(userImageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initUI() {
        userImageView = view.findViewById(R.id.userImageView);
        UserFullNameTextView = view.findViewById(R.id.UserFullNameTextView);

        RecentlyViewedLayout = view.findViewById(R.id.RecentlyViewedLayout);
        RecentlyReachedLayout = view.findViewById(R.id.RecentlyReachedLayout);
        RecentlySearchedLayout = view.findViewById(R.id.RecentlySearchedLayout);
        ProfileViewedLayout = view.findViewById(R.id.InsideProfileLayout);
        PaymentViewedLayout = view.findViewById(R.id.PaymentsLayout);
        NotificationLayout = view.findViewById(R.id.NotificationLayout);
        InviteFriendsLayout = view.findViewById(R.id.InviteFriendsLayout);
        LanguageLayout = view.findViewById(R.id.LanguageLayout);
        LogoutViewedLayout = view.findViewById(R.id.LogoutLayout);

        RecentlyViewedLayout.setOnClickListener(this);
        RecentlyReachedLayout.setOnClickListener(this);
        RecentlySearchedLayout.setOnClickListener(this);
        ProfileViewedLayout.setOnClickListener(this);
        PaymentViewedLayout.setOnClickListener(this);
        NotificationLayout.setOnClickListener(this);
        InviteFriendsLayout.setOnClickListener(this);
        LanguageLayout.setOnClickListener(this);
        LogoutViewedLayout.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.RecentlyViewedLayout) {
            replaceFragment(new RecentlyViewedFragment());
        } else if (id == R.id.RecentlyReachedLayout) {
            replaceFragment(new RecentlyReachedFragment());
        } else if (id == R.id.RecentlySearchedLayout) {
            replaceFragment(new RecentlySearchedFragment());
        } else if (id == R.id.InsideProfileLayout) {
            replaceFragment(new InsideProfileFragment());
        } else if (id == R.id.PaymentsLayout) {
            replaceFragment(new PaymentsFragment());
        } else if (id == R.id.NotificationLayout) {
            replaceFragment(new NotificationFragment());
        } else if (id == R.id.LanguageLayout) {
            showChangeLanguage();
        } else if (id == R.id.InviteFriendsLayout) {
            showInviteFriends();
        } else if (id == R.id.LogoutLayout) {
            auth.signOut();
            Intent intent = new Intent(getContext(), ConnectingActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showInviteFriends() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Check This App On Play Store - Real Estate Hub");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.example.realestatehub");
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void showChangeLanguage() {
        final String[] listItems = {"English", "Hebrew"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Choose Language..");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    LanguageUtils.setLocale(getContext(), "en");
                    LanguageUtils.recreateActivity(getActivity(), HomeBottomNavigation.class);
                } else if (which == 1) {
                    LanguageUtils.setLocale(getContext(), "iw");
                    LanguageUtils.recreateActivity(getActivity(), HomeBottomNavigation.class);
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
}
