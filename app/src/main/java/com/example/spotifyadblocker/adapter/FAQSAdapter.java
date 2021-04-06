package com.example.spotifyadblocker.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyadblocker.databinding.FaqsItemsBinding;

import java.util.ArrayList;

public class FAQSAdapter extends RecyclerView.Adapter<FAQSAdapter.ViewHolder> {
    Context context;
    ArrayList<Integer> FaqsQuestions;
    ArrayList<Integer> FaqsAnswers;

    public FAQSAdapter(Context context, ArrayList<Integer> faqsQuestions, ArrayList<Integer> faqsAnswer) {
        this.context = context;
        FaqsQuestions = faqsQuestions;
        FaqsAnswers = faqsAnswer;
    }

    @NonNull
    @Override
    public FAQSAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FaqsItemsBinding faqsItemsBinding= FaqsItemsBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ViewHolder(faqsItemsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQSAdapter.ViewHolder holder, int position) {
        FaqsItemsBinding binding=holder.binding;
        Integer faq_q=FaqsQuestions.get(position);
        Integer faq_a=FaqsAnswers.get(position);

        binding.faqQ.setText(faq_q);
        binding.faqA.setText(faq_a);

        binding.faqCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.faqA.getVisibility()==View.GONE){
                    animateBtnUp(binding.btnDropdown);
                    binding.faqA.setVisibility(View.VISIBLE);
                }
                else {
                    animateBtnDown(binding.btnDropdown);
                    binding.faqA.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return FaqsQuestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
          FaqsItemsBinding binding;
        public ViewHolder(@NonNull FaqsItemsBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
    public void animateBtnUp(View view){
        ViewCompat.animate(view)
                .rotation(180.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(10.0F))
                .start();
    }
    public void animateBtnDown(View view){
        ViewCompat.animate(view)
                .rotation(0.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(10.0F))
                .start();
    }
}
