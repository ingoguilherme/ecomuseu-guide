package com.ingoguilherme.ecomuseuguide.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ingoguilherme.ecomuseuguide.R;
import com.ingoguilherme.ecomuseuguide.bo.Achievement;
import com.ingoguilherme.ecomuseuguide.bo.Room;
import com.ingoguilherme.ecomuseuguide.dao.controller.AchievementDAO;
import com.ingoguilherme.ecomuseuguide.dao.controller.RoomDAO;
import com.ingoguilherme.ecomuseuguide.dao.handler.DatabaseHandler;
import com.ingoguilherme.ecomuseuguide.view.activities.MainActivity;

import java.util.ArrayList;


/**
 * Created by IngoGuilherme on 04-May-16.
 */
public class AchievementsFragment extends Fragment {

    private OnAchievementsFragmentInteractionListener mListener;

    View rootView;

    public AchievementsFragment() {
        // Required empty public constructor
    }

    public static AchievementsFragment newInstance() {
        AchievementsFragment fragment = new AchievementsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_achievements, container, false);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) rootView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        int size_im = width / 4;

        TableLayout table = (TableLayout) rootView.findViewById(R.id.table_achievement);
        table.setStretchAllColumns(true);

        DatabaseHandler dh = new DatabaseHandler(rootView.getContext());
        AchievementDAO achievementDAO = new AchievementDAO(dh);
        ArrayList<Achievement> all = achievementDAO.queryAllAchievement();
        ArrayList<Achievement> completed = achievementDAO.queryAllCompletedAchievement();

        TableRow row = new TableRow(rootView.getContext());
        row.setGravity(Gravity.CENTER);
        row.setPadding(5,5,5,15);
        int counter = 0;

        for(final Achievement a :all) {

            if(counter == 3){
                table.addView(row);
                row = new TableRow(rootView.getContext());
                row.setGravity(Gravity.CENTER);
                row.setPadding(5,5,5,15);
                counter = 0;
            }

            LinearLayout ll = new LinearLayout(rootView.getContext());
            ll.setOrientation(LinearLayout.VERTICAL);

            ImageView im = new ImageView(rootView.getContext());
            im.setImageResource(R.drawable.achievement_trophy_gray);

            for(Achievement c :completed){
                if(a.getId() == c.getId()){
                    im.setImageResource(R.drawable.achievement_trophy);
                    break;
                }
            }

            im.setLayoutParams(new TableRow.LayoutParams(size_im,size_im));

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) im.getLayoutParams();
            params.gravity = Gravity.CENTER;
            im.setLayoutParams(params);

            ll.addView(im);

            final RoomDAO roomDAO = new RoomDAO(dh);
            final Room r = roomDAO.queryRoomsByAchievementAndLanguage(a, MainActivity.selectedLanguage);

            TextView tv = new TextView(rootView.getContext());
            tv.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
            tv.setText(r.getName());

            ll.addView(tv);

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment f = ExpositionListFragment.newInstance(r.getId());
                    MainActivity.addLastOpenedFragment(f);
                    ft.replace(R.id.your_placeholder, f);
                    ft.commit();
                }
            });

            row.addView(ll);

            counter++;
        }

        table.addView(row);

        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAchievementsFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAchievementsFragmentInteractionListener) {
            mListener = (OnAchievementsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAchievementsFragmentInteractionListener {
        void onAchievementsFragmentInteraction(Uri uri);
    }
}