package com.example.dianzi.fragment;

import android.content.Context;
import android.graphics.Color;
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

import com.example.dianzi.common.Config;
import com.example.dianzi.FlowRecyclerViewAdapter;
import com.example.dianzi.MainApplication;
import com.example.dianzi.R;
import com.example.dianzi.activity.MainActivity;
import com.example.dianzi.common.SubsetSum;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.db.DataSet;
import com.example.dianzi.entity.BankflowReceive;
import com.example.dianzi.entity.CashflowReceivable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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


    private  ArrayList<CashflowReceivable> candidateMatchList  = new ArrayList<CashflowReceivable>();
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
        rootView = inflater.inflate(R.layout.fragment_new_bank_statement, container, false);
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
                float target = Float.parseFloat(((TextView)rootView.findViewById(R.id.amount)).getText().toString());

                matchResult = match(target);
                if(matchResult != null && matchResult.size() > 0) {
//                    for(int i = 0; i < candidateMatchList.size(); i++) {
//                        CashflowReceivable c = candidateMatchList.get(i);
//                        if(matchResult.contains(c)) {
//                            recyclerView.getChildAt(i).setBackgroundColor(Color.GRAY);
//                        } else {
//                            recyclerView.getChildAt(i).setBackgroundColor(Color.WHITE);
//                        }
//                    }


                    recyclerView.getAdapter().notifyDataSetChanged();
                   // recyclerView.setAdapter(new FlowRecyclerViewAdapter(matchResult));
                }

            }
        });

        return rootView;
    }


    protected List<CashflowReceivable> match(float target) {


        if(candidateMatchList.size() == 0) {
            return null;
        }

        // reset bankflowId
        for(CashflowReceivable c : candidateMatchList) {
            c.bankflowId = 0;
        }

        int[] numbers = new int[candidateMatchList.size()];
        int sum = (int)(target * 10);
        for(int i = 0; i < candidateMatchList.size(); i++) {
            numbers[i] = (int)(candidateMatchList.get(i).amount * 10);
            System.out.print(numbers[i] + ",");
        }
        System.out.println();
        System.out.println("numbers:" + numbers);
        System.out.println("sum:" + sum);
        SubsetSum subsetSum = new SubsetSum();
       ArrayList<ArrayList<Integer>> result = subsetSum.findAllSubsets(numbers, sum);

       if(result.size() > 0) {
           List<CashflowReceivable> resultList = new ArrayList<CashflowReceivable>();
           ArrayList<Integer> firstMatchPath = result.get(0);
           System.out.println("firstMatchPath:" + firstMatchPath);
           for(CashflowReceivable c : candidateMatchList) {
               if(firstMatchPath.contains((int) (c.amount * 10))) {
                   resultList.add(c);
                   c.bankflowId = -1;
               }
           }
           return resultList;
       }

       return null;
    }

    protected void init(View view) {
        EditText text = view.findViewById(R.id.date);
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        text.setText(df.format(new Date()));

        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, Config.ACCOUNT_NAMES);
        Spinner spinner = (Spinner) view.findViewById(R.id.account_name);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filterCashflowReceivableList(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loadCashflowReceivables(spinner.getSelectedItem().toString());

    }

    private void filterCashflowReceivableList(String name) {
        candidateMatchList.clear();
        String bankflowDate = ((TextView)rootView.findViewById(R.id.date)).getText().toString();
        for(CashflowReceivable c : DataSet.getInstance().unReceivedCashflowReceivalbeList) {
            if(c.name.equals(name) && c.flowDate.compareTo(bankflowDate) < 0) {
                candidateMatchList.add(c);
            }
        }
        recyclerView.setAdapter(new FlowRecyclerViewAdapter(candidateMatchList));
    }

    protected void loadCashflowReceivables(String name) {
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
                    filterCashflowReceivableList(name);
                }
            }
        };

        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        dbAsyncTask.getUnReceivedCashflowReceivables(handler);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), (new LinearLayoutManager(getActivity()).getOrientation())));
    }

    protected void saveBankflowReceive(View view) {
        BankflowReceive bankflowReceive = new BankflowReceive();

        bankflowReceive.flowDate = ((TextView)rootView.findViewById(R.id.date)).getText().toString();
        bankflowReceive.name = ((Spinner)rootView.findViewById(R.id.account_name)).getSelectedItem().toString();
        bankflowReceive.amount = Float.parseFloat(((TextView)rootView.findViewById(R.id.amount)).getText().toString());

        MainActivity activity = (MainActivity)getActivity();
        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        dbAsyncTask.insertBankflowReceive(bankflowReceive, matchResult);
    }
}