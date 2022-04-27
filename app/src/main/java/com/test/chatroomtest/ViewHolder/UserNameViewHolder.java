package com.test.chatroomtest.ViewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.chatroomtest.Activity.ChooseRoomActivity;
import com.test.chatroomtest.Data.UserNameData;
import com.test.chatroomtest.Module.Config;
import com.test.chatroomtest.R;


public class UserNameViewHolder extends RecyclerView.ViewHolder {
    TextView nameText;
    CheckBox checkBox;
    public UserNameViewHolder(@NonNull View itemView) {
        super(itemView);
        nameText=itemView.findViewById(R.id.recycler_user_name_text);
        checkBox=itemView.findViewById(R.id.recycler_user_name_checkbox);
    }
    public void setView(UserNameData model){
        if (model!=null){
            if (model.getName().toString().equals(Config.AccountName)){
                nameText.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
            }
            else {
                nameText.setText(model.getName());
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        Config.friendName.add(model.getName());
                        Config.countChoose+=1;
                        if (Config.countChoose>0){
                            ChooseRoomActivity.createNewChatRoomBtn.setEnabled(true);
                        }else {
                            ChooseRoomActivity.createNewChatRoomBtn.setEnabled(false);
                        }
                    }else {
                        Config.friendName.remove(model.getName());
                        Config.countChoose-=1;
                        if (Config.countChoose>0){
                            ChooseRoomActivity.createNewChatRoomBtn.setEnabled(true);
                        }else {
                            ChooseRoomActivity.createNewChatRoomBtn.setEnabled(false);
                        }
                    }
                }
            });
        }
    }
}
