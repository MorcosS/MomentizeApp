package systembatarka.com.batarka.app.batarkasystem;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import systembatarka.com.batarka.app.batarkasystem.Adapters.AttendanceAdapter;
import systembatarka.com.batarka.app.batarkasystem.Adapters.KashfFaslAdapter;
import systembatarka.com.batarka.app.batarkasystem.Data.FireBaseDB;
import systembatarka.com.batarka.app.batarkasystem.Data.m5dumData;

import static systembatarka.com.batarka.app.batarkasystem.MainActivity.PlaceholderFragment.newInstance;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static String myClass, myDate;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    public static Firebase myFirebaseRef;
    public static boolean onOpen;
    public FloatingActionButton fab;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    public static ArrayList<m5dumData> kashfFaslList, attendanceList;
    public static List<String> weeksList;
    public static ListView ls, ls1;
    ArrayList<Integer> list;
    boolean attendance;
    FireBaseDB fireBaseDB;
    DatabaseReference myClassRef;
    Spinner spinner;
    DatabaseReference m5dumRef;
    com.google.firebase.database.DataSnapshot myChild;
    Activity activity;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mViewPager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://batarkasystem.firebaseio.com/");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("SharedPreference", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        myClass = sharedPref.getString("Class", "");
        myDate = sharedPref.getString("LastWeekDate", "");

        m5dumRef = FirebaseDatabase.getInstance().getReference("Classes").child(myClass).child("m5dumin");
        m5dumRef.keepSynced(true);
        //  attendanceRef = myFirebaseRef.child("Classes").child(myClass).child("Attendance");
        myClassRef = FirebaseDatabase.getInstance().getReference("Classes").child(myClass);
        myClassRef.keepSynced(true);
        // spinner.setSelection(3);
       /*
       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, weeksList);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);*/

      /*  spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("hihi", "hi bgd");
                final ArrayList<Integer> absence = new ArrayList<Integer>();
                final ArrayList<Integer> all = new ArrayList<Integer>();
                myClassRef.child("Attendance").child(adapterView.getItemAtPosition(i).toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        Iterable<com.google.firebase.database.DataSnapshot> myChildren = dataSnapshot.getChildren();
                        int i = 0;
                        while (myChildren.iterator().hasNext()) {

                            myChild = myChildren.iterator().next();
                            if (Boolean.parseBoolean(myChild.getValue().toString()) == true) {
                                i++;
                                absence.add(Integer.parseInt(myChild.getKey().toString()));
                            }
                            all.add(Integer.parseInt(myChild.getKey().toString()));
                        }
                        for (int j = 0; j < absence.size(); j++) {
                            Log.v("BatarkaSys", absence.get(j) + "");
                            myClassRef.child("m5dumin").child(absence.get(j) + "").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                    Log.v("BatarkaSys", dataSnapshot.child("Name") + "");
                                    attendanceList.add(new m5dumData(dataSnapshot.child("Name").getValue().toString(),
                                            dataSnapshot.child("Photo").getValue().toString(), dataSnapshot.child("Address").getValue().toString(),
                                            dataSnapshot.child("FloorNo").getValue().toString(), dataSnapshot.child("FlatNo").getValue().toString(),
                                            dataSnapshot.child("Mobile").getValue().toString(), dataSnapshot.child("Phone").getValue().toString(),
                                            dataSnapshot.child("FatherMob").getValue().toString(), dataSnapshot.child("MotherMob").getValue().toString(),
                                            dataSnapshot.child("Points").child("PointsTotal").getValue().toString(), dataSnapshot.child("DOB").getValue().toString(), dataSnapshot.getKey().toString()));
                                    Log.v("BatarkaSys", attendanceList.size() + "Annaaaanaaaa");
                                    myAdapter1 = new AttendanceAdapter(attendanceList, activity);
                                    ls.setAdapter(myAdapter1);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        for (int j = 0; j < all.size(); j++) {
                            myClassRef.child("m5dumin").child(all.get(j) + "").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                    kashfFaslList.add(new m5dumData(dataSnapshot.child("Name").getValue().toString(),
                                            dataSnapshot.child("Photo").getValue().toString(), dataSnapshot.child("Address").getValue().toString(),
                                            dataSnapshot.child("FloorNo").getValue().toString(), dataSnapshot.child("FlatNo").getValue().toString(),
                                            dataSnapshot.child("Mobile").getValue().toString(), dataSnapshot.child("Phone").getValue().toString(),
                                            dataSnapshot.child("FatherMob").getValue().toString(), dataSnapshot.child("MotherMob").getValue().toString(),
                                            dataSnapshot.child("Points").child("PointsTotal").getValue().toString(), dataSnapshot.child("DOB").getValue().toString(), dataSnapshot.getKey().toString()));
                                    myAdapter = new KashfFaslAdapter(kashfFaslList, activity);
                                    ls1.setAdapter(myAdapter);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_attend) {
            Intent intent = new Intent(MainActivity.this, ScanAttendance.class);
            startActivity(intent);
        }else if(id == R.id.nav_tgawob) {
            Intent intent = new Intent(MainActivity.this, ScanTagawob.class);
            startActivity(intent);
        }else if(id == R.id.nav_nota) {
            Intent intent = new Intent(MainActivity.this, ScanNota.class);
            startActivity(intent);
        }else if(id == R.id.nav_tanawol) {
            Intent intent = new Intent(MainActivity.this, ScanTanawol.class);
            startActivity(intent);
        }else if(id == R.id.nav_taraneem) {
            Intent intent = new Intent(MainActivity.this, ScanTaranim.class);
            startActivity(intent);
        }else if(id == R.id.nav_excelence) {
            Intent intent = new Intent(MainActivity.this, ScanTamayoz.class);
            startActivity(intent);
        }
/*
        } else if (id == R.id.nav_2016) {
            LoginActivity.counter = 0;
            Intent mainIntent = new Intent(MainActivity.this, yearNew.class);
            startActivity(mainIntent);


        } else if (id == R.id.nav_message) {
            LoginActivity.counter = 0;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://docs.google.com/document/d/1w1NfNxWW_ePslsr1wH9kTxSy2__zlN1-iDrX6epeaUg/edit?usp=docslist_api"));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else if (id == R.id.nav_sheet) {
            LoginActivity.counter = 0;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://docs.google.com/spreadsheets/d/1yzekRD-XMQSHVxue93_iOronifsTpNVUdbu1N8NYUVA/edit?usp=docslist_api"));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;


    }

    public static class PlaceholderFragment extends Fragment {
        View rootView1, rootView;
        FloatingActionButton fab;
        Query queryRef;
        com.google.firebase.database.DataSnapshot myChild, myChild2;
        DataSnapshot myChild1;
        public static ArrayList<m5dumData> kashfFaslList, attendanceList;
        public static List<String> weeksList;
        public static KashfFaslAdapter myAdapter;
                public static AttendanceAdapter myAdapter1;
        public static ListView ls, ls1;
        ArrayList<Integer> list;
        boolean attendance;
        FireBaseDB fireBaseDB;
        DatabaseReference myClassRef;
        Spinner spinner;
        DatabaseReference m5dumRef;
        Firebase attendanceRef;
        Calendar c;
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /*
         * Returns a new instance of this fragment for the given section
         * number.
*/
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                 final Bundle savedInstanceState) {
            m5dumRef = FirebaseDatabase.getInstance().getReference("Classes").child(myClass).child("m5dumin");
            m5dumRef.keepSynced(true);
            kashfFaslList = new ArrayList<m5dumData>();
            //  attendanceRef = myFirebaseRef.child("Classes").child(myClass).child("Attendance");
            c= Calendar.getInstance();
            final int day = c.get(Calendar.DAY_OF_WEEK);

            myClassRef = FirebaseDatabase.getInstance().getReference("Classes").child(myClass);
            myClassRef.keepSynced(true);
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.mytabkashfy, container, false);
                spinner = (Spinner) rootView.findViewById(R.id.spinner3);
                // spinner.setSelection(3);
                fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(day==Calendar.FRIDAY) {

                            Query query = MainActivity.myFirebaseRef.child("Classes").child(MainActivity.myClass).child("Attendance");
                            query.addValueEventListener(new com.firebase.client.ValueEventListener() {
                                @Override
                                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.child(c.getTime().getDate() + "-" + (c.getTime().getMonth() + 1) + "-" + (c.getTime().getYear() + 1900)).exists()) {
                                        for (int i = 0; i < MainActivity.PlaceholderFragment.kashfFaslList.size(); i++) {
                                            MainActivity.myFirebaseRef.child("Classes").child(MainActivity.myClass).child("Attendance").child(c.getTime().getDate() + "-" + (c.getTime().getMonth() + 1) + "-" + (c.getTime().getYear() + 1900)).child(MainActivity.PlaceholderFragment.kashfFaslList.get(i).getM5dumID()).setValue(false);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }
                    }
                });

                attendanceList = new ArrayList<m5dumData>();

                myClassRef.child("Attendance").addValueEventListener(new ValueEventListener() {
                    final ArrayList<String> dates = new ArrayList<String>();
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        dates.clear();
                        Iterable<com.google.firebase.database.DataSnapshot> myChildren = dataSnapshot.getChildren();
                        while (myChildren.iterator().hasNext()) {
                            int i = 0;
                            myChild = myChildren.iterator().next();
                            dates.add(myChild.getKey().toString());
                        }

                        ArrayAdapter<String> adapter =new ArrayAdapter<String>(rootView.getContext(),
                                android.R.layout.simple_spinner_dropdown_item,dates);
// Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
               /* ArrayAdapter adapter = ArrayAdapter.createFromResource(rootView.getContext(),
                        R.array.class_array, android.R.layout.simple_spinner_dropdown_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
                        spinner.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                ls = (ListView) rootView.findViewById(R.id.listView3);

   ArrayAdapter<String> adapter =new ArrayAdapter<String>(rootView.getContext(),
                        android.R.layout.simple_spinner_dropdown_item,weeksList);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
               /* ArrayAdapter adapter = ArrayAdapter.createFromResource(rootView.getContext(),
                        R.array.class_array, android.R.layout.simple_spinner_dropdown_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
//                spinner.setAdapter(adapter);

                final ArrayList<Integer> absence = new ArrayList<Integer>();
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.v("hihi", "hi bgd");
                        absence.clear();
                        attendanceList.clear();
                        myClassRef.child("Attendance").child(adapterView.getItemAtPosition(i).toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                Iterable<com.google.firebase.database.DataSnapshot> myChildren = dataSnapshot.getChildren();
                                int i = 0;
                                while (myChildren.iterator().hasNext()) {

                                    myChild = myChildren.iterator().next();
                                    if (Boolean.parseBoolean(myChild.getValue().toString()) == false) {
                                        i++;
                                        absence.add(Integer.parseInt(myChild.getKey().toString()));
                                    }
                                }
                                for (int j = 0; j < absence.size(); j++) {
                                    Log.v("BatarkaSys", absence.get(j) + "");
                                    myClassRef.child("m5dumin").child(absence.get(j) + "").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                                                Log.v("BatarkaSys", dataSnapshot.child("Name") + "");
                                                attendanceList.add(new m5dumData(dataSnapshot.child("Name").getValue().toString(),
                                                        dataSnapshot.child("Photo").getValue().toString(), dataSnapshot.child("Address").getValue().toString(),
                                                        dataSnapshot.child("FloorNo").getValue().toString(), dataSnapshot.child("FlatNo").getValue().toString(),
                                                        dataSnapshot.child("Mobile").getValue().toString(), dataSnapshot.child("Phone").getValue().toString(),
                                                        dataSnapshot.child("FatherMob").getValue().toString(), dataSnapshot.child("MotherMob").getValue().toString(),
                                                        dataSnapshot.child("Points").child("PointsTotal").getValue().toString(), dataSnapshot.child("DOB").getValue().toString(), dataSnapshot.getKey().toString()));
                                                Log.v("BatarkaSys", attendanceList.size() + "Annaaaanaaaa");
                                                myAdapter1 = new AttendanceAdapter(attendanceList, (Activity) rootView.getContext());
                                                ls.setAdapter(myAdapter1);
                                                ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                        Intent intent = new Intent(rootView.getContext(), M5dumDetails.class);
                                                        intent.putExtra("Name", myAdapter1.list.get(i).getM5dumName());
                                                        intent.putExtra("Address", myAdapter1.list.get(i).getM5dumAddress());
                                                        intent.putExtra("DOB", myAdapter1.list.get(i).getDob());
                                                        intent.putExtra("FatherMob", myAdapter1.list.get(i).getM5dumFatherMob());
                                                        intent.putExtra("FlatNo", myAdapter1.list.get(i).getM5dumFlatNo());
                                                        intent.putExtra("FloorNo", myAdapter1.list.get(i).getM5dumFloorNo());
                                                        intent.putExtra("Mobile", myAdapter1.list.get(i).getM5dumMobile());
                                                        intent.putExtra("MotherMob", myAdapter1.list.get(i).getM5dumMotherMob());
                                                        intent.putExtra("Phone", myAdapter1.list.get(i).getM5dumPhone());
                                                        intent.putExtra("Photo", myAdapter1.list.get(i).getM5dumPhoto());
                                                        intent.putExtra("Points", myAdapter1.list.get(i).getM5dumPoints());

                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                return rootView;
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView1 = inflater.inflate(R.layout.kashfy_layout, container, false);
                attendance = false;
                ls1 = (ListView) rootView1.findViewById(R.id.listView2);
                fab = (FloatingActionButton) rootView1.findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(rootView1.getContext(), Add_M5dumin.class);
                        startActivity(intent);
                    }
                });
                DatabaseReference m5dumIDRef = FirebaseDatabase.getInstance().getReference("Classes").child(myClass).child("m5dumIDs");
                m5dumIDRef.keepSynced(true);
                m5dumRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                            kashfFaslList.clear();
                            final SharedPreferences sharedPref = ((Activity) rootView1.getContext()).getSharedPreferences("SharedPreference", Activity.MODE_PRIVATE);
                            final SharedPreferences.Editor editor = sharedPref.edit();
                            Iterable<com.google.firebase.database.DataSnapshot> myChildren = dataSnapshot.getChildren();
                            while (myChildren.iterator().hasNext()) {
                                int i = 0;
                                myChild = myChildren.iterator().next();
                                try {
                                    m5dumData myM5dumData = new m5dumData(myChild.child("Name").getValue().toString(),
                                            myChild.child("Photo").getValue().toString(), myChild.child("Address").getValue().toString(),
                                            myChild.child("FloorNo").getValue().toString(), myChild.child("FlatNo").getValue().toString(),
                                            myChild.child("Mobile").getValue().toString(), myChild.child("Phone").getValue().toString(),
                                            myChild.child("FatherMob").getValue().toString(), myChild.child("MotherMob").getValue().toString(),
                                            myChild.child("Points").child("PointsTotal").getValue().toString(), myChild.child("DOB").getValue().toString(), myChild.getKey().toString());
                                    kashfFaslList.add(myM5dumData);
                                    myAdapter = new KashfFaslAdapter(kashfFaslList, (Activity) rootView1.getContext());
                                    ls1.setAdapter(myAdapter);
                                    ls1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            Intent intent = new Intent(rootView1.getContext(), M5dumDetails.class);
                                            intent.putExtra("Name", myAdapter.list.get(i).getM5dumName());
                                            intent.putExtra("Address", myAdapter.list.get(i).getM5dumAddress());
                                            intent.putExtra("DOB", myAdapter.list.get(i).getDob());
                                            intent.putExtra("FatherMob", myAdapter.list.get(i).getM5dumFatherMob());
                                            intent.putExtra("FlatNo", myAdapter.list.get(i).getM5dumFlatNo());
                                            intent.putExtra("FloorNo", myAdapter.list.get(i).getM5dumFloorNo());
                                            intent.putExtra("Mobile", myAdapter.list.get(i).getM5dumMobile());
                                            intent.putExtra("MotherMob", myAdapter.list.get(i).getM5dumMotherMob());
                                            intent.putExtra("Phone", myAdapter.list.get(i).getM5dumPhone());
                                            intent.putExtra("Photo", myAdapter.list.get(i).getM5dumPhoto());
                                            intent.putExtra("Points", myAdapter.list.get(i).getM5dumPoints());

                                            startActivity(intent);
                                        }
                                    });
                                    ls1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                        @Override
                                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                                            final CharSequence[] items = {
                                                    "تعديل"
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(rootView1.getContext());
                                            builder.setTitle("اختار الأمر المناسب ..");
                                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int item) {

                                                    if (items[item].equals("تعديل")) {
                                                        Intent intent = new Intent(rootView1.getContext(), Add_M5dumin.class);
                                                        intent.putExtra("id", myAdapter.list.get(i).getM5dumID());
                                                        intent.putExtra("Name", myAdapter.list.get(i).getM5dumName());
                                                        intent.putExtra("Address", myAdapter.list.get(i).getM5dumAddress());
                                                        intent.putExtra("DOB", myAdapter.list.get(i).getDob());
                                                        intent.putExtra("FatherMob", myAdapter.list.get(i).getM5dumFatherMob());
                                                        intent.putExtra("FlatNo", myAdapter.list.get(i).getM5dumFlatNo());
                                                        intent.putExtra("FloorNo", myAdapter.list.get(i).getM5dumFloorNo());
                                                        intent.putExtra("Mobile", myAdapter.list.get(i).getM5dumMobile());
                                                        intent.putExtra("MotherMob", myAdapter.list.get(i).getM5dumMotherMob());
                                                        intent.putExtra("Phone", myAdapter.list.get(i).getM5dumPhone());
                                                        intent.putExtra("Photo", myAdapter.list.get(i).getM5dumPhoto());
                                                        intent.putExtra("Points", myAdapter.list.get(i).getM5dumPoints());

                                                        startActivity(intent);
                                                                                                        }
                                                }
                                            });
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                            return true;
                                        }
                                    });
                                    editor.putBoolean("" + i, sharedPref.getBoolean("" + i, false));
                                    editor.commit();
                                    i++;
                                } catch (Exception e) {

                                }
                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

                return rootView1;
            }
            return null;
        }

    }
       /* public static void Refresh() {
            RequestQueue queue = Volley.newRequestQueue(rootView1.getContext());
            String url = "https://spreadsheets.google.com/feeds/list/126KOdwxG_9J42s0-mPlZBSVJVCctvSoIOohwSG_cFDQ/default/public/values?alt=json";
            StringRequest s = new StringRequest(url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            String st = response.toString();
                            Gson gson = new Gson();
                            SpreadSheetData momentsData = gson.fromJson(st, SpreadSheetData.class);
                            momentsDataList = new ArrayList<OurMomentsData>();
                            for (int i = 0; i < momentsData.getFeed().getEntry().size(); i++) {
                                momentsDataList.add(new OurMomentsData(momentsData.getFeed().getEntry().get(i).getGsx$momentsname().get$t(), momentsData.getFeed().getEntry().get(i).getGsx$momentsdescribtion().get$t().toString()
                                        , momentsData.getFeed().getEntry().get(i).getGsx$momentsimage().get$t().toString(), momentsData.getFeed().getEntry().get(i).getGsx$momentsdate().get$t().toString()));
                            }
                            myAdapter = new MyAdapter(momentsDataList, (Activity) rootView1.getContext());
                            ls.setAdapter(myAdapter);
                            ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(rootView1.getContext(), MomentsDetailed.class);
                                    intent.putExtra("item", i);
                                  //  startActivity(intent);
                                }
                            });
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(rootView1.getContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
                        }
                    });

            queue.add(s);
        }
    }*/


        public class SectionsPagerAdapter extends FragmentPagerAdapter {

            public SectionsPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public Fragment getItem(int position) {
                // getItem is called to instantiate the fragment for the given page.
                // Return a PlaceholderFragment (defined as a static inner class below).

                return newInstance(position + 1);
            }

            @Override
            public int getCount() {
                // Show 3 total pages.
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:

                        return "الغياب";
                    case 1:
                        return "كشف الفصل";
                }
                return null;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 0) {

                if (resultCode == RESULT_OK) {
                    String contents = data.getStringExtra("SCAN_RESULT");
                }
                if (resultCode == RESULT_CANCELED) {
                    //handle cancel
                }
            }
        }
    }


