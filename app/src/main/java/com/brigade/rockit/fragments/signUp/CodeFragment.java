package com.brigade.rockit.fragments.signUp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.MailSender;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.StartActivity;
import com.brigade.rockit.database.ExceptionManager;

import java.util.ArrayList;



public class CodeFragment extends Fragment {

    private TextView timerTxt;
    private Button resendBtn;
    private int code;
    private StartActivity startActivity;
    private String timerText;
    private CountDownTimer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code, container, false);
        startActivity = (StartActivity)getActivity();
        timerText = getString(R.string.new_code);


        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTxt.setText(timerText + " "
                        + millisUntilFinished/1000/60 + ":" + millisUntilFinished/1000);
            }
            @Override
            public void onFinish() {
                resendBtn.setVisibility(View.VISIBLE);
            }
        };

        // Отправка письма с кодом на введенную почту
        sendEmail();

        //Получение виджетов
        timerTxt = view.findViewById(R.id.timer_txt);
        resendBtn = view.findViewById(R.id.resend_btn);

        Button nextBtn = view.findViewById(R.id.next_btn);
        Button backBtn = view.findViewById(R.id.back_btn);
        EditText codeEdit = view.findViewById(R.id.code_edit);

        // Повторная отправка письма
        resendBtn.setVisibility(View.INVISIBLE);
        resendBtn.setOnClickListener(v -> {
            sendEmail();
            resendBtn.setVisibility(View.INVISIBLE);
        });

        // Ввод кода
        nextBtn.setOnClickListener(v -> {
            if (!codeEdit.getText().toString().equals("")) {
                int enteredCode = Integer.parseInt(codeEdit.getText().toString());
                if (enteredCode == code) {
                    startActivity.nextRegFrag();
                    timer.onFinish();
                } else
                    Toast.makeText(getContext(), getString(R.string.invalid_code),
                            Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getContext(), getString(R.string.empty_code_error),
                        Toast.LENGTH_LONG).show();
        });

        // Возвращение на стартовый фрагмент
        backBtn.setOnClickListener(v -> {
            startActivity.goToStart();
        });


        return view;
    }




    // Отправка письма с кодом
    private void sendEmail() {
        String email = Data.getNewUser().getEmail();
        code = (int) ((Math.random() * 9 + 1) * 100000);
        Thread thread = new Thread(() -> {
            // Генерация кода

            ArrayList<String> recipient = new ArrayList<>();
            recipient.add(email);
            // Данные отправляемого письма
            String fromEmail = "rockit.noreply@yandex.ru";
            String emailPassword = "ibvpkqrxnerzlnyu";
            String subject = getActivity().getString(R.string.email_subject);
            String body = getActivity().getString(R.string.email_body) + " " + code;
            MailSender sender = new MailSender(fromEmail, emailPassword, recipient, subject, body);
            // Отправка
            try {
                sender.createEmailMessage();
                sender.sendEmail();
            } catch (Exception e) { // Обработка ошибок
                e.printStackTrace();
                //ExceptionManager.showError(e, getContext());
            }
        });
        thread.start();
        // Запуск таймера до новой возможности отправить письмо
        timer.start();
    }
}