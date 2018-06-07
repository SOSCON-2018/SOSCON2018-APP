package com.example.tcy.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tcy.app.R;
public class BaseDialog extends Dialog {
    private String mContent;
    private Dialog mDialog;
    TextView contentView;
    Button button;
    public BaseDialog(Context context,String content){
        super(context,R.style.Dialog);
        mContent=content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_dialog);
        mDialog=this;
        contentView=findViewById(R.id.dialog_content);
        button=findViewById(R.id.dialog_button);
        contentView.setText(mContent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        setCanceledOnTouchOutside(true);
    }
}
