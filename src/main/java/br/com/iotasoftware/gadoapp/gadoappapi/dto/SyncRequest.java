package br.com.iotasoftware.gadoapp.gadoappapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SyncRequest<T> {
    private List<T> data;
}