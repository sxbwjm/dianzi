package com.example.dianzi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dianzi.adapter.FlowRecyclerViewAdapter;
import com.example.dianzi.MainApplication;
import com.example.dianzi.R;
import com.example.dianzi.activity.MainActivity;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.db.DataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CashflowReceivableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CashflowReceivableFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int mColumnCount = 1;

    private View rootView = null;
    private RecyclerView recyclerView = null;

    public CashflowReceivableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CashflowReceivableFragment newInstance(String param1, String param2) {
        CashflowReceivableFragment fragment = new CashflowReceivableFragment();
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
        rootView = inflater.inflate(R.layout.fragment_flow_list, container, false);
        FloatingActionButton addButton = rootView.findViewById(R.id.button_add);
        addButton.setVisibility(Button.INVISIBLE);
//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(ReceivableFragment.this).navigate(R.id.action_nav_payment_to_newBankflowPayFragment);
//            }
//        });



        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());

        recyclerView = rootView.findViewById(R.id.flow_list);
        Context context = recyclerView.getContext();
        //RecyclerView recyclerView = (RecyclerView) view;
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.obj != null) {
                   // List<Payment> payments = (List<Payment>)msg.obj;
                    recyclerView.setAdapter(new FlowRecyclerViewAdapter(DataSet.getInstance().allCashflowReceivalbeList));
                }
            }
        };


        dbAsyncTask.getAllCashflowReceivables(handler);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), (new LinearLayoutManager(getActivity()).getOrientation())));

        ((MainActivity)getActivity()).setTitle(R.string.bottom_nav_receivable);

        return rootView;
    }

}