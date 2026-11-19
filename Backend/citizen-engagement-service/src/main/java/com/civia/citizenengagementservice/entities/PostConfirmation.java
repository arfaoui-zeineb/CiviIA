package com.civia.citizenengagementservice.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_confirmation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostConfirmation {

    @EmbeddedId
    private PostConfirmationId id;
}