package com.example.registeration;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CourseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseFragment newInstance(String param1, String param2) {
        CourseFragment fragment = new CourseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ArrayAdapter yearAdapter;
    private Spinner yearSpinner;
    private ArrayAdapter termAdapter;
    private Spinner termSpinner;
    private ArrayAdapter areaAdapter;
    private Spinner areaSpinner;
    private ArrayAdapter majorAdapter;
    private Spinner majorSpinner;

    private String courseColleage = "";
    private ListView courseListView;
    private CourseListAdapter adapter;
    private List<Course> courseList;

    @Override
    public void onActivityCreated(@Nullable Bundle b) {
        super.onActivityCreated(b);

        final RadioGroup courseColleageGroup = getView().findViewById(R.id.courseColleageGroup);
        yearSpinner = getView().findViewById(R.id.yearSpinner);
        termSpinner = getView().findViewById(R.id.termSpinner);
        areaSpinner = getView().findViewById(R.id.areaSpinner);
        majorSpinner = getView().findViewById(R.id.majorSpinner);

        courseColleageGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                RadioButton courseButton = getView().findViewById(i);
                courseColleage = courseButton.getText().toString();

                yearAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.year, android.R.layout.simple_spinner_dropdown_item);
                yearSpinner.setAdapter(yearAdapter);
                yearSpinner.setSelection(0);
                termAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.term, android.R.layout.simple_spinner_dropdown_item);
                termSpinner.setAdapter(termAdapter);
                termSpinner.setSelection(2);
                if (courseColleage.equals("학부")) {
                    areaAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.colleageArea, android.R.layout.simple_spinner_dropdown_item);
                    areaSpinner.setAdapter(areaAdapter);
                    majorAdapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.colleageRefinementMajor, android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                } else if (courseColleage.equals("전공심화")) {
                    areaAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.graduateArea, android.R.layout.simple_spinner_dropdown_item);
                    areaSpinner.setAdapter(areaAdapter);
                    majorAdapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.graduateMajor, android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                }
            }
        });

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (areaSpinner.getSelectedItem().equals("교양및기타")) {
                    majorAdapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.colleageRefinementMajor, android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                }
                if (areaSpinner.getSelectedItem().equals("전공")) {
                    majorAdapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.colleageMajor, android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                }
                if (areaSpinner.getSelectedItem().equals("전공심화전공")) {
                    majorAdapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.graduateMajor, android.R.layout.simple_spinner_dropdown_item);
                    majorSpinner.setAdapter(majorAdapter);
                }
           }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        courseListView = getView().findViewById(R.id.courseListView);
        courseList = new ArrayList<>();
        adapter = new CourseListAdapter(getContext().getApplicationContext(), courseList, this);
        courseListView.setAdapter(adapter);

        Button searchButton = getView().findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundTask().execute();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {
            try {
                target = "http://ysb0522.cafe24.com/CourseList.php?courseColleage=" + URLEncoder.encode(courseColleage, "UTF-8") +
                        "&courseYear=" + URLEncoder.encode(yearSpinner.getSelectedItem().toString().substring(0, 4), "UTF-8") + "&courseTerm=" + URLEncoder.encode(termSpinner.getSelectedItem().toString(), "UTF-8") +
                        "&courseArea=" + URLEncoder.encode(areaSpinner.getSelectedItem().toString(), "UTF-8") + "&courseMajor=" + URLEncoder.encode(majorSpinner.getSelectedItem().toString(), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                courseList.clear();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                int courseID;
                String courseColleage;
                int courseYear;
                String courseTerm;
                String courseArea;
                String courseMajor;
                String courseGrade;
                String courseTitle;
                int courseCredit;
                int courseDivide;
                int coursePersonnel;
                String courseProfessor;
                String courseTime;
                String courseRoom;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    courseID = object.getInt("courseID");
                    courseColleage = object.getString("courseColleage");
                    courseYear = object.getInt("courseYear");
                    courseTerm = object.getString("courseTerm");
                    courseArea = object.getString("courseArea");
                    courseMajor = object.getString("courseMajor");
                    courseGrade = object.getString("courseGrade");
                    courseTitle = object.getString("courseTitle");
                    courseCredit = object.getInt("courseCredit");
                    courseDivide = object.getInt("courseDivide");
                    coursePersonnel = object.getInt("coursePersonnel");
                    courseProfessor = object.getString("courseProfessor");
                    courseTime = object.getString("courseTime");
                    courseRoom = object.getString("courseRoom");
                    Course course=new Course(courseID, courseColleage, courseYear, courseTerm, courseArea, courseMajor, courseGrade, courseTitle,courseCredit, courseDivide,  coursePersonnel, courseProfessor, courseTime, courseRoom);
                    courseList.add(course);
                    count++;
                }
                if (count == 0) {
                    AlertDialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(CourseFragment.this.getActivity());
                    dialog = builder.setMessage("조회된 강의가 없습니다.\n날짜를 확인하세요.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}