/*
package com.example.capstoneproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ContactListAdaptersecond extends RecyclerView.Adapter<ContactListAdaptersecond.ViewHolder>  {
    String email;
    Context context;
    DBHelpersecond db;
    int layoutResourceId;
    ArrayList<EmerContactsecond> data=new ArrayList<EmerContactsecond>();
    public ContactListAdaptersecond(ArrayList<EmerContactsecond> data, int layoutResourceId, Context context, String email) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.email=email;
        db=new DBHelpersecond(context);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layoutResourceId, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EmerContactsecond contact=data.get(position);
        holder.textName.setText(contact._name);
        holder.textContact.setText(contact._phone);

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textName;
        public TextView textContact;
        public TextView btnEdit,btnDelete;

        public ViewHolder(View row) {
            super(row);
            textName=(TextView)row.findViewById(R.id.cont_name);
            textContact=(TextView)row.findViewById(R.id.cont_number);
            btnEdit=(TextView)row.findViewById(R.id.btn_edit);
            btnDelete=(TextView)row.findViewById(R.id.btn_delete);

            btnDelete.setOnClickListener(this);
            btnEdit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.btn_edit)
            {
                Intent intent=new Intent(context, contact_editsecond.class);
                Bundle b=new Bundle();
                b.putString("Name",textName.getText().toString());
                b.putString("Phone",textContact.getText().toString());
                b.putInt("ContactIndex", getPosition());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtras(b);
                context.startActivity(intent);
            }
            else if(v.getId()==R.id.btn_delete)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure you want to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int position = getPosition() + 1;
                                db.updatecontact("cont" + position, "NULL", email);
                                removeAt(getPosition());
                            }
                        }).setNegativeButton("No", null).show();
                builder.create();

            }


        }
        private void removeAt(int position) {
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(getPosition(), data.size());
        }
    }


    */
/*
     to store images efficiently in android
     retrieving is done in backgroud so as to avoid UI to slow down
     *//*



}*/
