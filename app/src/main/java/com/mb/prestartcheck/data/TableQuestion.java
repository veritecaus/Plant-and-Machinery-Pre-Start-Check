package com.mb.prestartcheck.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "question")
public class TableQuestion {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "type_id")
    public int type_id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "number")
    public int number;

    @ColumnInfo(name = "allow_machine_op")
    public int allow_machine_op;

    @ColumnInfo(name = "time_out")
    public int time_out;

    @ColumnInfo(name = "enabled")
    public int enabled;

    @ColumnInfo(name = "is_critical")
    public int is_critical;

    @ColumnInfo(name = "is_negativepostive")
    public int is_negativepostive;
    
    @ColumnInfo(name = "alternative_title")
    public  String alternative_title;

    @ColumnInfo(name = "deleted")
    public int deleted;

    @ColumnInfo(name="updated_datetime")
    public Date updated_datetime;

    @ColumnInfo(name="created_datetime")
    public Date created_datetime;

    @ColumnInfo(name="sequence")
    public int sequence;

    @ColumnInfo(name="custom_field_1")
    public String custom_field_1;

    @ColumnInfo(name="custom_field_2")
    public String custom_field_2;

    @ColumnInfo(name="custom_field_3")
    public String custom_field_3;

    @ColumnInfo(name="custom_field_4")
    public String custom_field_4;

    @ColumnInfo(name="expected_answer")
    public String expected_answer;

    @ColumnInfo(name="expected_answer_neg")
    public String expected_answer_neg;

    @ColumnInfo(name="image_uri_one")
    public String image_uri_one;

    @ColumnInfo(name="image_uri_two")
    public String image_uri_two;

    @ColumnInfo(name="image_uri_three")
    public String image_uri_three;

    @ColumnInfo(name="image_uri_four")
    public String image_uri_four;

    @ColumnInfo(name = "comment")
    public  String comment;

}

