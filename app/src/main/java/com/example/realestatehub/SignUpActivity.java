package com.example.realestatehub;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backButton;
    private ImageView userImageView;
    private Button editImageView;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText birthdayEditText;
    private EditText phoneNumberEditText;
    private CountryCodePicker cpp;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapter;
    private Handler handler;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_in);
        // In your activity or fragment
        autoCompleteTextView = findViewById(R.id.addressAutoCompleteTextView);

        // Set up adapter for autocomplete
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        autoCompleteTextView.setAdapter(adapter);

        // Set up text change listener
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Save the current query
                currentQuery = charSequence.toString();

                // Remove any previous messages
                handler.removeMessages(0);

                // Fetch address suggestions as the user types
                handler.sendEmptyMessage(0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Set up handler for immediate search
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                // Fetch address suggestions for the current query
                new FetchAddressTask().execute(currentQuery);
                return true;
            }
        });
    }

    private class FetchAddressTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            try {
                String encodedQuery = URLEncoder.encode(params[0], "UTF-8");
                String apiUrl = "https://nominatim.openstreetmap.org/search?q=" +
                        encodedQuery + "&format=json&limit=5";
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }

                return parseAddressSuggestions(response.toString());
            } catch (IOException e) {
                Log.e("OpenStreetMap", "Error fetching address suggestions", e);
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<String> addressSuggestions) {
            // Update the adapter with the new suggestions
            adapter.clear();
            adapter.addAll(addressSuggestions);
            adapter.notifyDataSetChanged();
        }

        private List<String> parseAddressSuggestions(String response) {
            List<String> suggestions = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonAddress = jsonArray.getJSONObject(i);
                    String address = jsonAddress.getString("display_name");
                    suggestions.add(address);
                }
            } catch (JSONException e) {
                Log.e("OpenStreetMap", "Error parsing address suggestions", e);
            }
            return suggestions;
        }
    }

    @Override
    public void onClick(View v) {

    }
}
