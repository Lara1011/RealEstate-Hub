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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realestatehub.HomeFragments.ProfileFragmentLayouts.InsideProfileFragment;
import com.example.realestatehub.Utils.Database;
import com.example.realestatehub.Utils.Language;
import com.example.realestatehub.LogIn.ConnectingActivity;
import com.example.realestatehub.R;
import com.example.realestatehub.HomeFragments.ProfileFragmentLayouts.RecentlyReachedFragment;
import com.example.realestatehub.HomeFragments.ProfileFragmentLayouts.RecentlySearchedFragment;
import com.example.realestatehub.HomeFragments.ProfileFragmentLayouts.RecentlyViewedFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private ImageView userImageView;
    private TextView UserFullNameTextView;
    private RelativeLayout RecentlyViewedLayout, RecentlyReachedLayout, RecentlySearchedLayout,
            ProfileViewedLayout,
            LanguageLayout, InviteFriendsLayout, LogoutViewedLayout;
    private View view;
    private Uri uriImage;
    private Database database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        database = new Database(getContext());
        database.getFirebaseUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
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
        database.CheckCurrUserIfExists(new Database.GeneralCallback() {
            @Override
            public void onSuccess() {
                UserFullNameTextView.setText(String.format("Welcome\n" + database.getReadWriteUserDetails().getFirstName() + " " + database.getReadWriteUserDetails().getLastName()));
                uriImage = database.getFirebaseUser().getPhotoUrl();
                Picasso.get().load(uriImage).into(userImageView);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (errorCode == 0) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initUI() {
        userImageView = view.findViewById(R.id.userImageView);
        UserFullNameTextView = view.findViewById(R.id.UserFullNameTextView);

        RecentlyViewedLayout = view.findViewById(R.id.RecentlyViewedLayout);
        RecentlyReachedLayout = view.findViewById(R.id.RecentlyReachedLayout);
        RecentlySearchedLayout = view.findViewById(R.id.RecentlySearchedLayout);
        ProfileViewedLayout = view.findViewById(R.id.InsideProfileLayout);
        InviteFriendsLayout = view.findViewById(R.id.InviteFriendsLayout);
        LanguageLayout = view.findViewById(R.id.LanguageLayout);
        LogoutViewedLayout = view.findViewById(R.id.LogoutLayout);

        RecentlyViewedLayout.setOnClickListener(this);
        RecentlyReachedLayout.setOnClickListener(this);
        RecentlySearchedLayout.setOnClickListener(this);
        ProfileViewedLayout.setOnClickListener(this);
        InviteFriendsLayout.setOnClickListener(this);
        LanguageLayout.setOnClickListener(this);
        LogoutViewedLayout.setOnClickListener(this);

        database = new Database(getContext());

        if (database.getReadWriteUserDetails().getPurpose().equals("Seller")) {
            RecentlyViewedLayout.setVisibility(View.GONE);
            RecentlySearchedLayout.setVisibility(View.GONE);
            // Add margin top to RecentlyReachedLayout
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) RecentlyReachedLayout.getLayoutParams();
            params.setMargins(0, 10, 0, 10); // Set your desired margin value here
            RecentlyReachedLayout.setLayoutParams(params);
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
        } else if (id == R.id.LanguageLayout) {
            showChangeLanguage();
        } else if (id == R.id.InviteFriendsLayout) {
            showInviteFriends();
        } else if (id == R.id.LogoutLayout) {
            database.getAuth().signOut();
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
                    Language.setLocale(getContext(), "en");
                    Language.recreateActivity(getActivity(), HomeBottomNavigation.class);
                } else if (which == 1) {
                    Language.setLocale(getContext(), "iw");
                    Language.recreateActivity(getActivity(), HomeBottomNavigation.class);
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
}
