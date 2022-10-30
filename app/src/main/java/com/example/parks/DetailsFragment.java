package com.example.parks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.parks.adapter.ViewPagerAdapter;
import com.example.parks.model.Park;
import com.example.parks.model.ParkViewModel;

import java.util.List;

public class DetailsFragment extends Fragment {
    private ParkViewModel parkViewModel;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager2 viewPager;


    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance() {
        DetailsFragment fragment = new DetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.details_viewpager);

        parkViewModel = new ViewModelProvider(requireActivity())
                .get(ParkViewModel.class);

        TextView parkName = view.findViewById(R.id.details_park_name);
        TextView parkDes = view.findViewById(R.id.detailsf_park_designation);
        TextView description = view.getRootView().findViewById(R.id.details_description);
        TextView activities = view.getRootView().findViewById(R.id.details_activites);
        TextView entranceFees = view.getRootView().findViewById(R.id.details_entrancefees);
        TextView opHours = view.getRootView().findViewById(R.id.details_operatinghours);
        TextView detailsTopics = view.getRootView().findViewById(R.id.details_topics);
        TextView directions = view.getRootView().findViewById(R.id.details_directions);

        parkViewModel.getSelectedPark().observe(getViewLifecycleOwner(), park -> {
            parkName.setText(park.getName());
            parkDes.setText(park.getDesignation());
            description.setText(park.getDescription());

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < park.getActivities().size(); i++) {
                stringBuilder.append(park.getActivities().get(i))
                        .append(" | ");
            }
            activities.setText(stringBuilder);
            if (park.getEntranceFees().size() > 0) {
                entranceFees.setText(String.format("Cost: $%s", park.getEntranceFees().get(0).getCost()));
            } else {
                entranceFees.setText(R.string.info_unavailable);
            }
            StringBuilder opsString = new StringBuilder();
            opsString.append("Wednesday: ").append(park.getOperatingHours().get(0)
                    .getStandardHours().getWednesday()).append("\n");
            opsString.append("Monday: ").append(park.getOperatingHours().get(0)
                    .getStandardHours().getMonday()).append("\n");
            opsString.append("Thursday: ").append(park.getOperatingHours().get(0)
                    .getStandardHours().getThursday()).append("\n");
            opsString.append("Sunday: ").append(park.getOperatingHours().get(0)
                    .getStandardHours().getSunday()).append("\n");
            opsString.append("Tuesday: ").append(park.getOperatingHours().get(0)
                    .getStandardHours().getTuesday()).append("\n");
            opsString.append("Friday: ").append(park.getOperatingHours().get(0)
                    .getStandardHours().getFriday()).append("\n");
            opsString.append("Saturday: ").append(park.getOperatingHours().get(0)
                    .getStandardHours().getSaturday()).append("\n");

            opHours.setText(opsString);

            StringBuilder topicBuilder = new StringBuilder();
            for (int i = 0; i < park.getTopics().size(); i++) {
                topicBuilder.append(park.getTopics().get(i).getName()).append(" | ");
            }
            detailsTopics.setText(topicBuilder);

            if (!TextUtils.isEmpty(park.getDirectionsInfo())) {
                directions.setText(park.getDirectionsInfo());
            } else {
                directions.setText(R.string.no_directions);
            }

            viewPagerAdapter = new ViewPagerAdapter(park.getImages());
            viewPager.setAdapter(viewPagerAdapter);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }
}