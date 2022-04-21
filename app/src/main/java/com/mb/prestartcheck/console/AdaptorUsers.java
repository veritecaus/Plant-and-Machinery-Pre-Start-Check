package com.mb.prestartcheck.console;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mb.prestartcheck.DateFormatScreen;
import com.mb.prestartcheck.Operator;
import com.mb.prestartcheck.Operators;
import com.mb.prestartcheck.R;
import com.mb.prestartcheck.Supervisor;
import com.mb.prestartcheck.Supervisors;
import com.mb.prestartcheck.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

public class AdaptorUsers extends RecyclerView.Adapter<AdaptorUsers.ViewHolder> {

    final Operators operators;
    private ArrayList<User> users = new ArrayList<User>();

    public  interface  OnUserClickListener
    {
        public void onUserSelected(User e);
    }

    private OnUserClickListener clickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        final ImageView imageView;
        final TextView textViewName;
        final TextView textViewLastLogin;
        final ConstraintLayout layout;

        public ViewHolder(View view)
        {
            super(view);
            view.setOnClickListener(this);
            imageView = (ImageView)view.findViewById(R.id.imageViewPhoto);
            textViewName = (TextView) view.findViewById(R.id.labelOperatorName);
            textViewLastLogin = (TextView)view.findViewById(R.id.labelOperatorLastLoginTime);
            layout = (ConstraintLayout)view.findViewById(R.id.constraintLayoutOpeatorView);
        }

        public ImageView getImageView() { return this.imageView;}
        public TextView getTextViewName() { return this.textViewName;}
        public TextView getTextViewLastLogin() { return this.textViewLastLogin;}
        public ConstraintLayout getConstraintLayout() { return this.layout;}

        @Override
        public void onClick(View v) {

            clickListener.onUserSelected(users.get(getAdapterPosition()));
        }
    }

    public AdaptorUsers(Operators operators, Supervisors supervisors, OnUserClickListener l)
    {
        this.operators = operators;
        this.clickListener = l;
        buildUserList(operators, supervisors, false);
    }

    public AdaptorUsers(Operators operators, Supervisors supervisors, OnUserClickListener l, boolean all)
    {
        this.operators = operators;
        this.clickListener = l;
        buildUserList(operators, supervisors, all);
    }

    public void buildUserList(Operators operators, Supervisors supervisors, boolean all)
    {
        this.users.clear();
        for(Operator op  : operators)
            if (all || op.getEnabled())
                this.users.add(op);

        for(Supervisor sup : supervisors)
            if (all || sup.getEnabled())
                this.users.add(sup);

        this.users.sort(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getFirstName().compareToIgnoreCase(o2.getFirstName());
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_user, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        User user = this.users.get(position);

        if (user != null)
        {
            boolean isSuper  = Supervisor.class.isAssignableFrom(user.getClass());

            viewHolder.getTextViewName().setText(isSuper ? "Supervisor: " + user.getFullName() : user.getFullName());

            String lastLogin = "Last login - never";

            if (user.getLastLogin().getTime() > 0 ) {
                DateFormatScreen dateFormatScreen = new DateFormatScreen();
                String loginDateTime = dateFormatScreen.format(user.getLastLogin());
                lastLogin = String.format("Last login - %s", loginDateTime);
            }

            viewHolder.getTextViewLastLogin().setText(lastLogin);
            int bkcolor = Color.WHITE;
            if (!user.getEnabled()) bkcolor = Color.rgb(255,127,127);
            else if (isSuper) bkcolor = Color.YELLOW;

            viewHolder.getConstraintLayout().setBackgroundColor(bkcolor);
            viewHolder.getImageView().setImageResource(R.drawable.outline_account_circle_24);
        }

    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

}
