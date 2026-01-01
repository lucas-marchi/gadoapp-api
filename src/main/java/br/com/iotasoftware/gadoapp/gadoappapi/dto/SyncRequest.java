package br.com.iotasoftware.gadoapp.gadoappapi.dto;

import java.util.List;

public class SyncRequest<T> {
    private List<T> data;

    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }
}