package layout;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.dcc.momentizeapp.Adapter.MemoryAdapter;
import com.dcc.momentizeapp.Data.Memory;
import com.dcc.momentizeapp.MainApp.MainApp;
import com.dcc.momentizeapp.R;
import com.dcc.momentizeapp.RegisterActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemoriesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MemoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemoriesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String blobId;
    TextView emptyText;
    GridView gridView;
    MemoryAdapter memoryAdapter;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ArrayList<Memory> memoryArrayList;
    private OnFragmentInteractionListener mListener;

    public MemoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemoriesFragment newInstance(String param1, String param2) {
        MemoriesFragment fragment = new MemoriesFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_memories, container, false);
        emptyText = (TextView) view.findViewById(R.id.emptyView);
        gridView = (GridView) view.findViewById(R.id.memories_gridview);
        memoryArrayList = new ArrayList<>();
        sharedPref = getActivity().getApplicationContext().getSharedPreferences("SharedPreference",Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        memoryAdapter = new MemoryAdapter(memoryArrayList,getActivity());
        gridView.setAdapter(memoryAdapter);
        MainApp.mDatabase.child("Users").child(MainApp.myUID).child(MainApp.BlobId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        blobId = dataSnapshot.getValue().toString();
                        if(blobId!=null) {
                            //Toast.makeText(getActivity(), blobId, Toast.LENGTH_LONG).show();
                            editor.putString(getString(R.string.blobId), blobId);
                            editor.commit();
                            fillInMemoriesList();
                            if (blobId ==null || blobId.equals(0 + "")) {
                                TastyToast.makeText(getActivity(),getString(R.string.memoryEmptyState),TastyToast.LENGTH_LONG,TastyToast.CONFUSING).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        return view;
    }

    public void fillInMemoriesList(){
        if(!blobId.isEmpty()){
        MainApp.mDatabase.child("Blobs").child(blobId).child("Memories").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            memoryArrayList.clear();
                         for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                             Memory memory = postSnapshot.getValue(Memory.class);
                             memoryArrayList.add(memory);
                        }
                        memoryAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
