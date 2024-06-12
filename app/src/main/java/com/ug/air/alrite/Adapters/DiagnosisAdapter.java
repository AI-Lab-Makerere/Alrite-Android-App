package com.ug.air.alrite.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ug.air.alrite.Models.Diagnosis;
import com.ug.air.alrite.R;

import java.util.List;

public class DiagnosisAdapter extends RecyclerView.Adapter<DiagnosisAdapter.DiagnosisViewHolder> {

    private List<Diagnosis> diagnosisList;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    private OnItemClickListener mListener;

    public DiagnosisAdapter(List<Diagnosis> diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    @NonNull
    @Override
    public DiagnosisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diagnosis, parent, false);
        DiagnosisViewHolder diagnosisViewHolder = new DiagnosisViewHolder(view, mListener);
        return diagnosisViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiagnosisViewHolder holder, int position) {
        Diagnosis diagnosis = diagnosisList.get(position);
        holder.txtDiagnosis.setText(diagnosis.getDiagnosis());

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.recyclerView2.getContext(),
                LinearLayoutManager.VERTICAL,
                false);

        layoutManager.setInitialPrefetchItemCount(diagnosis.getAssessmentList().size());

        AssessmentAdapter assessmentAdapter = new AssessmentAdapter(diagnosis.getAssessmentList());

        holder.recyclerView2.setLayoutManager(layoutManager);
        holder.recyclerView2.setAdapter(assessmentAdapter);
        holder.recyclerView2.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return diagnosisList.size();
    }

    public class DiagnosisViewHolder extends RecyclerView.ViewHolder {

        private TextView txtDiagnosis;
        private RecyclerView recyclerView2;
        private ImageView imageView;
        private LinearLayout linearLayout1, linearLayout2;

        public DiagnosisViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            txtDiagnosis = itemView.findViewById(R.id.title);
            recyclerView2 = itemView.findViewById(R.id.recyclerView);
            imageView = itemView.findViewById(R.id.accordion);
            linearLayout1 = itemView.findViewById(R.id.clickable);
            linearLayout2 = itemView.findViewById(R.id.summary);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION){
                        listener.onClick(position);
                    }
                }
            });

//            linearLayout1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onClick(position);
//                            if (linearLayout2.getVisibility() == View.GONE){
//                                linearLayout2.setVisibility(View.VISIBLE);
//                                imageView.setImageResource(R.drawable.ic_sub);
//                            }else{
//                                linearLayout2.setVisibility(View.GONE);
//                                imageView.setImageResource(R.drawable.ic_add);
//                            }
//                        }
//                    }
//                }
//            });
//
//            linearLayout2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onClick2(position);
//                            linearLayout2.setVisibility(View.GONE);
//                            imageView.setImageResource(R.drawable.ic_add);
//                        }
//                    }
//                }
//            });
        }
    }
}
