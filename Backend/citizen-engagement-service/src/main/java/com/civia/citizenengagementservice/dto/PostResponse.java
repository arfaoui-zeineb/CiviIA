
package com.civia.citizenengagementservice.dto;

import com.civia.citizenengagementservice.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private Long incidentId;
    private Long authorId;
    private String title;
    private String content;
    private PostStatus status;
    private int confirmationCount;

}
