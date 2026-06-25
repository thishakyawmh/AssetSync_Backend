package com.innov8.AssetSync.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConflictResponse {
    private String message;
    private List<Resource> alternativeResources;
    private List<String> alternativeTimes;
}
