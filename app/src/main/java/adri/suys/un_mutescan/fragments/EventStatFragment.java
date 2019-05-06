package adri.suys.un_mutescan.fragments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.model.Counterpart;
import adri.suys.un_mutescan.utils.MyFormatter;
import adri.suys.un_mutescan.presenter.EventStatPresenter;
import adri.suys.un_mutescan.viewinterfaces.EventStatViewInterface;

public class EventStatFragment extends Fragment implements EventStatViewInterface {

    private TextView eventName, eventDate, totalTicket, soldTicket, barChartTitle;
    private ProgressBar progressBar;
    private EventStatPresenter presenter;
    private PieChart pieChart;
    private HorizontalBarChart horizontalBarChart;
    private View view;
    private BarChart barChart;
    private Map<Counterpart, Integer> map;

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
        map = presenter.getDetailsOfSellCp();
        setPiechart();
        setHorizontalBarChart();
        initBarChart();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        setPiechart();
        setHorizontalBarChart();
    }


    public void displayEvent(String name, Date date, int totalTicket, int soldTicket, float presalePercentage){
        progressBar.setVisibility(View.GONE);
        this.eventName.setText(name);
        String totalTicketStr = getResources().getString(R.string.ticket_total, totalTicket);
        String soldTicketStr = getResources().getString(R.string.ticket_sold, soldTicket, presalePercentage);
        if (date != null){
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            this.eventDate.setText(df.format(date));
        }
        this.totalTicket.setText(totalTicketStr);
        this.soldTicket.setText(soldTicketStr);
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
        barChart = v.findViewById(R.id.barchart);
        barChartTitle = v.findViewById(R.id.barchart_title);
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

    private void initBarChart(){
        // graph
        float barWidth = 0.3f;
        float barSpace = 0.1f;
        float groupSpace = calculateGroupeSpace(barWidth, barSpace, 2);
        barChart.setDescription(null);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);
        BarDataSet nbTicketSold = new BarDataSet(nbTicketsPerCpStat(), "Nombre de tickets vendus");
        nbTicketSold.setColor(Color.RED);
        BarDataSet moneyMade = new BarDataSet(moneyPerCpStat(), "Somme récoltée");
        moneyMade.setColor(Color.BLUE);
        BarData data = new BarData(nbTicketSold, moneyMade);
        data.setValueFormatter(new LargeValueFormatter());
        barChart.setData(data);
        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.getData().setHighlightEnabled(false);
        barChart.invalidate();
        // legend
        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        // x & y-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(nbTicketsPerCpStat().size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getCpLabel()));
        barChart.setXAxisRenderer(new CustomXAxisRenderer(barChart.getViewPortHandler(), barChart.getXAxis(), barChart.getTransformer(YAxis.AxisDependency.LEFT)));
        barChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
        barChart.setExtraBottomOffset(50f);
        // visibility
        barChart.setVisibility(presenter.checkIfEventToday() ? View.VISIBLE : View.GONE);
        barChartTitle.setVisibility(presenter.checkIfEventToday() ? View.VISIBLE : View.GONE);

    }

    private List<BarEntry> nbTicketsPerCpStat(){
        List<BarEntry> nbTicketsPerCpStat = new ArrayList<>();
        Iterator it = map.entrySet().iterator();
        int i = 1;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            nbTicketsPerCpStat.add(new BarEntry(i, (Integer) pair.getValue()));
            i++;
        }
        return nbTicketsPerCpStat;
    }

    private List<BarEntry> moneyPerCpStat(){
        List<BarEntry> moneyPerCpStat = new ArrayList<>();
        Iterator it = map.entrySet().iterator();
        int i = 1;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Counterpart cp = (Counterpart) pair.getKey();
            float money = (float) (cp.getPrice() * (Integer) pair.getValue());
            moneyPerCpStat.add(new BarEntry(i, money));
            i++;
        }
        return moneyPerCpStat;
    }

    private String[] getCpLabel(){
        String[] cpName = new String[map.size()];
        Iterator it = map.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            cpName[i] = ((Counterpart)pair.getKey()).getName();
            i++;
        }
        return cpName;
    }

    private float calculateGroupeSpace(float barWidth, float barSpace, float nbBars){
        return 1 - ((barWidth + barSpace) * nbBars);
    }

    private class CustomXAxisRenderer extends XAxisRenderer {
        public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            String line[] = formattedLabel.split(" ");
            for (int i = 0; i < line.length; i++){
                Utils.drawXAxisValue(c, line[i], x, y + mAxisLabelPaint.getTextSize() * i, mAxisLabelPaint, anchor, angleDegrees);
            }
        }
    }


}
