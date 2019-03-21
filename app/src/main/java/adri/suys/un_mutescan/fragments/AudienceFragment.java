package adri.suys.un_mutescan.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.presenter.AudiencePresenter;
import adri.suys.un_mutescan.utils.OnSwipeTouchListener;

public class AudienceFragment extends Fragment {

    private ProgressBar progressBar;
    private Button inBtn, outBtn, allBtn;
    private View underlineIn, underlineOut, underlineAll;
    private RecyclerView recyclerView;
    private AudiencePresenter presenter;
    private AudienceAdapter adapter;
    private static final int ALL = 1;
    private static final int IN = 2;
    private static final int OUT = 3;
    private int currentTab = ALL;

    public AudienceFragment(){
        // required
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_audience, container, false);
        presenter = new AudiencePresenter(this);
        progressBar = view.findViewById(R.id.audience_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        inBtn = view.findViewById(R.id.audience_btn_in);
        outBtn = view.findViewById(R.id.audience_btn_out);
        allBtn = view.findViewById(R.id.audience_btn_all);
        underlineAll = view.findViewById(R.id.audience_underline_all);
        underlineIn = view.findViewById(R.id.audience_underline_in);
        underlineOut = view.findViewById(R.id.audience_underline_out);
        setButtonsText();
        recyclerView = view.findViewById(R.id.audience_recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        allBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_Green));
        setActions();
        getAudience(ALL, true);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        getAudience(ALL, true);
    }

    /**
     * Hides the ProgressBar
     */
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Update the adpater with the current state of the presenter
     * It updates the list of people we see on the screen
     */
    public void updateAudienceList() {
        if (adapter == null) {
            adapter = new AudienceAdapter(presenter);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setPresenter(presenter);
            adapter.notifyDataSetChanged();
        }
    }

    private void setActions(){
        inBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickIn();
            }
        });
        outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOut();
            }
        });
        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAll();
            }
        });
        recyclerView.setOnTouchListener(new OnSwipeTouchListener(getContext()){
            public void onSwipeRight() {
                switch (currentTab){
                    case OUT : clickIn(); break;
                    case IN : clickAll(); break;
                    default: break;
                }
            }
            public void onSwipeLeft() {
                switch (currentTab){
                    case ALL : clickIn(); break;
                    case IN : clickOut(); break;
                    default: break;
                }
            }
        });
    }

    private void setButtonsText(){
        inBtn.setText(getResources().getString(R.string.audience_btn_in, presenter.getNbIn()));
        outBtn.setText(getResources().getString(R.string.audience_btn_out, presenter.getNbOut()));
        allBtn.setText(getResources().getString(R.string.audience_btn_all, presenter.getNbAll()));
    }

    private void getAudience(int options, boolean starting){
        presenter.getAudience(options, starting);
    }

    private void clickAll(){
        inBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        outBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        allBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_Green));
        underlineIn.setBackgroundResource(0);
        underlineAll.setBackgroundResource(R.color.dark_Green);
        underlineOut.setBackgroundResource(0);
        progressBar.setVisibility(View.VISIBLE);
        currentTab = ALL;
        getAudience(ALL, false);
    }

    private void clickIn(){
        inBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_Green));
        outBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        allBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        underlineIn.setBackgroundResource(R.color.dark_Green);
        underlineAll.setBackgroundResource(0);
        underlineOut.setBackgroundResource(0);
        progressBar.setVisibility(View.VISIBLE);
        currentTab = IN;
        getAudience(IN, false);
    }

    private void clickOut(){
        inBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        outBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_Green));
        allBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        underlineIn.setBackgroundResource(0);
        underlineAll.setBackgroundResource(0);
        underlineOut.setBackgroundResource(R.color.dark_Green);
        progressBar.setVisibility(View.VISIBLE);
        currentTab = OUT;
        getAudience(OUT, false);
    }

    ////////////
    // HOLDER //
    ////////////

    public class AudienceHolder extends RecyclerView.ViewHolder {

        TextView buyerName, seatNo, ticketType;

        public AudienceHolder(View v){
            super(v);
            initViewElements(v);
        }

        public void displayInfos(String name, String ticketType, String seatNo){
            this.buyerName.setText(name);
            this.seatNo.setText(seatNo);
            this.ticketType.setText(ticketType);
        }

        private void initViewElements(View v){
            buyerName = v.findViewById(R.id.audience_buyer_name);
            seatNo = v.findViewById(R.id.audience_seat_no);
            ticketType = v.findViewById(R.id.audience_ticket_type);
        }
    }

    /////////////
    // ADAPTER //
    /////////////

    private class AudienceAdapter extends RecyclerView.Adapter<AudienceHolder>{

        private AudiencePresenter presenter;

        public AudienceAdapter(AudiencePresenter presenter){
            this.presenter = presenter;
        }

        @NonNull
        @Override
        public AudienceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_audience, viewGroup, false);
            return new AudienceHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AudienceHolder audienceHolder, int i) {
            presenter.onViewCounterpartAtPosition(i, audienceHolder);
        }

        @Override
        public int getItemCount() {
            return presenter.getItemCount();
        }

        public void setPresenter(AudiencePresenter presenter) {
            this.presenter = presenter;
        }
    }

}
