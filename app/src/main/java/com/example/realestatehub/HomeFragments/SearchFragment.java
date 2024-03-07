package com.example.realestatehub.HomeFragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realestatehub.HomeFragments.ReadPostAdapter.PostAdapter;
import com.example.realestatehub.HomeFragments.ReadPostAdapter.PostDetailsActivity;
import com.example.realestatehub.R;
import com.example.realestatehub.Utils.Database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView recyclerView;
    private HashMap<String, HashMap<String, String>> postList;
    private HashMap<String, HashMap<String, String>> filteredList;
    private String selectedQuery = "";
    private SearchView searchView;
    private TextView filterTextView;
    private Database database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        initUI();
        readUsersAndPosts();
        initSearch();

        return view;
    }

    private void initUI() {
        postList = new HashMap<>();
        filteredList = new HashMap<>();

        searchView = view.findViewById(R.id.searchEditText);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        filterTextView = view.findViewById(R.id.filterTextView);

        filterTextView.setOnClickListener(this);

        database = new Database(getContext());
    }

    private void initSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                selectedQuery = query;
                addToReSearchDB();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                selectedQuery = newText;
                String searchText = newText.trim().toLowerCase();
                applySearchFilter(searchText);
                return true;
            }
        });
    }

    private void applySearchFilter(String query) {
        filteredList.clear();
        int i = 0;
        for (HashMap<String, String> post : postList.values()) {
            if (post.get("Name").toLowerCase().contains(query.toLowerCase()) && !query.isEmpty()) {
                filteredList.put(String.valueOf(i++), post);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
        }
        PostAdapter adapter = new PostAdapter(filteredList);
        recyclerView.setAdapter(adapter);
    }

    private void readUsersAndPosts() {
        database.readUsersData(new Database.GeneralCallback() {
            @Override
            public void onSuccess() {
                database.readPostsData(new Database.PostsCallback() {
                    @Override
                    public void onSuccess(HashMap<String, HashMap<String, String>> postList) {
                        SearchFragment.this.postList = postList;
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMessage) {
                    }
                });
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
            }
        });
    }

    //------------------------------------------------------------------------------------------------
    //----------------------------------------F I L T E R I N G---------------------------------------
    //------------------------------------------------------------------------------------------------
    private HashSet<String> selectedFilters = new HashSet<>();
    private HashSet<String> typeSelectedFilters = new HashSet<>();
    private Dialog dialog;
    private LinearLayout rentLayout, buyLayout, apartmentLayout, buildingLayout, houseLayout, parkingLayout, penthouseLayout, loftLayout, storageLayout;
    private EditText minPriceEditText, maxPriceEditText, minNumOfRoomsEditText, maxNumOfRoomsEditText;
    private CheckBox elevatorsCheckBox, airConditionerCheckBox, kosherKitchenCheckBox, storageCheckBox, waterHeaterCheckBox, renovatedCheckBox, accessForDisabledCheckBox, furnitureCheckBox;
    private Button continueButton, resetButton;
    private String minNumOfRooms;
    private String maxNumOfRooms;
    private String minPrice;
    private String maxPrice;
    private boolean flag = false;


    private void dialogInitUI() {
        dialog = new Dialog(getContext());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_search_filter);

        //Type Options
        rentLayout = dialog.findViewById(R.id.rentLinearLayout);
        buyLayout = dialog.findViewById(R.id.buyLinearLayout);

        //Property Types
        apartmentLayout = dialog.findViewById(R.id.apartmentLinearLayout);
        buildingLayout = dialog.findViewById(R.id.buildingLinearLayout);
        houseLayout = dialog.findViewById(R.id.houseLinearLayout);
        parkingLayout = dialog.findViewById(R.id.parkingLinearLayout);
        penthouseLayout = dialog.findViewById(R.id.penthouseLinearLayout);
        loftLayout = dialog.findViewById(R.id.loftLinearLayout);
        storageLayout = dialog.findViewById(R.id.storageLinearLayout);

        //Price Range
        minPriceEditText = dialog.findViewById(R.id.minPriceEditText);
        maxPriceEditText = dialog.findViewById(R.id.maxPriceEditText);

        //Number of rooms
        minNumOfRoomsEditText = dialog.findViewById(R.id.minNumOfRoomsEditText);
        maxNumOfRoomsEditText = dialog.findViewById(R.id.maxNumOfRoomsEditText);

        //Characteristics
        elevatorsCheckBox = dialog.findViewById(R.id.elevatorsCheckBox);
        airConditionerCheckBox = dialog.findViewById(R.id.airConditionerCheckBox);
        kosherKitchenCheckBox = dialog.findViewById(R.id.kosherKitchenCheckBox);
        storageCheckBox = dialog.findViewById(R.id.storageCheckBox);
        waterHeaterCheckBox = dialog.findViewById(R.id.waterHeaterCheckBox);
        renovatedCheckBox = dialog.findViewById(R.id.renovatedCheckBox);
        accessForDisabledCheckBox = dialog.findViewById(R.id.accessForDisabledCheckBox);
        furnitureCheckBox = dialog.findViewById(R.id.furnitureCheckBox);

        //Buttons
        continueButton = dialog.findViewById(R.id.continueButton);
        resetButton = dialog.findViewById(R.id.resetButton);

        continueButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        rentLayout.setOnClickListener(this);
        buyLayout.setOnClickListener(this);
        apartmentLayout.setOnClickListener(this);
        buildingLayout.setOnClickListener(this);
        houseLayout.setOnClickListener(this);
        parkingLayout.setOnClickListener(this);
        penthouseLayout.setOnClickListener(this);
        loftLayout.setOnClickListener(this);
        storageLayout.setOnClickListener(this);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.filterTextView) {
            dialogInitUI();
        } else if (id == R.id.rentLinearLayout || id == R.id.buyLinearLayout || id == R.id.apartmentLinearLayout || id == R.id.buildingLinearLayout || id == R.id.houseLinearLayout || id == R.id.parkingLinearLayout || id == R.id.penthouseLinearLayout || id == R.id.loftLinearLayout || id == R.id.storageLinearLayout) {
            toggleFilterSelection(v);
        } else if (id == R.id.continueButton) {
            obtainData();
            applyFilters();
            if (!flag)
                checkCharacteristics();
            resetFilters();
        } else if (id == R.id.resetButton) {
            resetFilters();
        }
    }

    private void obtainData() {
        String elevators = String.valueOf(elevatorsCheckBox.isChecked());
        String airConditioner = String.valueOf(airConditionerCheckBox.isChecked());
        String kosherKitchen = String.valueOf(kosherKitchenCheckBox.isChecked());
        String storage = String.valueOf(storageCheckBox.isChecked());
        String waterHeater = String.valueOf(waterHeaterCheckBox.isChecked());
        String renovated = String.valueOf(renovatedCheckBox.isChecked());
        String accessForDisabled = String.valueOf(accessForDisabledCheckBox.isChecked());
        String furniture = String.valueOf(furnitureCheckBox.isChecked());
        minNumOfRooms = minNumOfRoomsEditText.getText().toString();
        maxNumOfRooms = maxNumOfRoomsEditText.getText().toString();
        minPrice = minPriceEditText.getText().toString();
        maxPrice = maxPriceEditText.getText().toString();

        if (elevators.equals("true")) {
            selectedFilters.add("Elevators");
        }

        if (airConditioner.equals("true")) {
            selectedFilters.add("Air Conditioner");
        }

        if (kosherKitchen.equals("true")) {
            selectedFilters.add("Kosher Kitchen");
        }

        if (storage.equals("true")) {
            selectedFilters.add("Storage");
        }

        if (waterHeater.equals("true")) {
            selectedFilters.add("Water Heater");
        }

        if (renovated.equals("true")) {
            selectedFilters.add("Renovated");
        }

        if (accessForDisabled.equals("true")) {
            selectedFilters.add("Access For Disabled");
        }

        if (furniture.equals("true")) {
            selectedFilters.add("Furniture");
        }
        if (apartmentLayout.isActivated()) {
            selectedFilters.add("Apartment");
        }

        if (buildingLayout.isActivated()) {
            selectedFilters.add("Building");
        }

        if (houseLayout.isActivated()) {
            selectedFilters.add("House");
        }

        if (parkingLayout.isActivated()) {
            selectedFilters.add("Parking");
        }

        if (penthouseLayout.isActivated()) {
            selectedFilters.add("Penthouse");
        }

        if (loftLayout.isActivated()) {
            selectedFilters.add("Loft");
        }

        if (storageLayout.isActivated()) {
            selectedFilters.add("Storage");
        }

    }

    private void checkCharacteristics() {
        String[] toBeRemoved = new String[filteredList.size()];
        int i = 0, currentPost = 0;
        if (!filteredList.isEmpty()) {
            int j = 0;
            outerLoop:
            for (HashMap<String, String> post : filteredList.values()) {
                boolean passFilter = true;
                innerLoop:
                for (String filter : selectedFilters) {
                    switch (filter) {
                        case "Elevators":
                        case "Air Conditioner":
                        case "Kosher Kitchen":
                        case "Water Heater":
                        case "Renovated":
                        case "Access For Disabled":
                        case "Furniture":
                        case "Storage":
                            passFilter &= checkFilter(post, filter);
                            if (!passFilter && !selectedFilters.isEmpty()) {
                                toBeRemoved[j++] = String.valueOf(currentPost);
                                break innerLoop;
                            }

                            if (filteredList.isEmpty()) {
                                break outerLoop;
                            }
                            break;
                    }
                }
                currentPost++;
            }
            for (String s : toBeRemoved) {
                filteredList.remove(s);
            }
        } else {
            for (HashMap<String, String> post : postList.values()) {
                boolean passFilter = true;
                for (String filter : selectedFilters) {
                    switch (filter) {
                        case "Elevators":
                        case "Air Conditioner":
                        case "Kosher Kitchen":
                        case "Water Heater":
                        case "Renovated":
                        case "Access For Disabled":
                        case "Furniture":
                        case "Storage":
                            passFilter &= checkFilter(post, filter);
                            break;
                    }
                }
                if (passFilter) {
                    filteredList.put(String.valueOf(i++), post);
                }
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
        }

        PostAdapter adapter = new PostAdapter(filteredList);
        recyclerView.setAdapter(adapter);
        dialog.dismiss();
    }

    private boolean checkFilter(HashMap<String, String> post, String filter) {
        return post.containsKey(filter) && post.get(filter).equals("true");
    }

    private void resetFilters() {
        for (LinearLayout layout : getFilterLayouts()) {
            TextView textView = layout.findViewById(getTextViewIdForLayoutId(layout.getId()));
            changeFrame(layout, textView, false);
        }
        selectedFilters.clear();
        typeSelectedFilters.clear();
    }

    private List<LinearLayout> getFilterLayouts() {
        List<LinearLayout> layouts = new ArrayList<>();
        layouts.add(rentLayout);
        layouts.add(buyLayout);
        layouts.add(apartmentLayout);
        layouts.add(buildingLayout);
        layouts.add(houseLayout);
        layouts.add(parkingLayout);
        layouts.add(penthouseLayout);
        layouts.add(loftLayout);
        layouts.add(storageLayout);
        return layouts;
    }

    private void toggleFilterSelection(View view) {
        LinearLayout layout = (LinearLayout) view;
        TextView textView = layout.findViewById(getTextViewIdForLayoutId(view.getId()));
        String filter = textView.getText().toString();

        if (typeSelectedFilters.contains(filter)) {
            typeSelectedFilters.remove(filter);
            changeFrame(layout, textView, false);
        } else {
            typeSelectedFilters.add(filter);
            changeFrame(layout, textView, true);
        }
    }

    private void changeFrame(LinearLayout layout, TextView textView, boolean isSelected) {
        if (isSelected) {
            // Set filled background and white text
            layout.setBackgroundResource(R.drawable.background_filled_frame_blue);
            textView.setTextColor(Color.WHITE);
        } else {
            // Set unfilled background and blue text
            layout.setBackgroundResource(R.drawable.background_empty_frame_blue);
            textView.setTextColor(Color.BLUE);
        }
    }

    private int getTextViewIdForLayoutId(int layoutId) {
        if (layoutId == R.id.rentLinearLayout) {
            return R.id.rentTextView;
        } else if (layoutId == R.id.buyLinearLayout) {
            return R.id.buyTextView;
        } else if (layoutId == R.id.apartmentLinearLayout) {
            return R.id.apartmentTextView;
        } else if (layoutId == R.id.buildingLinearLayout) {
            return R.id.buildingTextView;
        } else if (layoutId == R.id.houseLinearLayout) {
            return R.id.houseTextView;
        } else if (layoutId == R.id.parkingLinearLayout) {
            return R.id.parkingTextView;
        } else if (layoutId == R.id.penthouseLinearLayout) {
            return R.id.penthouseTextView;
        } else if (layoutId == R.id.loftLinearLayout) {
            return R.id.loftTextView;
        } else if (layoutId == R.id.storageLinearLayout) {
            return R.id.storageTextView;
        }
        return -1;
    }


    private void applyFilters() {
        filteredList.clear();
        int currentPost = 0;
        addToReSearchDB();

        for (HashMap<String, String> post : postList.values()) {
            for (String filter : typeSelectedFilters) {
                if (post.get("Type").toLowerCase().contains(filter.toLowerCase())) {
                    flag = true;
                    filteredList.put(String.valueOf(currentPost++), post);
                    break;
                }
            }
        }
        if (flag && filteredList.isEmpty()) {
            noSuchDataFound();
            return;
        }
        flag = false;
        findMinMax("Number Of Rooms", minNumOfRooms, maxNumOfRooms);
        if (flag && filteredList.isEmpty()) {
            noSuchDataFound();
            return;
        }
        findMinMax("Price", minPrice, maxPrice);
        if (flag && filteredList.isEmpty()) {
            noSuchDataFound();
        }
        flag = false;

    }

    private void findMinMax(String Child, String min, String max) {
        if (min.isEmpty()) {
            min = "0";
        }
        if (max.isEmpty()) {
            max = "100000000";
        }
        int currentPost = 0;
        if (filteredList.isEmpty()) {
            for (HashMap<String, String> post : postList.values()) {
                String postChild = post.get(Child);
                int val = Integer.parseInt(postChild);
                int minVal = Integer.parseInt(min);
                int maxVal = Integer.parseInt(max);
                if (val >= minVal && val <= maxVal) {
                    filteredList.put(String.valueOf(currentPost++), post);
                    flag = true;
                }
            }
        } else {
            int j = 0;
            String[] toBeRemoved = new String[filteredList.size()];
            for (HashMap<String, String> post : filteredList.values()) {
                String postChild = post.get(Child);
                int val = Integer.parseInt(postChild);
                int minVal = Integer.parseInt(min);
                int maxVal = Integer.parseInt(max);
                if (val < minVal || val > maxVal) {
                    toBeRemoved[j++] = String.valueOf(currentPost);
                    flag = true;
                }
                currentPost++;
            }
            for (String s : toBeRemoved) {
                filteredList.remove(s);
            }
        }
    }

    private void noSuchDataFound() {
        Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    private void addToReSearchDB() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(calendar.getTime());

        HashMap<String, Object> viewedItemMap = new HashMap<>();
        viewedItemMap.put("itemId", selectedQuery);
        viewedItemMap.put("keyword", selectedQuery);
        viewedItemMap.put("filter", "Types: " + TextUtils.join(", ", typeSelectedFilters)
                + ",\nCharacteristics: " + TextUtils.join(", ", selectedFilters)
                + ",\nPrice min: " + minPrice + ", max: " + maxPrice + ",\nNumOfRooms min: " + minNumOfRooms + ", max: " + maxNumOfRooms

        );
        viewedItemMap.put("Date", currentDate);

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(selectedQuery + TextUtils.join(",", typeSelectedFilters) + TextUtils.join(",", selectedFilters)
                + minPrice + maxPrice + minNumOfRooms + maxNumOfRooms, viewedItemMap);

        database.updateDatabaseReference(updateMap, "User Recently Searched", new Database.GeneralCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

