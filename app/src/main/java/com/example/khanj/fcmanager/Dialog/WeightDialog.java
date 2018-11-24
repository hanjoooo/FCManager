package com.example.khanj.fcmanager.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.khanj.fcmanager.Helper.WeightDialogListener;
import com.example.khanj.fcmanager.ManagePage.ManagementFragment;
import com.example.khanj.fcmanager.R;

import org.web3j.tx.Contract;

/**
 * Created by khanj on 2018-11-19.
 */

public class WeightDialog extends Dialog implements View.OnClickListener {
    private WeightDialogListener dialogListener;
    private static final int LAYOUT = R.layout.weightdialog;
    private Context context;

    private TextInputEditText etfweight;

    private TextView confirmTv;
    private TextView foodname;
    private TextView txnotice;

    private String fname;
    private int fweight;
    public WeightDialog(Context context, String fname, int fweight){
        super(context);
        this.context =context;
        this.fname = fname;
        this.fweight = fweight;



    }
    public void setDialogListener(WeightDialogListener dialogListener){
        this.dialogListener = dialogListener;
    }
    @Override
    protected void onCreate(Bundle savedIntanceState){
        super.onCreate(savedIntanceState);
        setContentView(LAYOUT);

        etfweight = (TextInputEditText) findViewById(R.id.foodweight);
        confirmTv = (TextView)findViewById(R.id.confirmdialog);
        foodname = (TextView)findViewById(R.id.foodname);
        txnotice = (TextView)findViewById(R.id.notice);
        foodname.setText(fname);
        txnotice.setText("실제 섭취량(g)을 입력해주세요.\n(기준량 : "+fweight+"g)");
        etfweight.setText(Integer.toString(fweight));
        etfweight.setSelection(etfweight.length());
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        confirmTv.setOnClickListener(this);


    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.confirmdialog:
                int foodweight = Integer.parseInt(etfweight.getText().toString());
                dialogListener.onPositiveClicked(foodweight);
                dismiss();
                break;
        }
    }
}
