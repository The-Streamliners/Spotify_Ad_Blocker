package com.example.spotifyadblocker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.spotifyadblocker.adapter.FAQSAdapter;
import com.example.spotifyadblocker.R;
import com.example.spotifyadblocker.databinding.ActivityFaqsBinding;

import java.util.ArrayList;

public class FAQSActivity extends AppCompatActivity {
    ActivityFaqsBinding b;
    ArrayList<Integer> faqsQuestions=new ArrayList<>();
    ArrayList<Integer> faqsAnswers=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b=ActivityFaqsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setupFaqsQuestion();

        setupFaqsAnswer();

        FAQSAdapter faqsAdapter=new FAQSAdapter(this,faqsQuestions,faqsAnswers);
        b.recyclerView.setAdapter(faqsAdapter);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        b.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupFaqsAnswer() {
       faqsAnswers.add(R.string.faq_1_ans);

    }

    private void setupFaqsQuestion() {
      faqsQuestions.add(R.string.faq_1);

    }

}