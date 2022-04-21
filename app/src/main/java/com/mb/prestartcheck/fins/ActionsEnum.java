package com.mb.prestartcheck.fins;

import com.mb.prestartcheck.QuestionMultipleChoice;

public enum ActionsEnum {
    EVENT_USER_LOGIN_ACTION ("User Login") ,
    EVENT_DEACTIVATION_INTERLOCK_ACTION (""),
    EVENT_ACTIVATION_INTERLOCK_ACTION (""),
    EVENT_USER_UNLOCKING_INTERLOCK_ACTION ("User unlocking the interlock"),
    EVENT_USER_DEACTIVATING_INTERLOCK_ACTION ("User deactivating the interlock"),
    EVENT_SUPERVISOR_UNLOCKING_INTERLOCK_ACTION ("Supervisor unlock the interlock"),
    EVENT_SUPERVISOR_ENTER_PIN_ACTION ("Supervisor PIN Entry"),
    EVENT_SUPERVISOR_UPDATE_YES_NO_TYPE_QUESTION_ACTION("Supervisor updates yes/no type question"),
    EVENT_SUPERVISOR_UPDATE_MULTIPLE_CHOICE_TYPE_QUESTION_ACTION("Supervisor updates multiple choice type question"),
    EVENT_SUPERVISOR_UPDATE_USER_INFORMATION_ACTION("Supervisor updates user information"),
    EVENT_SUPERVISOR_UPDATE_ADMIN_ACK_TYPE_QUESTION_ACTION("Supervisor updates admin ack type question"),
    EVENT_SUPERVISOR_UPDATE_ADMIN_OPTIONS_TYPE_QUESTION_ACTION("Supervisor updates admin options type question"),
    EVENT_SUPERVISOR_UPDATE_SECTION_ACTION ("Supervisor updates section"),
    EVENT_SUPERVISOR_UPDATE_EMAIL_SERVER_SETTINGS_ACTION ("Supervisor updates new email server settings"),
    EVENT_SUPERVISOR_UPDATE_EMAIL_OPTIONS_SETTINGS_ACTION ("Supervisor update new question reporting options settings"),
    EVENT_SUPERVISOR_UPDATE_BYPASS_OPTIONS_SETTINGS_ACTION ("Supervisor update new bypass reporting options settings"),
    EVENT_SUPERVISOR_UPDATE_MACHINE_SETTINGS_ACTION ("Supervisor update machine settings"),

    EVENT_SUPERVISOR_ADD_SECTION_ACTION ("Supervisor adds new section"),
    EVENT_SUPERVISOR_ADD_NEW_YES_NO_TYPE_QUESTION_ACTION ("Supervisor adds new yes/no type question"),
    EVENT_SUPERVISOR_ADD_NEW_MULTIPLE_CHOICE_TYPE_QUESTION_ACTION ("Supervisor adds new multiple choice type question"),
    EVENT_SUPERVISOR_ADD_NEW_ADMIN_ACK_TYPE_QUESTION_ACTION ("Supervisor adds new admin ack type question"),
    EVENT_SUPERVISOR_ADD_NEW_ADMIN_OPTIONS_TYPE_QUESTION_ACTION ("Supervisor adds new admin options type question"),
    EVENT_SUPERVISOR_ADD_NEW_USER_INFORMATION_ACTION ("Supervisor adds user information"),
    EVENT_SUPERVISOR_ADD_NEW_EMAIL_SERVER_SETTINGS_ACTION ("Supervisor adds new email server settings"),
    EVENT_SUPERVISOR_ADD_NEW_EMAIL_OPTIONS_SETTINGS_ACTION ("Supervisor adds new question reporting options settings"),
    EVENT_SUPERVISOR_ADD_NEW_BYPASS_OPTIONS_SETTINGS_ACTION ("Supervisor adds new bypass reporting options settings"),

    EVENT_SUPERVISOR_LOGIN_ACTION("Supervisor Login"),

    //Define audit contexts along the network stack.
    //Audit actions at the TCP/IP socket layer.
    EVENT_TCP_IP_SOCKETS("TCP/IP sockets"),
    //Audit actions at the FINS/TCP  layer after establishing socket layer communications.
    EVENT_FINS_TCP("FINS/TCP"),
    //Audit actions at the FIN command layer after establishing socket layer communications.
    EVENT_FINS_COMMAND("FINS command"),

    EVENT_LOGOUT_ACTION ("Logout");



    private String value;

    ActionsEnum(String value) {
        this.value = value;
    }
}
