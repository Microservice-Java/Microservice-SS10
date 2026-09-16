package com.vietmart.promotionservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {
    private Long id;
    private String title;
    private String imageUrl;
    private String targetUrl;
    private String status;
}
