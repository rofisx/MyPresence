package com.example.mypresence.Chart;

import com.google.gson.annotations.SerializedName;

public class BarChart {
    @SerializedName("year")
    private int Tahun;

    @SerializedName("growth_info")
    private float Pertumbuhan;

    public int getTahun() {
        return Tahun;
    }

    public float getPertumbuhan() {
        return Pertumbuhan;
    }
}
