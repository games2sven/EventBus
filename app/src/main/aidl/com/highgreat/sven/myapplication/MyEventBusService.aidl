// MyEventBusService.aidl
package com.highgreat.sven.myapplication;

// Declare any non-default types here with import statements
import com.highgreat.sven.myapplication.Request;
import com.highgreat.sven.myapplication.Responce;

interface MyEventBusService {
    Responce send(in Request request);
}
