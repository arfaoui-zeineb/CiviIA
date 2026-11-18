
package com.civia.citizenengagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    private Long incidentId;
    private Long authorId;
    private String title;
    private String content;

}
