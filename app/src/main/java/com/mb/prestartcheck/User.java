package com.mb.prestartcheck;

import com.mb.prestartcheck.data.TableUser;

import java.lang.String;
import java.util.Date;
import java.util.function.Predicate;

public abstract class User {


    protected String firstName = "";
    protected String lastName = "";
    protected String pin ="";
    protected long id = -1;
    protected boolean enabled = true;
    protected Date lastLogin = new Date();
    protected Date createdDateTime;
    protected Date updatedDatetime;

    public String getFirstName() { return this.firstName;}
    public String getLastName() { return this.lastName;}
    public String getFullName() { return String.format("%s %s", firstName, lastName);}
    public String getPin() { return this.pin;}
    public long getId () { return this.id;}
    public boolean getEnabled () { return this.enabled;}
    public Date getLastLogin() { return this.lastLogin;}
    public void setLastLogin(Date e) {  this.lastLogin = e;}

    public Date getCreatedDateTime() { return this.createdDateTime;}
    public void setCreatedDateTime(Date e) {this.createdDateTime = e;}

    public Date getUpdatedDatetime() { return this.updatedDatetime;}
    public void setUpdatedDatetime(Date e) { this.updatedDatetime = e;}

    public static final Predicate<User> pred_user_enabled = new Predicate<User>() {
        @Override
        public boolean test(User user) {
            return user == null ? false : user.getEnabled();

        }
    };

    User()
    {

    }



     User(String fn, String ln, String p) {
        this.firstName =  fn;
        this.lastName = ln;
        this.pin = p;
        this.id = -1L;
    }

    User(long id, String fn, String ln, String p) { this.firstName =  fn; this.lastName = ln; this.pin = p;
        this.id = id;}

        public static boolean isPinMatched(User u, String p)
        {
            return p.compareTo(u.getPin()) == 0;
        }

        public  static  TableUser toTableRow(User u)
        {
            TableUser row = new TableUser();
            row.id = u.getId();
            row.first_name = u.getFirstName();
            row.last_name = u.getLastName();
            row.pin = u.getPin();
            row.enabled = u.getEnabled() ? 1 : 0;
            Date now = new Date();

            row.updated_datetime = now.getTime();
            row.created_datetime = u.getCreatedDateTime().getTime();
            row.last_login = u.getLastLogin().getTime();
            row.role_id = u.getClass() == Operator.class ? 1 : 0;
            row.deleted = 0;
            return row;
        }

        public  static void fromTableRow(User u, TableUser table)
        {
            u.enabled = table.enabled  == 1 ;
            u.firstName = table.first_name;
            u.lastName = table.last_name;
            u.pin = table.pin;
            u.id = table.id;
            u.lastLogin = new Date(table.last_login);
            u.setCreatedDateTime( new Date(table.created_datetime));
            u.setUpdatedDatetime( new Date(table.updated_datetime));

        }

}
