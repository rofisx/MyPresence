package com.example.mypresence.Chart;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ChartAPIInterFaces {
    @GET("infopresencebar.php")
    Call<List<BarChart>> getBarChartInfo();
}
