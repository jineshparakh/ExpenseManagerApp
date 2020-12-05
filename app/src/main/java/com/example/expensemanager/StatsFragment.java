package com.example.expensemanager;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.expensemanager.Model.Data;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import java.util.ArrayList;

import java.util.Map;
import java.util.TreeMap;

import com.github.mikephil.charting.charts.PieChart;


public class StatsFragment extends Fragment {

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private String[] type={"Income", "Expense"};
    private int[] values={0,0};
    private Map<Date, Integer> DateWiseIncome = new TreeMap<Date, Integer>();
    private Map<Date, Integer> DateWiseExpense = new TreeMap<Date, Integer>();
    private static Set<Pair<Integer,Integer>> DateWiseIncomeSorter= new HashSet<Pair<Integer,Integer>>();;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View myView = inflater.inflate(R.layout.fragment_stats, container, false);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);


        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                values[0] = 0;
                DateWiseIncome.clear();
                for (DataSnapshot mysnap : snapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);

                    values[0] += data.getAmount();
                    DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                    Date date = null;
                    try {
                        date = format.parse(data.getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DateWiseIncome.put(date,DateWiseIncome.getOrDefault(date,0)+data.getAmount());


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

                values[1] = 0;

                for (DataSnapshot mysnap : snapshot.getChildren()) {

                    Data data = mysnap.getValue(Data.class);

                    values[1] += data.getAmount();
                    DateFormat format = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
                    Date date = null;
                    try {
                        date = format.parse(data.getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DateWiseExpense.put(date,DateWiseExpense.getOrDefault(date,0)+data.getAmount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Pie Chart
        PieChart pieChart = myView.findViewById(R.id.piechart);
        ArrayList<PieEntry> data=new ArrayList<PieEntry>() ;
        for(int i=0;i<values.length;i++){
            data.add(new PieEntry(values[i], type[i]));
        }

        int[] colorClassArray=new int[]{0xFF669900, 0xFFCC0000};
        PieDataSet pieDataSet = new PieDataSet(data,"");
        pieDataSet.setColors(colorClassArray);
        PieData pieData=new PieData(pieDataSet);
        pieData.setValueTextSize(25);
        pieChart.setData(pieData);
        pieChart.animateXY(2000, 2000);

        pieChart.setDrawHoleEnabled(false);
        Legend l = pieChart.getLegend();
        l.setTextSize(18);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTextColor(Color.CYAN);
        l.setEnabled(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();


        //Line Chart 1
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        String[] xAxisValues = new String[DateWiseIncome.size()];
        ArrayList<Entry> incomeEntries = new ArrayList<>();
        int i=0;
        for (Map.Entry<Date, Integer> entry : DateWiseIncome.entrySet()){
            Format formatter = new SimpleDateFormat("MMM d, yyyy");
            String s = formatter.format(entry.getKey());
            xAxisValues[i]=s;
            incomeEntries.add(new Entry(i,entry.getValue()));
            i++;
            Log.i("DATE", xAxisValues[i-1]);
            Log.i("AMOUNT", String.valueOf(entry.getValue()));
        }
        dataSets = new ArrayList<>();
        LineDataSet set1;

        set1 = new LineDataSet(incomeEntries, "Income");
        set1.setColor(0xFF669900);
        set1.setValueTextColor(Color.CYAN);
        set1.setValueTextSize(15);
        set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSets.add(set1);
        LineChart mLineGraph = myView.findViewById(R.id.linechart);
        YAxis rightYAxis = mLineGraph.getAxisRight();
        rightYAxis.setTextColor(Color.BLUE);
        YAxis leftYAxis = mLineGraph.getAxisLeft();
        leftYAxis.setEnabled(true);
        leftYAxis.setTextColor(Color.BLUE);
        XAxis topXAxis = mLineGraph.getXAxis();
        topXAxis.setEnabled(true);
        topXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        set1.setLineWidth(4f);
        set1.setCircleRadius(3f);
        XAxis xAxis = mLineGraph.getXAxis();
        xAxis.setTextColor(Color.BLUE);
        mLineGraph.getXAxis().setLabelCount(DateWiseIncome.size());
        mLineGraph.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));

        LineData data2 = new LineData(dataSets);
        mLineGraph.setData(data2);

        mLineGraph.animateX(3000);
        mLineGraph.getLegend().setEnabled(false);
        mLineGraph.invalidate();
        mLineGraph.getDescription().setEnabled(false);





        ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
        String[] xAxisValues1 = new String[DateWiseExpense.size()];
        ArrayList<Entry> expenseEntries = new ArrayList<>();
         int j=0;
        for (Map.Entry<Date, Integer> entry : DateWiseExpense.entrySet()){
            Format formatter = new SimpleDateFormat("MMM d, yyyy");
            String s = formatter.format(entry.getKey());
            xAxisValues1[j]=s;
            expenseEntries.add(new Entry(j,entry.getValue()));
            j++;
            Log.i("DATE", xAxisValues1[j-1]);
            Log.i("AMOUNT", String.valueOf(entry.getValue()));
        }
        dataSets1 = new ArrayList<>();
        LineDataSet set2;

        set2 = new LineDataSet(expenseEntries, "Expense");
        set2.setColor(0xFFCC0000);
        set2.setValueTextColor(Color.CYAN);
        set2.setValueTextSize(15);
        set2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSets1.add(set2);
        LineChart mLineGraph1 = myView.findViewById(R.id.lineChart1);
        YAxis rightYAxis1 = mLineGraph1.getAxisRight();
        rightYAxis1.setTextColor(Color.BLUE);
        YAxis leftYAxis1 = mLineGraph1.getAxisLeft();
        leftYAxis1.setEnabled(true);
        leftYAxis1.setTextColor(Color.BLUE);
        XAxis topXAxis1 = mLineGraph1.getXAxis();
        topXAxis1.setEnabled(true);
        topXAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        set2.setLineWidth(4f);
        set2.setCircleRadius(3f);
        XAxis xAxis1 = mLineGraph1.getXAxis();
        xAxis1.setTextColor(Color.BLUE);
        mLineGraph1.getXAxis().setLabelCount(DateWiseExpense.size());
        mLineGraph1.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues1));

        LineData data3 = new LineData(dataSets1);
        mLineGraph1.setData(data3);

        mLineGraph1.animateX(3000);
        mLineGraph1.getLegend().setEnabled(false);
        mLineGraph1.invalidate();
        mLineGraph1.getDescription().setEnabled(false);


        return myView;


    }
}
