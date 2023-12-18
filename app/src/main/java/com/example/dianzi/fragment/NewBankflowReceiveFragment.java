package com.example.dianzi.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dianzi.common.CommonFunc;
import com.example.dianzi.common.Config;
import com.example.dianzi.adapter.FlowRecyclerViewAdapter;
import com.example.dianzi.MainApplication;
import com.example.dianzi.R;
import com.example.dianzi.activity.MainActivity;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.db.DataSet;
import com.example.dianzi.entity.BankflowReceive;
import com.example.dianzi.entity.CashflowReceivable;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewBankflowReceiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewBankflowReceiveFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int mColumnCount = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View rootView;
    RecyclerView recyclerView;


    private  List<CashflowReceivable> candidateMatchList  = null;
    private BankflowReceive curBankflowReceive = null;
    List<CashflowReceivable> matchResult = null;

    public NewBankflowReceiveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewBankStatementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewBankflowReceiveFragment newInstance(String param1, String param2) {
        NewBankflowReceiveFragment fragment = new NewBankflowReceiveFragment();
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
        rootView = inflater.inflate(R.layout.fragment_new_bankflow_receive, container, false);
        init(rootView);
        rootView.findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBankflowReceive(view);
                NavHostFragment.findNavController(NewBankflowReceiveFragment.this).navigate(R.id.action_newBankStatementFragment_to_nav_bankflow);
            }
        });

        rootView.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(NewBankflowReceiveFragment.this).navigate(R.id.action_newBankStatementFragment_to_nav_bankflow);
            }
        });

        rootView.findViewById(R.id.button_match).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  float target = Float.parseFloat(((TextView)rootView.findViewById(R.id.amount)).getText().toString());
                BankflowReceive bankflowReceive = new BankflowReceive();
                bankflowReceive.amount = Float.parseFloat(((TextView)rootView.findViewById(R.id.amount)).getText().toString());
                matchResult = bankflowReceive.match(candidateMatchList);
                if(matchResult != null && matchResult.size() > 0) {
                    recyclerView.getAdapter().notifyDataSetChanged();
                }

            }
        });

        return rootView;
    }




    protected void init(View view) {

        curBankflowReceive = new BankflowReceive();

        EditText text = view.findViewById(R.id.date);
        text.setText(CommonFunc.getSystemDateString());
        curBankflowReceive.flowDate = text.getText().toString();

        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, Config.ACCOUNT_NAMES);
        Spinner spinner = (Spinner) view.findViewById(R.id.account_name);
        spinner.setAdapter(adapter);
        curBankflowReceive.name = spinner.getSelectedItem().toString();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                curBankflowReceive.name = spinner.getSelectedItem().toString();
                filterCashflowReceivableList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loadAllUnreceivedCashflowReceivables();


    }

    private void filterCashflowReceivableList() {
        candidateMatchList = curBankflowReceive.getCandidateCashflowReceivableList(DataSet.getInstance().unReceivedCashflowReceivalbeList);
        recyclerView.setAdapter(new FlowRecyclerViewAdapter(candidateMatchList));

    }

    protected void loadAllUnreceivedCashflowReceivables() {
        recyclerView = rootView.findViewById(R.id.flow_list);
        Context context = recyclerView.getContext();
        //RecyclerView recyclerView = (RecyclerView) view;
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        MainActivity activity = (MainActivity)getActivity();
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.obj != null) {
                    // List<Payment> payments = (List<Payment>)msg.obj;
                    filterCashflowReceivableList();
                }
            }
        };

        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        dbAsyncTask.getUnReceivedCashflowReceivables(handler);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), (new LinearLayoutManager(getActivity()).getOrientation())));
    }

    protected void saveBankflowReceive(View view) {
        curBankflowReceive.flowDate = ((TextView)rootView.findViewById(R.id.date)).getText().toString();
        curBankflowReceive.name = ((Spinner)rootView.findViewById(R.id.account_name)).getSelectedItem().toString();
        curBankflowReceive.amount = Float.parseFloat(((TextView)rootView.findViewById(R.id.amount)).getText().toString());

        MainActivity activity = (MainActivity)getActivity();
        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        dbAsyncTask.insertBankflowReceive(curBankflowReceive, matchResult);
    }
}