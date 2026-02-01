package com.example.mobileapp.data.model;
import com.google.gson.annotations.SerializedName;
public class ExecuteResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("output")
    private String output;
    @SerializedName("runtime")
    private String runtime;
    @SerializedName("error")
    private String error;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getOutput() {
        return output;
    }
    public void setOutput(String output) {
        this.output = output;
    }
    public String getRuntime() {
        return runtime;

    }
    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}