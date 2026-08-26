package com.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long id;

    private Long profileId;

    private String name;

    private String icon;

    private String type;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
