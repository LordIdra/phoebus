package org.phoebus.applications.alarm.client;


public enum AlarmClientStatus {
    CONNECTING,
    KAFKA_CONNECTION_TIMED_OUT,
    ALARMSERVER_NOT_RUNNING,
    CONFIG_NOT_FOUND,
    LOADING,
    CONNECTED,
}
