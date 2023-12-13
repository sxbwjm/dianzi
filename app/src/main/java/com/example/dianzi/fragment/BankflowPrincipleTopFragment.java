package com.example.dianzi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dianzi.BankflowPrincipleRecyclerViewAdapter;
import com.example.dianzi.FlowRecyclerViewAdapter;
import com.example.dianzi.MainApplication;
import com.example.dianzi.R;
import com.example.dianzi.activity.MainActivity;
import com.example.dianzi.databinding.FragmentBankflowPrincipleTopBinding;
import com.example.dianzi.db.DBAsyncTask;
import com.example.dianzi.db.DataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BankflowPrincipleTopFragment extends Fragment {

    private FragmentBankflowPrincipleTopBinding binding;
    private int mColumnCount = 1;

    private View rootView = null;
    private RecyclerView recyclerView = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentBankflowPrincipleTopBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        recyclerView = rootView.findViewById(R.id.principle_list);
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
                    recyclerView.setAdapter(new BankflowPrincipleRecyclerViewAdapter(DataSet.getInstance().bankflowPrinciplesList));
                }
            }
        };

        DBAsyncTask dbAsyncTask = new DBAsyncTask(MainApplication.instance.getDB());
        dbAsyncTask.getAllBankflowPrinciple(handler);

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), (new LinearLayoutManager(getActivity()).getOrientation())));
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton addButton = view.findViewById(R.id.button_add);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(BankflowPrincipleTopFragment.this)
                        .navigate(R.id.action_Top_to_New);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}