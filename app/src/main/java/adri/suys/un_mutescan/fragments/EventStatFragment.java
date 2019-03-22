package adri.suys.un_mutescan.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.utils.MyFormatter;
import adri.suys.un_mutescan.presenter.EventStatPresenter;

public class EventStatFragment extends Fragment {

    private TextView eventName, eventDate, totalTicket, soldTicket;
    private ProgressBar progressBar;
    private EventStatPresenter presenter;
    private PieChart pieChart;
    private HorizontalBarChart horizontalBarChart;
    private View view;

    public EventStatFragment(){
        // required
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_event_stat, container, false);
        setViewElements(view);
        this.presenter = new EventStatPresenter(this);
        setPiechart();
        setHorizontalBarChart();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        setPiechart();
        setHorizontalBarChart();
    }


    public void displayEvent(String name, Date date, String totalTicket, String soldTicket){
        progressBar.setVisibility(View.GONE);
        this.eventName.setText(name);
        if (date != null){
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            this.eventDate.setText(df.format(date));
        }
        this.totalTicket.setText(totalTicket);
        this.soldTicket.setText(soldTicket);
    }

    /////////////////////
    // private methods //
    /////////////////////

    private void setViewElements(View v){
        eventName = v.findViewById(R.id.stat_event_name);
        eventDate = v.findViewById(R.id.stat_event_date);
        totalTicket = v.findViewById(R.id.stat_event_total_ticket);
        soldTicket = v.findViewById(R.id.stat_event_total_ticket_sold);
        progressBar = v.findViewById(R.id.progressBar_stat);
        progressBar.setVisibility(View.VISIBLE);
        pieChart = v.findViewById(R.id.piechart);
        horizontalBarChart = v.findViewById(R.id.horizontal_barchart);
    }

    private void setHorizontalBarChart(){
        BarDataSet dataSet = new BarDataSet(getNbOfCashBcPayment(), view.getResources().getString(R.string.progress_state_payment_mode));
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(dataSet);
        this.horizontalBarChart.setData(data);
        this.horizontalBarChart.animateXY(2000, 2000);
        this.horizontalBarChart.getDescription().setEnabled(false);
        this.horizontalBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getXAxisValue()));
        this.horizontalBarChart.getXAxis().setGranularity(1f);
        this.horizontalBarChart.setDrawValueAboveBar(false);
        this.horizontalBarChart.getAxisLeft().setAxisMaximum(100);
        this.horizontalBarChart.getAxisLeft().setAxisMinimum(0);
        this.horizontalBarChart.getAxisRight().setAxisMaximum(100);
        this.horizontalBarChart.getAxisRight().setAxisMinimum(0);
        this.horizontalBarChart.getLegend().setEnabled(false);
    }

    private void setPiechart(){
        List<PieEntry> entries = getValuesList();
        PieDataSet pieDataSet = new PieDataSet(entries, "");
        PieData pieData = new PieData(pieDataSet);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueFormatter(new MyFormatter());
        this.pieChart.setData(pieData);
        this.pieChart.animateY(2000);
        this.pieChart.setDrawEntryLabels(false);
        this.pieChart.getDescription().setEnabled(false);
        Legend legend = pieChart.getLegend();
        legend.setTextSize(13f);
        legend.setWordWrapEnabled(true);
    }

    private List<PieEntry> getValuesList(){
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(presenter.getTotalScanned(), view.getResources().getString(R.string.pie_chart_scanned)));
        entries.add(new PieEntry(presenter.getTotalToBeScanned(), view.getResources().getString(R.string.pie_chart_to_be_scanned)));
        entries.add(new PieEntry(presenter.getTotalSoldOnSite(), view.getResources().getString(R.string.pie_chart_sold_on_site)));
        entries.add(new PieEntry(presenter.getTotalRemaining(), view.getResources().getString(R.string.pie_chart_remaining)));
        return entries;
    }

    private List<BarEntry> getNbOfCashBcPayment(){
        double x = ((double) presenter.getTotalPaidInCash()) / presenter.getTotalSoldOnSite();
        double cashPercentage = x * 100;
        double cardPercentage = 100 - cashPercentage;
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) cashPercentage));
        entries.add(new BarEntry(1, (float) cardPercentage));
        return entries;
    }

    private List<String> getXAxisValue(){
        List<String> xAxisValues = new ArrayList<>();
        String cashLabel = view.getResources().getString(R.string.horizontalchart_label_cash, presenter.getTotalPaidInCash(), presenter.getTotalSoldOnSite());
        xAxisValues.add(cashLabel);
        int totalPaidWithCard = presenter.getTotalSoldOnSite() - presenter.getTotalPaidInCash();
        String cbLabel = view.getResources().getString(R.string.horizontalchart_label_cb, totalPaidWithCard, presenter.getTotalSoldOnSite());
        xAxisValues.add(cbLabel);
        return xAxisValues;
    }
}
