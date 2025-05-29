package com.example.inspectioncarparts.domain.dto;

import com.example.inspectioncarparts.domain.entity.Part;
import lombok.Data;

@Data
public class ReplacePartRequest {
    private Integer oldPartId;
    private Integer newPartId;
    private Part newPart;
}
