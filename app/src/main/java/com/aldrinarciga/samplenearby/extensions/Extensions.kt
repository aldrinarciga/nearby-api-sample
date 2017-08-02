package com.aldrinarciga.samplenearby.extensions

import com.aldrinarciga.samplenearby.model.Endpoint

/**
 * Created by aldrinarciga on 8/2/2017.
 */


fun MutableList<Endpoint>.toEndpointIdList() : List<String>{
    var list : MutableList<String> = arrayListOf()
    forEach {
        list.add(it.endpointId!!)
    }
    return list
}