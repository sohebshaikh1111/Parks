package com.naindroidtech.parks.data;

import com.naindroidtech.parks.model.Park;

import java.util.List;

public interface AsyncResponse {

    void processPark(List<Park> parks);
}
