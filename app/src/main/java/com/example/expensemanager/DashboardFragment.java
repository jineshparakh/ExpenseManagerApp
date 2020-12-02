package com.example.expensemanager;

import android.app.AlertDialog;
import android.graphics.drawable.Animatable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.Model.Data;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseApp;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {


    //Floating Button

    private FloatingActionButton fab_main;
    private FloatingActionButton fab_income;
    private FloatingActionButton fab_expense;

    //Floating Button TextView

    private TextView fab_income_text;
    private  TextView fab_expense_text;


    private  boolean  isOpen=false;


    // animation class objects
    private Animation fadeOpen, fadeClose;

    //Dashboard income and expense result

    private TextView totalIncomResult;
    private TextView totalExpenseResult;


    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Recycler view
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        //Connect Floating Button to layout

        fab_main=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income=myview.findViewById(R.id.income_ft_btn);
        fab_expense=myview.findViewById(R.id.expense_ft_btn);

        // Connect floating text
        fab_income_text=myview.findViewById(R.id.income_ft_text);
        fab_expense_text=myview.findViewById(R.id.expense_ft_text);

        //Total income and expense

        totalIncomResult = myview.findViewById(R.id.income_set_result);
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);

        //Recycler

        mRecyclerIncome = myview.findViewById(R.id.recycler_income);
        mRecyclerExpense = myview.findViewById(R.id.recycler_expense);

        //Animations

        fadeOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        fadeClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
                floatingButtonAnimation();
            }
        });

        //Calculate total income

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int total = 0;

                for(DataSnapshot mysnap: snapshot.getChildren()){

                    Data data = mysnap.getValue(Data.class);

                    total += data.getAmount();

                    String stResult = String.valueOf(total);

                    totalIncomResult.setText(stResult+".00");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total expense

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int total= 0;

                for(DataSnapshot mysnap: snapshot.getChildren()){

                    Data data = mysnap.getValue(Data.class);

                    total += data.getAmount();

                    String stResult = String.valueOf(total);

                    totalExpenseResult.setText(stResult+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);


        return myview;
    }
    //Floating button animation

    private void floatingButtonAnimation(){
        if(isOpen){
            fab_income.startAnimation(fadeClose);
            fab_expense.startAnimation(fadeClose);
            fab_income.setClickable(false);
            fab_expense.setClickable(false);
            fab_income_text.startAnimation(fadeClose);
            fab_expense_text.startAnimation(fadeClose);
            fab_income_text.setClickable(false);
            fab_expense_text.setClickable(false);
        }
        else{
            fab_income.startAnimation(fadeOpen);
            fab_expense.startAnimation(fadeOpen);
            fab_income.setClickable(true);
            fab_expense.setClickable(true);
            fab_expense_text.startAnimation(fadeOpen);
            fab_income_text.startAnimation(fadeOpen);
            fab_income_text.setClickable(true);
            fab_expense_text.setClickable(true);
        }
        isOpen=!isOpen;

    }
    private void addData(){
        //Fab Button Income
        fab_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertIncomeData();
            }
        });

        fab_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertExpenseData();
            }
        });
    }

    public void insertIncomeData(){
        AlertDialog.Builder mydialog= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);
        final AlertDialog dialog=mydialog.create();
        dialog.setCancelable(false);
        EditText edtamount=myview.findViewById(R.id.amount);
        EditText edtType=myview.findViewById(R.id.type_edt);
        EditText edtNote=myview.findViewById(R.id.note_edt);

        Button saveBtn=myview.findViewById(R.id.btnSave);
        Button cancelBtn=myview.findViewById(R.id.btnCancel);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type=edtType.getText().toString().trim();
                String amount=edtamount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edtType.setError("Please Enter A Type");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    edtamount.setError("Please Enter Amount");
                    return;
                }
                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Please Enter A Note");
                    return;
                }
                float amountInFloat=Float.parseFloat(amount);

                //Create random ID inside database
                String id=mIncomeDatabase.push().getKey();

                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(amountInFloat, type, note, id, mDate);

                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Transaction Added Successfully!", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                floatingButtonAnimation();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingButtonAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void insertExpenseData(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);

        final AlertDialog dialog=mydialog.create();
        dialog.setCancelable(false);
        EditText edtamount=myview.findViewById(R.id.amount);
        EditText edttype=myview.findViewById(R.id.type_edt);
        EditText edtnote=myview.findViewById(R.id.note_edt);

        Button saveBtn=myview.findViewById(R.id.btnSave);
        Button cancelBtn=myview.findViewById(R.id.btnCancel);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount=edtamount.getText().toString().trim();
                String type=edttype.getText().toString().trim();
                String note=edtnote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edttype.setError("Please Enter A Type");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    edtamount.setError("Please Enter Amount");
                    return;
                }
                if(TextUtils.isEmpty(note)){
                    edtnote.setError("Please Enter A Note");
                    return;
                }
                float amountInFloat=Float.parseFloat(amount);

                //Create random ID inside database
                String id=mExpenseDatabase.push().getKey();

                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(amountInFloat, type, note, id, mDate);

                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Transaction Added Successfully!", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                floatingButtonAnimation();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                floatingButtonAnimation();
            }
        });
        dialog.show();
    }
}