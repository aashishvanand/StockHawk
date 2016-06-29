package com.aashish.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.aashish.stockhawk.R;
import com.aashish.stockhawk.rest.Utils;
import com.aashish.stockhawk.service.HistoricalData;
import com.aashish.stockhawk.service.SymbolParcelable;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by aashi on 6/19/2016.
 */


public class StockDetails extends AppCompatActivity implements HistoricalData.HistoricalDataCallback {

    private static final String TAG = StockDetails.class.getSimpleName();

    HistoricalData historicalData;

    ArrayList<SymbolParcelable> symbolParcelables = null;

    @Bind(R.id.lineChart_activity_line_graph)
    LineChart lineChart;

    @Bind(R.id.toolbar_activity_line_graph)
    Toolbar toolbar;

    @Bind(R.id.ll_activity_line_graph)
    LinearLayout linearLayout;

    @Bind(R.id.currency)
    TextView cu;

    @Bind(R.id.stocksymbol)
    TextView ss;

    @Bind(R.id.yearhigh)
    TextView yh;

    @Bind(R.id.yearlow)
    TextView yl;

    @Bind(R.id.daylow)
    TextView dl;

    @Bind(R.id.dayhigh)
    TextView dh;

    @Bind(R.id.stock_name)
    TextView sn;

    @Bind(R.id.last_trade_date)
    TextView ltd;

    @Bind((R.id.earningsshare))
    TextView es;

    @Bind(R.id.marketcaptalization)
    TextView mc;

    String symbol = "";
    String name = "";
    String currency = "";
    String lasttradedate = "";
    String daylow = "";
    String dayhigh = "";
    String yearlow = "";
    String yearhigh = "";
    String earningsshare = "";
    String marketcapitalization = "";
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        ButterKnife.bind(this);

        symbol = getIntent().getStringExtra("symbol_name");
        name = getIntent().getStringExtra("name");
        currency = getIntent().getStringExtra("currency");
        lasttradedate = getIntent().getStringExtra("lasttradedate");
        daylow = getIntent().getStringExtra("daylow");
        dayhigh = getIntent().getStringExtra("dayhigh");
        yearlow = getIntent().getStringExtra("yearlow");
        yearhigh = getIntent().getStringExtra("yearhigh");
        earningsshare = getIntent().getStringExtra("earningsshare");
        marketcapitalization = getIntent().getStringExtra("marketcaptalization");

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        historicalData = new HistoricalData(this, this);
        historicalData.getHistoricalData(symbol);

        ss.setText(symbol);
        sn.setText(name);
        cu.setText(currency);
        ltd.setText(lasttradedate);
        dl.setText(daylow);
        dh.setText(dayhigh);
        yl.setText(yearlow);
        yh.setText(yearhigh);
        es.setText(earningsshare);
        mc.setText(marketcapitalization);

    }

    @Override
    public void onSuccess(ArrayList<SymbolParcelable> sp) {

        this.symbolParcelables = sp;

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xvalues = new ArrayList<>();

        for (int i = 0; i < this.symbolParcelables.size(); i++) {

            SymbolParcelable symbolParcelable = this.symbolParcelables.get(i);
            double yValue = symbolParcelable.close;

            xvalues.add(Utils.convertDate(symbolParcelable.date));
            entries.add(new Entry((float) yValue, i));
        }

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setLabelsToSkip(5);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.rgb(182,182,182));

        YAxis left = lineChart.getAxisLeft();
        left.setEnabled(true);
        left.setLabelCount(10, true);
        left.setTextColor(Color.rgb(182,182,182));

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setTextSize(16f);
        lineChart.setDrawGridBackground(true);
        lineChart.setGridBackgroundColor(Color.rgb(25,118,210));
        lineChart.setDescriptionColor(Color.WHITE);
        lineChart.setDescription("Last 12 Months Stock Comparison");

        String name= getResources().getString(R.string.stock);
        LineDataSet dataSet = new LineDataSet(entries, name);
        LineData lineData = new LineData(xvalues, dataSet);

        lineChart.animateX(2500);
        lineChart.setData(lineData);

    }

    @Override
    public void onFailure() {
        String errorMessage = "";

        @HistoricalData.HistoricalDataStatuses
        int status = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(getString(R.string.historicalDataStatus), -1);

        switch (status) {
            case HistoricalData.STATUS_ERROR_JSON:
                errorMessage += getString(R.string.data_error_json);
                break;
            case HistoricalData.STATUS_ERROR_NO_NETWORK:
                errorMessage += getString(R.string.data_no_internet);
                break;
            case HistoricalData.STATUS_ERROR_PARSE:
                errorMessage += getString(R.string.data_error_parse);
                break;
            case HistoricalData.STATUS_ERROR_UNKNOWN:
                errorMessage += getString(R.string.data_unknown_error);
                break;
            case HistoricalData.STATUS_ERROR_SERVER:
                errorMessage += getString(R.string.data_server_down);
                break;
            case HistoricalData.STATUS_OK:
                errorMessage += getString(R.string.data_no_error);
                break;
            default:
                break;
        }

        final Snackbar snackbar = Snackbar
                .make(linearLayout, getString(R.string.no_data_show) + errorMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        historicalData.getHistoricalData(symbol);
                    }
                })
                .setActionTextColor(Color.GREEN);

        View subview = snackbar.getView();
        TextView tv = (TextView) subview.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.RED);
        snackbar.show();
    }
}